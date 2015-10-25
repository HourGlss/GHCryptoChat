## Currently In Work

Please don't judge an unfinished book by the lack of cover. I'm doing this to learn about Java and because I think this might be useful to some people.


QZMP
======

I don't have a name for it yet

QZMP is being made for people to talk securely while keeping two things in mind:

1) No message data goes to the server, ever.

2) Security and anonymity come first. Everything else is less important. 

Technically speaking it's a P2P chat client that implements 1024 bit RSA encryption and the basics of pseudo-random proxying.

Currently
------------
Server package contains the code needed to run the server, client for client. Currently the data passes through the server and the client only has one socket to connect with the server.  Encyption is only proof of concept and has not been implemented into the chat. The GUI needs to change.

Registering
-----------
Currently, I don't even have the register system built. But my idea is that the user signs up using a @university.edu then I hash your password. The DB only needs to store username/pass.

How it Works
------------
Server runs, holds channels and has a database for user info.  Client starts java and builds keys, logins to server and sends server username / pass, when accepted client sends public key and modulus to server.
Client joins channels, server sends client all publickeys and modulus for clients already in channel to that client. Client builds a socket for each of the clients in the channel.
When a clients sends text it uses the publickey and modulus for each of the clients in the channel and they decrypt with their private key.

Also, the server picks 2 people at random client Z and X lets say. Then when person A sends details to person B, what actually happens is person A sends information to person Z who sends it to person X who sends it to person B. The clients to use for Z and X are chosen at random, the clients only know the ips of the people they are connected to. If one person disconnects and they have people they are tunneling information for the server automatically replaces that connection with another random user. This is required.

Built-in commands
-----------------
There are a handful of built-in commands for instructing the bot at runtime but I haven't even come clost to implementing them yet.

Trust
-----
All of my code will be 100% of the time open source, period.
	
Feedback
--------
Email:  qzmpxo@gmail.com
