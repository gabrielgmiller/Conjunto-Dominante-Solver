# CSI546 – Projeto e Análise de Algoritmos  
## Trabalho Prático – **Conjunto Dominante**

**Autores:**  
- Gabriel Miller – Matrícula: XXXXXXXX  
- Gustavo Moreira – Matrícula: XXXXXXXX  

---

## 📌 Descrição do Problema

O **Problema do Conjunto Dominante** consiste em:  

> Dado um grafo não direcionado G = (V, E), determinar o menor subconjunto V' ⊆ V tal que todo vértice v ∈ V − V' tenha pelo menos um vizinho em V'.

Este é um problema **NP-completo**, o que significa que, para instâncias grandes, métodos exatos podem ser inviáveis em tempo razoável. Assim, este trabalho implementa tanto **uma abordagem exata** quanto **uma heurística eficiente**.

---

## 🎯 Objetivos

- Implementar uma solução exata usando **Branch-and-Bound**.  
- Implementar uma solução aproximada com **heurística gulosa** combinada com **busca local**.  
- Criar um **gerador de instâncias** que permita avaliar o desempenho dos algoritmos.  
- Analisar resultados, identificando instâncias intratáveis.  
- Atender às especificações e formato de entrega conforme edital da disciplina.

---

## 📂 Estrutura do Projeto

dominset/  
&nbsp;&nbsp;main.py – CLI: gerar instâncias, resolver, benchmark  
&nbsp;&nbsp;graph.py – Classe Grafo e utilidades  
&nbsp;&nbsp;solvers/  
&nbsp;&nbsp;&nbsp;&nbsp;greedy.py – Heurística gulosa  
&nbsp;&nbsp;&nbsp;&nbsp;local_search.py – Melhoria local  
&nbsp;&nbsp;&nbsp;&nbsp;branch_and_bound.py – Algoritmo exato  
&nbsp;&nbsp;generator.py – Gerador de instâncias ER e BA  

scripts/  
&nbsp;&nbsp;make_plot.py – (Opcional) gera gráficos a partir do CSV  

data/ – Instâncias de exemplo  
Makefile – Automação de execução e empacotamento  
RELATORIO.pdf – Documento em formato ACM (1 coluna)  

---

## ⚙️ Requisitos

- **Python** 3.8+  
- Bibliotecas padrão (nenhuma dependência externa obrigatória)  
- Sistema **Unix/Linux** ou WSL para execução e empacotamento (conforme edital)

---

## ▶️ Como Executar

1. Criar ambiente virtual e instalar dependências:  
```bash
make venv install
```  
2. Gerar instância de teste:  
```bash
make gen
```  
3. Resolver uma instância:  
```bash
make solve
```  
4. Rodar benchmarks:  
```bash
make bench
```  
5. (Opcional) Gerar gráfico a partir do CSV:  
```bash
make plot
```  

---

## 📥 Formato de Entrada/Saída

**Entrada (.txt):**  
```
n m
u1 v1
u2 v2
...
um vm
```
- `n` = número de vértices  
- `m` = número de arestas  
- `u v` = aresta entre u e v (vértices numerados de 1 a n)  

**Saída (stdout):**  
```
ALG=greedy+ls | n=... m=... | |S|=k | S={...} | time_ms=... | dominated=OK
```

---

## 🧠 Paradigma Utilizado

- **Heurística Gulosa** para solução inicial, escolhendo sempre o vértice que domina mais vértices não cobertos.  
- **Busca Local** para remover redundâncias e substituir vértices por outros que mantenham a dominância com menor tamanho.  
- **Branch-and-Bound** para busca exata com:  
  - Ordenação por grau decrescente  
  - Limite superior inicial obtido pela heurística  
  - Podas baseadas no fecho $begin:math:text$ N[v] $end:math:text$ e estimativas inferiores  

---

## 📊 Geração de Instâncias

- **Erdős–Rényi (ER):** parâmetro `p` = probabilidade de cada aresta existir  
- **Barabási–Albert (BA):** parâmetro `m` = número de arestas adicionadas por novo vértice  

O gerador salva arquivos no mesmo formato esperado pelo resolvedor.

---

## 📈 Resultados e Avaliação

- CSV com: `n, m, algoritmo, |S|, tempo_ms, ok, instância`  
- Gráfico **tempo × tamanho da instância**  
- Testes executados em máquina:  
  - **CPU:** Intel Core i7-1165G7 @ 2.80GHz (8 threads)  
  - **RAM:** 16 GB DDR4  
  - **SO:** Ubuntu 22.04.4 LTS 64-bit (via WSL2 no Windows 11 Pro)  

---

## 📦 Empacotamento para Entrega

Conforme edital da disciplina:  

1. Garantir que todos os arquivos tenham cabeçalho com **nome e matrícula**.  
2. Incluir:  
   - Código-fonte  
   - Makefile  
   - Gerador de instâncias  
   - RELATORIO.pdf (ACM 1 coluna, ≤ 800 palavras)  
3. Compactar:  
```bash
make zip
```  
4. Enviar via Moodle até **25/08/2025**.  
5. Realizar entrevista com o professor até **29/08/2025**.

---

## 📜 Licença

Uso acadêmico para a disciplina CSI546 – Projeto e Análise de Algoritmos (UFOP).
