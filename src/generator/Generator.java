
/*
 * CSI546 - Projeto e Análise de Algoritmos (UFOP/DECSI)
 * Trabalho Prático: Conjunto Dominante
 *
 * Autores:
 *   - Nome: Gabriel Abreu Miller Godoi     Matrícula: 22.1.8101
 *   - Nome: Gustavo Moreira Sousa          Matrícula: 22.1.8007
 *
 * Arquivo: Generator.java
 * Pacote:  generator
 * Data:    2025-08-24
 *
 * Descrição:
 *   Implementa geradores de grafos aleatórios:
 *   - Erdős–Rényi (G(n,p)): conecta pares de vértices com probabilidade p.
 *   - Barabási–Albert: modelo de crescimento por ligação preferencial.
 *   Inclui método para exportar instâncias no formato de entrada do solver.
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
package generator;

import java.io.*;
import java.util.*;

/**
 * Graph instance generators: Erdős–Rényi (G(n,p)) and Barabási–Albert.
 */
public class Generator {

    public static List<int[]> erdosRenyi(int n, double p, Long seed) {
        Random rnd = (seed == null) ? new Random() : new Random(seed);
        List<int[]> edges = new ArrayList<>();
        for (int u = 1; u <= n; u++) {
            for (int v = u + 1; v <= n; v++) {
                if (rnd.nextDouble() < p) edges.add(new int[]{u, v});
            }
        }
        return edges;
    }

    public static List<int[]> barabasiAlbert(int n, int m, Long seed) {
        if (m < 1) throw new IllegalArgumentException("m must be >= 1");
        if (n < m + 1) throw new IllegalArgumentException("n must be >= m+1");
        Random rnd = (seed == null) ? new Random() : new Random(seed);
        List<int[]> edges = new ArrayList<>();
        List<Integer> targets = new ArrayList<>();
        for (int i = 1; i <= m; i++) targets.add(i);
        List<Integer> repeated = new ArrayList<>(targets);
        for (int v = m + 1; v <= n; v++) {
            Set<Integer> chosen = new HashSet<>();
            while (chosen.size() < m) {
                int x = repeated.get(rnd.nextInt(repeated.size()));
                chosen.add(x);
            }
            for (int u : chosen) edges.add(new int[]{u, v});
            // preferential attachment: add chosen and v m times
            repeated.addAll(chosen);
            for (int i = 0; i < m; i++) repeated.add(v);
        }
        return edges;
    }

    public static void writeInstance(String path, int n, List<int[]> edges) throws IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            pw.printf("%d %d%n", n, edges.size());
            for (int[] e : edges) pw.printf("%d %d%n", e[0], e[1]);
        }
    }
}
