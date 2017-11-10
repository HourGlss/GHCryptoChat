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
        String ret = "Client [";
        if (displayName != null) {
            ret += "displayName=" + displayName + " ";
        } else {
            ret += "displayName=null ";
        }
        if (random != null) {
            ret += "random=" + random + " ";
        } else {
            ret += "random=null ";
        }
        if (uid != 0) {
            ret += "uid=" + uid + " ";
        } else {
            ret += "uid=0 ";
        }
        if (pub != null) {
            ret += "pub=" + pub.toString() + " ";
        } else {
            ret += "pub=null ";
        }
        if (ip != null) {
            ret += "ip=" + ip.toString() + " ";
        } else {
            ret += "ip=null ";
        }
        if (out != null) {
            ret += "out=" + out.toString() + " ";
        } else {
            ret += "out=null ";
        }
        if (in != null) {
            ret += "in=" + in.toString() + " ";
        } else {
            ret += "in=null ";
        }
        if (channel != null) {
            ret += "channel=" + channel.toString() + " ";
        } else {
            ret += "channel=null ";
        }
        if (rsa != null) {
            ret += "rsa=" + rsa.toString() + " ";
        } else {
            ret += "rsa=null ";
        }
        ret += "]";
        return ret;
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
