LATEX = pdflatex -interaction nonstopmode -file-line-error
RM = rm -f
BRANCH = $(shell git rev-parse --abbrev-ref HEAD)
UMLET = java -jar ../Umlet/umlet.jar

all: entwurf.pdf clean-entwurf-build

.PHONY: all clean clean-entwurf clean-entwurf-build

clean: clean-entwurf

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
