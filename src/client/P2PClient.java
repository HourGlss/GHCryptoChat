package client;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

import shared.Channel;
import shared.SimpleClient;


/*
 * this is the client Gui. Has everything required. This doesn't support P2P yet at
 * all. Failure.
 */
public class P2PClient extends JPanel implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2598998269263257122L;
	private String ipToUse = "127.0.0.1";
	private Client client;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private JFrame frame = new JFrame("P2P Client");
	private JTextField textField = new JTextField(40);
	private JTextArea messageArea = new JTextArea(8, 40);
	private JList<Channel> list;
	private DefaultListModel<Channel> listModel;
	private JLabel channelName = new JLabel("Join a Channel!");
	private JButton joinButton;
	private static final String joinString = "Join";

	public P2PClient(Client c) {
		client = c;
		//Setup the Gui
		frame.setTitle(frame.getTitle() + " NAME");
		JFrame.setDefaultLookAndFeelDecorated(true);
		textField.setEditable(false);
		textField.setText("Join a channel to begin!");
		messageArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) messageArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		frame.getContentPane().add(textField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");		
		listModel = new DefaultListModel<Channel>();
//		listModel.addElement(one);
//		listModel.addElement(two);
//		listModel.addElement(thr);
		list = new JList<Channel>(listModel);
//		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		list.setSelectedIndex(0);
//		list.addListSelectionListener(this);
//		list.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(list);
		joinButton = new JButton(joinString);
		joinButton.setActionCommand(joinString);
		joinButton.addActionListener(new JoinListener());
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,
				BoxLayout.LINE_AXIS));
		buttonPane.add(joinButton);
		buttonPane.add(channelName);
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		channelName.setVerticalTextPosition(JLabel.BOTTOM);
		frame.add(listScrollPane, BorderLayout.WEST);
		frame.add(buttonPane, BorderLayout.SOUTH);
		frame.pack();
		System.out.println(frame.getComponents().length);

		// Add Listeners for the textField
		textField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.out.println("Sending: "+textField.getText());
				try {
					out.writeObject("MESSAGE"+textField.getText());
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				textField.setText("");
			}
		});
	}

	@SuppressWarnings("unused")
	private String getServerAddress() {
		return JOptionPane.showInputDialog(
				frame,
				"Enter IP Address of the Server:",
				"Welcome to the Chatter",
				JOptionPane.QUESTION_MESSAGE);
	}

	@SuppressWarnings("unused")
	private String getAName() {
		return JOptionPane.showInputDialog(
				frame,
				"Choose a screen name:",
				"Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
	}

	public void run() {
		System.out.println("run");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Make connection and initialize streams
		//ipToUse = getServerAddress();
		Socket socket = null;
		try {
			//Connect to the server
			socket = new Socket(ipToUse, 9001);
			//Setup the streams
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());


			//Get some information on the client.
			//Give the server my RSA pub key
			boolean unique = true;
			while(unique){
				String nameInput = "";
				Object obj = null;
				try {
					obj = in.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if(obj != null){
					if(obj.getClass() == nameInput.getClass()) {

						nameInput = (String)obj;
						if (nameInput.startsWith("START")) {
							System.out.println("Server has assigned my name.");
							client.setDisplayName(nameInput.substring(4));
							frame.setTitle(frame.getTitle() + " "+client.getDisplayName());
						} else if (nameInput.startsWith("CHANINFO")) {
							
							Channel channelToAdd = (Channel)in.readObject();
							listModel.addElement(channelToAdd);
							if(listModel.size() > 0){
								list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								list.setSelectedIndex(0);
								list.addListSelectionListener(this);
								list.setVisibleRowCount(5);
							}
							System.out.println("Adding a channel: "+channelToAdd.toString());
							frame.repaint();
						}else if (nameInput.startsWith("RSAPUB")) {
							System.out.println("Server has asked for my RSAPUB and and I am sending it to server.");
							out.writeObject(client.getRSA().getPublicKey());
							out.flush();
							System.out.println("--sent");
							break;
						}else if(nameInput.startsWith("FAIL")){
							System.out.println("I am not unique.");
							unique = false;
							//frame.dispose();
							break;
						}else{
							System.out.println("Server has sent me the line "+nameInput);
						}
					}
				}
			}
			// Process all messages from server, according to the protocol.
			while(true){
				String line = "";
				Object obj = null;
				try {
					obj = in.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				;
				if(obj != null){
					if(obj.getClass() == line.getClass()) {
						//Get some information on the client.
						line = (String)obj;
						if (line.startsWith("CLIENT")) {
							System.out.println("Server is sending me a client.");
							SimpleClient c = (SimpleClient) in.readObject();
							System.out.println(c.getDisplayName());
						}else if (line.startsWith("MESSAGE")) {
							String message = line.substring(8);
							if(client.getCurrentChannel() != null){
								System.out.println("Server Sent incoming message: "+message);
								messageArea.append(message + "\n");
							}else{
								System.out.println("I am not yet assigned to a channel");  
								messageArea.append(message + "\n");
							}
						}else{							   
							System.out.println("Server sent a weird message to me "+line);
						}
					}else{
						System.out.println("Got an object that wasn't a string");
						System.out.println("obj = "+obj.getClass());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Client can't connect to server");
			//frame.dispose();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try 
			{
				System.out.println("Close that socket");
				socket.close();

			} catch (IOException e) {
			}
		}
	}

	//When you click on the channel list this fires.
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			joinButton.setEnabled(true);
		}

	}
	class JoinListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//This method can be called only if
			//there's a valid selection
			//so go ahead and remove whatever's selected.
			client.setCurrentChannel(list.getSelectedValue());
			channelName.setText(list.getSelectedValue().getTitle());
			textField.setEditable(true);
			//System.out.println("Joining "+client.getCurrentChannel().getTitle() +" - "+client.getCurrentChannel().getChannelId());
			if(out == null){
				System.out.println("out is null");
			}
			if(client == null){
				System.out.println("client is null");
			}
			try {
				out.writeObject("CHANNEL"+client.getCurrentChannel().getChannelId());
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Client();
	}
}