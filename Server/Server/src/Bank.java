import java.util.ArrayList;
import java.util.List;

public class Bank {
    List<User> users = new ArrayList<>();
	User u;

	public synchronized boolean checkPpsn(String ppsn) {
		for (User u : users) {
			if(u.ppsn().equals(ppsn))
				return false; // already exists
		}
		return true; // unique ppsn 
	}

	public synchronized boolean checkEmail(String email) {
		for (User u : users) {
			if(u.email().equals(email))
				return false; // already exists
		}
		return true; // unique email
	}
	
	//Add Users
	public synchronized void addUser(String name, String ppsn, String email, String pass, String address, double balance) {
		u = new User(name, ppsn, email, pass, address, balance);
		users.add(u);
	}

	//ValidateUser
	public synchronized boolean userValidation(String email, String pass) {
		for (User u : users) {
			if(u.email().equals(email) && u.pass().equals(pass))
				return true;
		}
		return false;
	}

	//Lodge Money
	public synchronized String lodgeFunds(String i, double amount) {
		for (User u : users) {
			if(u.ppsn().equals(i)) {
				u.balance += amount;
				return "Money successfully lodged.";
			}
		}
		return "User not found!";
	}
	
	//Search Users
	public synchronized String searchUser(String i) {
		for (User u : users) {
			if(u.ppsn().equals(i))
				return u.toString();
		}
		
		return "User not found!\n";
	}

	//Transfer Money
	public synchronized String transferFunds(String i, String j, String e, double amount) {
		for (User u : users) {
			if(u.ppsn().equals(i)) {
				for (User b : users) {
					if(b.ppsn().equals(j) && b.email().equals(e)) {
						if(u.balance >= amount) {
							u.balance -= amount;
							b.balance += amount;
							return "Money successfully transferred.";
						}
						else
							return "Insufficient funds!";
					}
				}
				return "User not found!";
			}
		}
		return "User not found!";
	}

	public synchronized String updatePass(String i, String newPass) {
		for (User u : users) {
			if(u.ppsn().equals(i)) {
				u.pass = newPass;
				return "pass successfully updated.";
			}
		}
		return "User not found!";
	}
	
	//List Users
	public synchronized String listUsers() {
		String s = "";
		for (User u : users) {
			s += u.toString();
		}
		return s;
	}
	
	
}
