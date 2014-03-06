#!/bin/bash
function output {
  echo "in $1"
  echo "Alle Zeilen: $2"
  echo "keine Leerzeilen: $3"
  echo "keine Leerzeilen, Kommentare, imports oder Zeilen nur mit schliessender Klammer: $4"
   
}

pushd ..> /dev/null
SUM=0
SUM_EMPTY=0
SUM_CLEAN=0

for part in src test; do
ALL=$(find $part -name '*.java' -exec cat {} \+ | wc -l)
NO_EMPTY=$(find $part -name '*.java' -exec cat {} \+ | grep -v '^$' | wc -l)
CLEAN=$(find $part -name '*.java' -exec cat {} \+ | grep -v '^$' | grep -v '^import .*;$' | grep -v '^[[:space:]]*}[[:space:]]*$'  | grep -v '^[[:space:]]*\(//\|/\?\*\).*$' | wc -l)

output $part $ALL $NO_EMPTY $CLEAN
echo

SUM=$(($SUM+$ALL))
SUM_EMPTY=$(($SUM_EMPTY+$NO_EMPTY))
SUM_CLEAN=$(($SUM_CLEAN+$CLEAN))
done

output Gesamt $SUM $SUM_EMPTY $SUM_CLEAN

popd > /dev/null
