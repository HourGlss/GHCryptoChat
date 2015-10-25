package shared;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.PublicKey;
import java.util.Random;

import client.P2PClient;
/*
 * What the server needs for each client. I keep it very simple.
 */
public class Client implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6211744576031189024L;
	private String displayName = null;
	private Random random = new Random();
	private int uid = showRandomInteger(random);
	private PublicKey pub= null;
	private InetAddress ip= null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private Channel channel;
	private RSA rsa = new RSA();

	private int showRandomInteger(Random aRandom){

		long range = (long)999999 - (long)100000 + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long)(range * aRandom.nextDouble());
		int randomNumber =  (int)(fraction + 100000);   
		return randomNumber;
	}
	
	public Client(){
	
	}

	public Client(InetAddress ip,String displayName,PublicKey publicKey){
		this.ip = ip;
		this.displayName = displayName;
		this.pub = publicKey;
	}



	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public PublicKey getPublicKey() {
		return pub;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.pub = publicKey;
	}

	public String getDisplayName(){
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public ObjectInputStream getIn() {
		return in;
	}

	public void setIn(ObjectInputStream in) {
		this.in = in;
	}

	public ObjectOutputStream getOut() {
		return out;
	}

	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	@Override
	public String toString() {
		return "Client [displayName=" + displayName + ", pub=" + pub.toString() + 
				", ip=" + ip.toString() + ", out=" + out.toString() + 
				", in=" + in.toString()	+ ", channel=" + channel.toString() + "]";
	}

	public boolean equals(Object obj) {
		System.out.println("Client.equals()");
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}

	public int getUID() {
		return uid;
	}
	public void setUID(Integer i) {
		this.uid = i;
	}
	/**
	 * @deprecated use getChannel() instead.
	 * @return
	 */
	public Channel getCurrentChannel() {
		return channel;
	}

	/**
	 * @deprecated use setChannel() instead.
	 * @param currentChannel
	 */
	public void setCurrentChannel(Channel currentChannel) {
		this.channel = currentChannel;
	}

	public RSA getRSA() {
		return rsa;
	}

	public void setRSA(RSA rsa) {
		this.rsa = rsa;
	}
}
