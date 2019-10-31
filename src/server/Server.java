package server;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server {
	
	JFrame serverFrame;
	JTextArea textAerea;
	private ServerSocket serverSocket;
	private Socket socket;
	public Hashtable<Socket, ObjectOutputStream> outputStreams;
	public Hashtable<String, ObjectOutputStream> clientes;

	public Server(int port) throws IOException{
		serverFrame = new JFrame("Server");
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverFrame.setSize(550, 550);
		textAerea = new JTextArea();
		serverFrame.add(new JScrollPane(textAerea), BorderLayout.CENTER);
		serverFrame.setVisible(true);

		
		outputStreams = new Hashtable<Socket, ObjectOutputStream>();
		clientes = new Hashtable<String, ObjectOutputStream>();
		
		serverSocket = new ServerSocket(port);
		mostrarMensagem("Esperando clientes se conectarem na porta: " + serverSocket);
	}

	public void esperandoClientes() throws IOException, ClassNotFoundException{
		
		while (true){
			socket = serverSocket.accept();
			
			new ServerThread(this, socket);
		}
	}
	
	public void mostrarMensagem(final String mensagem) {
		SwingUtilities.invokeLater(
			new Runnable(){

				@Override
				public void run() {
					textAerea.append(mensagem);
				}
				
			}
		);
	}
	
	public void enviarParaTodos(Object data) throws IOException{
		
		for (Enumeration<ObjectOutputStream> e = getOutputStreams(); e.hasMoreElements(); ){
			//para não remover um cliente ao enviar uma mensagem
			synchronized (outputStreams){
				ObjectOutputStream outputStream = e.nextElement();
				outputStream.writeObject(data);
				outputStream.flush();
			}
		}
	}

	private Enumeration<ObjectOutputStream> getOutputStreams() {
		return outputStreams.elements();
	}
	
	public void enviarPrivado(String usuario, String mensagem) throws IOException {
		ObjectOutputStream outputStream = clientes.get(usuario);
		outputStream.writeObject(mensagem);
		outputStream.flush();
	}

	public void removerCliente(String usuario) throws IOException{
		
		synchronized (clientes){
			clientes.remove(usuario);
			enviarParaTodos("!" + clientes.keySet());
		}
	}
	
	public void fecharConexao(Socket socket, String usuario) throws IOException{
		
		synchronized (outputStreams){
			outputStreams.remove(socket);
		}
		
		mostrarMensagem("\n" + usuario + "(" + socket.getInetAddress().getHostAddress() + ") está offline");
	}

}
