import java.lang.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
class SocketThread implements Runnable
{
    private int socketNumber;
    private final AtomicBoolean running = new AtomicBoolean(false);
    //private DatagramSocket ds = new DatagramSocket(socketNumber);
    
    public SocketThread(int num)
    {
        socketNumber = num;
    }
    
    public void stop()
    {
      running.set(false);
    }
    
    public void run()
    {
      running.set(true);
      while(running.get())
      {
        try{
              DatagramSocket ds = new DatagramSocket(socketNumber);   
              while (true)
              {
                byte[] b1 = new byte[1024];
                DatagramPacket dp = new DatagramPacket(b1, b1.length);
                ds.receive(dp);
                String str = new String(dp.getData());
                System.out.println( str);
              }
            }
            catch(Exception ex)
            {
              System.out.println("i");
            }	
     }     //This is to recieve data from the client
    //      DatagramSocket ds = new DatagramSocket(1025);
              //String[] data = str.split(":");
    
    }
}