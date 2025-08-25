/*
 * CSI546 - Projeto e Análise de Algoritmos (UFOP/DECSI)
 * Trabalho Prático: Conjunto Dominante
 *
 * Autores:
 *   - Nome: Gabriel Abreu Miller Godoi     Matrícula: 22.1.8101
 *   - Nome: Gustavo Moreira Sousa          Matrícula: 22.1.8007
 *
 * Arquivo: Main.java
 * Data:    2025-08-24
 *
 * Descrição:
 *   Ponto de entrada (CLI) do projeto. Orquestra os comandos:
 *   - gen: gera instâncias (ER/BA) e salva no formato de entrada.
 *   - solve: lê uma instância e resolve com greedy, greedy+ls ou exact (BnB).
 *   - bench: varre um diretório de instâncias e salva CSV com tempos/qualidade.
 *
 * Uso:
 *   gen   --model [ER|BA] --n N [--p P | --m M] -o ARQ
 *   solve -i ARQ -a [greedy|greedy+ls|exact]
 *   bench -d DIR -o CSV
 *
 * Observações:
 *   - Compila e executa em ambiente Unix/Linux via Makefile.
 *   - Entrada (instância):
 *       n m
 *       u1 v1
 *       ...
 *       um vm
 *   - Saída (linha única do solver):
 *       ALG=<alg> | n=<n> m=<m> | |S|=<k> | S={...} | time_ms=<T> | dominated=<true|false>
 *
 * Declaração de originalidade:
 *   Este código foi desenvolvido pela equipe identificada acima para a disciplina CSI546.
 *   Bibliotecas utilizadas são apenas as permitidas (estruturas elementares e I/O).
 */


import graph.Graph;
import generator.Generator;
import solvers.Greedy;
import solvers.LocalSearch;
import solvers.BranchAndBound;

import java.io.*;
import java.util.*;

public class Main {

    static class Result {
        Set<Integer> S;
        String name;
        Result(Set<Integer> S, String name) { this.S=S; this.name=name; }
    }

    private static Result runSolver(Graph G, String alg, double timeLimit) {
        if (alg.equals("greedy")) {
            Set<Integer> S = Greedy.dominatingSet(G);
            S = LocalSearch.localImprovement(G, S);
            return new Result(S, "greedy+ls");
        } else if (alg.equals("exact")) {
            Set<Integer> S = BranchAndBound.exactDominatingSet(G, timeLimit);
            return new Result(S, "exact-bnb");
        } else {
            throw new IllegalArgumentException("alg deve ser greedy ou exact");
        }
    }

    // ===== Subcommands =====
    private static void cmdGenerate(Map<String, String> args) throws Exception {
        String model = args.getOrDefault("--model", "er");
        int n = Integer.parseInt(args.get("-n"));
        String out = args.get("--out");
        Long seed = args.containsKey("--seed") ? Long.parseLong(args.get("--seed")) : null;
        List<int[]> edges;
        if (model.equals("er")) {
            double p = Double.parseDouble(args.getOrDefault("-p", "0.1"));
            edges = Generator.erdosRenyi(n, p, seed);
        } else {
            int m = Integer.parseInt(args.getOrDefault("-m", "2"));
            edges = Generator.barabasiAlbert(n, m, seed);
        }
        Generator.writeInstance(out, n, edges);
        System.out.printf("OK: %s  n=%d m=%d%n", out, n, edges.size());
    }

    private static void cmdSolve(Map<String, String> args, List<String> positionals) throws Exception {
        if (positionals.isEmpty()) throw new IllegalArgumentException("infile obrigatório");
        String infile = positionals.get(0);
        String alg = args.getOrDefault("--alg", "greedy");
        double timeLimit = Double.parseDouble(args.getOrDefault("--time-limit", "10.0"));
        Graph G = Graph.read(infile);
        long t0 = System.nanoTime();
        Result r = runSolver(G, alg, timeLimit);
        double dtMs = (System.nanoTime() - t0) / 1e6;
        boolean ok = Graph.isDominating(G, r.S);
        System.out.printf(
            "ALG=%s | n=%d m=%d | |S|=%d | S=%s | time_ms=%.2f | dominated=%s%n",
            r.name, G.size(), G.edgesCount(), r.S.size(), new ArrayList<>(r.S), dtMs, ok ? "OK" : "FAIL"
        );
    }

    private static void cmdBench(Map<String, String> args, List<String> positionals) throws Exception {
        String out = args.get("--out");
        if (out == null) throw new IllegalArgumentException("--out obrigatório");
        String algs = args.getOrDefault("--algs", "greedy,exact");
        double timeLimit = Double.parseDouble(args.getOrDefault("--time-limit", "10.0"));
        if (positionals.isEmpty()) throw new IllegalArgumentException("lista de instâncias obrigatória");

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out)))) {
            pw.println("n,m,alg,k,time_ms,ok,instance");
            for (String path : positionals) {
                Graph G = Graph.read(path);
                for (String alg : algs.split(",")) {
                    long t0 = System.nanoTime();
                    Result r = runSolver(G, alg.trim(), timeLimit);
                    double dtMs = (System.nanoTime() - t0) / 1e6;
                    boolean ok = Graph.isDominating(G, r.S);
                    pw.printf("%d,%d,%s,%d,%.2f,%s,%s%n",
                            G.size(), G.edgesCount(), r.name, r.S.size(), dtMs, ok, path);
                }
            }
        }
        System.out.printf("CSV salvo em %s%n", out);
    }

    // ===== Simple CLI parser =====
    private static void printHelp() {
        System.out.println("Uso:");
        System.out.println("  java Main generate -n <int> [--model er|ba] [-p <float>] [-m <int>] [--seed <long>] --out <path>");
        System.out.println("  java Main solve <infile> [--alg greedy|exact] [--time-limit <sec>]");
        System.out.println("  java Main bench --out <csv> [--algs list] [--time-limit <sec>] <instances...>");
    }

    private static void dispatch(String[] argv) throws Exception {
        if (argv.length == 0) { printHelp(); return; }
        String cmd = argv[0];
        Map<String, String> opts = new LinkedHashMap<>();
        List<String> positionals = new ArrayList<>();
        // Parse flags from argv[1:]
        for (int i = 1; i < argv.length; i++) {
            String a = argv[i];
            if (a.startsWith("--") || a.startsWith("-")) {
                String key = a;
                String val = null;
                if (i + 1 < argv.length && !argv[i + 1].startsWith("-")) {
                    val = argv[++i];
                } else {
                    // boolean flags not used here; keep null
                }
                opts.put(key, val);
            } else {
                positionals.add(a);
            }
        }

        switch (cmd) {
            case "generate": cmdGenerate(opts); break;
            case "solve":    cmdSolve(opts, positionals); break;
            case "bench":    cmdBench(opts, positionals); break;
            default:
                printHelp();
        }
    }

    public static void main(String[] args) throws Exception {
        dispatch(args);
    }
}
