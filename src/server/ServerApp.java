package server;

import java.io.IOException;

public class ServerApp {
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		Server server = new Server(5555);
		server.esperandoClientes();
	}
}
