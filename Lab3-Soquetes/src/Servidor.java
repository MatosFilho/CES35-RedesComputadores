

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Servidor {
	
	ServerSocket servidor;
	ArrayList<String> listaDeMusicas;
	String path = System.getProperty("user.dir") + "/musicas/";
	
	public Servidor() {
		listaDeMusicas = new ArrayList<String>();
		criarLista();
		criarServidor();
		escutarClientes();
	}
	
	public void criarLista() {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	listaDeMusicas.add(listOfFiles[i].getName());
	    }
	}
	
	private void criarServidor() {
		try {
			servidor = new ServerSocket(5000);
		} catch (Exception e) {}
	}
	
	/**  
	 * Metodo que fica escutando as requisicoes de clientes  
	 */
	private void escutarClientes() {
		Scanner leitorDeAcesso;
		try {
			while (true) {
				boolean conexaoCliente = false;
				Socket socket = servidor.accept();
				leitorDeAcesso = new Scanner(socket.getInputStream());
				String mensagem = leitorDeAcesso.nextLine();
				/* Servidor espera mensagem de conexao */
				for(int k = 0; k < 1000000; k++) {
					if(mensagem.equals("Posso conectar?")) {
						conexaoCliente = true;
						break;
					}
					mensagem = leitorDeAcesso.nextLine();
				}
				
				if (conexaoCliente) {
					boolean confirmaConexao = false;
					PrintWriter p = new PrintWriter(socket.getOutputStream());
					p.println("Pode conectar!");
					p.flush();
					/* Servidor espera a mensagem de confirmacao*/
					mensagem = leitorDeAcesso.nextLine();
					for(int k = 0; k < 1000000; k++) {
						if(mensagem.equals("Envia tabela!")) {
							confirmaConexao = true;
							break;
						}
						mensagem = leitorDeAcesso.nextLine();
					}
					/* Recebida a mensagem de confirmacao, o servidor cria uma 
					   thread para conversar com o cliente */
					if(confirmaConexao)
						new Thread(new RecebeDoCliente(socket)).start();
				}
			}
		} catch (Exception e){}
	}
	

	private class RecebeDoCliente implements Runnable {

		Scanner leitor;
		PrintWriter escritor;
		OutputStream out;
		
		public RecebeDoCliente(Socket socket) {
			try {
			leitor = new Scanner(socket.getInputStream());
			escritor = new PrintWriter(socket.getOutputStream());
			out = socket.getOutputStream();
			} catch(Exception e) {}
		}
		
		private void enviarTabela() {
			int cont = 1;
			escritor.println("\nLista de musicas:\n");
			escritor.flush();
			escritor.println("0. Encerra conexao");
			escritor.flush();
			for(String m : listaDeMusicas ) {
				escritor.println(cont + ". " + m);
				escritor.flush();
				cont++;
			}
			escritor.println("\nEscolha a musica a partir do numero\n");
			escritor.flush();
		}
		
		public void enviarMusica(int indice) {
			escritor.println("Enviando...");
			escritor.flush();
			
//			/* Simulando tempo de envio */
//			try {
//				Thread.sleep((long) Integer.parseInt(listaDeMusicas.get(indice-1).getTamanho()));
//			} catch (InterruptedException e) {System.out.println("Erro!");}
			File file = new File(path+listaDeMusicas.get(indice-1));
			escritor.println((int)file.length());
			escritor.flush();
			escritor.println(listaDeMusicas.get(indice-1));
			escritor.flush();
			byte [] mybytearray  = new byte [(int)file.length()];
	        FileInputStream fis;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        try {
				bis.read(mybytearray,0,mybytearray.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				out.write(mybytearray,0,mybytearray.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println("Done.");
			escritor.println("Enviado");
			escritor.flush();
		}
		
		@Override
		public void run() {
			try {
				enviarTabela();									//Envio da tabela
				String texto;
				while(( texto = leitor.nextLine() ) != null) {
					int numMusica = Integer.parseInt(texto);
					if (( numMusica < 1 ) || ( numMusica > listaDeMusicas.size() )) {
						escritor.println("Numero de musica invalido.\nConsulte a tabela novamente.");
						escritor.flush();
					}
					else
						enviarMusica(numMusica);
				}
			} catch (Exception e) {}
		}
		
	}
	
	public static void main(String[] args) {
		new Servidor();
	}
	
}
