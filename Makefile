# Uso do Makefile para experimentos:
# ---------------------------------
# Fluxo normal (instâncias pequenas/médias):
#   make clean
#   make
#   make gen-batch      # gera instâncias ER/BA (50–300 vértices)
#   make bench-all      # roda benchmarks em todas as instâncias
#   python3 plot.py     # gera gráfico em results/bench_plot.png
#
# Fluxo big (instâncias maiores para forçar o exato a quebrar):
#   make clean
#   make
#   make gen-big        # gera instâncias grandes (300–500 vértices)
#   make bench-all TLIM=10 ALGS=greedy,exact
#   python3 plot.py     # gera gráfico em results/bench_plot.png
#
# Observação: o bench-all gera arquivos CSV timestampados em results/,
# e o plot.py sempre usa o mais recente automaticamente.


# ====== Config ======
JFLAGS  = -Xlint:all -encoding UTF-8
SRC_DIR = src
BIN_DIR = out
MAIN    = Main

SOURCES := $(shell find $(SRC_DIR) -name "*.java")

# ====== Defaults for experiments ======
SEED   ?= 42
ER_NS  ?= 50 100 150 200 300
ER_P   ?= 0.05
BA_NS  ?= 50 100 150 200 300
BA_M   ?= 2
TLIM   ?= 5
ALGS   ?= greedy,exact
DATE   := $(shell date +%Y%m%d-%H%M%S)

# ====== Alvos ======
.PHONY: all run help gen gen-batch gen-big solve bench bench-all bench-greedy bench-exact plot clean deepclean zip

all: $(BIN_DIR)/.compiled

$(BIN_DIR)/.compiled: $(SOURCES)
	@mkdir -p $(BIN_DIR)
	javac $(JFLAGS) -d $(BIN_DIR) $(SOURCES)
	@touch $(BIN_DIR)/.compiled
	@echo "✓ Compilado em $(BIN_DIR)"

run: all
	java -cp $(BIN_DIR) $(MAIN) --help

help: run

# Exemplo de geração (ajuste parâmetros à vontade)
gen: all
	@mkdir -p data
	java -cp $(BIN_DIR) $(MAIN) generate -n 50 --model er -p 0.1 --seed 42 --out data/er_50_01.txt

# Geração em lote (ER e BA) com listas de tamanhos
gen-batch: all
	@mkdir -p data
	@echo ">> Gerando ER: n in [$(ER_NS)], p=$(ER_P), seed=$(SEED)"
	@for n in $(ER_NS); do \
	  echo "  - ER $$n"; \
	  java -cp $(BIN_DIR) $(MAIN) generate -n $$n --model er -p $(ER_P) --seed $(SEED) --out data/er_$${n}_$$(printf '%.3f' $(ER_P) | tr -d .).txt; \
	done
	@echo ">> Gerando BA: n in [$(BA_NS)], m=$(BA_M), seed=$(SEED)"
	@for n in $(BA_NS); do \
	  echo "  - BA $$n"; \
	  java -cp $(BIN_DIR) $(MAIN) generate -n $$n --model ba -m $(BA_M) --seed $(SEED) --out data/ba_$${n}_m$(BA_M).txt; \
	done
	@echo "✓ gen-batch concluído"

# Geração rápida de instâncias grandes padrão (ex.: força o exato a ficar caro)
gen-big: all
	@$(MAKE) gen-batch ER_NS="300 400 500" ER_P=0.04 BA_NS="300 400 500" BA_M=2

# Exemplo de solução (ajuste o arquivo e o algoritmo)
solve: all
	java -cp $(BIN_DIR) $(MAIN) solve data/er_50_01.txt --alg greedy

# Benchmark sobre todos .txt em data/ -> results/bench.csv
bench: all
	@mkdir -p results
	java -cp $(BIN_DIR) $(MAIN) bench --out results/bench.csv data/*.txt
	@echo "✓ Bench salvo em results/bench.csv"

# Bench completo com parâmetros (usa ALGS e TLIM; cria arquivo timestampado)
bench-all: all
	@mkdir -p results
	java -cp $(BIN_DIR) $(MAIN) bench --out results/bench_$(DATE).csv --algs $(ALGS) --time-limit $(TLIM) data/*.txt
	@echo "✓ Bench salvo em results/bench_$(DATE).csv (algs=$(ALGS), tlim=$(TLIM)s)"

bench-greedy: all
	@mkdir -p results
	java -cp $(BIN_DIR) $(MAIN) bench --out results/bench_greedy_$(DATE).csv --algs greedy data/*.txt
	@echo "✓ Bench (greedy) salvo em results/bench_greedy_$(DATE).csv"

bench-exact: all
	@mkdir -p results
	java -cp $(BIN_DIR) $(MAIN) bench --out results/bench_exact_$(DATE).csv --algs exact --time-limit $(TLIM) data/*.txt
	@echo "✓ Bench (exact, tlim=$(TLIM)s) salvo em results/bench_exact_$(DATE).csv"

clean:
	@rm -rf $(BIN_DIR)
	@echo "✓ Limpo (binários removidos)"

deepclean: clean
	@rm -rf results/*.csv
	@echo "✓ Limpo (resultados removidos)"

plot:
	@echo ">> Gerando gráfico a partir de results/bench.csv"
	@python3 plot.py
	
