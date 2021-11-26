# Client-Server-Chat-Application
This is a client-server chat application that utilizes UDP at the transport layer. The goal of the project was to design and implement an application layer protocol.  We needed to implement an application layer protocol to support the client-server architecture, as well as implement a server that can manage the interaction between clients. We implemented a chat client that allows one or more pairs of users to exchange messages in real-time. As this is a network application, the different clients and the server run on different hosts. Since this application will be uses UDP at its transport layer protocol (which provides unreliable service), the application layer protocol was designed with its own mechanism for achieving reliability. Users of the application need confirmation that their messages have been received by either the server or destination client (similar to WhatsApps single or double tick)  Technologies used: Java
