package app;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server extends Thread {

	private static ArrayList<BufferedWriter> client;
	private static ServerSocket server;
	private String name;
	private Socket con;

	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;

	//possibilita uma conexão específica para cada cliente
	public Server(Socket con) {
		this.con = con;
		try {
			in = con.getInputStream();
			inr = new InputStreamReader(in);
			bfr = new BufferedReader(inr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void run() {
		try {
			String msg;
			OutputStream ou = this.con.getOutputStream();
			Writer ouw = new OutputStreamWriter(ou);
			BufferedWriter bfw = new BufferedWriter(ouw);
			client.add(bfw);
			name = msg = bfr.readLine();

			while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
				msg = bfr.readLine();
				sendToAll(bfw, msg);
				System.out.println(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//método que envia mensagens para todos os clientes conectados
	private void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bwS;

		for (BufferedWriter bw : client) {
			bwS = (BufferedWriter) bw;
			if (!(bwSaida == bwS)) {
				bw.write(name + ": " + msg + "\r\n");
				bw.flush();
			}
		}

	}

	public static void main(String[] args){
	
		try {	
			JLabel lblMensagem = new JLabel("Porta do Servidor");
			JTextField txtPorta = new JTextField("12345");
			Object[] texts = {lblMensagem, txtPorta};
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			client = new ArrayList<BufferedWriter>();
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: "+ txtPorta.getText());
					
			while(true) {
				System.out.println("Aguardando conexão...");
				//aguarda até que um cliente seja aceito/"escutado" pela porta.
				Socket con = server.accept();
				System.out.println("Cliente conectado!");
				Thread t = new Server(con);
				t.start();
			}
		
	
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
