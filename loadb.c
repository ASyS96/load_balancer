/*
 * Auth: ASyS
 *
 * File: loadb.c
 *
 * Desc: A server that takes PORT_NUM as argument and bind the server to that
 *       PORT_NUM. 'svrlist' file within the script directory is then opened 
 *       in which server entry is indexed as 0 to n - 1 where n is the total
 *       number of entry. The server listens indefinitely for connection from
 *       client. Upon successful connection, server index sent by client
 *       is retrieved and connection to server with corresponding index is
 *       made. Response gotten from the server is sent to the client. This C
 *       version is different as it lacks timeout for connection attempt to
 *       requested server compared to the Java and Ruby version.
 * 
 *
 * Err : 1)Error opening svrlist
 *       2)svrlist file is empty
 *       3)Load balancer socket creation fails
 *       4)Bind fails
 *       5)Connection to client fails
 *       6)Server socket creation fails
 */
#include <stdlib.h>//atoi() ascii to integer
#include <unistd.h>//close() close socket
#include <stdio.h>
#include <sys/socket.h>//socket(), bind(), listen, accept() - socket func.
#include <sys/types.h>
#include <string.h>//strcat() concatenate string
#include <arpa/inet.h>//htons() host to network short
#include <netinet/in.h>//struct sockaddr_in
#include <sys/utsname.h>//uname() get node name



int main(int args, char **argv)
{

    FILE *svrlist = fopen("svrlist", "r");
    if(svrlist == NULL) {
	printf("Error opening svrlist!!\n");
	exit(1);
    }

    char line[1024], *lineptr;

    struct sockaddr_in svr[1024];//Addresses and ports of server

    int svrcount = 0;//Keep count of servers
    while(lineptr = fgets(line, sizeof line, svrlist)) {
	if(*lineptr == '#' || *lineptr == '\n')//Regards line as comment if
					       //+ the first char is '#' and
					       //+ ignore empty line
	    continue;
	++svrcount;
	char *token;
	for(int i = 0; (token = strsep(&lineptr, " ")) && i < 2; ++i) {
	    if(strcmp(token, " ") == 0)//Ignore extra spaces
	       continue;
	    svr[svrcount - 1].sin_family = AF_INET;
	    if(i == 0)
	       svr[svrcount - 1].sin_addr.s_addr = inet_addr(token);
	    else 
	       svr[svrcount - 1].sin_port = htons(atoi(token));
	}
    }
    
    if(svrcount == 0) {
	printf("svrlist is empty!");
	exit(2);
    }

    int ldbsock;//Load balancer socket descriptor

    if((ldbsock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
	printf("Load balancer socket creation failed\n");
	exit(3);
    }

    struct sockaddr_in ldb;

    bzero(&ldb,sizeof ldb);
    ldb.sin_family = AF_INET;
    ldb.sin_port = htons(atoi(argv[1]));//Assign port number from command
					//+line argument
    ldb.sin_addr.s_addr = htonl(INADDR_ANY);

    if(bind(ldbsock, (struct sockaddr *) &ldb, sizeof ldb) < 0) {
	printf("Bind failed\n");
	exit(4);
    }

    listen(ldbsock, 4);
    struct sockaddr_in cli;
    int c = sizeof ldb;
    
    for(;;) { //Indefinite loop that always wait for connection from client
	int csockd;//Socket descriptor of the client
	csockd = accept(ldbsock,(struct sockaddr*) &cli,(socklen_t*) &c);
	if(csockd < 0) {
	    printf("Connection with client failed\n");
	    exit(5);
	}

	char svri[1024];//Hold server index requested by the client
	recv(csockd, svri, sizeof svri, 0);

	int ssockd;
	if((ssockd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
	    printf("Server socket creation failed\n");
	    exit(6);
	}
	if(connect(ssockd, (struct sockaddr *) &svr[atoi(svri)],
	    sizeof svr[atoi(svri)]) < 0) {
	    char errmsg[1024] = "Server not available..";
	    strcat(errmsg, "\n");
	    send(csockd, errmsg, sizeof errmsg, 0);
	    continue;
	}
    
	char svrres[1024];
	recv(ssockd, svrres, sizeof svrres, 0);
	send(csockd, svrres, sizeof svrres, 0);
	close(ssockd);
	close(csockd);
    }
}
