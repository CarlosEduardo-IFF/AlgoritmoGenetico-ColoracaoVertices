package coloracaoGenetica;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class ColoracaoGrafosGenetica {
	int M;
	int Ng;
	int N0;
	double taxaMutacao;
	double taxaCrossover;
	int[][] matrizAdjacencia;
	int V;
	int[] melhor;

	public ColoracaoGrafosGenetica(int tamanhoPopulacao, int geracoes, int percentualDescendentes, double taxaMutacao,
			double taxaCrossover, int[][] matrizAdjacencia) {
		this.M = tamanhoPopulacao;
		this.Ng = geracoes;
		this.N0 = percentualDescendentes;
		this.taxaMutacao = taxaMutacao;
		this.taxaCrossover = taxaCrossover;
		this.matrizAdjacencia = matrizAdjacencia;
		this.V = matrizAdjacencia.length;
	}

	public void executar() {

		int[][] populacaoAtual = new int[M][V];
		inicializarPopulacao(populacaoAtual);
		avaliarPopulacao(populacaoAtual, this.M);

		int[] melhorSolucao = populacaoAtual[0];
		int melhorAptidao = calcularAptidao(melhorSolucao);

		for (int gen = 0; gen < Ng; gen++) {

			if (melhorAptidao < V) {
				reduzirCores(populacaoAtual);
			}

			int[][] P1 = new int[M / 2][V];
			int[][] P2 = new int[M / 2][V];
			dividirPopulacao(populacaoAtual, P1, P2);

			int[][] descendentes = new int[N0][V];
			crossover(P1, descendentes, taxaCrossover);
			mutacao(P2, descendentes, gen, taxaMutacao);

			avaliarPopulacao(descendentes, this.N0);

			populacaoAtual = selecionar(populacaoAtual, descendentes);

			for (int[] cromossomo : populacaoAtual) {
				int aptidaoAtual = calcularAptidao(cromossomo);
				if (aptidaoAtual < melhorAptidao) {
					melhorSolucao = cromossomo;
					melhorAptidao = aptidaoAtual;
				}
			}
		}

		imprimirCoresVertices(melhorSolucao);

		melhor = melhorSolucao;

		if (!temVerticesAdjacentesMesmaCor(melhorSolucao)) {
			System.out.println("A solução encontrada é válida: não há vértices adjacentes com a mesma cor.");
		} else {
			System.out.println("A solução encontrada é inválida: há vértices adjacentes com a mesma cor.");
		}
	}

	private void inicializarPopulacao(int[][] populacao) {
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < V; j++) {

				populacao[i][j] = encontrarCorValidaParaVertice(populacao[i], j);
			}
		}
	}

	private int encontrarCorValidaParaVertice(int[] cromossomo, int vertice) {
		Random rand = new Random();
		int cor = rand.nextInt(V);

		while (!corEValidaParaVertice(cromossomo, vertice, cor)) {
			cor = (cor + 1) % V;
		}

		return cor;
	}

	private void avaliarPopulacao(int[][] populacao, int N0) {
		for (int i = 0; i < N0; i++) {
			calcularAptidao(populacao[i]);
		}
	}

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

	private void reduzirCores(int[][] populacao) {
		int[] contagemCores = new int[V];
		for (int[] cromossomo : populacao) {
			for (int cor : cromossomo) {
				contagemCores[cor]++;
			}
		}

		int[] novasCores = new int[V];
		int novaCor = 0;
		for (int i = 0; i < V; i++) {
			if (contagemCores[i] > 0) {
				novasCores[i] = novaCor;
				novaCor++;
			} else {
				novasCores[i] = V;
			}
		}
	}

	private void dividirPopulacao(int[][] populacao, int[][] P1, int[][] P2) {
		int meio = M / 2;
		System.arraycopy(populacao, 0, P1, 0, meio);
		System.arraycopy(populacao, meio, P2, 0, meio);
	}

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

	private boolean corEValidaParaVertice(int[] cromossomo, int vertice, int cor) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			if (matrizAdjacencia[vertice][i] == 1 && cromossomo[i] == cor) {
				return false;
			}
		}
		return true;
	}

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

	private boolean podeRecolorir(int[] cromossomo, int vertice, int cor) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			if (matrizAdjacencia[vertice][i] == 1 && cromossomo[i] == cor) {
				return false;
			}
		}
		return true;
	}

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

	private void imprimirCoresVertices(int[] coresVertices) {
		System.out.println("Cores dos vértices:");
		Set<Integer> coresUnicas = new HashSet<>();
		for (int i = 0; i < coresVertices.length; i++) {
			System.out.println("Vértice " + i + ": Cor " + coresVertices[i]);
			coresUnicas.add(coresVertices[i]);
		}
		System.out.println("Total de cores únicas usadas: " + coresUnicas.size());
	}

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

	public int contarCoresUnicas(int[] solucao) {
		Set<Integer> coresUnicas = new HashSet<>();
		for (int cor : solucao) {
			coresUnicas.add(cor);
		}
		return coresUnicas.size();
	}

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

	public int[] getMelhorSolucao() {
		return melhor;
	}
}