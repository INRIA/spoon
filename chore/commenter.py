import itertools
import json
import os
import sys


def fat(str):
    return '**' + str + '**'


def header(str, n):
    return ('#' * n) + ' ' + str + '\n\n'


def spoiler(summary, content):
    return '<details><summary>{}</summary>\n\n{}\n\n</details>'.format(summary, content)


def create_source_url(problem):
    server_url = os.environ['GITHUB_SERVER_URL']
    repository = os.environ['GITHUB_REPOSITORY']
    commit_sha = os.environ['GITHUB_SHA']

    our_file = problem['sources'][0]
    if not our_file['type'] == 'file':
        return None
    path = problem['sources'][0]['path']
    problem_line = our_file['line']
    l1 = max(0, problem_line - 1)
    l2 = problem_line + 1
    return "{}/{}/blob/{}/{}#L{}-L{}".format(server_url, repository, commit_sha, path, l1, l2)


if __name__ == '__main__':
    extract_category = lambda problem: problem['category']
    json_string = sys.stdin.read()
    content = json.loads(json_string)
    if not content['version'] == "3":
        print('The report might not be parsed correctly.')

    problem_list = content["listProblem"]
    if not problem_list:
        print('::set-output name=body:: Qodana has not reported any issues. Yay!')
    else:
        comment = 'Qodana reported following issues:\n\n'
        for category, problems in itertools.groupby(problem_list, extract_category):
            comment = comment + header(category, 2)
            for problem in problems:
                comment = comment + fat(problem['type']) + ': ' + problem['comment'] + '\n\n'
                comment = comment + spoiler('Explanation', problem['detailsInfo']) + '\n\n'
                comment = comment + create_source_url(problem) + '\n'
                comment = comment + "\n" + ('-' * 4) + '\n\n'
            comment = comment[:-7]

        comment = comment.replace('%', '%25').replace('\n', '%0A').replace('\r', '%0D')

        print('::set-output name=body::' + comment)
        print('::set-output name=has-problems::true')