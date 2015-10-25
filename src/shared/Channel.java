package shared;

import java.util.HashSet;

/**
 * TODO make these SEND to the client.... Not really that important right now.
 *
 */
public class Channel implements java.io.Serializable{
	private static final long serialVersionUID = -6475042971272026933L;
	private HashSet<SimpleClient> clients = new HashSet<SimpleClient>();
	private String title = null;
	private int channelId = 0;
	
	public Channel(String t, int i){
		title = t;
		channelId=i;
	}
	
	public void addClient(SimpleClient c){
		this.clients.add(c);
		System.out.println("Added client "+c.getDisplayName());
		// TODO make sure to notify all other clients in channel
	}
	
	public void removeClient(SimpleClient c){
		SimpleClient[] clientList = clients.toArray(new SimpleClient[0]);
		SimpleClient temp = null;
		for(int x = 0; x<clientList.length;x++){
			if(c.getPublicKey().equals(clientList[x].getPublicKey())){
				temp = clientList[x];
				break;
			}
		}
		clients.remove(temp);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getChannelId() {
		return channelId;
	}
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	
}
