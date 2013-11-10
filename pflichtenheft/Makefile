LATEX = pdflatex -interaction nonstopmode

dokument: clean
	-$(LATEX) dokument.tex # initial
	makeglossaries dokument
	-$(LATEX) dokument.tex # make glossary
	-$(LATEX) dokument.tex # add glossary to TOC

clean:
	rm -f dokument.aux dokument.glg dokument.glo dokument.gls dokument.ist dokument.log dokument.out dokument.pdf dokument.toc
