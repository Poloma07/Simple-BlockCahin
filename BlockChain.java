package mypackage;
import java.util.*;
import java.security.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.crypto.Cipher;
/*javax.crypto.Cipher类提供加密和解密功能*/
public class BlockChain {
	
	/*SHA方法-生成hash值*/
    /*参考https://gist.github.com/avilches/750151*/
    public static String Hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    
    /*byte字节流向hex转换*/
    public static String bytesToHex(byte[]Bs) {
    	StringBuffer res=new StringBuffer();
    	for(byte byt:Bs) {
    		String hex=Integer.toHexString(byt&0xff);
    		if(hex.length()<2) {
    			res.append(0);
    		}
    		res.append(hex);
    	}
    	return res.toString();	
    }
	
    
/*RSA方法*/
/*生成私钥与公钥*/
/*https://gist.github.com/dmydlarz/32c58f537bb7e0ab9ebf*/
public static  KeyPair buildKeyPair() throws NoSuchAlgorithmException {
    final int keySize = 2048;
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(keySize);
    return keyPairGenerator.genKeyPair();
}

/*公钥加密信息字符串*/
/*明文由string转为byte[]字节流，再进行加密操作*/
public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);

    return cipher.doFinal(message.getBytes());
}

/*私钥解密字节流信息*/
public static byte[] decrypt(PrivateKey privateKey, byte [] encrypted) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);

    return cipher.doFinal(encrypted);
}

/*用私钥生成数字签名*/
/*https://gist.github.com/LuisMichaelis/53c40a1681607e758d4e65b85f210117*/
/*byte[]明文*/
public  static byte[] sign(PrivateKey key, byte[] data) {
    try {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(data);

        return signature.sign();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Unexpected: No RSA algorithm found!", e);
    } catch (SignatureException | InvalidKeyException e) {
        throw new RuntimeException("Error signing some data!", e);
    }
}
/*字符串明文*/
public static String sign(PrivateKey key, String data) {
	//base编码
    return Base64.getEncoder().encodeToString(sign(key, data.getBytes(UTF_8)));
}

/*公钥校验*/
public static boolean verify(PublicKey key, byte[] data, byte[] signs) { //传输数字签名与信息
    try {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(key);
        signature.update(data);
        return signature.verify(signs);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Unexpected: No RSA algorithm found!", e);
    } catch (SignatureException | InvalidKeyException e) {
        throw new RuntimeException("Error verifying some data!", e);
    }
}
public static boolean verify(PublicKey key, String data, String signs) {
    return verify(key, data.getBytes(UTF_8), Base64.getDecoder().decode(signs));
}



static List<Block> Blocks = new ArrayList<Block>();//创建区块链
static Map<String,Wallet> wallets = new HashMap<String,Wallet>();  //账户名建立键值对

/*创建第一个区块*/
public static void createBlock() throws Exception {
    int nonce = 1; //挖矿次数
    Wallet Sys = new Wallet("__Myacc__");  //建立系统账户
    wallets.put("__Myacc__",Sys);
     String prerand = "0000"+Hash256(Long.toString((new Date()).getTime())).substring(0,60); 
     while(true) {
        String hash = Hash256(prerand+nonce+null+0);
        if (hash.startsWith("0000")) {
            System.out.println("---------结果正确\n---------计算次数为：" +nonce+ "\n------------hash: " + hash+'\n');
            break;
        }
        nonce++;//递增工作量
    }
    Block newBlock = new Block(0,prerand+nonce+null+0,prerand,nonce,null);
    Blocks.add(newBlock);
    System.out.println("创世区块：\n" + newBlock.recString());
}



/*创建钱包*/
public static boolean createWallet(String name){
    if(wallets.get(name) != null){
        System.out.println("账户 " + name + " 已经存在！");
        return false;
    }
    Wallet Acct = new Wallet(name);
    wallets.put(name,Acct);
    return true;
}


