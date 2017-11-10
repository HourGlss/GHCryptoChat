QZMP
======

Currently
------------
Server package contains the code needed to run the server, client for client. Currently the data passes through the server and the client only has one socket to connect with the server.  Encyption is only proof of concept and has not been implemented into the chat. The GUI needs to change.


How it Works
------------
Server runs, holds channels and users.  Client starts java, logins to server and gets a username when accepted client sends public key and modulus to server.
When a clients sends text it gets sent to each of the clients in the channel.


Trust
-----
All of my code will be 100% of the time open source, period.
	
