package grafo;

public class Grafo {

	private int vertices;
	private int arestas;
	private int[][] matrizAdjacencia;

	public Grafo(int vertices) {
		this.vertices = vertices;
	}

	public Grafo(int vertices, int arestas, int[][] matrizAdjacencia) {
		this.vertices = vertices;
		this.matrizAdjacencia = matrizAdjacencia;
		this.arestas = arestas;
	}

	// Método construtor para instanciar a matrizAdjancencia apenas com 0's
	public Grafo(int vertices, int arestas) {
		this.vertices = vertices;
		this.matrizAdjacencia = new int[vertices][vertices]; // Inicializa a matriz com zeros
		this.arestas = arestas;
	}

	// Cria um grafo aleatório
	public void gerarGrafo() {
		int[][] grafo = new int[this.vertices][this.vertices];
		Random random = new Random();

		for (int i = 0; i < this.vertices; i++) {
			for (int j = i + 1; j < this.vertices; j++) {
				if (random.nextBoolean()) {
					grafo[i][j] = 1;
					grafo[j][i] = 1;
				}
			}
		}

		this.matrizAdjacencia = grafo;
	}

	// Conta a quantidade de arestas do grafo
	public void contarArestas() {

		for (int i = 0; i < this.matrizAdjacencia.length; i++) {
			for (int j = i + 1; j < this.matrizAdjacencia[i].length; j++) {
				this.arestas += this.matrizAdjacencia[i][j];
			}
		}
	}

	public int getVertices() {
		return vertices;
	}

	public void setVertices(int vertices) {
		this.vertices = vertices;
	}

	public int getArestas() {
		return arestas;
	}

	public void setArestas(int arestas) {
		this.arestas = arestas;
	}

	public int[][] getMatrizAdjacencia() {
		return this.matrizAdjacencia;
	}

	public void setMatrizAdjacencia(int[][] matrizAdjacencia) {
		this.matrizAdjacencia = matrizAdjacencia;
	}

	public void adicionarAresta(int linha, int coluna) {
		this.matrizAdjacencia[linha][coluna] = 1;
	}

}