/*挖矿*/
public static void Mining(List<Transaction>translist,Wallet miner) throws Exception{
    List<Transaction>newtrans=new ArrayList<Transaction>();
    for(Transaction ts:translist) {
    	if(ts.getReceiver()==ts.getTransfer())
    		{System.out.println("不能转账给自己！\n");
    	continue;
    		}
    	 if(ts.getTransfer().makeTrans(ts.getAmount(),ts.getReceiver())) {
    		 newtrans.add(ts);
    	 }
    	 else {
    		 System.out.println("此笔交易检验不通过！"+ts.toString()+'\n');
    	 }
    }
    try {
	Block newestBlock = Blocks.get(Blocks.size()-1);     //上一块
    if(wallets.get("__Myacc__").makeTrans(10,miner)){ 
        newtrans.add(new Transaction(wallets.get("__Myacc__"),miner,10,"Award for Mining"));
    }
    else{
        System.out.println("无法完成交易!\n"); 
        }
    int nonce = 1;
    //String hash = "";
   // StringBuffer Pre=new StringBuffer("1111");
    while(true){
       //String hash=Hash256(newestBlock.gethash());
        String hash = Hash256((newestBlock.getindex()+1)+newestBlock.gethash()+newtrans+nonce); //更新随机数
        if (hash.startsWith("0000")) {
            System.out.println("---------结果正确\n---------计算次数为：" +nonce+ "\n-----------hash: " + hash);
            //Pre.replace(0,3,hash);
            break;
        }
        nonce++;
        //System.out.println("+1");
    }
    //String pre=new String(Pre);
    Block newBlock = new Block(newestBlock.getindex()+1,(newestBlock.getindex()+1)+newestBlock.gethash()+newtrans+nonce,newestBlock.gethash(),nonce,newtrans);
    Blocks.add(newBlock);  //挖到矿，把新区块加入链
    System.out.println("新区块信息：\n" + newBlock.recString());//newblock.toString()
    }
    catch(Exception e) {
    	System.out.println(e);
    }
   }


/*检验区块链*/
public static boolean isChainValid(List<Block>Bs) {
	
	String preHash=Bs.get(0).gethash();
	try {
	for(int i=1;i<Bs.size();i++) { 
		//if(i.gethash()==(BlockChain.Hash256())
		/*if(!(Bs.get(i).gethash().equals(hash))){
			//System.out.println(Bs.get(i).gethash()+'\n'+Hash256(calHash)+'\n');
			System.out.println("Error 1!");
			return false;//自身hash验证
		}*/
		if(Bs.get(i).getprehash()!=preHash) {System.out.println("Error!");return false;}//前后区块验证
		preHash=Bs.get(i).gethash();
		//if(i+1<Bs.size()) {
		//calHash=Integer.toString(Bs.get(i).getindex()+1)+Bs.get(i+1).gettrans()+Bs.get(i).gethash()+Bs.get(i+1).getnonce();
	}
	}
	catch (Exception e) {
		System.out.println(e);
	}
	/*计算值未检验*/
	return true;
}

/*演示*/
public static void main(String args[]) {
	 try{

         createBlock();
         createWallet("Yuzz");
         createWallet("Dazzi");

         List<Transaction> cbA = new ArrayList<Transaction>();
         List<Transaction> cbB = new ArrayList<Transaction>();
         cbA.add(new Transaction(wallets.get("__Myacc__"),wallets.get("Yuzz"),10,"Hello Yuzz!"));
         cbA.add(new Transaction(wallets.get("Yuzz"),wallets.get("Dazzi"),5,"Hi Dazzi!"));
         cbB.add(new Transaction(wallets.get("Yuzz"),wallets.get("Yuzz"),11.2,"Banknote"));
         cbB.add(new Transaction(wallets.get("Dazzi"),wallets.get("Yuzz"),2.45,"juice"));
         Mining(cbA,wallets.get("Yuzz"));
         Mining(cbB,wallets.get("Dazzi"));
                  
         //List<Transaction> cbB = new ArrayList<Transaction>();
         //System.out.println("最后一个区块的信息:\n" + Blocks.get(Blocks.size()-1).recString());
         System.out.println("系统账户余额: \t" + wallets.get("__Myacc__").getbalance());
         System.out.println("Yuzz账户余额: \t" + wallets.get("Yuzz").getbalance());
         System.out.println("Dazzi账户余额:\t" + wallets.get("Dazzi").getbalance());
         
         
         if(isChainValid(Blocks))System.out.println("正常的区块链！\n");
         else System.out.println("存在伪区块链！\n");
         
     }
     catch(Exception e){
         System.out.println(e);
     }
}
}
