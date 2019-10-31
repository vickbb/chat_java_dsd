package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {

	private Server server;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String usuario;
	Object mensagem;
	
	public ServerThread(Server server, Socket socket) throws IOException, ClassNotFoundException {
		this.server = server;
		this.socket = socket;
		output = new ObjectOutputStream(this.socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(this.socket.getInputStream());
		
		usuario = (String) input.readObject();
		
		server.clientes.put(usuario, output);
		server.outputStreams.put(socket, output);
		
		server.enviarParaTodos("!" + server.clientes.keySet());
		
		server.mostrarMensagem("\n" + usuario + "(" + socket.getInetAddress().getHostAddress() + ") está online");
		
		start();
	}
	
	@SuppressWarnings("deprecation")
	public void run(){
		
		try {
			while(true) {
				try{
					mensagem = input.readObject();
				}catch (Exception e){
					stop();
				}
				
				if (mensagem.toString().contains("@EE@"))
					server.enviarParaTodos(mensagem);
				else {
					String formattedMsg = "@" + usuario + mensagem.toString().substring(mensagem.toString().indexOf(':'), mensagem.toString().length());
					server.enviarPrivado(mensagem.toString().substring(1, mensagem.toString().indexOf(':')), formattedMsg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				server.removerCliente(usuario);
				server.fecharConexao(socket, usuario);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
