

# Conjunto Dominante – Solver em Java

Implementação em **Java** para o problema de **Conjunto Dominante** (Minimum Dominating Set).  
Projeto da disciplina **CSI546 – Projeto e Análise de Algoritmos (UFOP/DECSI)**.

---

## 📂 Estrutura

- `src/` – código fonte em Java (Main, Gerador, Solvers).
- `data/` – instâncias de teste (ER/BA).
- `results/` – saídas dos benchmarks (CSV e gráficos).
- `Makefile` – automação de compilação, execução e experimentos.
- `plot.py` – script em Python para gerar gráficos a partir dos CSVs.

---

## ▶️ Como compilar e rodar

### Pré-requisitos
- Java 11+ (`javac`, `java`)
- Python 3 com `pandas` e `matplotlib` (para o gráfico):
```bash
python3 -m pip install --user pandas matplotlib
```

### Fluxo normal (instâncias médias)
```bash
make clean
make
make gen-batch      # gera instâncias ER/BA de n=50..300
make bench-all      # roda greedy e exact em todas as instâncias
python3 plot.py     # gera gráfico results/bench_plot.png
```

### Fluxo big (instâncias grandes, força o exato a quebrar)
```bash
make clean
make
make gen-big        # gera instâncias grandes (n=300..500)
make bench-all TLIM=10 ALGS=greedy,exact
python3 plot.py
```

---

## 📊 Resultados

- Os benchmarks são salvos em `results/bench_YYYYMMDD-HHMMSS.csv`.
- O gráfico final é salvo em `results/bench_plot.png`.
- Heurísticas (`greedy`, `greedy+ls`) rodam em tempo viável mesmo em instâncias grandes.
- O exato (Branch-and-Bound) escala mal, mostrando o crescimento exponencial esperado.

---

## ✍️ Autores

- Gabriel Abreu Miller Godoi – Matrícula: 22.1.8101  
- Gustavo Moreira Sousa – Matrícula: 22.1.8007