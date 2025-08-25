/*
 * CSI546 - Projeto e Análise de Algoritmos (UFOP/DECSI)
 * Trabalho Prático: Conjunto Dominante
 *
 * Autores:
 *   - Nome: Gabriel Abreu Miller Godoi     Matrícula: 22.1.8101
 *   - Nome: Gustavo Moreira Sousa          Matrícula: 22.1.8007
 *
 * Arquivo: Greedy.java
 * Pacote:  solvers
 * Data:    2025-08-24
 *
 * Descrição:
 *   Implementa a heurística gulosa para Conjunto Dominante:
 *   em cada iteração escolhe o vértice que domina o maior número
 *   de vértices ainda não dominados.
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

package solvers;

import graph.Graph;
import java.util.*;

public class Greedy {
    /** Heuristic: repeatedly choose vertex with max number of newly dominated vertices. */
    public static Set<Integer> dominatingSet(Graph G) {
        Set<Integer> S = new HashSet<>();
        Set<Integer> dominated = new HashSet<>();
        int n = G.size();
        while (dominated.size() < n) {
            int best = -1;
            int bestGain = -1;
            for (int u = 1; u <= n; u++) {
                if (S.contains(u)) continue;
                int newDom = dominated.contains(u) ? 0 : 1;
                for (int v : G.neighbors(u)) if (!dominated.contains(v)) newDom++;
                if (newDom > bestGain) {
                    bestGain = newDom;
                    best = u;
                }
            }
            S.add(best);
            dominated.add(best);
            dominated.addAll(G.neighbors(best));
        }
        return S;
    }
}
