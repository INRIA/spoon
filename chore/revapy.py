#!/usr/bin/python
# Computes the revapi report and publish it as PR comment

import os, sys, json, re
from github import *

revapi_file = "./target/revapi_report.md"
accepted_actions = ["pull_request"]

args = sys.argv

os.system("mvn -U revapi:report")

if (not(os.path.isfile(revapi_file))):
    print "Revapi report file cannot be found at the following location: "+revapi_file
    exit(1)


file_content = ""
with open(revapi_file) as f:
    for line in f:
        file_content += line

if (file_content == ""):
    print "Revapi report is empty"
    exit(1)

if (len(re.findall("changes.*: [123456789]", file_content))==0): exit(0)

login = os.environ["GITHUB_AUTH_USER"]
token = os.environ["GITHUB_AUTH_TOKEN"]

try:
    gh = Github(login,token)
except GithubException as e:
    print "Error while connecting to github, are you sure about your credentials?"
    print e
    exit(1)


repo_name = os.environ["TRAVIS_REPO_SLUG"]
pr_id = os.environ["TRAVIS_PULL_REQUEST"]
action = os.environ["TRAVIS_EVENT_TYPE"]

if (action in accepted_actions):
    try:
        repo = gh.get_repo(repo_name,True)

        pr = repo.get_pull(pr_id)
        # get_issue_comments() must be called and not get_comments()
        for comment in pr.get_issue_comments():
            # login is "spoon-bot" by default
            if comment.user.login == login:
                print "deleted ",comment
                comment.delete()
        pr.create_issue_comment(file_content)
    except GithubException as e:
        print "Error while creating the PR comment."
        print e
        exit(1)
else:
    print "Call action: "+action
    exit(0)
