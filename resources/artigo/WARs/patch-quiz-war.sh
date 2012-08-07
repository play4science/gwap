#!/bin/sh
# Modifies the playn-showcase-html.war so that it runs on the correct
# artigo jboss server

[ -e /resources/artigo/WARs/playn-quiz-html-1.0.war ] || (echo "Could not find file, run from project root"; exit 1)

cd tmp && rm -rf *
jar xf ../resources/artigo/WARs/playn-quiz-html-1.0.war
cat > WEB-INF/jboss-web.xml <<EOF
<jboss-web>
        <context-root>/playn-quiz</context-root>
        <virtual-host>artigo</virtual-host>
</jboss-web>
EOF
jar cf ../resources/artigo/WARs/playn-quiz-html-1.0.war *

