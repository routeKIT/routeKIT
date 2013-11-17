java -jar Umlet/umlet.jar -action=convert -format=pdf -filename=Paketdiagramm.uxf -output=Paketdiagramm
pdflatex -interaction nonstopmode dokument.tex
makeglossaries dokument
pdflatex -interaction nonstopmode dokument.tex
pdflatex -interaction nonstopmode dokument.tex