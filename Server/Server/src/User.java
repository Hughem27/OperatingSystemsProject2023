public class User {

	String name, ppsn, email, pass, address;
	double balance;
	
	public User(String name, String ppsNumber, String email, String pass, String address, double balance) {
		this.name = name;
		this.ppsn = ppsNumber;
		this.pass = pass;
		this.address = address;
		this.email = email;		
		this.balance = balance;
	}

	@Override
	public String toString() {
		return name + " " + ppsn + " " + email + " " + pass + " " + address + " " + balance + "\n";
	}

	// Return methods
	public String name() {
		return name;
	}

	public String ppsn() {
		return ppsn;
	}

	public String email() {
		return email;
	}

	public String pass() {
		return pass;
	}

	public String address() {
		return address;
	}

	public double balance() {
		return balance;
	}
}
