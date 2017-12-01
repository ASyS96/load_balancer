#!/usr/local/bin/ruby23
=begin
Auth: ASyS

File: server.rb

Desc: Simple server that takes PORT_NUMBER as the only argument. The port
      number is then associated with its socket descriptor and later listen
      and accept connection attempt by client indefinitely. Upon successful
      connection, the server sends a line with content that reads
      "Connected to <hostname>..." where <hostname> is the name of the
      server machine.To terminate the server, sends a kill signal or
      CTRL-C in interactive shell if the server is not running in the
      background.

Err : No error handling within the script itself. All are handled by the
      interpreter.
=end

require 'socket'

svr = TCPServer.open '', ARGV[0]

loop do
    client = svr.accept
    client.puts "Connected to #{Socket.gethostname}"
    client.close
end
