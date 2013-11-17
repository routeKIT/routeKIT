java -jar Umlet/umlet.jar -action=convert -format=pdf -filename=Systemmodell1.uxf -output=Systemmodell1
pdflatex -interaction nonstopmode dokument.tex
makeglossaries dokument
pdflatex -interaction nonstopmode dokument.tex
pdflatex -interaction nonstopmode dokument.tex