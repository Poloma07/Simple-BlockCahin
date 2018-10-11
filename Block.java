package mypackage;
import java.util.*;
/*区块类*/
public class Block{
		private int index;//区块索引
		private long timestramp;//时间戳
		private String hash;
		private String prehash;//前一区块hash值
		private int nonce;//计算正确hash的次数，工作量
		private List<Transaction>trans;//交易集合
		
		//构造区块
		public Block(int indexs,String calhash,String pre,int n,List<Transaction>t) {
			try {
				//BlockChain bc=new BlockChain();//静态方法的调用
				this.index=indexs;
				this.prehash=pre;
				this.timestramp=(new Date()).getTime();//当前时间
				this.nonce=n;
				//生成hash的方法
				//@SuppressWarnings("unchecked")
				this.hash=BlockChain.Hash256(calhash);
				this.trans=t;
			}
			catch(Exception e){
				System.out.println("Error!");
			}
		}
		
		public int getindex() {
			return this.index;
		}
		
		public int getnonce() {
			return this.nonce;
		}
		public String getprehash() {
			return this.prehash;
		}
		public String gethash() {
			return this.hash;
		}
		public long gettimestramp() {
			return this.timestramp;
		}
		public List<Transaction>gettrans(){
			return this.trans;
		}
		/*public String calhash() {
			String rechash=Integer.toString(this.index)+trans.toString(this.trans)+;
		}*/
		
		/*打印区块信息*/
		public String recString() {
			String s="Index:\t"+Integer.toString(this.index)+'\n'
					+"Prehash:\t"+this.prehash+'\n'
					+"TimeStramp:\t"+Long.toString(this.timestramp)+'\n'
					+"Nonce:\t"+Integer.toString(this.nonce)+'\n'
					+"Hash:\t"+this.hash+'\n';
			
			if(this.trans!=null) {
				for(Transaction t:this.trans) {
					s+="Transaction:\t"+t.toString()+'\n';
				}
			}
			return s;
		}
	}

	