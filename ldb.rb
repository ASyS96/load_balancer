#!/usr/local/bin/ruby23
=begin
Auth: ASyS

File: ldb.rb

Desc: A server that takes PORT_NUM as argument and bind the server to that
      PORT_NUM. 'svrlist' file within the script directory is then opened 
      in which server entry is indexed as 0 to n - 1 where n is the total
      number of entry. The server listens indefinitely for connection from
      client. Upon successful connection, server index sent by client
      is retrieved and connection to server with corresponding index is
      made. Response gotten from the server is sent to the client.

Err : Error with opening svrlist file results in exit code 1 and zero entry
      svrlist will result code 2 error.
=end

require 'socket'
require 'timeout'


begin
    svrfile = File.new 'svrlist', 'r'#Open svrlist file that contains list
				     #+ of server
rescue Errno::ENOENT
    puts 'Error opening svrlist!!'
    exit 1
end
svrcount = 0#Number of server entries in svrlist
svrip = []#List of server IP address
svrport = []#List of server Port address
while line = svrfile.gets
    if line.slice(0) == '#' || line == "\n"#Ignore entry that begins with '#'
					   #+ character and empty line
	next
    end
    ip, port = line.split#Get server entry ip and port
    svrip.push ip
    svrport.push port
    svrcount += 1
end

if svrcount == 0#Exit program with code 2 if no entry is provided in svrlist
    puts 'svrlist is empty!'
    exit 2
end

lbsock = TCPServer.open '', ARGV[0]#Load balancer server that faces client
    
ttimeout = 2#Timeout time for connection to server in second
loop do
    clisock = lbsock.accept#Listen and accept client connection
    svri = clisock.gets.to_i#Get requested server index from client
    if svri > svrcount - 1 || svri < 0#If server index is out of range
	clisock.puts 'Index not in range of available servers...'
	clisock.close
	next
    end
    begin
	svrsock = nil#Placeholder for server socket
	Timeout.timeout(ttimeout) do
	#Request connection to server
	    svrsock = TCPSocket.open svrip[svri], svrport[svri]
	end
    rescue Timeout::Error
	#Catch exception raised if connection timed out
	clisock.puts "Connection to server[#{svri}] timed out..."
	clisock.close
	next
    rescue Errno::ECONNREFUSED
	#Catch exception raised if connection refused
	clisock.puts "Connection to server[#{svri}] refused..."
	clisock.close
	next
    end
    clisock.puts svrsock.gets#Write to client data received from server
    svrsock.close
    clisock.close
end
