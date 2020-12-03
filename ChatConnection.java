import java.net.*;
import java.util.Collection;
import java.util.Iterator;
import java.io.*;

public class ChatConnection implements Runnable {
    private Socket client;
    private java.util.Map<String, BufferedOutputStream> users;
    private String message;

    // sends info from client to server
    BufferedOutputStream toServer;
    // reads info from the client
    // BufferedReader fromClient;
    // sends info from server to client
    BufferedOutputStream toClient;
    // reads info from server
    BufferedReader fromServer;

    public ChatConnection(Socket client, java.util.Map<String, BufferedOutputStream> users) {
        this.client = client;
        this.users = users;
    }

    /**
     * This method runs in a separate thread.
     */

    // input stream from socket and add username to the socket connection
    // adding user name
    // use a read.line to get username from client
    public void run() {
        try {
            process();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
        // have client write the join protocol to server and print it out
        // parse protocol and then parse username to add the username to the socket
        // dictionary
    }

    // client writes join protocol to server
    // must use header and parse username
    // after that have the server read from the client and print out
    // then have server write the protocol and message back to the client

    public void process() throws java.io.IOException {
        String username;

        // logic goes here NO LONGER USING HANDLER
        System.out.println("connection made!");
        // put while loop in at some point

        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedOutputStream toClient = new BufferedOutputStream(client.getOutputStream());

            String line = fromClient.readLine();
            String sub[] = line.split(" ");
            username = sub[1];
            System.out.println(username);

            users.put(username, toClient);

            // while loop to read from the socket
            // if protocol is MSG then split the message like done above

            while (true) {
                line = fromClient.readLine();
                String split[] = line.split(" ");
                String protocol = split[0];
                message = line.substring(line.indexOf(" ") + 1);
                System.out.println(message);

                if (protocol.equals("MSG")) {
                    Collection<BufferedOutputStream> connections = users.values();
                    // now iterate over the different socket connections
                    Iterator<BufferedOutputStream> itr = connections.iterator();
                    // use broadcast message loop here also add username
                    while (itr.hasNext()) {
                        toClient = itr.next();
                        toClient.write(("MSG " + username + ": " + message + "\r\n").getBytes());
                        toClient.flush();
                    }
                    toClient.flush();
                }

                else if (protocol.equals("EXIT")) {
                    users.remove(username);
                    Collection<BufferedOutputStream> connections = users.values();
                    Iterator<BufferedOutputStream> itr = connections.iterator();
                    while (itr.hasNext()) {
                        toClient = itr.next();
                        toClient.write(("EXIT " + username + " has left the chat" + "\r\n").getBytes());
                        toClient.flush();

                    }
                }

                else if (protocol.equals("JOIN")) {
                    Collection<BufferedOutputStream> connections = users.values();
                    Iterator<BufferedOutputStream> itr = connections.iterator();
                    while (itr.hasNext()) {
                        toClient = itr.next();
                        toClient.write(("JOIN " + username + " has joined the chat" + "\r\n").getBytes());
                        toClient.flush();
                    }
                }

            }

            // GET MESSAGING WORKING THEN WORK ON EXIT, remove users from hashmap

            // Collection<BufferedOutputStream> connections = users.values();
            // now iterate over the different socket connections
            // Iterator<BufferedOutputStream> itr = connections.iterator();
            // PUT THIS IN THE TOP WHILE LOOP
            /*
             * while (itr.hasNext()) { System.out.println(itr.next()); toClient =
             * itr.next(); toClient.write(("MSG " + line + "\r\n").getBytes());
             * 
             * }
             */

        } catch (

        IOException ioe) {
            System.err.println(ioe);
        } finally {
        }

    }
}
