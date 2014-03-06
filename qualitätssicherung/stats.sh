#!/bin/bash
pushd ..> /dev/null

for part in src test; do
echo "in $part"
echo -n "Alle Zeilen: "
find $part -name '*.java' -exec cat {} \+ | wc -l
echo -n "keine Leerzeilen: "
find $part -name '*.java' -exec cat {} \+ | grep -v '^$' | wc -l
echo -n "keine Leerzeilen, Kommentare, imports oder Zeilen nur mit schliessender Klammer: "
find $part -name '*.java' -exec cat {} \+ | grep -v '^$' | grep -v '^import .*;$' | grep -v '^[[:space:]]*}[[:space:]]*$'  | grep -v '^[[:space:]]*\(//\|/\?\*\).*$' | wc -l
echo
done

popd > /dev/null
