LATEX = pdflatex -interaction nonstopmode -file-line-error
RM = rm -f
BRANCH = $(shell git rev-parse --abbrev-ref HEAD)
UMLET = java -jar ../Umlet/umlet.jar

all: entwurf.pdf clean-entwurf-build Klassendiagramm.pdf

.PHONY: all clean clean-entwurf clean-entwurf-build clean-Klassendiagramm

clean: clean-entwurf clean-Klassendiagramm

entwurf.pdf: entwurf.tex $(ARTEFAKTE) $(PNGS)
	$(MAKE) clean-$(@:.pdf=) # if the pdftex build fails fatally, we don’t want the old pdf laying around and confusing the user
	-$(LATEX) $< 2>&1 > /dev/null # initial build
	-makeglossaries $(@:.pdf=) # initial glossary
	-$(LATEX) $< 2>&1 > /dev/null # include glossary in document
	-makeglossaries $(@:.pdf=) # include glossary cross-refs in glossary
	-$(LATEX) $< # add glossary to TOC

clean-entwurf: clean-entwurf-build
	-$(RM) entwurf.pdf

clean-entwurf-build:
	-$(RM) entwurf.aux entwurf.glg entwurf.glo entwurf.gls entwurf.ist entwurf.log entwurf.out entwurf.toc

Klassendiagramm.pdf: Klassendiagramm.uxf
	sed '4,14d' $^ > $^.tmp # strip lines 4-14 – the “Origin” element
	$(UMLET) -action=convert -format=pdf -filename=$^.tmp -output=$(@:.pdf=)
	-$(RM) $^.tmp

clean-Klassendiagramm:
	-$(RM) Klassendiagramm.pdf
