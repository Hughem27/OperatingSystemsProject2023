import java.io.*;
import java.net.*;
public class Provider{
	
	
	public static void main(String args[])
	{
		ServerSocket providerSocket;
		try 
		{
			Bank bank = new Bank();

			providerSocket = new ServerSocket(2004, 10);
			
			while(true)
			{
			
				//	connect message
				System.out.println("Waiting for connection");
			
				Socket connection = providerSocket.accept();
				ServerThread T1 = new ServerThread(connection, bank);
				T1.start();
			} 
		}
		
		catch (IOException e1) 
		{
			
			e1.printStackTrace();
		}
		
			
		
	}
	
}
