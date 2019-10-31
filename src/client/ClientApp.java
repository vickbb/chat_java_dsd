package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class ClientApp {
	public static void main(String[] args) throws UnknownHostException, IOException{
		Client.MontarJanelaPrincipal();
		Client.Initialize();
		Client.janelaLogin();
	}
}
