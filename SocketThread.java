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
                  String Sender[] = str.split(":");
                  resultField.setText(resultField.getText() + "\n" + str);
                  if (str.trim().compareTo("*Recieved*")!=0)
                  {
                   //   resultField.setText(resultField.getText() + "\n" + str);
              
                      if (str.trim().compareTo("")!=0)
                      {
                          DatagramSocket ss = new DatagramSocket();
                          str = "recieved:"+Sender[0].trim();
                          byte[] b = str.getBytes();
                          InetAddress ia = InetAddress.getLocalHost();
                          dp = new DatagramPacket(b, b.length, ia, 1025);
                          ss.send(dp);
                      }
                  }
              }
            }
            catch(Exception ex)
            {
              stop();
            }	
     }
    }
}