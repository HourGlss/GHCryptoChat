package server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import shared.Channel;
import shared.Client;

public class P2PServer {
	private static final int PORT = 9001;
	private static List<Client> clients = new ArrayList<Client>();
	private static List<Channel> channels = new ArrayList<Channel>();

	private void run() throws IOException{
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(PORT);
			while (true) {
				new Handler(listener.accept()).start();
			}
		}  catch (IOException e) {
			System.out.println("Server already running");
		}finally {
			listener.close();
		}
	}

	private void setupChannels() {
		Channel one = new Channel("Lobby", 1);
		Channel two = new Channel("Not Lobby", 2);
		Channel thr = new Channel("Off-Topic", 3);
		channels.add(one);
		channels.add(two);
		channels.add(thr);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");
		P2PServer ps = new P2PServer();
		ps.setupChannels();
		ps.run();

	}

	private static class Handler extends Thread {
		private Client internalClient;
		private Socket socket;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		private String generateDisplayName(){
			String s = "";
			int min = 33;
			int max = 126;
			for(int x = 0;x<10;x++){
				s+= (char)ThreadLocalRandom.current().nextInt(min, max + 1);
			}
			return s;
		}

		public void run() {
			try {
				System.out.println("Setup the Client");
				internalClient = new Client();
				internalClient.setIp(socket.getInetAddress());
				internalClient.setChannel(null);
				internalClient.setOut(new ObjectOutputStream(socket.getOutputStream()));
				internalClient.getOut().flush();
				internalClient.setIn(new ObjectInputStream(socket.getInputStream()));

				boolean uniqueClient = true;
				while (true && uniqueClient) {
					System.out.println("Giving user their name");
					String internalClientName = generateDisplayName();
					internalClient.setDisplayName(internalClientName);
					internalClient.getOut().writeObject("START"+internalClientName);
					internalClient.getOut().flush();
					//SERIALIZABLE WORKS HERE YESSS!!!
					for(Channel channelToSend : channels){
						internalClient.getOut().writeObject("CHANINFO");
						internalClient.getOut().flush();
						internalClient.getOut().writeObject(channelToSend);
						internalClient.getOut().flush();
					}
					System.out.println("Flushed the submit name");
					for(Client cl : clients) {
						if (cl.getIp().equals(internalClient.getIp())) {
							//TODO THIS IS DISABLED FOR TESTING USING LOCALHOST
							//uniqueClient = false;
						}
					}
					if(uniqueClient){
						System.out.println("Client is a unique client");
						break;
					}else{
						internalClient.getOut().writeObject("FAIL");
						internalClient.getOut().flush();
						System.out.println("Client is not a unique client");
					}
				}
				if(uniqueClient){
					// Now that a successful name has been chosen, add the
					// socket's print writer to the set of all writers so
					// this client can receive broadcast messages.
					System.out.println("Asking for RSA pub key");
					internalClient.getOut().writeObject("RSAPUB");
					internalClient.getOut().flush();
					KeyPairGenerator keyGen;
					PublicKey pub = null;
					try {
						keyGen = KeyPairGenerator.getInstance("RSA");
						keyGen.initialize(2048);
						KeyPair key = keyGen.generateKeyPair();
						pub = key.getPublic();
					}catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					Object obj = null;
					try {
						obj = internalClient.getIn().readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					if(obj != null){
						if(obj.getClass() == pub.getClass()) {
							pub = (PublicKey) obj;
							internalClient.setPublicKey(pub); 
						}
					}
					Integer uniqueID = 0;
					Object objectToCompare = null;
					try {
						objectToCompare = internalClient.getIn().readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					if(objectToCompare != null){
						if(objectToCompare.getClass() == uniqueID.getClass()) {
							uniqueID = (int)objectToCompare;
							System.out.println("Setting Clients UID to "+uniqueID);
							internalClient.setUID(uniqueID);
						}else{
							System.out.println("JK what the fuck "+objectToCompare.getClass());
						}
					}
					internalClient.setIp(socket.getInetAddress());
					internalClient.setOut(internalClient.getOut());

					System.out.println(internalClient.getDisplayName() +" has connected w/ UID "+internalClient.getUID());
					if(clients.size() == 0){
						System.out.println("clients size() is zero");
						//This is the first client
						internalClient.getOut().writeObject("MESSAGE you are the first to connect");
						internalClient.getOut().flush();
					}else{
						System.out.println("clients size() is not zero");
						internalClient.getOut().writeObject("MESSAGE you are NOT the first to connect");
						internalClient.getOut().flush();
					}

					//This is where clients are added to the ArrayList of clients.
					clients.add(internalClient);


					// Accept messages from this client and broadcast them.
					// Ignore other clients that cannot be broadcasted to.
					while (true) {
						String input = "";
						Object obj1 = null;
						try {
							obj1 = internalClient.getIn().readObject();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}

						if(obj1 != null){
							if(obj1.getClass() == input.getClass()) {
								input = (String)obj1;
								if(input.startsWith("CHANNEL")){
									System.out.println("User has sent request to join channel");
									int chanid = Integer.parseInt(input.substring(7, input.length()));
									Channel channelToChangeTo = null;
									for(Channel channel : channels){
										if(channel.getChannelId() == chanid){
											channelToChangeTo = channel;
											Client toAdd = new Client(internalClient);
											channelToChangeTo.addClient(toAdd);
											break;
										}
									}
									if(channelToChangeTo == null){
										//Security?
										//TODO I'm sure this isn't smart
										socket.close();
									}
									internalClient.setChannel(channelToChangeTo);
									//2 things to do
									//Send internal client details to all clients currently in the channel
									//send all details from currently connected clients to internal client
									//if the client is in a channel and joins a channel
									System.out.println("changing clients channel to "+channelToChangeTo.getChannelId());

									for(Client c : clients){
										if(c.getChannel() != null && c.getChannel().getChannelId() == internalClient.getChannel().getChannelId() &&
												!c.getDisplayName().equals(internalClient.getDisplayName())){
											try {
												c.getOut().writeObject("CLIENT");
												c.getOut().flush();
												Client toSend = new Client(internalClient);
												c.getOut().writeObject(toSend);
												c.getOut().flush();
												c.getOut().writeObject("MESSAGE " + internalClient.getDisplayName() + " has joined the channel.");
												c.getOut().flush();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
								}
								if(input.startsWith("MESSAGE")){
									System.out.println("User has sent incoming message");
									for (Client cl: clients) {
										if(cl.getChannel() != null && cl.getChannel().getChannelId() !=0 &&
												internalClient.getChannel().getChannelId() == cl.getChannel().getChannelId()){

											cl.getOut().writeObject("MESSAGE " + internalClient.getDisplayName() + ": " + input.substring(7));
											cl.getOut().flush();
										}
									}
								}
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Making a new socket failed Somewhere\n");
				e.printStackTrace();
			} finally {
				// This client is going down!
				clients.remove(internalClient);
				if(internalClient.getChannel() != null){
					System.out.println("Removing client from channel "+internalClient.getChannel());
					internalClient.getChannel().removeClient(internalClient);
					for(Channel c : channels){
						System.out.println(c + " "+c.getSize());
					}
				}
				for(Client c : clients){
					if(c.getChannel().getChannelId() == internalClient.getChannel().getChannelId()){
						try {
							c.getOut().writeObject("MESSAGE " + internalClient.getDisplayName() + " has disconnected from server.");
							c.getOut().flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Failed to close the socket");
					e.printStackTrace();
				}
			}
		}
	}
}