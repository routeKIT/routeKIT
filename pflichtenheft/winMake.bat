java -jar Umlet/umlet.jar -action=convert -format=pdf -filename=Paketdiagramm.uxf -output=Paketdiagramm
pdflatex -interaction nonstopmode -file-line-error dokument.tex
makeglossaries dokument
pdflatex -interaction nonstopmode -file-line-error dokument.tex
makeglossaries dokument
pdflatex -interaction nonstopmode -file-line-error dokument.tex