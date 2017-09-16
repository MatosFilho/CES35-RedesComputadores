

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Servidor {
	
	ServerSocket servidor;
	ArrayList<Musica> listaDeMusicas;
	
	public Servidor() {
		listaDeMusicas = new ArrayList<Musica>();
		criarLista();
		criarServidor();
		escutarClientes();
	}
	
	public void criarLista() {
		listaDeMusicas.add(new Musica("Atirei o Pau no Gato", "Zina Baby", "2017", "2432"));
	}
	
	private void criarServidor() {
		try {
			servidor = new ServerSocket(5000);
		} catch (Exception e) {
			System.out.println("Não foi possível criar Servidor nessa porta.");
		}
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
				System.out.println("Servidor conectado com cliente e esperando mensagem.");
				String mensagem = leitorDeAcesso.nextLine();
				System.out.println("Cliente: "+mensagem);
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
					System.out.println("Servidor: Pode conectar!");
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
		
		public RecebeDoCliente(Socket socket) {
			try {
			leitor = new Scanner(socket.getInputStream());
			escritor = new PrintWriter(socket.getOutputStream());
			} catch(Exception e) {}
		}
		
		private void enviarTabela() {
			int cont = 1;
			for(Musica m : listaDeMusicas ) {
				escritor.println(cont + ". " + m.getMusica());
				escritor.flush();
				cont++;
			}
		}
		
		public void enviarMusica(int indice) {
			escritor.println("Nome: "+listaDeMusicas.get(indice-1).getMusica());
			escritor.flush();
			escritor.println("Autor: "+listaDeMusicas.get(indice-1).getAutor());
			escritor.flush();
			escritor.println("Ano de Lancamento: "+listaDeMusicas.get(indice-1).getAnoLancamento());
			escritor.flush();
			escritor.println("Tamanho: "+listaDeMusicas.get(indice-1).getTamanho());
			escritor.flush();
		}
		
		@Override
		public void run() {
			try {
				enviarTabela();									//Envio da tabela
				String texto;
				while(( texto = leitor.nextLine() ) != null) {
					enviarMusica(Integer.parseInt(texto));
				}
			} catch (Exception e) {}
		}
		
	}
	
	public static void main(String[] args) {
		new Servidor();
	}
	
}
