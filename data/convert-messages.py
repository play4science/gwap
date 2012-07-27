#!/usr/bin/python
import os
import re

messagesRE = re.compile('messages_([a-z]{2}).properties')
lineRE = re.compile('\s*([\S^=]+)\s*=\s*([^#\n\r]+)(?:#.*)?\s*')

for filename in os.listdir('../resources'):
	match = messagesRE.match(filename)
	if (match):
		locale = match.group(1) # de/en/fr/...
		with open(filename) as file:
			for line in file:
				lineMatch = lineRE.match(line)
				if lineMatch:
					# escaping is a little bit more difficult :(
					print "insert into message (locale, key, value) values ('%s', '%s', '%s');" % \
							(locale, lineMatch.group(1), lineMatch.group(2).replace('\\', '\\\\').replace('"', '\\"').replace("'", "\\'"))


