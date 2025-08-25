

package solvers;

/*
 * CSI546 - Projeto e Análise de Algoritmos (UFOP/DECSI)
 * Trabalho Prático: Conjunto Dominante
 *
 * Autores:
 *   - Nome: Gabriel Abreu Miller Godoi     Matrícula: 22.1.8101
 *   - Nome: Gustavo Moreira Sousa          Matrícula: 22.1.8007
 *
 * Arquivo: BranchAndBound.java
 * Pacote:  solvers
 * Data:    2025-08-24
 *
 * Descrição:
 *   Implementa o algoritmo exato por Branch-and-Bound para Conjunto Dominante.
 *   Usa ordenação decrescente por grau, solução gulosa como limite superior,
 *   e podas por limite inferior baseado na cobertura máxima possível.
 *
 * Observações:
 *   - Compila e executa em ambiente Unix/Linux via Makefile.
 *   - Entrada/saída seguem o formato:
 *       n m
 *       u1 v1
 *       ...
 *       um vm
 *
 * Declaração de originalidade:
 *   Este código foi desenvolvido pela equipe identificada acima para a disciplina CSI546.
 *   Bibliotecas utilizadas são apenas as permitidas (estruturas elementares e I/O).
 */

import graph.Graph;
import java.util.*;

public class BranchAndBound {

    public static Set<Integer> exactDominatingSet(Graph G, double timeLimitSec) {
        long start = System.nanoTime();
        int n = G.size();

        // Order vertices by non-increasing degree
        List<Integer> order = new ArrayList<>();
        for (int u = 1; u <= n; u++) order.add(u);
        order.sort((a, b) -> Integer.compare(G.degree(b), G.degree(a)));

        // Initial upper bound from greedy
        Set<Integer> best = Greedy.dominatingSet(G);

        // Precompute closures (u ∪ N(u))
        List<Set<Integer>> closure = new ArrayList<>(n + 1);
        closure.add(new HashSet<>()); // index 0 unused
        for (int u = 1; u <= n; u++) {
            Set<Integer> c = new HashSet<>();
            c.add(u);
            c.addAll(G.neighbors(u));
            closure.add(c);
        }

        class Node {
            int i;
            Set<Integer> S;
            Set<Integer> dominated;
            Node(int i, Set<Integer> S, Set<Integer> dominated) { this.i=i; this.S=S; this.dominated=dominated; }
        }

        // DFS stack to avoid recursion depth issues
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(new Node(0, new HashSet<>(), new HashSet<>()));

        while (!stack.isEmpty()) {
            // Time check
            double elapsed = (System.nanoTime() - start) / 1e9;
            if (elapsed > timeLimitSec) break;

            Node node = stack.pop();
            int i = node.i;
            Set<Integer> S = node.S;
            Set<Integer> dom = node.dominated;

            // prune: cannot improve current best
            if (best != null && S.size() >= best.size()) continue;

            if (dom.size() == n) {
                best = new HashSet<>(S);
                continue;
            }
            if (i == order.size()) continue;

            // lower bound: naive coverage bound using max closure size
            int remaining = n - dom.size();
            int maxCov = 1;
            for (int v = 1; v <= n; v++) {
                maxCov = Math.max(maxCov, closure.get(v).size());
            }
            int lowerAdd = (remaining + maxCov - 1) / maxCov;
            if (best != null && S.size() + lowerAdd >= best.size()) continue;

            int u = order.get(i);

            // Branch 2: exclude u (try to cover with others)
            // Push second so "include" branch is explored first (DFS)
            stack.push(new Node(i + 1, new HashSet<>(S), new HashSet<>(dom)));

            // Branch 1: include u
            Set<Integer> S1 = new HashSet<>(S);
            S1.add(u);
            Set<Integer> dom1 = new HashSet<>(dom);
            dom1.addAll(closure.get(u));
            stack.push(new Node(i + 1, S1, dom1));
        }

        return best;
    }
}
