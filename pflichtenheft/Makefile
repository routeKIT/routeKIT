LATEX = pdflatex -interaction nonstopmode
RM = rm -f
SVG2PDF = cairosvg -f pdf -o

all: dokument.pdf clean-dokument-build

clean: clean-dokument clean-Paketdiagramm clean-Profilverwaltung

dokument.pdf: Paketdiagramm.pdf Profilverwaltung.pdf dokument.tex
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
	java -jar Umlet/umlet.jar -action=convert -format=pdf -filename=Paketdiagramm.uxf -output=Paketdiagramm

clean-Paketdiagramm:
	-$(RM) Paketdiagramm.pdf

Profilverwaltung.pdf: Profilverwaltung.svg ui.css
	$(SVG2PDF) Profilverwaltung.pdf Profilverwaltung.svg

clean-Profilverwaltung:
	-$(RM) Profilverwaltung.pdf

artefakte: Profilverwaltung.pdf
	git checkout -B artefakte
	git add -f Profilverwaltung.pdf
	git commit -m "Artefakte"
	git push --set-upstream --force origin artefakte
	git checkout master
	git branch -d artefakte
