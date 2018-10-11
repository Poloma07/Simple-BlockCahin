package mypackage;
import java.security.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Wallet{

	/*钱包账户*/
		private PublicKey pubkey;//公钥
		private PrivateKey prikey;//私匙
		private String name;//账号名称
		private double balance;//账户的余额
		//BlockChain bc=new BlockChain();
		/*构造账户函数*/
		public Wallet(String logn){
	        try{
	        	this.name = logn;
	            this.balance = 0.0;
	            if(this.name == "__Myacc__")
	            	this.balance=1000.0;
	            	//this.balance = Double.POSITIVE_INFINITY; 
	            ////@SuppressWarnings("unchecked")
	            KeyPair keyPair =BlockChain.buildKeyPair();
	            // 获取公钥
	            this.pubkey = keyPair.getPublic();
	            // 获取私钥
	            this.prikey = keyPair.getPrivate();
	        }
	        catch(Exception e){
	            System.out.println(e);
	        }
	    }
		public PublicKey getpubk() {
			return this.pubkey;
		}
		public PrivateKey getprik() {
			return this.prikey;
		}
		public String getname() {
			return this.name;
		}
		public double getbalance() {
			return this.balance;
		}
		
	    
	    /*发送方--创建交易信息*/
	    public boolean makeTrans(double amt,Wallet receiver){  
	    	if(amt <= 0.0) return false; 
	        if(amt > this.balance) return false;
	        boolean res = false;
	        try{
	        //@SuppressWarnings("unchecked")
	        byte[] encryp =BlockChain.encrypt(receiver.getpubk(),Double.toString(amt));//加密信息
	        byte[] dataSign=BlockChain.sign(this.prikey,Double.toString(amt).getBytes(UTF_8));//数字签名
	        res = receiver.verifyTrans(this,encryp,dataSign);//验证交易
	        if(res) this.balance -= amt; //扣除金额
	        }
	        catch(Exception e){
	            System.out.println(e);
	        }
	        return res;
	    }
	    
	    /*接收方--检测交易信息*/
	    public boolean verifyTrans(Wallet Sender,byte[] encryp,byte[] dataSign){
	        boolean verifys = false;
	        try{
	        	//BlockChain bc=new BlockChain();其他类的静态方法调用 
	        	//@SuppressWarnings("unchecked")
	            byte[] data =BlockChain.decrypt(this.prikey,encryp);  //解密信息
	            verifys =BlockChain.verify(Sender.getpubk(),data,dataSign);  //用发送方的公钥解密签名并对照
	            if(verifys) this.balance += Double.valueOf(new String(data));  //加入信息中的金额
	        
	        }
	        catch(Exception e){
	            System.out.println(e);
	        }
	        return verifys;
	    }
	}


