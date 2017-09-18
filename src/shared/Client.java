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
public class Client implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6211744576031189024L;
    private String displayName = null;
    private Random random = new Random();
    private int uid = showRandomInteger(random);

    private InetAddress ip = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Channel channel;
    private RSA rsa = new RSA();
    private PublicKey pub = rsa.getPublicKey();

    private int showRandomInteger(Random aRandom) {

        long range = (long) 999999 - (long) 100000 + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + 100000);
        return randomNumber;
    }

    public Client() {

    }

    public Client(InetAddress ip, String displayName, PublicKey publicKey) {
        this.ip = ip;
        this.displayName = displayName;
        this.pub = publicKey;
    }


    public Client(Client i) {
        this.displayName = i.displayName;
        this.uid = i.uid;
        this.pub = i.pub;
        this.ip = i.ip;
        this.channel = i.channel;

        this.rsa = null;
        this.random = null;
        this.in = null;
        this.out = null;
    }

    @Override
    public String toString() {
        if (rsa != null) {
            return "Client [displayName=" + displayName + ", random=" + random + ", uid=" + uid + ", pub=" + pub.toString() + ", ip="
                    + ip.toString() + ", out=" + out.toString() + ", in=" + in.toString() + ", channel=" + channel.toString() + ", rsa=" + rsa.toString() + "]";
        } else {
            return "Client [displayName=" + displayName + ", uid=" + uid + ", pub=" + pub + ", ip="
                    + ip + ", channel=" + channel + "]";
        }

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

    public String getDisplayName() {
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

    public boolean equals(Object obj) {
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
     * @return
     * @deprecated use getChannel() instead.
     */
    public Channel getCurrentChannel() {
        return channel;
    }

    /**
     * @param currentChannel
     * @deprecated use setChannel() instead.
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
