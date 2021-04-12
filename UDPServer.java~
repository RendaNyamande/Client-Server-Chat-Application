import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer
{
    public static void main(String args[]) throws Exception
    {
           int UserCount = 0;//This will keep count of active users
           int portNum = 1026;//This tracks available port numbers... it will increment whenever a new user is assigned a port
          
           DatagramSocket ds = new DatagramSocket(1025); //This opens up the servers socket which then starts listening for incoming packets
           UserStore storage = new UserStore();
           while (true)
           {
                 //This if statement ends the server if all users have disconnected
                 if (UserCount == -10)
                 {
                      break;
                 }
          
                 //This is to recieve data from the client
                 byte[] b1 = new byte[1024];
                 DatagramPacket dp = new DatagramPacket(b1, b1.length);
                 ds.receive(dp);
                 String str = new String(dp.getData());
                 String[] data = str.split(":");
                 String result = "";
            //      if(data.length == 4){
            //       System.out.println(data[0]+""+data[1]+""+data[2]+""+data[3]);
            //      }
                 
                 //There are various different symbols the client will send in order to let the server
                 //know what it wants to do... these if statements check all of those. The possible symbols are "connect", "send", "message", and "kill"
                 if (data[0].equals("connect"))
                 {
                       //When the client sends a connection request the server will check if the username is available
                       User person = new User(data[1], portNum);
                       if (storage.find(person.getName()))
                       {
                             result = "Username already taken:";//tells user if the name is taken
                       }
                       else
                       {
                             //This adds the user to the database and sends a response to the client confirming his name, and providing him with a port number for future communicaition
                             storage.add(person);
                             UserCount++;
                             result = "Username accepted:"+portNum;
                       }	
             
              
                }
                else if(data[0].equals("login")){
                        User person = new User(data[1], data[2], portNum);
                        if (storage.findPassword(person.getName(), person.getPassword()))
                        {
                              result = "login successful:";//tells user if the name is taken
                        }
                        else
                        {
                              //This adds the user to the database and sends a response to the client confirming his name, and providing him with a port number for future communicaition
                              //storage.add(person);
                              //UserCount++;
                              result = "login failed:"+portNum;
                        }	

                }
                //This is a response to the clients request to send a message to a recipient
                //The recipient is check to make sure he exists, and then a response is sent to the client	
                else if (data[0].equals("send"))
                {
                      if (storage.find(data[1].trim()))
                      {	
                            result = "confirmed"; //confirmation to be sent to client if user exists
                      }
                      else
                      {
                           result = "denied";
                      }
       
                }
                //This is a response to a message that a client has sent to a recipient.
                //The server recieves the message, and relays it to the recipient
                else if(data[0].equals("message"))
                {
                     String senderName = data[2].trim();//This isolates the name of the sender
                     int port = storage.findNum(data[1].trim());//This gets the port number of the recipient
                     
                     DatagramSocket cs = new DatagramSocket();
                     String reply = senderName+": "+data[3].trim();//This fills the packet with the sender name and his message
                     //System.out.println(reply);
                     byte[] b2 = (reply).getBytes();
                     InetAddress ia = InetAddress.getLocalHost();
                     
                     DatagramPacket cp1 = new DatagramPacket(b2, b2.length, ia, port);
                     cs.send(cp1); //this sends the packet to the recipient
                }
                //This is in response to when a client disconnects... the server recieves a kill string that lets it know to remove the client
                //Note at this time this does not remove the client from the data base... so technically the client still exists to recieve messages even after he has disconnected... 
                //this will have to be fixed at some point
                else if(data[0].equals("kill"))
                {
                     UserCount--; //decrements the user count
                     if (UserCount == 0)
                     {
                          UserCount = -10;//if usercount is 0 then its set to -10 so that on the next loop the server will know to break
                     }
                     //All of this just sends a message to the client thread which is still running at this point as it waits for one last packet before it dies
                     int port = storage.findNum(data[1].trim());
                     DatagramSocket cs = new DatagramSocket();
                     String reply = "kill";
                     byte[] b2 = (reply).getBytes();
                     InetAddress ia = InetAddress.getLocalHost();
                     DatagramPacket cp1 = new DatagramPacket(b2, b2.length, ia, port);
                     cs.send(cp1);
                }

          //Notice that each of the above if statements overwrites a variable called "result"... over here at the end of the loop, whatever is stored in result is sent to the client         
          byte[] b2 = (result).getBytes();
          InetAddress ia = InetAddress.getLocalHost();
          DatagramPacket dp1 = new DatagramPacket(b2, b2.length, ia, dp.getPort());
          ds.send(dp1);
          
          portNum++;
         
      }
    
    }
}