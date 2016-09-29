#!/usr/bin/python
# checks the links of the Spoon documentation
# a problem is reported as an exception, hence as a Unic return code != -1, hence as a build failure

import CommonMark
import glob
import os
import codecs
import requests

URLS = []

def check_external_url(url):
  # only checking the local or github files
  # we may comment since later and check everything but it takes time
  if "spoon.forge" not in url and "INRIA/spoon" not in url : return

  if url in URLS: return
  r = requests.get(url, headers = {"user-agent": "Mozilla/5.0 FakeBrowser"}) # sf.net, elsevier use stupid UA detection
  if r.status_code != 200:
    raise Exception(url+" invalid "+str(r.status_code))
  URLS.append(url)


def main(where):
  parser = CommonMark.Parser()

  for root, subdirs, files in os.walk(where):
    for i in files:
      filename = root + '/' + i
      if not filename.endswith('.md'): continue

      ast = parser.parse(codecs.open(filename, encoding="utf8").read())
      for i,_ in ast.walker(): 

        if i.__dict__['t'] == "link":
          url = i.__dict__['destination']
          if url.startswith('http'): check_external_url(url)
          # local file
          elif not url.startswith('/'):
            raise Exception("a link to a local page must start with / "+url+ " in "+filename)
          else:
            linked_page = where + '/' + url.replace(".html",".md")
            if not os.path.exists(linked_page): raise Exception("no such page "+linked_page)
    
def debug(filename):
  print "\n".join(str(x) for x in list(CommonMark.Parser().parse(codecs.open(filename, encoding="utf8").read()).walker()))

main("./doc")
