package mypackage;
/*交易*/
public class Transaction{
	private Wallet transfer;//转帐方
	private Wallet receiver;//接收方
	private double amount;//转账功能
	private String data;//交易的信息
	public Transaction(Wallet ts,Wallet rc,double amt,String sdata) {
		this.transfer=ts;
		this.receiver=rc;
		this.amount=amt;
		this.data=sdata;
	}
	public Wallet getTransfer(){
        return this.transfer;
    }
    public Wallet getReceiver(){
        return this.receiver;
    }
    public double getAmount(){
        return this.amount;
    }
    public String getdata() {
    	return this.data;
    }
    public String toString(){
        return "From: " + this.transfer.getname() + " , To: " + this.receiver.getname() + " , Amount: " + this.amount+" ,Data: "+this.data+'\n';
    }

}

