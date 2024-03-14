//Grupo: Caio Moretti e Carlos Eduardo Braga
package coloracaoGenetica;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class ColoracaoGrafosGenetico {
	int M; // Tamanho da popula��o
	int Ng; // N�mero de gera��es
	int N0; // Percentual de descendentes
	double taxaMutacao; // Taxa de muta��o
	double taxaCrossover; // Taxa de crossover
	int[][] matrizAdjacencia; // Matriz de adjac�ncia
	int V; // N�mero de v�rtices
	int[] melhor; // Melhor solu��o encontrada
	int numeroDeCores;

	// Construtor
	public ColoracaoGrafosGenetico(int tamanhoPopulacao, int geracoes, int percentualDescendentes, double taxaMutacao,
			double taxaCrossover, int[][] matrizAdjacencia) {
		this.M = tamanhoPopulacao;
		this.Ng = geracoes;
		this.N0 = percentualDescendentes;
		this.taxaMutacao = taxaMutacao;
		this.taxaCrossover = taxaCrossover;
		this.matrizAdjacencia = matrizAdjacencia;
		this.V = matrizAdjacencia.length;
	}

	// M�todo principal para executar o algoritmo gen�tico
	public void executar() {

		int[][] populacaoAtual = new int[M][V];
		inicializarPopulacao(populacaoAtual);
		avaliarPopulacao(populacaoAtual, this.M);

		int[] melhorSolucao = populacaoAtual[0];
		int melhorAptidao = calcularAptidao(melhorSolucao);

		this.numeroDeCores = V;
		// Loop pelas gera��es
		for (int gen = 0; gen < Ng; gen++) {

			// Se a melhor aptid�o � menor que o n�mero de v�rtices, reduz as cores
			if (melhorAptidao < numeroDeCores) {
				reduzirCores();
			}

			int[][] P1 = new int[M / 2][V];
			int[][] P2 = new int[M / 2][V];
			dividirPopulacao(populacaoAtual, P1, P2);

			int[][] descendentes = new int[N0][V];
			crossover(P1, descendentes, taxaCrossover);
			mutacao(P2, descendentes, gen, taxaMutacao);

			avaliarPopulacao(descendentes, this.N0);

			populacaoAtual = selecionar(populacaoAtual, descendentes);

			// Atualiza a melhor solu��o encontrada
			for (int[] cromossomo : populacaoAtual) {
				int aptidaoAtual = calcularAptidao(cromossomo);
				if (aptidaoAtual < melhorAptidao) {
					melhorSolucao = cromossomo;
					melhorAptidao = aptidaoAtual;
				}
			}
		}

		// Imprime as cores dos v�rtices da melhor solu��o encontrada
		imprimirCoresVertices(melhorSolucao);

		// Atualiza a vari�vel 'melhor' com a melhor solu��o encontrada
		melhor = melhorSolucao;

		// Verifica se a solu��o encontrada � v�lida ou inv�lida
		if (!temVerticesAdjacentesMesmaCor(melhorSolucao)) {
			System.out.println("A solu��o encontrada � v�lida: n�o h� v�rtices adjacentes com a mesma cor.");
			System.out.println("\n======================================================================\n");
		} else {
			System.out.println("A solu��o encontrada � inv�lida: h� v�rtices adjacentes com a mesma cor.");
			System.out.println("\n======================================================================\n");
		}
	}

	// Inicializa a popula��o com cores v�lidas para os v�rtices
	private void inicializarPopulacao(int[][] populacao) {
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < V; j++) {

				populacao[i][j] = encontrarCorValidaParaVertice(populacao[i], j);
			}
		}
	}

	// Encontra uma cor v�lida para um v�rtice espec�fico
	private int encontrarCorValidaParaVertice(int[] cromossomo, int vertice) {
		Random rand = new Random();
		int cor = rand.nextInt(V);

		while (!corEValidaParaVertice(cromossomo, vertice, cor)) {
			cor = (cor + 1) % V;
		}

		return cor;
	}

	// Avalia a popula��o calculando a aptid�o de cada indiv�duo
	private void avaliarPopulacao(int[][] populacao, int M) {
		for (int i = 0; i < M; i++) {
			calcularAptidao(populacao[i]);
		}
	}

	// Calcula a aptid�o de um cromossomo (n�mero de cores �nicas utilizadas)
	private int calcularAptidao(int[] cromossomo) {
		Set<Integer> cores = new HashSet<>();
		for (int cor : cromossomo) {
			cores.add(cor);
		}

		int aptidao = cores.size();
		for (int i = 0; i < cromossomo.length; i++) {
			for (int j = i + 1; j < cromossomo.length; j++) {
				if (matrizAdjacencia[i][j] == 1 && cromossomo[i] == cromossomo[j]) {
					aptidao--;
				}
			}
		}
		return aptidao;
	}

	// Reduz as cores se a melhor aptid�o for menor que o n�mero de v�rtices
	private void reduzirCores() {
	this.numeroDeCores = this.numeroDeCores - 1;
	}


	// Divide a popula��o em duas partes
	private void dividirPopulacao(int[][] populacao, int[][] P1, int[][] P2) {
		int meio = M / 2;
		System.arraycopy(populacao, 0, P1, 0, meio);
		System.arraycopy(populacao, meio, P2, 0, meio);
	}

	// Realiza o crossover entre os indiv�duos da popula��o
	private void crossover(int[][] P1, int[][] descendentes, double taxaCrossover) {
		Random rand = new Random();
		for (int i = 0; i < N0; i++) {
			if (rand.nextDouble() < taxaCrossover) {
				int pai1 = rand.nextInt(M / 2);
				int pai2 = rand.nextInt(M / 2);
				int pontoCrossover = rand.nextInt(V);

				System.arraycopy(P1[pai1], 0, descendentes[i], 0, pontoCrossover);

				Set<Integer> coresUtilizadas = new HashSet<>();
				for (int j = 0; j < pontoCrossover; j++) {
					coresUtilizadas.add(descendentes[i][j]);
				}
				for (int j = pontoCrossover; j < V; j++) {
					int gene = P1[pai2][j];
					if (!coresUtilizadas.contains(gene) && corEValidaParaVertice(descendentes[i], j, gene)) {
						descendentes[i][j] = gene;
						coresUtilizadas.add(gene);
					} else {

						int corValida = -1;
						for (int k = 0; k < V; k++) {
							if (!coresUtilizadas.contains(k) && corEValidaParaVertice(descendentes[i], j, k)) {
								corValida = k;
								break;
							}
						}
						descendentes[i][j] = corValida;
						coresUtilizadas.add(corValida);
					}
				}
			} else {

				int pai = rand.nextInt(M / 2);
				System.arraycopy(P1[pai], 0, descendentes[i], 0, V);
			}
		}
	}

	// Verifica se a cor � valida
	private boolean corEValidaParaVertice(int[] cromossomo, int vertice, int cor) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			if (matrizAdjacencia[vertice][i] == 1 && cromossomo[i] == cor) {
				return false;
			}
		}
		return true;
	}

	// Realiza a muta��o nos descendentes
	private void mutacao(int[][] P2, int[][] descendentes, int geracao, double taxaMutacao) {
		Random rand = new Random();

		for (int i = 0; i < N0; i++) {
			for (int j = 0; j < V; j++) {
				if (rand.nextDouble() < taxaMutacao) {

					int vertice = rand.nextInt(V);

					descendentes[i][vertice] = encontrarCorValidaParaVertice(descendentes[i], vertice);
				} else {
					descendentes[i][j] = P2[i][j];
				}
			}

			corrigirSolucao(descendentes[i]);
		}
	}

	// Corrige a solu��o, garantindo que n�o haja v�rtices adjacentes com a mesma
	// cor
	@SuppressWarnings("unchecked")
	private void corrigirSolucao(int[] cromossomo) {
		Set<Integer>[] coresAdjacentes = new HashSet[V];
		for (int i = 0; i < V; i++) {
			coresAdjacentes[i] = new HashSet<>();
		}

		for (int i = 0; i < cromossomo.length; i++) {
			for (int j = i + 1; j < cromossomo.length; j++) {
				if (matrizAdjacencia[i][j] == 1 && cromossomo[i] == cromossomo[j]) {

					cromossomo[j] = encontrarCorValidaParaVertice(cromossomo, j);
				}
				coresAdjacentes[i].add(cromossomo[j]);
				coresAdjacentes[j].add(cromossomo[i]);
			}
		}

		for (int i = 0; i < cromossomo.length; i++) {
			for (int cor : coresAdjacentes[i]) {
				if (podeRecolorir(cromossomo, i, cor)) {
					cromossomo[i] = cor;
					break;
				}
			}
		}
	}

	// Verifica se uma cor pode ser utilizada para um v�rtice espec�fico
	private boolean podeRecolorir(int[] cromossomo, int vertice, int cor) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			if (matrizAdjacencia[vertice][i] == 1 && cromossomo[i] == cor) {
				return false;
			}
		}
		return true;
	}

	// Seleciona a nova popula��o combinando os indiv�duos da popula��o atual e os
	// descendentes
	private int[][] selecionar(int[][] populacaoAtual, int[][] descendentes) {
		int[][] populacaoCombinada = new int[M + N0][V];
		System.arraycopy(populacaoAtual, 0, populacaoCombinada, 0, M);
		System.arraycopy(descendentes, 0, populacaoCombinada, M, N0);

		int[][] novaPopulacao = new int[M][V];
		Random rand = new Random();

		for (int i = 0; i < M; i++) {

			int idx1 = rand.nextInt(M + N0);
			int idx2 = rand.nextInt(M + N0);
			int idxSelecionado = calcularAptidao(populacaoCombinada[idx1]) > calcularAptidao(populacaoCombinada[idx2])
					? idx1
					: idx2;
			novaPopulacao[i] = populacaoCombinada[idxSelecionado];
		}

		return novaPopulacao;
	}

	// Imprime as cores dos v�rtices da solu��o
	private void imprimirCoresVertices(int[] coresVertices) {
		System.out.println("Cores dos V�rtices:");
		Set<Integer> coresUnicas = new HashSet<>();
		for (int i = 0; i < coresVertices.length; i++) {
			System.out.println("V�rtice " + i + ": Cor " + coresVertices[i]);
			coresUnicas.add(coresVertices[i]);
		}
		System.out.println("Total de cores �nicas usadas: " + coresUnicas.size());
	}

	// Verifica se h� v�rtices adjacentes com a mesma cor na solu��o
	private boolean temVerticesAdjacentesMesmaCor(int[] coresVertices) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			for (int j = i + 1; j < matrizAdjacencia.length; j++) {
				if (matrizAdjacencia[i][j] == 1 && coresVertices[i] == coresVertices[j]) {
					return true;
				}
			}
		}
		return false;
	}

	// Retorna o n�mero de cores �nicas utilizadas na solu��o
	public int contarCoresUnicas(int[] solucao) {
		Set<Integer> coresUnicas = new HashSet<>();
		for (int cor : solucao) {
			coresUnicas.add(cor);
		}
		return coresUnicas.size();
	}

	// Verifica se a solu��o � v�lida (n�o h� v�rtices adjacentes com a mesma cor)
	public boolean solucaoEValida(int[] solucao) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			for (int j = i + 1; j < matrizAdjacencia.length; j++) {
				if (matrizAdjacencia[i][j] == 1 && solucao[i] == solucao[j]) {
					return false;
				}
			}
		}
		return true;
	}

	// Retorna a melhor solu��o encontrada
	public int[] getMelhorSolucao() {
		return melhor;
	}
}
