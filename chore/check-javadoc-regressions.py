#!/usr/bin/env python3

import os
import re
import sys
from collections import Counter
from contextlib import contextmanager
from pathlib import Path
from subprocess import PIPE, CalledProcessError, check_output
from tempfile import NamedTemporaryFile
from typing import List, Optional, Pattern

_COLOR_RESET = "\033[0m"
_COLOR_SUCCESS = "\033[92;1m"
_COLOR_HIGHLIGHT = "\033[36;1m"
_COLOR_DIM = "\033[2m"
_COLOR_WARNING = "\033[91;1m"


@contextmanager
def temporary_path(suffix: str):
    """
    Creates and returns a Path to a temporary file that will be deleted when
    the context manager exits.

    This is needed as NamedTemporaryFile is hopelessly broken on Windows due to mandatory file locks.
    See https://bugs.python.org/issue14243.
    """
    with NamedTemporaryFile(suffix=suffix, delete=False) as f:
        try:
            f.close()
            yield Path(f.name)
        finally:
            os.unlink(f.name)


def run_command(command: List[str], stderr: Optional[int] = None) -> str:
    """
    Runs a given native command. Also prints out what it is doing to keep users informed.
    """
    print(hl("$"), colored(" ".join(command), _COLOR_DIM))
    return check_output(command, stderr=stderr).decode()


def colored(text: str, color: str) -> str:
    return color + text + _COLOR_RESET


def success(text: str) -> str:
    return colored(text, _COLOR_SUCCESS)


def warn(text: str) -> str:
    return colored(text, _COLOR_WARNING)


def hl(text: str) -> str:
    return colored(text, _COLOR_HIGHLIGHT)


def print_quality_check_not_parseable_error():
    """
    If the user has setup problem that prevents the quality check from running they'd likely
    appreciate *some* output. If we can't extract a violation count the quality check
    probably didn't run successfully. Let's warn them.
    """
    print()
    print(warn("Quality check output doesn't contain a violation count. Maybe your setup has an error?"))
    if len(sys.argv) > 1:
        print(
            f" {hl('Suggestion')}: Run this script without arguments and inspect the unfiltered "
            "quality check output"
        )


def run_quality_check(target_branch: Optional[str] = None) -> str:
    """
    Runs the quality check and returns the output as a decoded string.
    Optionally you can pass a branch to borrow the quality check from, if
    it does not exist locally.
    """
    check_path = Path("chore/CheckJavadoc.java")
    runner_helper = Path("chore/run-with-spoon-javadoc-classpath.sh")

    if target_branch:
        print(warn(f"Borrowing quality script from '{target_branch}'"))
        run_command(["git", "checkout", target_branch, "--", str(check_path)])

    if not runner_helper.exists():
        print(warn(f"Borrowing runner script from '{target_branch}'"))
        run_command(["git", "checkout", target_branch, "--", str(runner_helper)])

    return run_command([
        str(runner_helper),
        str(check_path)
    ])


def extract_violation_count(quality_check_output: str) -> Optional[int]:
    """
    Tries to extract the violation count from the quality check output.
    Returns None if no count could be found.
    """
    match = re.search(r"There are (\d+) errors reported by Checkstyle", quality_check_output)
    if match:
        return int(match.group(1))
    return None


def filter_relevant_lines(lines: List[str]) -> List[str]:
    """Tries to filter the quality check output to only include lines with violations."""
    return [line for line in lines if re.search(r":\d+:", line) and "ERROR" in line]


def strip_line_number(line: str) -> str:
    return re.sub(r".java:\d+(:\d+)?:", ".java", line)


def find_added_violation_lines(reference: 'Counter[str]', other: 'Counter[str]') -> 'Counter[str]':
    """
    Tries to find out which violation lines are new in "other" and which
    were already present in the "reference". These lines are added violations the
    contributor should fix.

    Leftover lines that are only in the reference will be ignored, as they were apparently
    fixed in other.
    """
    return other - reference


def try_readd_line_numbers(without_numbers: 'Counter[str]', with_numbers: List[str]) -> List[str]:
    """
    Tries to re-add the line numbers to a dict of strings.

    This is not necessarily accurate, as it doesn't know which occurrence
    in "with_numbers" is the real source. If the error message is unique within
    a file it will find the exact match though.
    """
    output: List[str] = []

    for line in with_numbers:
        stripped = strip_line_number(line)
        if stripped in without_numbers:
            if without_numbers[stripped] == 1:
                without_numbers.pop(stripped)
            else:
                without_numbers[stripped] -= 1
            output.append(line)

    for line in without_numbers:
        output.append(line)

    return output


def print_regression_lines(reference: str, other: str) -> None:
    """
    Receives two quality check outputs, one for the reference and one for the current branch and
    prints all identified regressions.
    """
    lines_reference = filter_relevant_lines(reference.splitlines())
    lines_other = filter_relevant_lines(other.splitlines())

    new_errors = find_added_violation_lines(
        Counter(map(strip_line_number, lines_reference)),
        Counter(map(strip_line_number, lines_other))
    )
    with_line_numbers = try_readd_line_numbers(new_errors, lines_other)

    print(warn("You likely added the following new errors."), end=" ")
    print("Line numbers might be incorrect if there are multiple violations of the same type in the file.")
    for error in with_line_numbers:
        print(error.strip())


