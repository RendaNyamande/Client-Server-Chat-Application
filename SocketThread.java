import java.lang.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTextArea;
class SocketThread implements Runnable
{
    private int socketNumber;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private byte[] b1;
    private String str;
    private DatagramPacket dp; 
    private DatagramSocket ds; 
    private JTextArea resultField;
    
    public SocketThread(int num, JTextArea resultField)
    {
      socketNumber = num;
      this.resultField = resultField;
    }
    
    public void stop()
    {
      running.set(false);
    }
    public String getStr(){
      return str;
    }
    
    public void run()
    {
      running.set(true);
      while(running.get())
      {
        try{
              ds = new DatagramSocket(socketNumber);   
              while (true)
              {
              b1 = new byte[1024];
              dp = new DatagramPacket(b1, b1.length);
              ds.receive(dp);
              str = new String(dp.getData());
              resultField.setText(resultField.getText() + "\n" + str);
              }
            }
            catch(Exception ex)
            {
              stop();
            }	
     }
    }
}