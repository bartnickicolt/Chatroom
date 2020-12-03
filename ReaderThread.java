
/**
 * This thread is passed a socket that it reads from. Whenever it gets input
 * it writes it to the ChatScreen text area using the displayMessage() method.
 */

import java.io.*;
import java.net.*;
import javax.swing.*;

public class ReaderThread implements Runnable {
    BufferedReader fromServer;
    ChatClient screen;

    public ReaderThread(BufferedReader fromServer, ChatClient screen) {
        this.fromServer = fromServer;
        this.screen = screen;
    }

    public void run() {
        try {
            // fromServer = new BufferedReader(new
            // InputStreamReader(fromServer.getInputStream()));

            while (true) {
                String line = fromServer.readLine();

                // now display it on the display area
                String message = line.substring(line.indexOf(" ") + 1);
                screen.displayMessage(message);
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }
}
