#!/usr/local/bin/ruby23
=begin
Auth: ASyS

File: ldbcli.rb

Desc: Modified version of client program 'client.rb' that takes
      SERVER_IP_ADDRESS, SERVER_PORT_NUMBER and SERVER_INDEX argument and
      attempt to connect to them. This version is intended to connect to
      the load balancer as it supplies additional input SERVER_INDEX that
      request for particular server with certain index from the load
      balancer. Upon successful connection, it reads a single line from its
      socket and print them to STDOUT.

Err : Exception is entirely dependent on the interpreter. None is
      handled within this script.
=end

require 'socket'

puts "Connecting to #{ARGV[0]}:#{ARGV[1]}..."
socket = TCPSocket.open ARGV[0], ARGV[1]#Connect to HOST at PORT

socket.puts ARGV[2]#Send which server to request to
puts socket.gets#Get the server hostname requested from
socket.close
