/*
 * Auth: ASyS
 *
 * File: LoadBalancer.java
 *
 * Desc: A server that takes PORT_NUM as argument and bind the server to that
 *       PORT_NUM. 'svrlist' file within the script directory is then opened 
 *       in which server entry is indexed as 0 to n - 1 where n is the total
 *       number of entry. The server listens indefinitely for connection from
 *       client. Upon successful connection, server index sent by client
 *       is retrieved and connection to server with corresponding index is
 *       made. Response gotten from the server is sent to the client. In short,
 *       it behaves just like ldb.rb except when it comes to exception handling.
 *       
 *
 * Err : Error in opening svrlist file will result the program to exit with code
 *       1 while empty server list causes the program to exit with code 2. If
 *       the IP is unknown and any other IOException occurs, the program will
 *       exit with code 3 and 4 respectively.
 */
import java.net.*;
import java.io.*;

class LoadBalancer {
    public static void main(String... args) {

    BufferedReader svrlist = null;//Placeholder for svrlist reader
    try {
	svrlist = new BufferedReader(new FileReader(new File("svrlist")));
    } catch(FileNotFoundException fnfe) {
	System.err.println("Error opening svrlist!!");
	System.exit(1);
    }

    String line;
    String[][] svr = new String[1024][];
    int svrcount = 0;//Hold count of server entry in svrlist
    try {
	while((line = svrlist.readLine()) != null) {
	    //Ignore line with '#' as first character
	    if(line.charAt(0) == '#' || line == "")
		continue;
	    ++svrcount;
	    svr[svrcount - 1] = line.split(" +");
	    if(svr[svrcount - 1][0] == "") {
		svr[svrcount - 1][0] = svr[svrcount - 1][1];
		svr[svrcount - 1][1] = svr[svrcount - 1][2];
	    }
	}
    }
    catch (IOException ioe) {
    }

    if(svrcount == 0) {//If no entry is present in svrlist
	System.err.println("svrlist is empty!");
	System.exit(2);
    }
    try {
	ServerSocket ldbsock = new ServerSocket(Integer.parseInt(args[0]));
	for(;;) {
	    Socket clisock = ldbsock.accept();
	    BufferedReader clireader = new BufferedReader(
					    new InputStreamReader(
						clisock.getInputStream()
					    )
					);
	    int svri = Integer.parseInt(clireader.readLine());//Server index
	    Socket svrsock = new Socket();
	    int ttimeout = 2000;//Timeout for connection in millisecond
	    try {
		svrsock.connect(new InetSocketAddress(
				   svr[svri][0], 
				   Integer.parseInt(svr[svri][1])
				   ), ttimeout
			       );
	    }
	    catch (IOException ioeconn) {
		new PrintWriter(clisock.getOutputStream(), true)
		    .println("Connection to server[" + svri + "] failed...");
		clisock.close();
		continue;
	    }
	    new PrintWriter(clisock.getOutputStream(), true).println(
		new BufferedReader(
		    new InputStreamReader(
			svrsock.getInputStream()
		    )
		).readLine()
	    );
	    clisock.close();
	    svrsock.close();
	}
    } catch(UnknownHostException uhe) {
	System.err.println("Unknown host: " + args[0]);
	System.exit(3);
    } catch(IOException ioe) {
	System.err.println("I/O exception");
	System.exit(4);
    }
    }
}
