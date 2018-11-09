package edu.stanford.nlp.parser.lexparser.demo;

public class Database {
	private String PIN;
	private String CardNumber;
	private String balance;
	private String amount;
	
	public Database() {
		this.PIN = "";
		this.CardNumber = "";
		this.balance = "";
		this.amount = "";
	}
	
	public void setPin (String pin) {
		this.PIN = pin;
	}
	
	public void setCardNumber(String num) {
		this.CardNumber = num;
	}
	
	public void setBalance (String balance) {
		this.balance = balance;
	}
	
	public void setAmount (String amount) {
		this.amount = amount;
	}
	
	public String getPin() {
		return this.PIN;
	}
	
	public String getCardNum() {
		return this.CardNumber;
	}
	
	public String getBalance() {
		return this.balance;
	}
	
	public String getAmount() {
		return this.amount;
	}
}
