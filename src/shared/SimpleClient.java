package shared;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.PublicKey;
/*
 * What the server needs for each client. I keep it very simple.
 */
public class SimpleClient implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6211744576031189024L;
	private String displayName = null;
	private PublicKey pub= null;
	private InetAddress ip= null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private int channelId;
	
	
	public SimpleClient(){

	}

	public SimpleClient(InetAddress ip,String displayName,PublicKey publicKey){
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

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
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
				", in=" + in.toString()	+ ", channelId=" + channelId + "]";
	}

	public boolean equals(Object obj) {
		System.out.println("Client.equals()");
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleClient other = (SimpleClient) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}

}
