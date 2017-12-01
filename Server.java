/*
 * Auth: ASyS
 *
 * File: Server.java
 *
 * Desc: Simple server that takes PORT_NUMBER as the only argument. The port
 *       number is then associated with its socket descriptor and later listen
 *       and accept connection attempt by client indefinitely. Upon successful
 *       connection, the server sends a line with content that reads
 *       "Connected to <hostname>..." where <hostname> is the name of the
 *       server machine.To terminate the server, sends a kill signal or
 *       CTRL-C in interactive shell if the server is not running in the
 *       background.
 *
 * Err : Any IO failure with the socket triggers the IOException with exit
 *       code 1.
 */
import java.net.*;
import java.io.*;

class Server {
    public static void main(String... args) {
    try {
	ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
	for(;;) {
	    Socket client = server.accept();
	    new PrintWriter(client.getOutputStream(), true).println(
		"Connected to " + InetAddress.getLocalHost().getHostName());
	    client.close();
	}
    } catch(UnknownHostException uhe) {
	System.err.println("Unknown host: " + args[0]);
	System.exit(1);
    } catch(IOException ioe) {
	System.err.println("I/O connection failed");
	System.exit(2);
    }
    }
}
