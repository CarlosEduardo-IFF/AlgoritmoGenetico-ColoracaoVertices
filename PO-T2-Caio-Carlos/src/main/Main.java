//Grupo: Caio Moretti e Carlos Eduardo Braga
package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import coloracaoGenetica.ColoracaoGrafosGenetico;
import grafo.Grafo;
import manipulacaoArquivos.*;

public class Main {

	public static void main(String[] args) throws IOException {

		Scanner scanner = new Scanner(System.in);

		//Utilizando um arquivo para gerar um objeto grafo
		ManipulaDataset manipulador = new ManipulaDataset();
		Grafo novoGrafo = new Grafo(manipulador.obterVerticesArestas()[0], manipulador.obterVerticesArestas()[1]);
		manipulador.lerDoDataset(novoGrafo);

		// Outra forma de inicializar o programa, fazendo com que ele mesmo gere uma
		// matriz de adjacência a partir de uma quantidade de vértices

		/*
		System.out.print("Digite a quantidade de vértices: ");
		int vertices = scanner.nextInt();
		scanner.nextLine();

		Grafo novoGrafo = new Grafo(vertices);
		novoGrafo.gerarGrafo();
		novoGrafo.contarArestas();

		manipulador.escreverMatrizAdjacenciaEmArquivo(novoGrafo.getMatrizAdjacencia(), novoGrafo.getVertices(),
				novoGrafo.getArestas(), "matriz_adjacencia.txt");
		System.out.println("Matriz de adjacência gerada com sucesso!\n");
		*/
		
		// Defini��o de quantas vezes o programa ser� executado
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

			// Realizando a colora��o de um grafo de uma execu��o
			ColoracaoGrafosGenetico coloracaoGrafos = new ColoracaoGrafosGenetico(tamanhoPopulacao, geracoes,
					percentualDescendentes, taxaMutacao, taxaCrossover, novoGrafo.getMatrizAdjacencia());
			coloracaoGrafos.executar();

			int[] melhorSolucao = coloracaoGrafos.getMelhorSolucao();
			int coresUnicas = coloracaoGrafos.contarCoresUnicas(melhorSolucao);

			// verifica��o de solu��o
			if (coloracaoGrafos.solucaoEValida(melhorSolucao)) {
				solucoesValidas++;
			} else {
				solucoesInvalidas++;
			}

			contagemCores.put(coresUnicas, contagemCores.getOrDefault(coresUnicas, 0) + 1);
		}

		// Imprimindo os resultados obtidos ap�s todas execu��es
		System.out.println("\nEstat�sticas das execu��ees:");
		for (Map.Entry<Integer, Integer> entrada : contagemCores.entrySet()) {
			System.out.println(entrada.getValue() + " vezes usou " + entrada.getKey() + " cores.");
		}
		System.out.println("\nSolu��es v�lidas: " + solucoesValidas);
		System.out.println("Solu��es inv�lidas: " + solucoesInvalidas);

		scanner.close();
	}
}
