import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {

    public static final int DEFAULT_PORT = 63546;

    private static final Executor exec = Executors.newCachedThreadPool();

    // An alternative - although perhaps more difficult - technique is to place
    // every message
    // that is sent to the chat room in a java.util.Vector. A separate broadcast
    // thread will
    // periodically awaken (i.e. 10 times per second) and see if there are any
    // messages in this Vector.
    // If so, it will remove the message and broadcast the message to the list of
    // all connections.
    public static void main(String[] args) throws IOException {
        ServerSocket sock = null;
        java.util.Map<String, BufferedOutputStream> users = new HashMap<String, BufferedOutputStream>();

        try {
            // establish the socket
            sock = new ServerSocket(DEFAULT_PORT);

            while (true) {
                /**
                 * now listen for connections and service the connection in a separate thread.
                 */
                Runnable task = new ChatConnection(sock.accept(), users);
                exec.execute(task);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
            if (sock != null)
                sock.close();
        }
    }

}
