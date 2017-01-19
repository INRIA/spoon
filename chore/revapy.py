#!/usr/bin/python
# Retrieve revapi Report and publish it as PR comment

import os, sys
from github import *

revapi_file = "./target/revapi.report"

args = sys.argv

if (args.__len__() < 3) or os.environ.get('GITHUB_TOKEN') == None or os.environ.get('TRAVIS_PULL_REQUEST') == None:
    print "Usage: "+str(args[0])+ " <login> <repository>"
    print "The following environement variable must be set as well: GITHUB_TOKEN and TRAVIS_PULL_REQUEST"
    exit(1)

if (str(os.environ.get('TRAVIS_PULL_REQUEST')) == "false"):
    print "Revapi report ignored as this is not launched by PR."
    exit(0)

login = args[1]
token = os.environ['GITHUB_TOKEN']
repo_name = args[2]
pr_id = int(os.environ['TRAVIS_PULL_REQUEST'])

if (not(os.path.isfile(revapi_file))):
    print "Revapi report file cannot be found at the following location: "+revapi_file
    exit(1)

file_content = "";
with open(revapi_file) as f:
    for line in f:
        file_content += line+"\n"

if (file_content == ""):
    print "Revapi report is empty"
    exit(1)

try:
    gh = Github(login,token)
    repo = gh.get_repo(repo_name,True)

    pr = repo.get_pull(pr_id)
    pr.create_issue_comment(file_content)
except GithubException as e:
    print "Error while creating the PR comment."
    print e
    exit(1)
