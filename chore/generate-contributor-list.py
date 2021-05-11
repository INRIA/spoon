#!/usr/bin/python
"""
Generate the contributor list for the readme of Spoon at https://github.com/INRIA/spoon/
"""

import subprocess

def get_raw_contributors():
  # git log --pretty="%an" | sort -u
  result = subprocess.run(['sh', '-c', 'git log --pretty="%an" | sort -u'], stdout=subprocess.PIPE)
  return result.stdout.decode('utf-8').split("\n")


def clean(raw_contributors):
  cleaned = []
  for i in raw_contributors:
    name = i
    if len(i) == 0: continue
    # full name for pre-Git era commits
    if i == 'noguera': name = "Carlos Noguera" # Spoon co-founder
    if i == 'renaud': name = "Renaud Pawlak" # Spoon co-founder
    if i == 'martin': continue # will be Martin Monperrus
    if i == 'matiasmartinez': name = "Matias Martinez"
    if i == "martinezmatias": continue
    if i == "petitpre": continue # will be Nicolas Petitprez
    if i == "seinturier": continue # will be Lionel Seinturier

    # deduplication
    if i == 'GerardPaligot': continue # will be "Gérard Paligot" 
    if i == 'Gerard Paligot': continue # will be "Gérard Paligot" 
    if i == 'gerard': continue # will be "Gérard Paligot" 
    if i == 'Gerard': continue # will be "Gérard Paligot" 
    if i == 'Tomasz Zielinski': continue # will be Tomasz Zielinski
    if i == 'tdurieux': continue # will be "Thomas Durieux" 
    if i == 'bdanglot': continue # will be "Benjamin Danglot" 
    if i == 'danglotb': continue # will be "Benjamin Danglot" 
    if i == 'maxcleme': continue # will be "Maxime Clément" 
    if i == 'Marcel': continue # will be "Marcel Manseer/Steinbeck" 
    if i == 'Egor18': continue # will be  Egor Bredikhin
    if i == 'arno': continue # will be "Arnaud Blouin" 
    if i == 'arno_b': name = "Arnaud Blouin" 
    if i == 'bcornu': name = "Benoit Cornu" 

    # we don't keep bots
    if i == 'anonsvn': continue
    if i == 'Spoon Bot': continue
    if i == 'renovate[bot]': continue
  
    cleaned.append(name)
  return cleaned
def format_to_md(contributors):
  return '\n'.join(["* "+x for x in contributors])

print(format_to_md(clean(get_raw_contributors())))
