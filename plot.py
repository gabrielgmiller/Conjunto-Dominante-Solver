import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

files = sorted(Path("results").glob("bench_*.csv"), key=lambda f: f.stat().st_mtime, reverse=True)
df = pd.read_csv(files[0]) if files else pd.read_csv("results/bench.csv")

# tenta inferir o modelo a partir da coluna disponível
if "file" in df.columns:
    df["model"] = df["file"].str.extract(r'^(er|ba)_', expand=False).fillna("mix")
elif "instance" in df.columns:
    df["model"] = df["instance"].str.extract(r'^(er|ba)_', expand=False).fillna("mix")
else:
    df["model"] = "mix"

# média por (alg, n, model)
g = df.groupby(["alg","n","model"], as_index=False)["time_ms"].mean()

plt.figure(figsize=(9,5))
for (alg, model), sub in g.groupby(["alg","model"]):
    sub = sub.sort_values("n")
    label = f"{alg} ({model})"
    plt.plot(sub["n"], sub["time_ms"], marker="o", label=label)

plt.xlabel("n (número de vértices)")
plt.ylabel("Tempo (ms)")
plt.yscale("log")   # exponencial fica mais visível
plt.title("Tempo de execução por algoritmo")
plt.grid(True, linestyle="--", alpha=0.6)
plt.legend()
plt.tight_layout()
plt.savefig("results/bench_plot.png")
print("✓ Gráfico salvo em results/bench_plot.png")