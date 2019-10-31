package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

public class Client {
	
	private static ClientThread clientThread;
	public static String usuario = "Usuario";
	public static Socket SOCKET;
	public static ObjectOutputStream output;
	
	public static JFrame janelaPrincipal = new JFrame();
	public static JPanel painel = new JPanel();

	public static JPanel barraTopo = new JPanel();
	public static JLabel topo = new JLabel();
	public static JPanel PLAFContainer = new JPanel();
	public static String[] themeNames;

	public static JPanel listaUsuario = new JPanel();
	@SuppressWarnings("rawtypes")
	public static JList listaUsuariosOnline = new JList();
	public static JScrollPane rolagem = new JScrollPane();
	public static JButton enviar = new JButton();

	public static JPanel textoCentro = new JPanel();
	public static JTextArea mostrarTexto = new JTextArea();
	public static JPanel botaoLabel = new JPanel();
	public static JPanel botaoTexto = new JPanel();
	public static JTextArea tipoTexto = new JTextArea();
	public static JLabel mensagem = new JLabel("Mensagem:");

	public static JFrame janelaLogin = new JFrame();
	public static JPanel painelLogin = new JPanel();
	public static JLabel inserirUsuario = new JLabel("Nome: ");
	public static JTextField caixaLoginUsuario = new JTextField(20);
	public static JButton botaoLogin = new JButton("Conectar");

	public static void Connect(){
		
		try{
			final int port = 5555;
			SOCKET = new Socket(InetAddress.getLocalHost(),port);
			
			clientThread = new ClientThread(SOCKET);
			
			output = new ObjectOutputStream(SOCKET.getOutputStream());
			try{
				output.writeObject(usuario);
				output.flush();
			}catch(IOException ioException){
				JOptionPane.showMessageDialog(null, "Erro - usuario não encontrado!");
			}
			
			topo.setText("Online");
			
			Thread X = new Thread(clientThread);
			X.start();
		}
		catch(Exception x){
			System.out.println(x);
			JOptionPane.showMessageDialog(null, "Servidor não respondendo");
			System.exit(0);
		}
	}

	public static void MontarJanelaPrincipal(){
		janelaPrincipal.setTitle("JavaCompany Chat - " + usuario);
		ConfigurarJanelaPrincipal();
		JanelaPrincipalListener();
		janelaPrincipal.setVisible(true);
	}

	public static void ConfigurarJanelaPrincipal(){
		janelaPrincipal.setContentPane(painel);
		janelaPrincipal.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		janelaPrincipal.setMinimumSize(new Dimension(500,300));
		janelaPrincipal.pack();
		janelaPrincipal.setLocationRelativeTo(null);
		try {
			janelaPrincipal.setLocationByPlatform(true);
			janelaPrincipal.setMinimumSize(janelaPrincipal.getSize());
		} 
		catch(Throwable ignoreAndContinue) {
		}
		
		topo.setText("Offline");
		
		final UIManager.LookAndFeelInfo[] themes = UIManager.getInstalledLookAndFeels();     
        themeNames = new String[themes.length];
        int ii;
        for (ii=0; ii<themes.length; ii++) {
                themeNames[ii] = themes[ii].getName();
        }
        
        PLAFContainer.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        
        barraTopo.setLayout(new BorderLayout(5,5));
		barraTopo.setBorder(new TitledBorder(""));
        barraTopo.add(topo, BorderLayout.WEST);
        barraTopo.add(PLAFContainer, BorderLayout.EAST);
              
        rolagem.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        rolagem.setViewportView(listaUsuariosOnline);
        rolagem.setPreferredSize(new Dimension(130,200));
        rolagem.setMinimumSize(new Dimension(130,200));
        
        enviar.setText("ENVIAR");
        enviar.setPreferredSize(new Dimension(100,100));
        enviar.setMinimumSize(new Dimension(100,30));

		listaUsuario.setLayout(new BorderLayout(5,5));
		listaUsuario.add(rolagem,BorderLayout.CENTER);
		listaUsuario.add(enviar,BorderLayout.SOUTH);
                
        mostrarTexto.setText("");
        mostrarTexto.setBorder(new LineBorder(Color.GRAY));
        mostrarTexto.setEditable(false);

        tipoTexto.setPreferredSize(new Dimension(400,60));
        tipoTexto.setEditable(true);
        tipoTexto.setBorder(new LineBorder(Color.GRAY));
        
        botaoTexto.setLayout(new BorderLayout(5,5));
		botaoTexto.add(new JScrollPane(tipoTexto),BorderLayout.CENTER);
		
        botaoLabel.setLayout(new BorderLayout(5,5));
		botaoLabel.add(botaoTexto,BorderLayout.CENTER);
		botaoLabel.add(mensagem,BorderLayout.WEST);

        textoCentro.setLayout(new BorderLayout(5,5));
        textoCentro.add(new JScrollPane(mostrarTexto), BorderLayout.CENTER);
		textoCentro.add(botaoLabel,BorderLayout.SOUTH);

		painel.setLayout(new BorderLayout(5,5));
		painel.add(barraTopo, BorderLayout.NORTH);
        painel.add(listaUsuario, BorderLayout.EAST);
		painel.add(textoCentro,BorderLayout.CENTER);
	}

