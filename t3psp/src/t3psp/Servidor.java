package t3psp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Servidor extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	static ServerSocket servidor;
	static final int PUERTO = 44444;
	static int CONEXIONES = 0;
	static int ACTUALES = 0;
	static int MAXIMO = 15;
	static JTextField msg = new JTextField("");
	static JTextField msg2 = new JTextField("");
	static JLabel numeroRNDM = new JLabel("N?mero a adivinar");
	static JTextField msg3 = new JTextField("");
	private JScrollPane scrollpane1;
	static JTextArea textarea;
	JButton salir = new JButton("Salir");	
	static Socket[] tabla = new Socket[MAXIMO];	
	static int numero;
	
	public Servidor()
	{
		// Construimos el entorno gr?fico
		super(" Servidor ");
		setLayout(null);
		msg.setBounds(10, 10, 400, 30);
		add(msg);
		msg.setEditable(false);
		msg2.setBounds(10, 350, 400, 30);
		add(msg2);
		msg2.setEditable(false);
		numeroRNDM.setBounds(415, 58, 120, 30);
		add(numeroRNDM);
		msg3.setBounds(450, 90, 30, 30);
		add(msg3);
		msg3.setEditable(false);
		textarea = new JTextArea();
		scrollpane1 = new JScrollPane(textarea);
		scrollpane1.setBounds(10, 50, 400, 300);
		add(scrollpane1);
		salir.setBounds(420, 10, 100, 30);
		add(salir);
		textarea.setEditable(false);
		// Se ha anulado el cierre de la ventana para que la finalizaci?n
		// del servidor se haga desde el bot?n Salir.		
		// Cuando se pulsa el bot?n se cierra el ServerSocket y
		// finaliza la ejecuci?n
		salir.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	public static void main(String args[]) throws Exception
	{
		// Desde el main se inicia el servidor
		// y las variables y se prepara la pantalla
		servidor = new ServerSocket(PUERTO);
		System.out.println("Servidor iniciado");
		Servidor ventana = new Servidor();
		ventana.setBounds(0, 0, 540, 450);
		ventana.setVisible(true);
		msg.setText("Conexiones actuales: " + 0);
		random();
		msg3.setText(numero+"");
		// Se usa un bucle para controlar el n?mero de conexiones.
		// Dentro del bucle el servidor espera la conexi?n
		// del cliente y cuando se conecta se crea un socket
		while(CONEXIONES < MAXIMO)
		{
			Socket socket;
			try
			{
				socket = servidor.accept();
			}
			catch (SocketException sex)
			{
				// Sale por aqu? si pulsamos el bot?n salir
				break;
			}
			// El socket creado se a?ade a la tabla,
			// se incrementa el n?mero de conexiones
			// y se lanza el hilo para gestionar los msgs
			// del cliente que se acaba de conectar
			tabla[CONEXIONES] = socket;
			CONEXIONES++;
			ACTUALES++;
			Hilos hilo = new Hilos(socket);
			hilo.start();
		}
		

		// Si se alcanzan 15 conexiones o se pulsa el bot?n Salir,
		// el programa se sale del bucle.
		// Al pulsar Salir se cierra el ServerSocket
		// lo que provoca una excepci?n (SocketException)
		// en la sentencia accept(), la excepci?n se captura
		// y se ejecuta un break para salir del bucle
		if(!servidor.isClosed())
		{
			try
			{
				msg2.setForeground(Color.red);
				msg2.setText("M?ximo N? de conexiones establecidas: " +
						CONEXIONES);
				servidor.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			System.out.println("Servidor finalizado");
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==salir)
		{
			try
			{
				servidor.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			System.exit(0);
		}
	}
	
	public static void random() 
	{
		Random r = new Random();
		numero = r.nextInt(100)+1;
	}
}