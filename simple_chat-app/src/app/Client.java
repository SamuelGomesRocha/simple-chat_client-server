package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener, KeyListener {

	
	private JPanel pnlContent;
	private JTextArea txtArea;
	private JTextField txtMsg;
	private JLabel lblChat;
	private JLabel lblMensagem;

	//campos utilizados para validação
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField txtCliente;

	//botões
	private JButton btnSair;
	private JButton btnLimpaHistorico;
	private JButton btnEnviar;

	//atributos de stream e socket
	private Socket socket;
	private OutputStream ou;
	private Writer writer;
	private BufferedWriter bfw;

	public Client() throws IOException {

		lblMensagem = new JLabel("Login");
		txtIP = new JTextField("192.112.0.1");
		txtIP.setToolTipText("Campo alusivo ao IP, ex: '192.112.0.1'");
		txtPorta = new JTextField("12345");
		txtPorta.setToolTipText("Campo alusivo à porta, ex: '64000'");
		txtCliente = new JTextField("Host");
		txtCliente.setToolTipText("Campo referente ao nome do cliente, ex: 'localhost111'");

		Object[] mensagem = { lblMensagem, txtIP, txtPorta, txtCliente };
		JOptionPane.showMessageDialog(null, mensagem);

		pnlContent = new JPanel();

		txtArea = new JTextArea(15, 28);
		txtArea.setEditable(false);
		txtArea.setBackground(new Color(127, 255, 212));

		txtMsg = new JTextField(25);
		txtMsg.addKeyListener(this);
		txtMsg.setPreferredSize(new Dimension(279, 26));

		lblChat = new JLabel("Chat");
		lblMensagem = new JLabel(
				"                                                 Mensagem                                                 ");
		lblMensagem.setBackground(new Color(127, 255, 212));

		btnLimpaHistorico = new JButton("Limpar");
		btnLimpaHistorico.setToolTipText("Limpa histórico de mensagens");
		btnLimpaHistorico.addActionListener(this);
		btnLimpaHistorico.addKeyListener(this);
		btnLimpaHistorico.setBackground(new Color(95, 158, 160));

		btnEnviar = new JButton(">");
		btnEnviar.setToolTipText("Enviar mensagem");
		btnEnviar.addActionListener(this);
		btnEnviar.addKeyListener(this);
		btnEnviar.setBackground(new Color(95, 158, 160));

		btnSair = new JButton("Desconectar");
		btnSair.setToolTipText("Desconectar do chat");
		btnSair.addActionListener(this);
		btnSair.addKeyListener(this);
		btnSair.setBackground(new Color(95, 158, 160));

		JScrollPane scroll = new JScrollPane(txtArea);
		txtArea.setLineWrap(true);

		txtArea.setBorder(BorderFactory.createEtchedBorder(Color.black, Color.black));
		txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.black, Color.black));
		this.setTitle(txtCliente.getText());
		pnlContent.add(lblChat);
		pnlContent.add(scroll);
		pnlContent.add(btnLimpaHistorico);
		pnlContent.add(lblMensagem);
		pnlContent.add(txtMsg);
		pnlContent.add(btnEnviar);
		pnlContent.add(btnSair);
		pnlContent.setBackground(Color.cyan.darker().darker());

		setContentPane(pnlContent);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(350, 440);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	
	//método que estabelece a conexão entre o cliente e o servidor
	public void connect() throws NumberFormatException, UnknownHostException, IOException {
		Random rdm = new Random();
		String[] msgAleatory = { " acabou de entrar", " caiu de paraquedas", " chegou nesse chat", " entrou no chat",
				" chegô, tá preparde pra atacar" };

		int i = rdm.nextInt(4);

		socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
		ou = socket.getOutputStream();
		writer = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(writer);
		bfw.write(txtCliente.getText() + "\r\n");
		txtArea.append(txtCliente.getText() + msgAleatory[i]+"\r\n");
		bfw.flush();
	}

	//método utilizado para o envio de mensagens do cliente
	public void sendMsg(String msg) throws IOException {
		if (msg.equals("Desconectar")) {
			txtArea.append(txtCliente.getText() + " saiu (._. )");
			bfw.write("Desconectado! ( ._.)");
			
		} else {
			bfw.write(msg + "\r\n");
			txtArea.append(txtCliente.getText() + ": " + txtMsg.getText() + "\r\n");
		}
		bfw.flush();
		txtMsg.setText("");
	}

	//método que aguarda ("escuta") as mensagens vindas do servidor
	public void listener() throws IOException {
		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"Desconectar".equalsIgnoreCase(msg)) {
			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equals("Desconectar"))
					txtArea.append("O servidor caiu!");
				else
					txtArea.append(msg + "\r\n");
			}
		}
	}

	//método alusivo à desconexão, que fecha todas as "streams" e o socket
	public void sair() throws IOException {
		sendMsg("Desconectar");
		bfw.close();
		writer.close();
		ou.close();
		socket.close();
	}

	//conjunto de operações realizadas pelos botões
	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getActionCommand().equals(btnEnviar.getActionCommand()))
				sendMsg(txtMsg.getText());
			else if(e.getActionCommand().equals(btnLimpaHistorico.getActionCommand()))
				txtArea.setText("");
			else if(e.getActionCommand().equals(btnSair.getActionCommand()))
		        sair();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	//método que envia mensagem ao pressionar a tecla enter
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
		       try {
		          sendMsg(txtMsg.getText());
		       } catch (IOException e1) {
		           // TODO Auto-generated catch block
		           e1.printStackTrace();
		       }
		   }

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws IOException {
		Client c = new Client();
		//inicia a conexão
		c.connect();
		//"escuta" as mensagens advindas do servidor e as insere no "txtArea"
		c.listener();
	}

}
