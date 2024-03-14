//Grupo: Caio Moretti e Carlos Eduardo Braga
package manipulacaoArquivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import grafo.Grafo;


public class ManipulaDataset {

	// Método para obter o número de vértices e arestas do arquivo de grafo
	public int[] obterVerticesArestas() throws IOException {
		String currentDirectory = new File("").getAbsolutePath();

		BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "\\static\\grafos_para_teste\\myciel4_adjacency_matrix.txt"));
		String linhaLida = reader.readLine();
		String[] campos = linhaLida.split(" ");
		int[] informacao = new int[2];
		informacao[0] = Integer.valueOf(campos[0]);
		informacao[1] = Integer.valueOf(campos[1]);
		reader.close();
		return informacao;
	};

	// Método para ler o arquivo de grafo e construir a estrutura de dados do grafo
	public void lerDoDataset(Grafo estrutura) throws IOException {
		String currentDirectory = new File("").getAbsolutePath();
		BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "\\static\\grafos_para_teste\\myciel4_adjacency_matrix.txt"));
		String linhaLida = reader.readLine();
		String[] campos = linhaLida.split(" ");
		linhaLida = reader.readLine();
		int contagemLinhalida = 0;
		while (linhaLida != null) {
			campos = linhaLida.split(" ");
			try {
				for (int i = 0; i <= campos.length - 1; i++) {
					if (campos[i].contains("1")) {
						estrutura.adicionarAresta(contagemLinhalida, i);
					}
				}
				linhaLida = reader.readLine();
			} catch (Exception e) {
				linhaLida = reader.readLine();
			}
			contagemLinhalida++;
		}
		reader.close();
	}

	// Método para escrever a matriz de adjacência em um arquivo
	public void escreverMatrizAdjacenciaEmArquivo(int[][] grafo, int vertices, int arestas, String nomeArquivo) {
		try (FileWriter fileWriter = new FileWriter(nomeArquivo)) {
			fileWriter.write(vertices + " " + arestas + "\n");
			for (int i = 0; i < vertices; i++) {
				for (int j = 0; j < vertices; j++) {
					fileWriter.write(grafo[i][j] + " ");
				}
				fileWriter.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
