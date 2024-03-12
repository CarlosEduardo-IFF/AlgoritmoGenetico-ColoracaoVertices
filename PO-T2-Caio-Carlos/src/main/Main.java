package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import coloracaoGenetica.ColoracaoGrafosGenetica;
import grafo.Grafo;
import manipulacaoArquivos.ManipuladorArquivo;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Digite a quantidade de vértices: ");
		int vertices = scanner.nextInt();
		scanner.nextLine();

		Grafo novoGrafo = new Grafo(vertices);
		novoGrafo.gerarGrafo();
		novoGrafo.contarArestas();

		ManipuladorArquivo.escreverMatrizAdjacenciaEmArquivo(novoGrafo.getMatrizAdjacencia(), novoGrafo.getVertices(),
				novoGrafo.getArestas(), "matriz_adjacencia.txt");
		System.out.println("Matriz de adjacência gerada com sucesso!");

		System.out.println("Quantas vezes você deseja executar o algoritmo?");
		int numeroExecucoes = scanner.nextInt();

		Map<Integer, Integer> contagemCores = new HashMap<>();
		int solucoesValidas = 0;
		int solucoesInvalidas = 0;

		for (int i = 0; i < numeroExecucoes; i++) {
			int tamanhoPopulacao = 100;
			int geracoes = 100;
			int percentualDescendentes = geracoes / 2;
			double taxaMutacao = 0.5;
			double taxaCrossover = 0.8;

			int[][] matrizAdjacencia = { 
					{ 0, 1, 1, 0, 1 }, 
					{ 1, 0, 1, 1, 0 }, 
					{ 1, 1, 0, 1, 1 }, 
					{ 0, 1, 1, 0, 0 },
					{ 1, 0, 1, 0, 0 } 
			};

			ColoracaoGrafosGenetica coloracaoGrafos = new ColoracaoGrafosGenetica(tamanhoPopulacao, geracoes,
					percentualDescendentes, taxaMutacao, taxaCrossover, novoGrafo.getMatrizAdjacencia());
			coloracaoGrafos.executar();

			int[] melhorSolucao = coloracaoGrafos.getMelhorSolucao();
			int coresUnicas = coloracaoGrafos.contarCoresUnicas(melhorSolucao);

			if (coloracaoGrafos.solucaoEValida(melhorSolucao)) {
				solucoesValidas++;
			} else {
				solucoesInvalidas++;
			}

			contagemCores.put(coresUnicas, contagemCores.getOrDefault(coresUnicas, 0) + 1);
		}

		System.out.println("\nEstatísticas das execuções:");
		for (Map.Entry<Integer, Integer> entrada : contagemCores.entrySet()) {
			System.out.println(entrada.getValue() + " vezes usou " + entrada.getKey() + " cores.");
		}
		System.out.println("\nSoluções válidas: " + solucoesValidas);
		System.out.println("Soluções inválidas: " + solucoesInvalidas);

		scanner.close();
	}
}
