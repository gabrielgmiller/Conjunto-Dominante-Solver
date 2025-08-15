from typing import Set, List
from graph import Graph, cover_count, is_dominating

def exact_branch_and_bound(G: Graph, time_limit_sec:float=10.0) -> Set[int]:
    import time
    start = time.time()
    order = sorted(range(1, G.n+1), key=lambda u: -G.degree(u))  # ordena por grau desc.
    best = None

    # limite superior inicial: heurística gulosa
    from solvers.greedy import greedy_dominating_set
    best = greedy_dominating_set(G)

    dominated = set()
    choice: List[int] = []

    # pré-calcula fechos (u ∪ N(u)) pra podas rápidas
    closure = [set() for _ in range(G.n+1)]
    for u in range(1, G.n+1):
        closure[u] = set([u]) | G.neighbors(u)

    def backtrack(i:int, S:Set[int], dominated:Set[int]):
        nonlocal best
        if time.time() - start > time_limit_sec:
            return
        # poda: se já não pode melhorar
        if best is not None and len(S) >= len(best):
            return
        if len(dominated) == G.n:
            best = set(S)
            return
        if i == len(order):
            return

        u = order[i]

        # bound: estimativa inferior mínima de quantos vértices ainda faltam
        # (cobertura ingênua): quantos ainda não dominados / (grau_max+1)
        remaining = G.n - len(dominated)
        max_cov = max((len(closure[v]) for v in range(1, G.n+1)), default=1)
        lower_add = (remaining + max_cov - 1)//max_cov
        if best is not None and len(S) + lower_add >= len(best):
            return

        # 1) incluir u
        backtrack(i+1, S | {u}, dominated | closure[u])

        # 2) excluir u (tente cobrir via outros) – só faz sentido se algum vizinho for incluído
        backtrack(i+1, S, dominated)

    backtrack(0, set(), set())
    return best