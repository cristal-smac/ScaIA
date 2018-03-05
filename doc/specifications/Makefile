# Maxime MORGE copyright 2018
MAIN = specification
SRC1 = coalitionBehaviourSelectiveExact
SRC2 = coalitionBehaviourSelectiveApproximation
SRC3 = coalitionBehaviourInclusive

all: se

se:	$(MAIN).tex $(SRC1).tex $(SRC2).tex $(SRC3).tex
	pdflatex -shell-escape $(MAIN).tex

# --------------------------------
clean:
	rm -rf *.log *.out *~ *.bak *.aux *.dvi *.bbl *.blg *.idx
	rm -rf *.cb  *.lof *.los *.lot *.toc *.tpt *.tpm *.cache
	rm -rf _minted-*


# --------------------------------
cleanall:
	${MAKE} clean
	rm  *.ps *.pdf