def command_compare_with_branch(target_branch: str) -> None:
    """
    Compares the current branch with a given reference branch and prints a change summary.
    Exits with an error if the current branch has more violations than the passed target branch.
    """
    # Switch to the root, so the quality check will run against the whole project
    os.chdir(run_command(["git", "rev-parse", "--show-toplevel"]).strip())

    original_branch = run_command(["git", "rev-parse", "--abbrev-ref", "HEAD"]).strip()
    if original_branch == "HEAD":
        original_branch = run_command(["git", "rev-parse", "HEAD"]).strip()
    run_command(["git", "checkout", target_branch], stderr=PIPE)
    reference_output = run_quality_check(original_branch)

    run_command(["git", "checkout", original_branch], stderr=PIPE)
    other_output = run_quality_check()

    handle_compare_with_branch_output(target_branch, reference_output, other_output)


def handle_compare_with_branch_output(target_branch: str, reference_output: str, other_output: str):
    reference_violation_count = extract_violation_count(reference_output)
    other_violation_count = extract_violation_count(other_output)

    if reference_violation_count is None:
        print(warn("The quality check run for the reference branch did not yield any result."))
        print_quality_check_not_parseable_error()
        exit(1)
    if other_violation_count is None:
        print(warn("The quality check run for your branch did not yield any result."))
        print_quality_check_not_parseable_error()
        exit(1)

    print_current_status(target_branch, reference_violation_count, other_violation_count)

    if reference_violation_count < other_violation_count:
        print_status_deteriorated(reference_output, other_output)
        exit(1)
    elif reference_violation_count == other_violation_count:
        print(success("Javadoc quality has not deteriorated!"))
        exit(0)
    else:
        print(success("You improved the Javadoc quality! Thank you :)"))
        exit(0)


def print_current_status(target_branch: str, reference_violation_count: int, other_violation_count: int):
    print(f"""
Current status
  Violations on {hl('comparison')} branch ({target_branch}): {hl(str(reference_violation_count))}
  Violations on {hl('your')}       branch   {' '*len(target_branch)}: {hl(str(other_violation_count))}
""")


def print_status_deteriorated(reference_output: str, other_output: str):
    print(f"""{warn("Javadoc quality has deteriorated!")}
Run the chore/check-javadoc-regresssions.py script locally to find errors.
See {hl('https://github.com/inria/spoon/issues/3923')} for details.
""")
    print_regression_lines(reference_output, other_output)


def command_filtered_quality_check_errors(regex_str: str) -> None:
    """
    Runs the quality check in the current working directory and filters the output using the passed regex.
    """
    regex: Pattern = re.compile(regex_str)
    quality_check_output = run_quality_check()

    for line in quality_check_output.splitlines():
        if regex.search(line):
            print(line)

    if extract_violation_count(quality_check_output) is None:
        print_quality_check_not_parseable_error()


def fix_colors_windows_cmd():
    # Taken from https://github.com/Microsoft/WSL/issues/1173#issuecomment-459934080
    import ctypes

    kernel32 = ctypes.windll.kernel32
    kernel32.SetConsoleMode(kernel32.GetStdHandle(-11), 7)


def print_help():
    print(f"""
 {hl('USAGE')}
   {sys.argv[0]} [COMPARE_WITH_MASTER | <regex file filter>] [--help]

 {hl('DESCRIPTION')}
   This is a small helper script to ensure the Javadoc quality in the project does not
   deteriorate. It runs during CI and enforces that new changes never increase the amount
   of Javadoc errors.
   Additionally, it tries to create a helpful output showing you the approximate location
   of any errors you might introduce so you have some guidance while fixing them.

 {hl('EXAMPLES')}
   {success('Compare your branch with the master branch to check for regressions.')}
     This will checkout the master branch, run the quality check, checkout your branch
     and compare the results.
     Note that the checkout will {hl('fail')} if you have incompatible uncommitted changes.

     {warn('python ' + sys.argv[0] + ' COMPARE_WITH_MASTER')}

   {success('List all violations in a set of files in your current working directory.')}
     Running this script with a regex as its only argument will execute the quality check in
     your working directory and filter the results using the regex you provided.
     This can be useful if you want to find all violations in files you touched or
     files the CI found errors in.

     {warn('python ' + sys.argv[0])} src/main/java/

   {success('List all violations in your current working directory.')}
     Running this script without any arguments will echo the full output back to you.
     This can be useful if you just want to invoke the quality check with an adequate
     configuration.

     {warn('python ' + sys.argv[0])}
    """[1:])


if __name__ == "__main__":
    if os.name == "nt":
        fix_colors_windows_cmd()

    if "--help" in sys.argv:
        print_help()
        exit(0)

    if len(sys.argv) > 2:
        print_help()
        exit(1)

    try:
        if len(sys.argv) == 2 and sys.argv[1] == "COMPARE_WITH_MASTER":
            command_compare_with_branch("master")
        else:
            command_filtered_quality_check_errors(sys.argv[1] if len(sys.argv) > 1 else "")
    except CalledProcessError as e:
        print("Error executing native command:", e.cmd)
        print("Error output:")
        if e.stderr:
            for line in e.stderr.decode().splitlines():
                print("  " + line)
        else:
            print("  No error output captured. Maybe it escaped and was printed already?")
        exit(1)
