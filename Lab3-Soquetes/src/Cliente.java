

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Cliente extends JFrame{
	
	String nome;
	Socket socketCliente;
	JTextField textoEnviado;
	JTextArea textoRecebido;
	PrintWriter escritor;
	Scanner leitor;
	String path = System.getProperty("user.dir") + "/downloads/";
	
	/*  Thread que monitora o InputStream que vem do servidor 
	    e captura as mensagens para jogar na tela  */
	private class RecebeDoServidor implements Runnable {

		@Override
		public void run() {
			boolean conexaoServidor = false;
			try {
				String texto ="";
				while(true) {  //Estabelece a conexao com o servidor
					if (!conexaoServidor) {						//Espera por resposta do servidor
						escritor.println(nome);
						escritor.flush();
						texto = leitor.nextLine();
						for(int k = 0; k < 1000000; k++) {
							if(texto.equals("Cliente conectado!")) {
								conexaoServidor = true;
								break;
							}
							texto = leitor.nextLine();  // <--
						}
					} else {   									//Quando servidor responde
						textoRecebido.append("Servidor : "+texto + "\n");
						escritor.println("Envia tabela!");
						escritor.flush();
						textoRecebido.append("Cliente   : Envia tabela!\n");
						break;
					}
				}
				/*
				boolean tabela = false;
				while(!tabela) {
					for (int l = 0; l < 1000000; l++) {
						if ((texto = leitor.nextLine()).equals("\nLista de m�sicas:\n")) {
							tabela = true;
							break;
						}
					}
					escritor.println("Envia tabela!");
					escritor.flush();
					textoRecebido.append("Cliente   : Envia tabela!\n");
				}
				*/
				while(true) { //Recebe lista de musicas
					texto = leitor.nextLine();
					textoRecebido.append(texto + "\n");
					//texto = leitor.nextLine();
					if(texto.equals("Enviando...")) {
						recebeMusica();
					}
				}
			} catch (Exception e) {}
		}

		private void recebeMusica() {
			System.out.println("Começou a receber!!!");
			int bytesRead;
		    int current = 0;
		    FileOutputStream fos = null;
		    BufferedOutputStream bos = null;
		    Socket sock = null;
		    int FILE_SIZE = Integer.parseInt(leitor.nextLine()); 
			byte [] mybytearray  = new byte [FILE_SIZE];
		    InputStream is;
			try {
				is = socketCliente.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		    String FILE_TO_RECEIVED = leitor.nextLine();
		    File arquivo = new File(path+FILE_TO_RECEIVED);
		    System.out.println("Criando um arquivo em: "+path+FILE_TO_RECEIVED);
		    try {
				arquivo.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    try {
				fos = new FileOutputStream(path+FILE_TO_RECEIVED);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    bos = new BufferedOutputStream(fos);
		    try {
				bytesRead = is.read(mybytearray,0,mybytearray.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		    current = bytesRead;
		    do {
		    	try {
					bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
					System.out.println("To preso aqui dentro! "+bytesRead);
		    	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        if(bytesRead > 0) current += bytesRead;
		    } while(bytesRead > 0);
		    try {
				bos.write(mybytearray, 0 , current);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    textoRecebido.append("File " + FILE_TO_RECEIVED
			          + " downloaded (" + current + " bytes read)" + "\n");
		}
		
	}
	
	public Cliente (String nome) {
		super ("Cliente: " + nome);
		this.nome = nome;
		
		Font fonte = new Font("Serif", Font.PLAIN, 26);
		textoEnviado = new JTextField();
		textoEnviado.setFont(fonte);
		JButton botao = new JButton("Enviar");
		botao.setFont(fonte);
		botao.addActionListener(new EnviaAoServidor());
		textoRecebido = new JTextArea();
		textoRecebido.setFont(fonte);
		JScrollPane scroll = new JScrollPane(textoRecebido);
		
		Container containerEnvio = new JPanel();
		containerEnvio.setLayout(new BorderLayout());
		containerEnvio.add(BorderLayout.CENTER, textoEnviado);
		containerEnvio.add(BorderLayout.EAST, botao);
		getContentPane().add(BorderLayout.CENTER, scroll);
		getContentPane().add(BorderLayout.SOUTH, containerEnvio);
		
		conectaAoServidor();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setVisible(true);
	}

	private void conectaAoServidor() {
		try {
			socketCliente = new Socket("127.0.0.1", 5000);
			escritor = new PrintWriter(socketCliente.getOutputStream());
			leitor = new Scanner(socketCliente.getInputStream());
			new Thread(new RecebeDoServidor()).start();
		} catch (Exception e) {}
	}
	
	private class EnviaAoServidor implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			textoRecebido.append(nome + ": " +textoEnviado.getText()+"\n");
			escritor.println(textoEnviado.getText());
			escritor.flush();
			textoEnviado.setText("");
			textoEnviado.requestFocus();
		}
		
	}
	
	public static void main(String[] args) {
		new Cliente("Zina");
		//new Cliente("Matos");
		//new Cliente("Panda");
	}

}
