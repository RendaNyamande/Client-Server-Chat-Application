import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;
import java.lang.Thread;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UDPClient extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;
    public static final int NUMBER_OF_CHAR = 10;
    public static String operand = "";

    private JTextField operandField;
    private JTextField resultField;

    public UDPClient(){
        setTitle("Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLayout(new FlowLayout());
        JLabel signInLabel= new JLabel("Please enter your name");
        add(signInLabel);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.GRAY);
        fieldsPanel.setLayout(new FlowLayout());

        operandField = new JTextField("Please enter your name", NUMBER_OF_CHAR);
        operandField.setBackground(Color.GRAY);
        operandField.setEditable(true);
        fieldsPanel.add(operandField);
        add(fieldsPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.GRAY);
        buttonPanel.setLayout(new FlowLayout());

        JButton enterButton= new JButton("Enter");
        enterButton.addActionListener(this);
        buttonPanel.add(enterButton);
        add(buttonPanel);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            client(e);
        } catch (Exception e2) {
            operandField.setText("Error: Could not run client.");
        }
    }
    public static void main(String[] args){
        UDPClient client = new UDPClient();
        client.setVisible(true);
    }
    public void client(ActionEvent e) throws Exception
    {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("Enter")){
    
            //The chunk of code under here creates an initial packet that will be sent to establish a connection
            DatagramSocket ds = new DatagramSocket();
            System.out.println("Please enter your name");
            //operandField.setText(operandField.getText() + "Please enter your name");

            Scanner input = new Scanner(System.in);// Users name is recieved
            //String myName = input.nextLine();
            String myName = operandField.getText();

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
}
class CheckOnExit implements WindowListener{
    public void windowOpened(WindowEvent e){}
    public void windowClosing(WindowEvent e){
        Messages window = new Messages();
        window.setVisible(true);
    }
    public void windowClosed(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
}
class Messages extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;
    public static final int NUMBER_OF_CHAR = 10;
    public static String operand = "";

    private JTextField operandField;
    private JTextField resultField;
    public Messages(){
        setTitle("Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.GRAY);
        fieldsPanel.setLayout(new FlowLayout());
        JTextArea theText = new JTextArea("Enter\ntext here.", 5, 20);

        operandField = new JTextField(NUMBER_OF_CHAR);
        operandField.setBackground(Color.GRAY);
        operandField.setEditable(true);
        fieldsPanel.add(operandField);
        add(fieldsPanel, BorderLayout.CENTER);

        
    }
    public void actionPerformed(ActionEvent e){}
}