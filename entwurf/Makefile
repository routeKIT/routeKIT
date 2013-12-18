LATEX = pdflatex -interaction nonstopmode -file-line-error
RM = rm -f
BRANCH = $(shell git rev-parse --abbrev-ref HEAD)
UMLET = java -jar ../Umlet/umlet.jar
ARTEFAKTE = StartSequenz.pdf RenderSequenz.pdf ProfilSequenz.pdf RouteCalculationSequenz.pdf PreCalculationSequenz.pdf

all: entwurf.pdf clean-entwurf-build Klassendiagramm.pdf

.PHONY: all clean clean-entwurf clean-entwurf-build clean-Klassendiagramm clean-StartSequenz clean-RenderSequenz clean-ProfilSequenz clean-RouteCalculationSequenz clean-PreCalculationSequenz

clean: clean-entwurf clean-Klassendiagramm clean-StartSequenz clean-RenderSequenz clean-ProfilSequenz clean-RouteCalculationSequenz clean-PreCalculationSequenz

entwurf.pdf: entwurf.tex $(ARTEFAKTE)
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

StartSequenz.pdf: StartSequenz.uxf
	$(UMLET) -action=convert -format=pdf -filename=$^ -output=$(@:.pdf=)

clean-StartSequenz:
	-$(RM) StartSequenz.pdf

RenderSequenz.pdf: RenderSequenz2.uxf
	$(UMLET) -action=convert -format=pdf -filename=$^ -output=$(@:.pdf=)

clean-RenderSequenz:
	-$(RM) RenderSequenz.pdf

ProfilSequenz.pdf: ProfilSequenz2.uxf
	$(UMLET) -action=convert -format=pdf -filename=$^ -output=$(@:.pdf=)

clean-ProfilSequenz:
	-$(RM) ProfilSequenz.pdf

RouteCalculationSequenz.pdf: RouteCalculationSequenz.uxf
	sed 's.<w>1690</w>.<w>1890</w>.' $^ > $^.tmp # work around UMLet bug that cuts off part of the diagram
	$(UMLET) -action=convert -format=pdf -filename=$^.tmp -output=$(@:.pdf=)
	-$(RM) $^.tmp

clean-RouteCalculationSequenz:
	-$(RM) RouteCalculationSequenz.pdf

PreCalculationSequenz.pdf: PreCalculationSequenz2.uxf
	$(UMLET) -action=convert -format=pdf -filename=$^ -output=$(@:.pdf=)

clean-PreCalculationSequenz:
	-$(RM) PreCalculationSequenz.pdf

artefakte: $(ARTEFAKTE)
	git checkout -B artefakte-$(BRANCH)
	git add -f $(ARTEFAKTE)
	git commit -m "Artefakte" --author="$(MAKE) $(MAKECMDGOALS) <make@example.com>"
	git push --set-upstream --force origin artefakte-$(BRANCH)
	git checkout $(BRANCH)
	git branch -d artefakte-$(BRANCH)

get-artefakte:
	git checkout origin/artefakte-$(BRANCH) $(ARTEFAKTE) # get the artifacts...
	-git reset -q -- $(ARTEFAKTE) # ...and remove them from the index
