import argparse, time, sys, json
from graph import Graph, is_dominating
from generator import erdos_renyi, barabasi_albert, write_instance
from solvers.greedy import greedy_dominating_set
from solvers.local_search import local_improvement
from solvers.branch_and_bound import exact_branch_and_bound

def cmd_generate(args):
    if args.model == "er":
        edges = erdos_renyi(args.n, args.p, args.seed)
    else:
        edges = barabasi_albert(args.n, args.m, args.seed)
    write_instance(args.out, args.n, edges)
    print(f"OK: {args.out}  n={args.n} m={len(edges)}")

def run_solver(G, alg, time_limit):
    if alg=="greedy":
        S = greedy_dominating_set(G)
        S = local_improvement(G, S)
        return S, "greedy+ls"
    elif alg=="exact":
        S = exact_branch_and_bound(G, time_limit_sec=time_limit)
        return S, "exact-bnb"
    else:
        raise ValueError("alg deve ser greedy ou exact")

def cmd_solve(args):
    G = Graph.read(args.infile)
    t0=time.time()
    S, name = run_solver(G, args.alg, args.time_limit)
    dt=(time.time()-t0)*1000
    ok = is_dominating(G, S)
    print(f"ALG={name} | n={G.n} m={sum(len(G.adj[i]) for i in range(1,G.n+1))//2} | |S|={len(S)} | S={sorted(S)} | time_ms={dt:.2f} | dominated={'OK' if ok else 'FAIL'}")

def cmd_bench(args):
    import csv, statistics as st
    header=["n","m","alg","k","time_ms","ok","instance"]
    with open(args.out,"w",newline="") as f:
        w=csv.writer(f); w.writerow(header)
        for path in args.instances:
            G=Graph.read(path)
            for alg in args.algs.split(","):
                t0=time.time()
                S,name = run_solver(G,alg,args.time_limit)
                dt=(time.time()-t0)*1000
                ok=is_dominating(G,S)
                m=sum(len(G.adj[i]) for i in range(1,G.n+1))//2
                w.writerow([G.n,m,name,len(S),f"{dt:.2f}",ok,path])
    print(f"CSV salvo em {args.out}")

def main():
    p=argparse.ArgumentParser()
    sub=p.add_subparsers()

    g=sub.add_parser("generate", help="gerar instância")
    g.add_argument("--model", choices=["er","ba"], default="er")
    g.add_argument("-n", type=int, required=True)
    g.add_argument("-p", type=float, default=0.1, help="probabilidade ER")
    g.add_argument("-m", type=int, default=2, help="grau alvo BA")
    g.add_argument("--seed", type=int, default=None)
    g.add_argument("--out", required=True)
    g.set_defaults(func=cmd_generate)

    s=sub.add_parser("solve", help="resolver instância")
    s.add_argument("infile")
    s.add_argument("--alg", choices=["greedy","exact"], default="greedy")
    s.add_argument("--time-limit", type=float, default=10.0)
    s.set_defaults(func=cmd_solve)

    b=sub.add_parser("bench", help="rodar lote e salvar CSV")
    b.add_argument("--out", required=True)
    b.add_argument("--algs", default="greedy,exact")
    b.add_argument("--time-limit", type=float, default=10.0)
    b.add_argument("instances", nargs="+")
    b.set_defaults(func=cmd_bench)

    args=p.parse_args()
    if hasattr(args,"func"): args.func(args)
    else: p.print_help()

if __name__=="__main__":
    main()