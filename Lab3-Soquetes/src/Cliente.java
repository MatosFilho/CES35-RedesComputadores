

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	
	/*  Thread que monitora o InputStream que vem do servidor 
	    e captura as mensagens para jogar na tela  */
	private class RecebeDoServidor implements Runnable {

		@Override
		public void run() {
			boolean conexaoServidor = false;
			try {
				String texto;
				while(( texto = leitor.nextLine()) != null ) {  //Estabelece a conex�o com o servidor
					if (!conexaoServidor) {						//Espera por resposta do servidor
						escritor.println("Posso conectar?");
						System.out.println("Chegou aqui");
						textoRecebido.append("Cliente: Posso conectar?\n");
						for(int k = 0; k < 1000000; k++) {
							if(texto.equals("Pode conectar!")) {
								conexaoServidor = true;
								break;
							}
							texto = leitor.nextLine();
						}
					} else {   									//Quando servidor responde
						textoRecebido.append("Servidor: "+texto + "\n");
						escritor.println("Envia tabela!");
						textoRecebido.append("Cliente: Envia tabela!\n");
						break;
					}
				}
				
				while(( texto = leitor.nextLine()) != null ) { //Recebe lista de m�sicas
					textoRecebido.append(texto + "\n");
				}
			} catch (Exception e) {}
		}
		
	}
	
	public Cliente (String nome) {
		super ("Chat: " + nome);
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
			escritor.println(nome + ": " +textoEnviado.getText());
			escritor.flush();
			textoEnviado.setText("");
			textoEnviado.requestFocus();
		}
		
	}
	
	public static void main(String[] args) {
		new Cliente("Zina");
	}

}