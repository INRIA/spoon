#!/usr/bin/python
# Retrieve revapi report from target and publish it as PR comment
# This script intends to be launched by a webhooks and take as parameter the hook payload
# see: https://developer.github.com/v3/activity/events/types/#pullrequestevent

import os, sys, json
from github import *

revapi_file = "./target/revapi_report.md"
accepted_actions = ["opened", "synchronize"]

args = sys.argv

def usage():
    print "Usage: "+str(args[0])+ " <github_login> <github_token> <PR_payload_path>"
    exit(1)

if (args.__len__() < 4):
    print "Error in the args number"
    usage()

if (not(os.path.isfile(revapi_file))):
    print "Revapi report file cannot be found at the following location: "+revapi_file
    exit(1)

if (not(os.path.isfile(args[3]))):
    print "JSON payload file cannot be found at the following location: "+args[3]
    exit(1)

file_content = ""
with open(revapi_file) as f:
    for line in f:
        file_content += line

if (file_content == ""):
    print "Revapi report is empty"
    exit(1)

login = args[1]
token = args[2]

try:
    gh = Github(login,token)
except GithubException as e:
    print "Error while connecting to github, are you sure about your credentials?"
    print e
    exit(1)

payload_string = ""
with open(args[3]) as f:
    for line in f:
        payload_string += line

if (payload_string == ""):
    print "Payload is empty"
    exit(1)

payloadJSON = json.loads(payload_string)
repo_name = payloadJSON['repository']['full_name']
pr_id = payloadJSON['pull_request']['number']
action = payloadJSON['action']

if (action in accepted_actions):
    try:
        repo = gh.get_repo(repo_name,True)

        pr = repo.get_pull(pr_id)
        pr.create_issue_comment(file_content)
    except GithubException as e:
        print "Error while creating the PR comment."
        print e
        exit(1)
else:
    print "Call action: "+action
    exit(0)
