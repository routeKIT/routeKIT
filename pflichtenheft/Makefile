LATEX = pdflatex -interaction nonstopmode

dokument.pdf: Systemmodell1.pdf dokument.tex
	-$(LATEX) dokument.tex # initial
	makeglossaries dokument
	-$(LATEX) dokument.tex # make glossary
	-$(LATEX) dokument.tex # add glossary to TOC

clean:
	rm -f dokument.aux dokument.glg dokument.glo dokument.gls dokument.ist dokument.log dokument.out dokument.pdf dokument.toc
	rm -f Systemmodell1.pdf

Systemmodell1.pdf: Systemmodell1.uxf
	java -jar Umlet/umlet.jar -action=convert -format=pdf -filename=Systemmodell1.uxf -output=Systemmodell1