	public static void JanelaPrincipalListener(){
		
		janelaPrincipal.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
			       int result = JOptionPane.showConfirmDialog(null, "Você tem certeza?","Confirmar",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

			       	if(result == JOptionPane.YES_OPTION){
			        	try {
							output.close();
							clientThread.in.close();
							SOCKET.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						System.exit(0);
					}
				}
			}
		);
		
		enviar.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					try {
						enviarAction();
					} catch (IOException e1) {
						e1.printStackTrace();
					}					
				}
				
			}
		);
		
		listaUsuariosOnline.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
			        usuarioOnlineAction(e);
				}
			}
		);
	}

	public static void usuarioOnlineAction(MouseEvent e){
		if (e.getClickCount() == 2) {
			final String selecionarUsuario = (String) listaUsuariosOnline.getSelectedValue();
			SwingUtilities.invokeLater(
				new Runnable(){
					public void run() {
						tipoTexto.setText("@" + selecionarUsuario + ": ");
						tipoTexto.requestFocus();
					}
				}
			);
        }
	}
	
	public static void enviarAction() throws IOException{
		if(!tipoTexto.getText().equals("")){
			clientThread.Enviar(tipoTexto.getText());
			tipoTexto.requestFocus();
			tipoTexto.setText("");
		}
	}

	public static void Initialize(){
		enviar.setEnabled(false);
		janelaPrincipal.setEnabled(false);
	}

	public static void janelaLogin(){
		
		janelaLogin.setTitle("Login");
		
		configurarJanelaLogin();
		JanelaLoginAction();
		janelaLogin.setVisible(true);
	}

	public static void configurarJanelaLogin(){
		janelaLogin.setContentPane(painelLogin);
		janelaLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janelaLogin.setMinimumSize(new Dimension(370,90));
		janelaLogin.pack();
		janelaLogin.setLocationRelativeTo(null);
		
		try {
			janelaLogin.setLocationByPlatform(true);
			janelaLogin.setMinimumSize(janelaLogin.getSize());
		} 
		catch(Throwable ignoreAndContinue) {
		}

		painelLogin.setLayout(new FlowLayout());
		painelLogin.add(inserirUsuario);
		painelLogin.add(caixaLoginUsuario);
		painelLogin.add(botaoLogin);
	}

	public static void JanelaLoginAction(){
		botaoLogin.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LoginAction();
				}
				
			}
		);
		
		caixaLoginUsuario.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LoginAction();
				}					
			}
		);
	}

	public static void LoginAction() {
		if(!caixaLoginUsuario.getText().equals("")) {
			usuario = caixaLoginUsuario.getText().trim();
			janelaPrincipal.setTitle("JavaCompany Chat - " + usuario);
			janelaLogin.dispose();
			enviar.setEnabled(true);
			janelaPrincipal.setEnabled(true);
			tipoTexto.requestFocus();
			Connect();
		}
		else {
			JOptionPane.showMessageDialog(null, "Porfavor insira seu nome!");
		}
	}	
}
