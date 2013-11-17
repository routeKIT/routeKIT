LATEX = pdflatex -interaction nonstopmode
RM = rm -f

dokument.pdf: Paketdiagramm.pdf dokument.tex
	-$(LATEX) dokument.tex # initial build
	makeglossaries dokument # initial glossary
	-$(LATEX) dokument.tex # include glossary in document
	makeglossaries dokument # include glossary cross-refs in glossary
	-$(LATEX) dokument.tex # add glossary to TOC

clean:
	-$(RM) dokument.aux dokument.glg dokument.glo dokument.gls dokument.ist dokument.log dokument.out dokument.pdf dokument.toc
	-$(RM) Paketdiagramm.pdf

Paketdiagramm.pdf: Paketdiagramm.uxf
	java -jar Umlet/umlet.jar -action=convert -format=pdf -filename=Paketdiagramm.uxf -output=Paketdiagramm
