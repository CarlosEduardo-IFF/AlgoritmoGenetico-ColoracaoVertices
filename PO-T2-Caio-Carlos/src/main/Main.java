package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import coloracaoGenetica.ColoracaoGrafosGenetica;
import grafo.Grafo;
import manipulacaoArquivos.*;

public class Main {

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		ManipulaDataset leitor = new ManipulaDataset();

		Grafo novoGrafo = new Grafo(leitor.obterVerticesArestas()[0], leitor.obterVerticesArestas()[1]);
		leitor.lerDoDataset(novoGrafo);
		
/*
		System.out.print("Digite a quantidade de v�rtices: ");
		int vertices = scanner.nextInt();
		scanner.nextLine();

		Grafo novoGrafo = new Grafo(vertices);
		novoGrafo.gerarGrafo();
		novoGrafo.contarArestas();
*/
	/*	ManipuladorArquivo.escreverMatrizAdjacenciaEmArquivo(novoGrafo.getMatrizAdjacencia(), novoGrafo.getVertices(),
				novoGrafo.getArestas(), "matriz_adjacencia.txt");
		System.out.println("Matriz de adjac�ncia gerada com sucesso!");*/

		System.out.println("Quantas vezes voc� deseja executar o algoritmo?");
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

		System.out.println("\nEstat�sticas das execu��es:");
		for (Map.Entry<Integer, Integer> entrada : contagemCores.entrySet()) {
			System.out.println(entrada.getValue() + " vezes usou " + entrada.getKey() + " cores.");
		}
		System.out.println("\nSolu��es v�lidas: " + solucoesValidas);
		System.out.println("Solu��es inv�lidas: " + solucoesInvalidas);

		scanner.close();
	}
}
