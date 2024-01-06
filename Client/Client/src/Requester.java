
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Requester{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	String message;
 	String response;
	boolean validate;
 	Scanner input;
	Requester(){
		
		input = new Scanner(System.in);
	}
	void run()
	{
		int cont = 0;
		double balance;		

		try{
			// creating a socket to connect to the server
			
			requestSocket = new Socket("127.0.0.1", 2004);
			System.out.println("Connected to localhost in port 2004");
			// get i/o streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			try 
			{
				do {

					//	reads in user messages and responses
					message = (String)in.readObject();
					System.out.println(message);
					response = input.next();
					cont = Integer.parseInt(response);
					sendMessage(response);
					
					if(response.equalsIgnoreCase("1"))
					{
                        // Register
						message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); // name

                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	ppsn

                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	email

                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	password

                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	address

                        message = (String)in.readObject();
                        System.out.println(message);
                        balance = input.nextDouble(); //	balance
                        sendMessage(""+balance);
                        
                        message = (String)in.readObject();
                        System.out.println(message);
					}
					else if(response.equalsIgnoreCase("2"))
					{
                        // 	login
                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	email

                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	password

                        message = (String)in.readObject();
                        System.out.println(message);

                       
					}
                    else if(response.equalsIgnoreCase("3"))
                    {
						// lodge to own account
						message = (String)in.readObject();
						System.out.println(message);
						sendMessage(input.next()); // ppsn validation

						message = (String)in.readObject();
						System.out.println(message);
						balance = input.nextDouble(); //	balance
						sendMessage(""+balance);

						message = (String)in.readObject();
						System.out.println(message);
						
                    }

					else if(response.equalsIgnoreCase("4"))
					{
                        // 	list users
						message = (String)in.readObject(); //	list users
						System.out.println(message);

						message = (String)in.readObject(); //	sending back result
						System.out.println(message);
					}
                    else if(response.equalsIgnoreCase("5"))
                    {
                        // 	transfer to another account
						message = (String)in.readObject();
						System.out.println(message);
						sendMessage(input.next()); // ppsn validation

						message = (String)in.readObject();
						System.out.println(message);
						sendMessage(input.next()); // ppsn validation

						message = (String)in.readObject();
						System.out.println(message);
						sendMessage(input.next()); // email validation

						message = (String)in.readObject();
						System.out.println(message);
						balance = input.nextDouble(); //	return balance
						sendMessage(""+balance);

						message = (String)in.readObject();
						System.out.println(message);

                    }
                    else if(response.equalsIgnoreCase("6"))
                    {
						// view all transactions
						message = (String)in.readObject();
						System.out.println(message); //	transactions

						message = (String)in.readObject();
						System.out.println(message); // read in result
                    }
                    else if(response.equalsIgnoreCase("7"))
                    {
                        // update Password
                        message = (String)in.readObject(); 
                        System.out.println(message);
                        sendMessage(input.next()); // ppsn validation

                        message = (String)in.readObject();
                        System.out.println(message);
                        sendMessage(input.next()); //	update password

                        message = (String)in.readObject();
                        System.out.println(message);
                    }
					else if (!response.equalsIgnoreCase("-1"))
					{
						message = (String)in.readObject();
						System.out.println(message);
					}
				}while(cont != -1);
				message = (String)in.readObject(); //	termination
				System.out.println(message);
			} 
			
			
			catch (ClassNotFoundException e) {				
				e.printStackTrace();
			}				
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//	close connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client> " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	void sendBoolean(Boolean bool)
	{
		try{
			out.writeObject(bool);
			out.flush();
			System.out.println("validate> " + bool);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		Requester client = new Requester();
		client.run();
	}
}
