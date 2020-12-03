
/**
 * This program is a rudimentary demonstration of Swing GUI programming.
 * Note, the default layout manager for JFrames is the border layout. This
 * enables us to position containers using the coordinates South and Center.
 *
 * Usage:
 *	java ChatScreen
 *
 * When the user enters text in the textfield, it is displayed backwards 
 * in the display area.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame implements ActionListener, KeyListener {
	private JButton sendButton;
	private JButton exitButton;
	private JTextField sendText;
	private JTextArea displayArea;
	private BufferedOutputStream fromClient;
	static String message;

	public ChatClient(BufferedOutputStream fromClient) {
		/**
		 * a panel used for placing components
		 */
		// this.server = server;
		this.fromClient = fromClient;

		JPanel p = new JPanel();

		Border etched = BorderFactory.createEtchedBorder();
		Border titled = BorderFactory.createTitledBorder(etched, "Enter Message Here ...");
		p.setBorder(titled);

		/**
		 * set up all the components
		 */
		sendText = new JTextField(30);
		sendButton = new JButton("Send");
		exitButton = new JButton("Exit");

		/**
		 * register the listeners for the different button clicks
		 */
		sendText.addKeyListener(this);
		sendButton.addActionListener(this);
		exitButton.addActionListener(this);

		/**
		 * add the components to the panel
		 */
		p.add(sendText);
		p.add(sendButton);
		p.add(exitButton);

		/**
		 * add the panel to the "south" end of the container
		 */
		getContentPane().add(p, "South");

		/**
		 * add the text area for displaying output. Associate a scrollbar with this text
		 * area. Note we add the scrollpane to the container, not the text area
		 */
		displayArea = new JTextArea(15, 40);
		displayArea.setEditable(false);
		displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane(displayArea);
		getContentPane().add(scrollPane, "Center");

		/**
		 * set the title and size of the frame
		 */
		setTitle("GUI Demo");
		pack();

		setVisible(true);
		sendText.requestFocus();

		/** anonymous inner class to handle window closing events */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

	}

	/**
	 * Displays a message
	 */
	public void displayMessage(String text) {
		displayArea.append(text + "\n");
	}

	/**
	 * This gets the text the user entered and outputs it in the display area.
	 * 
	 * 
	 */
	public void displayText() throws IOException {
		message = sendText.getText().trim();
		fromClient.write(("MSG " + message + "\r\n").getBytes());
		fromClient.flush();

		sendText.setText("");
		sendText.requestFocus();

	}

	/**
	 * This method responds to action events .... i.e. button clicks and fulfills
	 * the contract of the ActionListener interface.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == sendButton)
			try {
				displayText();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else if (source == exitButton)
			try {
				fromClient.write(("EXIT" + "\r\n").getBytes());
				fromClient.flush();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		// System.exit(0);
	}

	/**
	 * These methods responds to keystroke events and fulfills the contract of the
	 * KeyListener interface.
	 */

	/**
	 * This is invoked when the user presses the ENTER key.
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			try {
				displayText();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}

	/** Not implemented */
	public void keyReleased(KeyEvent e) {
	}

	/** Not implemented */
	public void keyTyped(KeyEvent e) {
	}

	// LOGIC FOR CLIENT GOES HERE
	public static void main(String[] args) {
		Socket server;
		BufferedOutputStream fromClient = null;
		try {
			// opens socket
			server = new Socket(args[0], 63546);
			// change constructor to get the output stream and pass the join in the
			// constructor
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
			fromClient = new BufferedOutputStream(server.getOutputStream());
			ChatClient win = new ChatClient(fromClient);
			// write message to the socket, output stream from socket
			fromClient.write(("JOIN " + args[1] + "\r\n").getBytes());
			fromClient.flush();
			fromClient.write(("JOIN" + "\r\n").getBytes());
			fromClient.flush();

			win.displayMessage("My name is " + args[1]);
			String line = fromServer.readLine();
			String welcome = line.substring(line.indexOf(" ") + 1);
			win.displayMessage(welcome);

			Thread ReaderThread = new Thread(new ReaderThread(fromServer, win));

			ReaderThread.start();
		} catch (UnknownHostException uhe) {
			System.out.println(uhe);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}

	}
}