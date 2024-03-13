
package manipulacaoArquivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import grafo.Grafo;


public class ManipulaDataset {
	public int[] obterVerticesArestas() throws IOException {
		String currentDirectory = new File("").getAbsolutePath();

		BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "\\static\\grafo.txt"));
		String linhaLida = reader.readLine();
   	 	String[] campos = linhaLida.split(" ");
   	 	int[] informacao = new int[2];
   	 	informacao[0] = Integer.valueOf(campos[0]);
   	 	informacao[1] = Integer.valueOf(campos[1]);
		reader.close();
		return informacao;
	};
	
	 public void lerDoDataset(Grafo estrutura) throws IOException {
			String currentDirectory = new File("").getAbsolutePath();
		 BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "\\static\\grafo.txt"));
		 String linhaLida = reader.readLine();
    	 String[] campos = linhaLida.split(" ");
    	 linhaLida = reader.readLine();
    	 int contagemLinhalida = 0;
		 while (linhaLida != null) {
	         campos = linhaLida.split(" ");	         
	         try {	
			        for(int i = 0; i <= campos.length-1;i++) {
			        	if(campos[i].contains("1")) {
			        		estrutura.adicionarAresta(contagemLinhalida ,i);
			        		 }
			        	 }          	 
			        linhaLida = reader.readLine();
	         }
	         catch (Exception e) {
	        	 linhaLida = reader.readLine();
	         }
			 contagemLinhalida++; 
		 }
		 reader.close();
	}
}
