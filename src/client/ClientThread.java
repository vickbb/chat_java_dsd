package client;

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.util.*;


public class ClientThread implements Runnable{

	Socket SOCKET;
	public ObjectInputStream in;
	String[] usuarios;

	public ClientThread(Socket X){
		this.SOCKET = X;
	}

	@Override
	public void run() {
		try{
			in = new ObjectInputStream(SOCKET.getInputStream());
			ChecarStream();
		}catch(Exception E){
			JOptionPane.showMessageDialog(null, E);
		}
	}

	public void ChecarStream() throws IOException, ClassNotFoundException{
		while(true){
			Receber();
		}
	}

	public void Receber() throws IOException, ClassNotFoundException{
		if(!in.equals(null)){
			String mensagem = (String) in.readObject();
			
			
			if(mensagem.startsWith("!")) {
				String temp1 = mensagem.substring(1);
				temp1 = temp1.replace("[", "");
				temp1 = temp1.replace("]", "");
				
				usuarios = temp1.split(", ");
				Arrays.sort(usuarios);
				
				try {
					SwingUtilities.invokeLater(
						new Runnable(){
							@SuppressWarnings("unchecked")
							public void run() {
								Client.listaUsuariosOnline.setListData(usuarios);
							}
						}
					);
				} 
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Não foi possivel recuperar lista de usuarios online");
				}
			}
			
			else if(mensagem.startsWith("@EE@|")) {
				final String temp2 = mensagem.substring(5);
				
				SwingUtilities.invokeLater(
					new Runnable(){
						public void run() {
							Client.mostrarTexto.append("\n" + temp2);				
						}
					}
				);
			}
			
		
			else if(mensagem.startsWith("@")){
				final String temp3 = mensagem.substring(1);
				
				SwingUtilities.invokeLater(
					new Runnable(){
						public void run() {
							Client.mostrarTexto.append("\n"+temp3);					
						}
					}
				);
			}	
		}
	}
	
	public  void Enviar(final String str) throws IOException{
		String writeStr;
		if(str.startsWith("@")){
			SwingUtilities.invokeLater(
					new Runnable(){

						@Override
						public void run() {
							Client.mostrarTexto.append("\n" + Client.usuario + ": " + str);
						}	
					}
			);
			writeStr = str;
		} 
		else 
			writeStr = "@EE@|" + Client.usuario + ": " + str;
			
			Client.output.writeObject(writeStr);
			Client.output.flush();
	}
}
