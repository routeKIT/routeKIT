LATEX = pdflatex -interaction nonstopmode -file-line-error
RM = rm -f
BRANCH = $(shell git rev-parse --abbrev-ref HEAD)
UMLET = java -jar ../Umlet/umlet.jar

all: entwurf.pdf clean-entwurf-build Klassendiagramm.pdf

.PHONY: all clean clean-entwurf clean-entwurf-build clean-Klassendiagramm

clean: clean-entwurf clean-Klassendiagramm

entwurf.pdf: $(ARTEFAKTE) $(PNGS) entwurf.tex
	$(MAKE) clean-entwurf # if the pdftex build fails fatally, we don’t want the old pdf laying around and confusing the user
	-$(LATEX) entwurf.tex # initial build
	makeglossaries entwurf # initial glossary
	-$(LATEX) entwurf.tex # include glossary in document
	makeglossaries entwurf # include glossary cross-refs in glossary
	-$(LATEX) entwurf.tex # add glossary to TOC

clean-entwurf: clean-entwurf-build
	-$(RM) entwurf.pdf

clean-entwurf-build:
	-$(RM) entwurf.aux entwurf.glg entwurf.glo entwurf.gls entwurf.ist entwurf.log entwurf.out entwurf.toc

Klassendiagramm.pdf: Klassendiagramm.uxf
	sed '4,14d' Klassendiagramm.uxf > Klassendiagramm.uxf.tmp # strip lines 4-14 – the “Origin” element
	$(UMLET) -action=convert -format=pdf -filename=Klassendiagramm.uxf.tmp -output=Klassendiagramm
	-$(RM) Klassendiagramm.uxf.tmp

clean-Klassendiagramm:
	-$(RM) Klassendiagramm.pdf
