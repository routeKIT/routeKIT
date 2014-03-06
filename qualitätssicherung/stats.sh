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

finder='find $part -name '"'"'*.java'"'"' -exec cat {} \+'
empty_regex='^$'
import_regex='^import .*;$'
single_closing_brace_regex='^[[:space:]]*}[[:space:]]*$'
comment_regex='^[[:space:]]*\(//\|/\?\*\).*$'

for part in src test; do
    all=$(eval "$finder" | wc -l)
    no_empty=$(eval "$finder" | grep -vc "$empty_regex")
    clean=$(eval "$finder" | grep -vce "$empty_regex" -e "$import_regex" -e "$single_closing_brace_regex" -e "$comment_regex")
    
    output $part $all $no_empty $clean
    echo
    
    sum=$(($sum+$all))
    sum_empty=$(($sum_empty+$no_empty))
    sum_clean=$(($sum_clean+$clean))
done

output Gesamt $sum $sum_empty $sum_clean

popd > /dev/null
