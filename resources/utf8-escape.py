#!/usr/bin/python
import sys
import codecs

f = codecs.open(sys.argv[1], encoding='utf-8')
for line in f:
  print line.encode('unicode-escape').replace("'", "\\'").replace('\\u','u')
