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

import shared.SimpleClient;

/*
 * This is currently the server. It doesn't act anything like the server I want.
 * Lot's of TODOS
 */
public class P2PServer {
	private static final int PORT = 9001;
	private static List<SimpleClient> clients = new ArrayList<SimpleClient>();

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



	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");

		P2PServer ps = new P2PServer();
		ps.run();

	}

	private static class Handler extends Thread {
		private SimpleClient internalClient;
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
				internalClient = new SimpleClient();
				internalClient.setIp(socket.getInetAddress());
				internalClient.setChannelId(0);
				internalClient.setOut(new ObjectOutputStream(socket.getOutputStream()));
				internalClient.getOut().flush();
				internalClient.setIn(new ObjectInputStream(socket.getInputStream()));

				boolean uniqueClient = true;
				while (true && uniqueClient) {
					System.out.println("Giving user their name");
					String internalClientName = generateDisplayName();
					internalClient.setDisplayName(internalClientName);
					internalClient.getOut().writeObject("NAME"+internalClientName);
					internalClient.getOut().flush();
					System.out.println("Flushed the submit name");
					for(SimpleClient cl : clients) {
						if (cl.getIp().equals(internalClient.getIp())) {
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
						keyGen.initialize(1024);
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
					internalClient.setIp(socket.getInetAddress());
					internalClient.setOut(internalClient.getOut());
					System.out.println(internalClient.getDisplayName() +" has connected");
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
						;
						if(obj1 != null){
							if(obj1.getClass() == input.getClass()) {
								input = (String)obj1;
								if(input.startsWith("CHANNEL")){
									System.out.println("User has sent request to join channel");
									int chanid = Integer.parseInt(input.substring(7, input.length()));

									//if the client is in a channel and joins a channel
									if(internalClient.getChannelId() !=0 ){
										for(SimpleClient c : clients){
											if(c.getChannelId() == internalClient.getChannelId() &&
													!c.getDisplayName().equals(internalClient.getDisplayName())){
												try {
													c.getOut().writeObject("MESSAGE " + internalClient.getDisplayName() + " has left the channel.");
													c.getOut().flush();
												} catch (IOException e) {
													e.printStackTrace();
												}
											}
										}
									}
									System.out.println("changing clients channel to "+chanid);
									internalClient.setChannelId(chanid);
									for(SimpleClient c : clients){
										if(c.getChannelId() == internalClient.getChannelId() &&
												!c.getDisplayName().equals(internalClient.getDisplayName())){
											try {
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
									for (SimpleClient cl: clients) {
										if(cl.getChannelId() !=0 &&
												internalClient.getChannelId() == cl.getChannelId()){

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
				// This client is going down!  Remove its name and its print
				// writer from the sets, and close its socket.
				clients.remove(internalClient);
				for(SimpleClient c : clients){
					if(c.getChannelId() == internalClient.getChannelId()){
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