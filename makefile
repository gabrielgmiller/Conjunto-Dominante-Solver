# Makefile para projeto "Conjunto Dominante" em Python

PY=python3
PKG=dominset

.PHONY: help venv install lint test gen solve bench plot clean zip

help:
	@echo "Alvos: venv | install | gen | solve | bench | plot | zip"

venv:
	$(PY) -m venv .venv

install:
	. .venv/bin/activate && $(PY) -m pip install --upgrade pip

# Gerar uma instância ER de exemplo
gen:
	. .venv/bin/activate && $(PY) -m $(PKG).main generate --model er -n 200 -p 0.06 --seed 42 --out data/er_200_006.txt

# Resolver com heurística (rápido)
solvea:
	. .venv/bin/activate && $(PY) -m $(PKG).main solve data/er_200_006.txt --alg greedy

# Benchmark e salva CSV
bench:
	. .venv/bin/activate && $(PY) -m $(PKG).main bench --out results.csv data/*.txt

# (Opcional) gerar gráfico a partir do CSV
plot:
	. .venv/bin/activate && $(PY) scripts/make_plot.py results.csv plot_time_vs_n.png

clean:
	rm -f results.csv plot_time_vs_n.png
	find . -name "__pycache__" -type d -exec rm -rf {} +

# Compactar para envio (ajuste NOME e MATRÍCULA)
zip:
	zip -r TP-ConjuntoDominante_SeuNome_Matricula.zip $(PKG) scripts Makefile data README.md RELATORIO.pdf