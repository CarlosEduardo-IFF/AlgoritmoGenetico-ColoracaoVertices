package manipulacaoArquivos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ManipuladorArquivo {
    
    public static void escreverCoresEmArquivo(int[] cores) {
        String nomeArquivo = "cores.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (int i = 0; i < cores.length; i++) {
                writer.write(String.valueOf(cores[i]));
                writer.newLine();
            }
            System.out.println("As cores foram escritas no arquivo " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
    
    public static void escreverMatrizAdjacenciaEmArquivo(int[][] grafo, int vertices, int arestas, String nomeArquivo) {
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
