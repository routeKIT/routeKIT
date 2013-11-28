LATEX = pdflatex -interaction nonstopmode -file-line-error
RM = rm -f
SVG2PDF = rsvg-convert -f pdf -o
ARTEFAKTE = Paketdiagramm.pdf Profilverwaltung.pdf Kartenverwaltung.pdf Verlauf.pdf
PNGS = mockup_screenshot_about.png mockup_screenshot_main.png mockup_screenshot_menu.png mockup_screenshot_nicht_berechnet.png
BRANCH = $(shell git rev-parse --abbrev-ref HEAD)
UMLET = java -jar ../Umlet/umlet.jar

all: dokument.pdf clean-dokument-build

.PHONY: all clean clean-dokument clean-dokument-build clean-Paketdiagramm clean-Profilverwaltung clean-Kartenverwaltung clean-Verlauf artefakte

clean: clean-dokument clean-Paketdiagramm clean-Profilverwaltung clean-Kartenverwaltung clean-Verlauf

dokument.pdf: $(ARTEFAKTE) $(PNGS) dokument.tex
	$(MAKE) clean-dokument # if the pdftex build fails fatally, we don’t want the old pdf laying around and confusing the user
	-$(LATEX) dokument.tex # initial build
	makeglossaries dokument # initial glossary
	-$(LATEX) dokument.tex # include glossary in document
	makeglossaries dokument # include glossary cross-refs in glossary
	-$(LATEX) dokument.tex # add glossary to TOC

clean-dokument: clean-dokument-build
	-$(RM) dokument.pdf

clean-dokument-build:
	-$(RM) dokument.aux dokument.glg dokument.glo dokument.gls dokument.ist dokument.log dokument.out dokument.toc

Paketdiagramm.pdf: Paketdiagramm.uxf
	$(UMLET) -action=convert -format=pdf -filename=Paketdiagramm.uxf -output=Paketdiagramm

clean-Paketdiagramm:
	-$(RM) Paketdiagramm.pdf

Profilverwaltung.pdf: Profilverwaltung.svg ui.css
	$(SVG2PDF) Profilverwaltung.pdf Profilverwaltung.svg

clean-Profilverwaltung:
	-$(RM) Profilverwaltung.pdf

Kartenverwaltung.pdf: Kartenverwaltung.svg ui.css
	$(SVG2PDF) Kartenverwaltung.pdf Kartenverwaltung.svg

clean-Kartenverwaltung:
	-$(RM) Kartenverwaltung.pdf

Verlauf.pdf: Verlauf.svg ui.css
	$(SVG2PDF) Verlauf.pdf Verlauf.svg

clean-Verlauf:
	-$(RM) Verlauf.pdf

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
