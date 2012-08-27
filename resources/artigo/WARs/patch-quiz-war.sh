#!/bin/sh
# Modifies the playn-showcase-html.war so that it runs on the correct
# artigo jboss server

FILE="resources/artigo/WARs/playn-quiz-html-1.0.war"

error() {
	echo $*
	[ "$TEMPDIR" != "" ] && echo "Failed, leaving directory $TEMPDIR for inspection"
	exit 1
}

[ -e $FILE ] || error "Could not find file, run from project root"

TEMPDIR=`mktemp -d --tmpdir=.`
cd $TEMPDIR || error "Could not create temp dir"
jar xf ../$FILE || error "Could not extract file"
/bin/echo -e "<jboss-web>\n        <context-root>/playn-quiz</context-root>\n        <virtual-host>artigo</virtual-host>\n</jboss-web>" > WEB-INF/jboss-web.xml || error "Could not create jboss-web.xml"
jar cf ../$FILE * || error "Could not create new archive"
cd ..
rm -rf $TEMPDIR
echo "Successfully patched archive"
