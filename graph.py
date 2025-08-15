from typing import List, Set, Tuple
import sys

class Graph:
    def __init__(self, n: int, edges: List[Tuple[int,int]]):
        self.n = n
        self.adj = [set() for _ in range(n+1)]
        for u,v in edges:
            if u==v: continue
            self.adj[u].add(v); self.adj[v].add(u)

    @staticmethod
    def read(path: str):
        with open(path) as f:
            n,m = map(int, f.readline().split())
            edges = [tuple(map(int, line.split())) for line in f if line.strip()]
        return Graph(n, edges)

    def neighbors(self, u:int)->Set[int]:
        return self.adj[u]

    def degree(self, u:int)->int:
        return len(self.adj[u])

def is_dominating(G: Graph, S: Set[int]) -> bool:
    dominated = set(S)
    for u in S:
        dominated.update(G.neighbors(u))
    return len(dominated) == G.n

def cover_count(G: Graph, S: Set[int]) -> int:
    dominated = set(S)
    for u in S:
        dominated.update(G.neighbors(u))
    return len(dominated)