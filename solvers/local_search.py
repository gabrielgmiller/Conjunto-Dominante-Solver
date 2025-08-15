from typing import Set
from graph import Graph, is_dominating

def local_improvement(G: Graph, S: Set[int], max_iter:int=200) -> Set[int]:
    S = set(S)
    # 1) Remoção redundante (se continuar dominando, remova)
    changed = True
    while changed:
        changed = False
        for u in list(S):
            T = S - {u}
            if is_dominating(G, T):
                S = T
                changed = True
                break
    # 2) Trocas 1-1: tenta reduzir |S|
    it = 0
    while it < max_iter:
        it += 1
        improved = False
        for u in list(S):
            for v in range(1, G.n+1):
                if v in S: continue
                T = (S - {u}) | {v}
                if len(T) <= len(S) and is_dominating(G, T):
                    S = T
                    improved = True
                    break
            if improved:
                break
        if not improved:
            break
    return S