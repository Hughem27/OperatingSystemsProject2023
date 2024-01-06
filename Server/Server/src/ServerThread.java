import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.Scanner;


public class ServerThread extends Thread {

	Socket myConnection;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
    boolean loggedIn = false;
    Bank bank;

    private static Map<String, User> users = new HashMap<>(); // hashmap of users
    private static List<String> currentTransactions = new ArrayList<>(); // transaction list
	
	public ServerThread(Socket s, Bank b)
	{
		myConnection = s;
        this.bank = b;
	}
	
	public void run()
	{
		int cont = 0;

        String ppsn;
        double amount;	
        
		//	Bank b = new Bank();
        User currentUser = null;

        //	read from file
        readUsersFromFile(bank);
		
		try
		{
			out = new ObjectOutputStream(myConnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(myConnection.getInputStream());
		
			//	server communication
			do {
				
				//	main menu - Select an option
                if(!loggedIn)
                    sendMessage("Please select an option\n 1: Register with the system. \n 2: Login. \n -1 to EXIT.");
                else
                    sendMessage("Please select an option\n 1: Register with the system. \n 2: Login. \n 3: Lodge money to the user account. \n 4: Retrieve all registered users. \n 5: Transfer money to another account \n 6: View all transactions \n 7: Update your pass \n -1 to EXIT.");
				
                message = (String)in.readObject();
				cont = Integer.parseInt(message);
			
				switch (message) {
                    case "1":
                        userReg(bank); // user reg
                        break;
                    case "2":
                        userLogin(bank, currentUser); // user login 
                        break;
                    case "3":                        
                        // lodge funds
                        sendMessage("Please enter ppsn for account you wish to lodge money to: ");
                        ppsn = (String)in.readObject();

                        sendMessage("Please enter amount you wish to lodge: ");
                        amount = Double.parseDouble((String)in.readObject());

                        sendMessage(bank.lodgeFunds(ppsn, amount));
                        currentTransactions.add("Lodged " + amount + " to account " + ppsn);
                        break;
                    case "4":
                        listUsers(bank); // List users
                        break;
                    case "5":
                        transferFunds(bank); // Transfer money
                        break;
                    case "6":
                        viewTransactions(currentUser); // View all transactions (for all users)
                        break;
                    case "7":
                        updatePass(bank); // Update pass
                        break;
                    case "-1":
                        sendMessage("Closing connection."); // Close connection
                        break;
                    default:
                        sendMessage("Please enter a valid option."); // Invalid option
                        break;
                }
                
			}while(cont != -1);

            //Write to file
            outputUsers();
            transactionOutput();
			
			in.close();
			out.close();
		}
		catch(ClassNotFoundException classnot)
		{
			System.err.println("Data received in unknown format");
		}
		catch(IOException e)
		{
            e.printStackTrace();
		}
	}
    
    private void readUsersFromFile(Bank b) {
        try {
            File file = new File("users.txt");
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split(" ");
                users.put(line[1], new User(line[0], line[1], line[2], line[3], line[4], Double.parseDouble(line[5])));
                b.addUser(line[0], line[1], line[2], line[3], line[4], Double.parseDouble(line[5]));
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void outputUsers() {
        try {
            FileWriter writer = new FileWriter("userList.txt");
            for (User u : users.values()) {
                writer.write(u.toString());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readTransactionsFromFile() {
        String transactions = "";
        
        try {
            File file = new File("transactionList.txt");
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                transactions += sc.nextLine() + "\n";
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private void transactionOutput() {
        try {
            FileWriter writer = new FileWriter("transactionList.txt", true);
            for (String s : currentTransactions) {
                writer.write(s + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void userReg(Bank b) {
        sendMessage("Please enter name: ");
        String name = (String) readObject();

        //Check for unique pps and email
        
        String ppsn;
        do {
            sendMessage("Please enter ppsn: ");
            ppsn = (String) readObject();
            if (b.checkPpsn(ppsn)) {
                break;
            } else {
                sendMessage("This PPSN is already in use. Please choose a different one.");
            }
        } while (true);

        String email;
        do {
            sendMessage("Please enter email: ");
            email = (String) readObject();
            if (b.checkEmail(email)) {
                break;
            } else {
                sendMessage("Email already in use. Please choose a different one.");
            }
        } while (true);        

        sendMessage("Please enter pass: ");
        String pass = (String) readObject();
        sendMessage("Please enter address: ");
        String address = (String) readObject();
        sendMessage("Please enter balance: ");
        double balance = Double.parseDouble((String) readObject());

        b.addUser(name, ppsn, email, pass, address, balance);
        users.put(ppsn, new User(name, ppsn, email, pass, address, balance));
        sendMessage("User details registered");
    }

    private void userLogin(Bank b, User currentUser) {
        if (loggedIn) {
            sendMessage("You are already logged in");
        } else {
            sendMessage("Please enter email for login: ");
            String loginEmail = (String) readObject();
            sendMessage("Please enter pass for login: ");
            String loginpass = (String) readObject();

            if (b.userValidation(loginEmail, loginpass)) {
                loggedIn = true;

                // set current user
                for (User u : users.values()) {
                    if (u.email().equals(loginEmail) && u.pass().equals(loginpass)) {
                        currentUser = u;
                    }
                }
                sendMessage(currentUser.name + "Logged-In successfully!");
            } else {
                sendMessage("Login failed!");
                loggedIn = false;
            }
        }
    }

    private void lodgeFunds(Bank b, User currentUser) {
        try {
            sendMessage("Please enter amount you wish to lodge: ");
            double amount = Double.parseDouble((String)in.readObject());
            if (amount <= 0) {
                sendMessage("The amount must be positive.");
                return;
            }
            sendMessage(b.lodgeFunds(currentUser.ppsn, amount));
            currentTransactions.add("Lodged " + amount + " to account " + currentUser.ppsn);
        } catch (NumberFormatException e) {
            sendMessage("Invalid amount.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void listUsers(Bank b) {
        sendMessage("Listing users...");
        sendMessage(b.listUsers());
    }

    private void transferFunds(Bank b) {
        sendMessage("Please enter ppsn for account you wish to transfer money from: ");
        String ppsn = (String) readObject(); 
        sendMessage("Please enter ppsn for account you wish to transfer money to: ");
        String transferPps = (String) readObject();
        sendMessage("Please enter email for account you wish to transfer money to: ");
        String transferEmail = (String) readObject();

        sendMessage("Please enter amount you wish to transfer: ");
        double amount = Double.parseDouble((String) readObject());

        sendMessage(b.transferFunds(ppsn, transferPps, transferEmail, amount));
        currentTransactions.add("Transferred " + amount + " from account " + ppsn + " to account " + transferPps);
    }

    private void viewTransactions(User currentUser) {
        // view transactions (for all users)
        sendMessage("Viewing all transactions...");
        sendMessage(readTransactionsFromFile());

        
    }

    private void updatePass(Bank b) {
        sendMessage("Please enter pps for account you wish to update: ");
        String ppsn = (String) readObject();
        sendMessage("Please enter new pass: ");
        String pass = (String) readObject();
        sendMessage(b.updatePass(ppsn, pass));
    }

    private Object readObject() {
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	void sendMessage(String msg)
	{
		try{
			out.writeObject("server> "+msg);
			out.flush();
			System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

    void sendBoolean(Boolean bool)
	{
		try{
			out.writeObject("Validate> "+bool);
			out.flush();
			System.out.println("Validate>" + bool);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
}