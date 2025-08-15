import random
from typing import List, Tuple

def erdos_renyi(n:int, p:float, seed:int=None)->List[Tuple[int,int]]:
    rnd = random.Random(seed)
    edges=[]
    for u in range(1,n+1):
        for v in range(u+1,n+1):
            if rnd.random() < p:
                edges.append((u,v))
    return edges

def barabasi_albert(n:int, m:int, seed:int=None)->List[Tuple[int,int]]:
    rnd = random.Random(seed)
    edges=[]
    targets=list(range(1,m+1))
    repeated=targets.copy()
    for v in range(m+1, n+1):
        chosen=set()
        while len(chosen)<m:
            x = rnd.choice(repeated)
            chosen.add(x)
        for u in chosen:
            edges.append((u,v))
        repeated += list(chosen) + [v]*m
    return edges

def write_instance(path:str, n:int, edges:List[Tuple[int,int]]):
    with open(path,'w') as f:
        f.write(f"{n} {len(edges)}\n")
        for u,v in edges:
            f.write(f"{u} {v}\n")