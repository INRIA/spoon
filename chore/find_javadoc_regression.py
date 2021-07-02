#!/usr/bin/env python3

import re
import sys
from pathlib import Path
from typing import List, Dict


def filter_relevant_lines(lines: List[str]) -> List[str]:
    """Tries to filter the checkstyle output to only include lines with violations."""
    new_lines = []

    for line in lines:
        if re.search(r":\d+:", line) and "ERROR" in line:
            new_lines.append(line)

    return new_lines


def strip_line_number(line: str) -> str:
    return re.sub(r".java:\d+(:\d+)?:", ".java", line)


def strip_line_numbers(lines: List[str]) -> Dict[str, int]:
    """
    Strips the violation line numbers to account for shifts.
    This method returns a dictionary of "stripped line -> Count", as the same
    error might appear multiple times.
    """
    output_dict: Dict[str, int] = dict()

    for line in map(strip_line_number, lines):
        if line in output_dict:
            output_dict[line] += 1
        else:
            output_dict[line] = 1

    return output_dict


def try_match_lines(reference: Dict[str, int], other: Dict[str, int]) -> Dict[str, int]:
    """Tries to find out which lines are *new* in other and which were already present in the reference."""
    other = other.copy()
    for line, amount in reference.items():
        for _ in range(0, amount):
            if line in other:
                if other[line] > 1:
                    other[line] -= 1
                else:
                    other.pop(line)

    # Lines that are only in the reference aren't critical, as they were
    # apparently fixed in "other"
    return other


def try_readd_line_numbers(without_numbers: Dict[str, int], with_numbers: List[str]) -> List[str]:
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


def main(file_reference: Path, file_other: Path):
    with open(file_reference, "r") as file:
        lines_reference = filter_relevant_lines(file.readlines())
    with open(file_other, "r") as file:
        lines_other = filter_relevant_lines(file.readlines())

    new_errors = try_match_lines(
        strip_line_numbers(lines_reference),
        strip_line_numbers(lines_other)
    )
    with_line_numbers = try_readd_line_numbers(new_errors, lines_other)

    print("You likely added the following new errors. "
          "Line numbers might be incorrect if there are "
          "multiple violations of the same type in the file.")
    for error in with_line_numbers:
        print(error.strip())


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(f"Usage: {sys.argv[0]} <reference file> <own file>")
        exit(1)

    main(Path(sys.argv[1]), Path(sys.argv[2]))
