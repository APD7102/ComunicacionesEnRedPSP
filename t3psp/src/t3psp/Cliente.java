package t3psp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Cliente extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	Socket socket;
	DataInputStream entrada;
	DataOutputStream salida;
	String nombre;
	static JTextField mensaje = new JTextField();
	private JScrollPane scrollpane;
	static JTextArea textarea;
	JButton boton = new JButton("Enviar");
	JButton desconectar = new JButton("Salir");
	boolean repetir = true;
	static boolean repetir2 = true;
	public Cliente(Socket socket, String nombre)

	{
	
		super("JUGADOR: " + nombre);
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		textarea = new JTextArea();
		scrollpane = new JScrollPane(textarea);
		scrollpane.setBounds(10, 50, 400, 300);
		add(scrollpane);
		boton.setBounds(420, 10, 100, 30);
		add(boton);
		desconectar.setBounds(420, 50, 100, 30);
		add(desconectar);
		textarea.setEditable(false);
		boton.addActionListener(this);
		this.getRootPane().setDefaultButton(boton);
		desconectar.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.socket = socket;
		this.nombre = nombre;
		
		/* 
		 
		 Se crean los flujos de entrada y salida.
		 En el flujo de salida se escribe un mensaje
		 indicando que el cliente se ha unido al Chat.
		 El Hilo recibe este mensaje y
		 lo reenvía a todos los clientes conectados
		 
		*/
		try
		{
			entrada = new DataInputStream(socket.getInputStream());
			salida = new DataOutputStream(socket.getOutputStream());
			String texto = nombre +">"+" ";
			salida.writeUTF(texto);
		}
		catch (IOException ex)
		{
			System.out.println("Error de E/S");
			ex.printStackTrace();
			System.exit(0);
		}
	}

	/* 
		El método main es el que lanza el cliente,
		para ello en primer lugar se solicita el nombre o nick del
	 	cliente, una vez especificado el nombre
	 	se crea la conexión al servidor y se crear la pantalla del Chat
	 	
	 
	*/
	public static void main(String[] args) throws Exception
	{
		do {
			int puerto = 44444;
			String nombre = JOptionPane.showInputDialog("Introduce tu nickname:");
			if(!nombre.trim().equals("")) 
			{
				Socket socket = null;
				try
				{
					socket = new Socket("127.0.0.1", puerto);
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:1>>", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}

				Cliente cliente = new Cliente(socket, nombre);
				cliente.setBounds(0,0,540,400);
				cliente.setVisible(true);
				cliente.ejecutar();
				repetir2 = false;

			}		
			else
			{
				JOptionPane.showMessageDialog(null,"El nombre está vacío");
				System.out.println("El nombre está vacío");
			}
		} 
		
		while (repetir2);

	}
	/*
	  
	 Cuando se pulsa el botón Enviar
	 el mensaje introducido se envía al servidor
	 
	*/  
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==boton)
		{
			if (isNumeric(mensaje.getText())) 
			{
				String texto = nombre + ">" + mensaje.getText();

				try
				{
					mensaje.setText("");
					salida.writeUTF(texto);
					boton.setEnabled(false);
					Thread.sleep(5000);
					boton.setEnabled(true);
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				} 
				
				catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else 
			{
				JOptionPane.showMessageDialog(null,"Introduce solo números.");
			}

		}
		/* 
		 Si se pulsa el botón Salir,
		 se envía un mensaje indicando que el cliente abandona el chat 
		 y también se envía un * para indicar al servidor que el cliente se ha cerrado
		 
		*/
		else if(e.getSource()==desconectar)
		{
			String texto = nombre + " ha abandonado el juego " +" >"+" ";
			try
			{
				salida.writeUTF(texto);
				salida.writeUTF("*");
				repetir = false;
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/* 
	 Dentro del método, el cliente lee lo que el
	 hilo le manda y lo muestra en el textarea.
	 Esto se ejecuta en un bucle del que solo se sale
	 en el momento que el cliente pulse el botón Salir y se modifique la variable repetir
	 
	*/
	
	public void ejecutar()
	{
		String texto = "";
		while(repetir)
		{
			try 			
			{
				texto = entrada.readUTF();
				textarea.setText(texto);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(null, "Imposible conectar con	el servidor \n" + ex.getMessage(), "<<Mensaje de Error:2>>", JOptionPane.ERROR_MESSAGE);
				repetir = false;
			}
		}
		try
		{
			socket.close();
			System.exit(0);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private boolean isNumeric(String cadena)
	{
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe){
			return false;
		}
	}
}