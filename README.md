# load_balancer
As the name suggests, this repo contains "load balancer"(if you will) for school work.
It's the worst code to learn from and only "works" with the weakest definition of "works".
There are 3 components in this repo; the client, server and load balancer. All the source
codes fall into these categories(only implemented in different language). Kindly read
the documentation inside the source code first to get the idea of this mess I did :-(

## Compiling C and Java
All the Java and C source codes need to be compiled beforehand. This can be done as below:
```shell
#On GNU/Linux system, use gcc instead(or other C compiler available)
#Replace path/to/target with the name of executable to be generated
$ cc client2.c -o path/to/target
$ cc loadb.c -o path/to/target
$ cc server.c -o path/to/target

#Replace path/to/dir to your desired directory path where .class is to be generated
$ javac Client2.java -d path/to/dir
$ javac LoadBalancer.java -d path/to/dir
$ javac Server.java -d path/to/dir
```

## Ruby script
For the ruby script to be executed, the first line may need to be changed depending on where
the `ruby` interpreter is installed. Simply change the first line as `#!/path/to/ruby` where
*path/to/ruby* is where your system have `ruby` installed. Next is to give permission
for the script to execute by `$ chmod u+x /path/to/script.rb`.

## Usage
All the clients, servers and load balancers written in C, Java and Ruby within this repo pretty
much behave the same way(except for timeout and exception handling) and therefore interoperable.
Ensure that entry in *svrlist* isn't empty and valid before firing up the load balancer.
* Execute the load balancer:- {*load_balancer*} {*port_num*}. Example => `./ldb.rb 8000`
* Execute the server:- {*server*} {*port_num*}. Example => `./server 5000`
* Run client:- {*client*} {*load_balancer_ip_addr*} {*load_balancer_port_num*} {*svr_index*}. Example =>
`./client 192.168.159.128 8000 0`
Further notes on usage can be found inside the source code itself.

