from typing import Set
from graph import Graph, cover_count

def greedy_dominating_set(G: Graph) -> Set[int]:
    # Estratégia: escolher sempre o vértice que maximiza novos dominados
    S: Set[int] = set()
    dominated = set()
    while len(dominated) < G.n:
        best, best_gain = None, -1
        for u in range(1, G.n+1):
            if u in S: continue
            new = 0
            if u not in dominated: new += 1
            for v in G.neighbors(u):
                if v not in dominated: new += 1
            if new > best_gain:
                best, best_gain = u, new
        S.add(best)
        # atualiza dominados
        dominated.add(best)
        dominated.update(G.neighbors(best))
    return S