#!/bin/bash
function output {
    echo "in $1"
    echo "Alle Zeilen: $2"
    echo "keine Leerzeilen: $3"
    echo "keine Leerzeilen, Kommentare, imports oder Zeilen nur mit schliessender Klammer: $4"
}

pushd ..> /dev/null
sum=0
sum_empty=0
sum_clean=0

for part in src test; do
    t_all="$(find $part -name '*.java' -exec cat {} \+)"
    t_no_empty="$(grep -v '^$' <<< "$t_all")"
    t_clean="$(grep -v '^import .*;$' <<< "$t_no_empty" | grep -v '^[[:space:]]*}[[:space:]]*$' | grep -v '^[[:space:]]*\(//\|/\?\*\).*$')"
    
    all=$(wc -l <<< "$t_all")
    no_empty=$(wc -l <<< "$t_no_empty")
    clean=$(wc -l <<< "$t_clean")
    
    output $part $all $no_empty $clean
    echo
    
    sum=$(($sum+$all))
    sum_empty=$(($sum_empty+$no_empty))
    sum_clean=$(($sum_clean+$clean))
done

output Gesamt $sum $sum_empty $sum_clean

popd > /dev/null
