package graph;

/*
 * CSI546 - Projeto e Análise de Algoritmos (UFOP/DECSI)
 * Trabalho Prático: Conjunto Dominante
 *
 * Autores:
 *   - Nome: Gabriel Abreu Miller Godoi     Matrícula: 22.1.8101
 *   - Nome: Gustavo Moreira Sousa          Matrícula: 22.1.8007
 *
 * Arquivo: Graph.java
 * Pacote:  graph
 * Data:    2025-08-24
 *
 * Descrição:
 *   Implementa a estrutura de grafo não-direcionado simples com indexação
 *   de vértices a partir de 1. Fornece operações de leitura a partir de
 *   arquivo, vizinhança, grau, contagem de arestas e verificação de
 *   dominância de um conjunto de vértices.
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

import java.io.*;
import java.util.*;

/**
 * Undirected simple graph with 1-based vertex indexing.
 * Mirrors the Python Graph class.
 */
public class Graph {
    private final int n;
    private final List<Set<Integer>> adj;

    public Graph(int n, List<int[]> edges) {
        this.n = n;
        this.adj = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) {
            this.adj.add(new HashSet<>());
        }
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            if (u == v) continue;
            this.adj.get(u).add(v);
            this.adj.get(v).add(u);
        }
    }

    public static Graph read(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine();
            if (header == null) throw new IOException("Empty file: " + path);
            String[] parts = header.trim().split("\\s+");
            int n = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            List<int[]> edges = new ArrayList<>(m);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] uv = line.split("\\s+");
                edges.add(new int[]{Integer.parseInt(uv[0]), Integer.parseInt(uv[1])});
            }
            return new Graph(n, edges);
        }
    }

    public Set<Integer> neighbors(int u) {
        return adj.get(u);
    }

    public int degree(int u) {
        return adj.get(u).size();
    }

    public int size() {
        return n;
    }

    public int edgesCount() {
        int sum = 0;
        for (int i = 1; i <= n; i++) sum += adj.get(i).size();
        return sum / 2;
    }

    // === Utils ===
    public static boolean isDominating(Graph G, Set<Integer> S) {
        Set<Integer> dominated = new HashSet<>(S);
        for (int u : S) dominated.addAll(G.neighbors(u));
        return dominated.size() == G.size();
    }

    public static int coverCount(Graph G, Set<Integer> S) {
        Set<Integer> dominated = new HashSet<>(S);
        for (int u : S) dominated.addAll(G.neighbors(u));
        return dominated.size();
    }
}
