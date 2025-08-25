package solvers;

/*
 * CSI546 - Projeto e Análise de Algoritmos (UFOP/DECSI)
 * Trabalho Prático: Conjunto Dominante
 *
 * Autores:
 *   - Nome: Gabriel Abreu Miller Godoi     Matrícula: 22.1.8101
 *   - Nome: Gustavo Moreira Sousa          Matrícula: 22.1.8007
 *
 * Arquivo: LocalSearch.java
 * Pacote:  solvers
 * Data:    2025-08-24
 *
 * Descrição:
 *   Implementa rotina de Busca Local para o Conjunto Dominante:
 *   - Remoção de vértices redundantes do conjunto atual;
 *   - Trocas 1→1 para tentar reduzir o tamanho mantendo a dominância.
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

public class LocalSearch {
    public static Set<Integer> localImprovement(Graph G, Set<Integer> S, int maxIter) {
        S = new HashSet<>(S);

        // 1) Redundant removal
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int u : new ArrayList<>(S)) {
                Set<Integer> T = new HashSet<>(S);
                T.remove(u);
                if (Graph.isDominating(G, T)) {
                    S = T;
                    changed = true;
                    break;
                }
            }
        }

        // 2) 1-1 swaps
        int it = 0;
        int n = G.size();
        while (it < maxIter) {
            it++;
            boolean improved = false;
            for (int u : new ArrayList<>(S)) {
                for (int v = 1; v <= n; v++) {
                    if (S.contains(v)) continue;
                    Set<Integer> T = new HashSet<>(S);
                    T.remove(u);
                    T.add(v);
                    if (T.size() <= S.size() && Graph.isDominating(G, T)) {
                        S = T;
                        improved = true;
                        break;
                    }
                }
                if (improved) break;
            }
            if (!improved) break;
        }
        return S;
    }

    public static Set<Integer> localImprovement(Graph G, Set<Integer> S) {
        return localImprovement(G, S, 200);
    }
}
