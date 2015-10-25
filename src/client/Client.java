package client;

import shared.Channel;

/*
 * Everything that the client program needs to run.
 */
public class Client {

	private P2PClient gui = null;
	private boolean needUI = true;
	private String displayName;
	private RSA rsa;
	private Channel currentChannel = null;
	public Client(){
		rsa = new RSA();
		initialize();
	}

	public void initialize(){
		if(needUI){
			this.gui = new P2PClient(this);
			gui.run();
		}
	}

	public void setDisplayName(String n){
		this.displayName = n;
	}
	public String getDisplayName(){
		return displayName;
	}

	public RSA getRSA() {
		return rsa;
	}

	public Channel getCurrentChannel() {
		return currentChannel;
	}

	public void setCurrentChannel(Channel currentChannel) {
		this.currentChannel = currentChannel;
	}
}
