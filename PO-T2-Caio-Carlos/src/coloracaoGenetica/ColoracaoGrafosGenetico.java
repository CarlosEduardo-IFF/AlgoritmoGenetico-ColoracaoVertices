//Grupo: Caio Moretti e Carlos Eduardo Braga
package coloracaoGenetica;

import java.util.Random;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

public class ColoracaoGrafosGenetico {
	int M; // Tamanho da população
	int Ng; // Número de gerações
	int N0; // Percentual de descendentes
	double taxaMutacao; // Taxa de mutação
	double taxaCrossover; // Taxa de crossover
	int[][] matrizAdjacencia; // Matriz de adjacência
	int V; // Número de vértices
	int[][] populacaoAtual;// Individuos atuais
	int[] melhorSolucao; // Melhor solução
	int numeroDeCores; // Numero cromatico
	double melhorAptidaoCores; // Melhor aptidão

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
		this.populacaoAtual = new int[M][V];
	}

	// Método principal para executar o algoritmo genético
	public void executar() {

		// inicia a população
	    inicializarPopulacao(this.populacaoAtual);
	    
	    // Avaliar e ordenar a população inicial
	    populacaoAtual = avaliarPopulacao(populacaoAtual, M);

	    // Manter o melhor individuo
	    manterMelhor();

	    // Define numero de cores com o valor da quantidade de vértices
	    this.numeroDeCores = V;

	    // Loop pelas gerações
	    for (int gen = 0; gen < Ng; gen++) {

	        // Se a melhor aptidão é menor que o número de vértices, reduz as cores
	        if (melhorAptidaoCores < numeroDeCores) {
	            reduzirCores();
	        }

	        // Divide a populacao
	        int[][] P1 = new int[M / 2][V];
	        int[][] P2 = new int[M / 2][V];
	        dividirPopulacao(populacaoAtual, P1, P2);

	        // Criacao da variavel de descendentes e realizacao de crossover e mutacao
	        int[][] descendentes = new int[N0][V];
	        crossover(P1, descendentes, taxaCrossover);
	        mutacao(P2, descendentes, taxaMutacao);

	        // Avaliar e ordenar a população de descendentes
	        descendentes = avaliarPopulacao(descendentes, N0);

	        // Criando nova populacao a partir da selecao
	        int[][] populacaoNova = new int[M][V];
	        populacaoNova = selecionar(populacaoAtual, descendentes);
	        
	        // Avaliar populacaoNova
		    populacaoNova = avaliarPopulacao(populacaoNova, M);
		    
		    // Atualiza a População atual
		    populacaoAtual = populacaoNova;
		    
		    manterMelhor();
	    }

	    // Imprime as cores dos vértices da melhor solução encontrada
	    imprimirCoresVertices(melhorSolucao);

	    // Verifica se a solução encontrada é válida ou inválida
	    if (!temVerticesAdjacentesMesmaCor(melhorSolucao)) {
	        System.out.println("A solução encontrada é válida: não há vértices adjacentes com a mesma cor.");
	        System.out.println("\n======================================================================\n");
	    } else {
	        System.out.println("A solução encontrada é inválida: há vértices adjacentes com a mesma cor.");
	        System.out.println("\n======================================================================\n");
	    }
	}

	// Inicializa a população com cores válidas para os vértices
	private void inicializarPopulacao(int[][] populacao) {
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < V; j++) {

				populacao[i][j] = encontrarCorValidaParaVertice(populacao[i], j);
			}
		}
	}

	// Encontra uma cor válida para um vértice específico
	private int encontrarCorValidaParaVertice(int[] cromossomo, int vertice) {
		Random rand = new Random();
		int cor = rand.nextInt(V);

		while (!corEValidaParaVertice(cromossomo, vertice, cor)) {
			cor = (cor + 1) % V;
		}

		return cor;
	}

	// Avalia a população calculando a aptidão de cada indivíduo e reordenando-a
	private int[][] avaliarPopulacao(int[][] populacao, int M) {
	    
	    double[] aptidoes = new double[M];
	    for (int i = 0; i < M; i++) {
	        aptidoes[i] = calcularAptidao(populacao[i]);
	    }

	    int[][] populacaoOrdenada = Arrays.copyOf(populacao, M);

	    for (int i = 0; i < M - 1; i++) {
	        for (int j = i + 1; j < M; j++) {
	            if (aptidoes[j] > aptidoes[i]) {

	                int[] tempIndividuo = populacaoOrdenada[i];
	                populacaoOrdenada[i] = populacaoOrdenada[j];
	                populacaoOrdenada[j] = tempIndividuo;

	                double tempAptidao = aptidoes[i];
	                aptidoes[i] = aptidoes[j];
	                aptidoes[j] = tempAptidao;
	            }
	        }
	    }

	    return populacaoOrdenada;
	}

	// Calcula a aptidão de um cromossomo (número de cores únicas utilizadas)
	private double calcularAptidao(int[] cromossomo) {
	    Set<Integer> cores = new HashSet<>();
	    boolean conflito = false;

	    for (int i = 0; i < cromossomo.length && !conflito; i++) {
	        cores.add(cromossomo[i]); 
	        for (int j = i + 1; j < cromossomo.length; j++) {
	            if (matrizAdjacencia[i][j] == 1 && cromossomo[i] == cromossomo[j]) {
	                conflito = true; 
	                break; 
	            }
	        }
	    }

	    if (conflito) {
	        return 0; 
	    } else {
	        return 1.0 / cores.size(); 
	    }
	}

	// Mantem o melhor individuo e a melhor aptidão de cores
	private void manterMelhor() {
	    if (this.melhorSolucao == null) {
	        this.melhorSolucao = populacaoAtual[0];
	        melhorAptidaoCores = 0;
	        Set<Integer> cores = new HashSet<>();
	        for (int cor : populacaoAtual[0]) {
	            cores.add(cor);
	        }
	        melhorAptidaoCores = cores.size();
	    } else {
	        int coresSolucaoAtual = 0;
	        Set<Integer> cores = new HashSet<>();
	        for (int cor : populacaoAtual[0]) {
	            cores.add(cor);
	        }
	        coresSolucaoAtual = cores.size();
	        if (coresSolucaoAtual < melhorAptidaoCores) {
	            this.melhorSolucao = populacaoAtual[0];
	            melhorAptidaoCores = coresSolucaoAtual;
	        }
	    }
	}
	
	// Reduz as cores em 1
	private void reduzirCores() {
	this.numeroDeCores = this.numeroDeCores - 1;
	}

	// Divide a população em duas partes
	private void dividirPopulacao(int[][] populacao, int[][] P1, int[][] P2) {
		int meio = M / 2;
		System.arraycopy(populacao, 0, P1, 0, meio);
		System.arraycopy(populacao, meio, P2, 0, meio);
	}

	// Realiza o crossover entre os indivíduos da população
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

	// Verifica se a cor é valida
	private boolean corEValidaParaVertice(int[] cromossomo, int vertice, int cor) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			if (matrizAdjacencia[vertice][i] == 1 && cromossomo[i] == cor) {
				return false;
			}
		}
		return true;
	}

	// Realiza a mutação nos descendentes
	private void mutacao(int[][] P2, int[][] descendentes, double taxaMutacao) {
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

	// Corrige a solução, garantindo que não haja vértices adjacentes com a mesma
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

	// Verifica se uma cor pode ser utilizada para um vértice específico
	private boolean podeRecolorir(int[] cromossomo, int vertice, int cor) {
		for (int i = 0; i < matrizAdjacencia.length; i++) {
			if (matrizAdjacencia[vertice][i] == 1 && cromossomo[i] == cor) {
				return false;
			}
		}
		return true;
	}

	// Seleciona a nova população combinando os indivíduos da população atual e os
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

	// Imprime as cores dos vértices da solução
	private void imprimirCoresVertices(int[] coresVertices) {
		System.out.println("Cores dos Vértices:");
		for (int i = 0; i < coresVertices.length; i++) {
			System.out.println("Vértice " + i + ": Cor " + coresVertices[i]);
		}
		System.out.println("Total de cores únicas usadas: " + this.numeroDeCores);
	}

	// Verifica se há vértices adjacentes com a mesma cor na solução
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

	// Retorna o número de cores únicas utilizadas na solução
	public int contarCoresUnicas(int[] solucao) {
		Set<Integer> coresUnicas = new HashSet<>();
		for (int cor : solucao) {
			coresUnicas.add(cor);
		}
		return coresUnicas.size();
	}

	// Verifica se a solução é válida (não há vértices adjacentes com a mesma cor)
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

	// Retorna a melhor solução encontrada
	public int[] getMelhorSolucao() {
		return melhorSolucao;
	}

	// Retorna numero de cores
	public int getNumeroDeCores() {
		return numeroDeCores;
	}
}
