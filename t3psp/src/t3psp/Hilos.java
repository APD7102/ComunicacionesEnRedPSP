package t3psp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Hilos extends Thread
{
	DataInputStream fentrada;
	Socket socket;
	boolean fin = false;
	public Hilos(Socket socket)
	{
		this.socket = socket;
		try
		{
			fentrada = new DataInputStream(socket.getInputStream());
		}

		catch (IOException e)
		{
			System.out.println("Error de E/S");
			e.printStackTrace();
		}
	}
	// En el método run() lo primero que hacemos
	// es enviar todos los mensajes actuales al cliente que se
	// acaba de incorporar
	public void run()
	{
		Servidor.mensaje.setText("Número de conexiones actuales: " + Servidor.ACTUALES);
		String texto = Servidor.textarea.getText();
		EnviarMensajes(texto);
		// Seguidamente, se crea un bucle en el que se recibe lo que el cliente escribe en el chat.
		// Cuando un cliente finaliza con el botón Salir, se envía un * al servidor del Chat,
		// entonces se sale del bucle while, ya que termina el proceso del cliente,
		// de esta manera se controlan las conexiones actuales
		while(!fin)
		{
			String cadena = "";
			try
			{
				cadena = fentrada.readUTF();
				if(cadena.trim().equals("*"))
				{
					Servidor.ACTUALES--;
					Servidor.mensaje.setText("Conexiones actuales: "
							+ Servidor.ACTUALES);
					fin=true;
				}
				// El texto que el cliente escribe en el chat,
				// se añade al textarea del servidor y se reenvía a todos los clientes
				else
				{
					String[] apuestas = cadena.split(">");
					
					if(apuestas[1].equals(" ")) 
					{
						Servidor.textarea.append("¡" + apuestas[0] + " ha entrado en el juego!" + "\n");
					}
					
					
					else if(!apuestas[1].equals(" ")) 
					{
						
						int numeroApuesta = Integer.parseInt(apuestas[1]);
						
						// Comprobaciones
						if(numeroApuesta > Servidor.numero) 
						{							
							Servidor.textarea.append(apuestas[0]+" piensa que el número es el "+numeroApuesta+"."+" Pero el número es menor a "+numeroApuesta+ ".\n");						
							
						}
						else if(numeroApuesta < Servidor.numero) 
						{
							
							Servidor.textarea.append(apuestas[0]+" piensa que el número es el "+numeroApuesta+"."+" Pero el número es mayor a "+numeroApuesta+ ".\n");							
							
						}
						else if(numeroApuesta == Servidor.numero) 
						{
							Servidor.textarea.append(apuestas[0]+" piensa que el número es el "+numeroApuesta+"."+" y está en lo  cierto"+ "\n");			
							Servidor.textarea.append("Fin de Partida."+ "\n"+ "\n");							
							
							Servidor.textarea.append("Nueva Partida."+ "\n");
							Servidor.random();
							Servidor.mensaje3.setText(Servidor.numero+"");
						}
						
					}
					
						texto = Servidor.textarea.getText();
						EnviarMensajes(texto);
					
					
					
					
				}
			}

			catch (Exception ex)
			{
				ex.printStackTrace();
				fin=true;
			}
		}
	}
	// El método EnviarMensajes() envía el texto del textarea a
	// todos los sockets que están en la tabla de sockets,
	// de esta forma todos ven la conversación.
	// El programa abre un stream de salida para escribir el texto en el socket
	private void EnviarMensajes(String texto)
	{
		for(int i=0; i<Servidor.CONEXIONES; i++)
		{
			Socket socket = Servidor.tabla[i];
			try
			{
				DataOutputStream fsalida = new
						DataOutputStream(socket.getOutputStream());
				fsalida.writeUTF(texto);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}