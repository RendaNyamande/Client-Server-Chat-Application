import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;
import java.lang.Thread;
public class UDPClient
{
   
    public static void main(String[] args) throws Exception
    {
    
        //The chunk of code under here creates an initial packet that will be sent to establish a connection
        DatagramSocket ds = new DatagramSocket();
        System.out.println("Please enter your name");
        Scanner input = new Scanner(System.in);// Users name is recieved
        String myName = input.nextLine();

        byte[] b = ("connect:"+myName).getBytes();// This loads the packet with the keyword "Connect" as well as the users name
        InetAddress ia = InetAddress.getLocalHost();
        DatagramPacket dp = new DatagramPacket(b,b.length,ia, 1025);
        ds.send(dp); // The packet is sent to the server which will then make sense of it
    
    
        //This chunk of code waits to recieve a response from the server
        byte[] b1 = new byte[1024];
        DatagramPacket dp2 = new DatagramPacket(b1, b1.length);
        ds.receive(dp2);// Server response is recieved here
        
        //Over here the response form the server is read, and if the server has accepted the username, then we will continue
        String str = new String(dp2.getData());
        String[] data = str.split(":");
        if (data[0].equals("Username accepted"))
        {
            //Over here a new thread is created in order to open a socket in the background which will listen for any incoming messages
            //to the client
            Thread t = new Thread(new SocketThread(Integer.parseInt(data[1].trim())));
            t.start();
            System.out.println("Username is accepted\n");
            
            //Now that a connection has been established and the user in in the servers database, the Users messaging loop can begin
            while (true)
            {
                //These three lines make up a basic menu that asks the user who he would like to message, and gives him the option to type in 
                //a keyword "*End*" which will allow him to exit the program
                System.out.println("Type \"*End*\" to exit\n");
                System.out.println("Who would you like to message?");
                String recipeint = input.nextLine();
             
                //This kills the program if the user types *End*
                //Note this uses the deprecated "Thread.stop" method which is bad practise and we should find a better way to do this
                if (recipeint.equals("*End*"))
                {
                     t.stop();
                     b = ("kill:"+myName).getBytes();//This is a message sent to the server to let it know the user is leaving.
                     ia = InetAddress.getLocalHost();
                     dp = new DatagramPacket(b,b.length,ia, 1025);
                     ds.send(dp);//sends message to server
                     break;
                }
                
                //This is for the user to request to send a message to a specific person
                b = ("send:"+recipeint).getBytes();
                ia = InetAddress.getLocalHost();
                dp = new DatagramPacket(b,b.length,ia, 1025);
                ds.send(dp);//The server is sent a packet with the name of the recipient
            
                b1 = new byte[1024];
                dp2 = new DatagramPacket(b1, b1.length);
                ds.receive(dp2);//The user recieves confirmation from the server as to whether or not the recipient exists
                str = new String(dp2.getData());
           
                //This is where we finally send a message to a recipient if he exists
                if (str.trim().equals("confirmed"))
                {
                     System.out.println("Type \"\\b\" to back out\n");//Client recieves the option to back out from the chat
                     System.out.println("To send "+recipeint+" a message, type below");
                     while(true)
                     {
                            String text = input.nextLine();
                            if (text.equals("\\b"))
                            {
                                  break;//Breaks the loop if the client requests to back out
                            }
                            
                            //This chunk sends the message to the to the server, which will relay the message to the recipient
                            b = ("message:"+recipeint+":"+myName+":"+text).getBytes();
                            ia = InetAddress.getLocalHost();
                            dp = new DatagramPacket(b,b.length,ia, 1025);
                            ds.send(dp);
                    }
                
               }
               else if(str.trim().equals("denied"))//If a user does not exist then this will be shown
               {	
                     System.out.println("This user does not exist\n");
               }
               else
               {
                     System.out.println("There has been an error, please try again\n"); //This catches a bug that comes up when a user backs out from one chat before trying to enter another
               }
             }
        }
        
    }
}