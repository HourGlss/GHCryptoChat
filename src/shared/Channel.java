package shared;

import java.util.HashSet;

public class Channel implements java.io.Serializable {
    private static final long serialVersionUID = -6475042971272026933L;
    private HashSet<Client> clients = new HashSet<Client>();
    private String title = null;
    private int channelId = 0;
    private int size = 0;

    public Channel(String t, int i) {
        title = t;
        channelId = i;
    }

    public void addClient(Client c) {
        size++;
        this.clients.add(c);
        System.out.println("Added client " + c.getDisplayName());

    }

    public void removeClient(Client c) {
        size--;
        Client[] clientList = clients.toArray(new Client[0]);
        Client temp = null;
        for (int x = 0; x < clientList.length; x++) {
            if (c.getPublicKey().equals(clientList[x].getPublicKey())) {
                temp = clientList[x];
                break;
            }
        }
        clients.remove(temp);
    }

    public String toString() {
        return title;
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

    public int getSize() {
        return size;
    }

}
