#!/bin/bash
pushd ..> /dev/null
SUM=0
SUM_EMPTY=0
SUM_CLEAN=0

for part in src test; do
ALL=`find $part -name '*.java' -exec cat {} \+ | wc -l`
NO_EMPTY=`find $part -name '*.java' -exec cat {} \+ | grep -v '^$' | wc -l`
CLEAN=`find $part -name '*.java' -exec cat {} \+ | grep -v '^$' | grep -v '^import .*;$' | grep -v '^[[:space:]]*}[[:space:]]*$'  | grep -v '^[[:space:]]*\(//\|/\?\*\).*$' | wc -l`

echo "in $part"
echo "Alle Zeilen: $ALL"
echo "keine Leerzeilen: $NO_EMPTY"
echo "keine Leerzeilen, Kommentare, imports oder Zeilen nur mit schliessender Klammer: $CLEAN"
echo

SUM=$(($SUM+$ALL))
SUM_EMPTY=$(($SUM_EMPTY+$NO_EMPTY))
SUM_CLEAN=$(($SUM_CLEAN+$CLEAN))
done

echo "gesamt:"

echo "Alle Zeilen: $SUM"
echo "keine Leerzeilen: $SUM_EMPTY"
echo "keine Leerzeilen, Kommentare, imports oder Zeilen nur mit schliessender Klammer: $SUM_CLEAN"


popd > /dev/null
