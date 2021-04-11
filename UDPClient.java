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
import java.awt.event.WindowAdapter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

public class UDPClient extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 150;
    public static final int NUMBER_OF_CHAR = 10;
    public static String operand = "";
    public static String myName;

    private JTextField operandField;
    private JTextField resultField;
    private DatagramSocket ds;
    private byte[] b;
    private InetAddress ia; 
    private DatagramPacket dp; 
    private byte[] b1;
    private DatagramPacket dp2; 
    private String str;
    private String[] data;
    private Thread t;
    private static UDPClient clientWindow;
    private Chat chatWindow;
    private NameDenied nameDeniedWindow;
    private OnlineUser  onlineUserWindow;

    public UDPClient(){
        setTitle("Sign in");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setLayout(new FlowLayout());
        JLabel signInLabel= new JLabel("Please enter your name");
        add(signInLabel);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(Color.GRAY);
        fieldsPanel.setLayout(new FlowLayout());

        operandField = new JTextField(NUMBER_OF_CHAR);
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
        clientWindow = new UDPClient();
        clientWindow.setVisible(true);
    }
    public void client(ActionEvent e) throws Exception
    {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("Enter")){
            
            myName = operandField.getText();

            connectUser();

            clientWindow.setVisible(false);
            onlineUserWindow = new OnlineUser();

            chatWindow = new Chat();
            str = new String(dp2.getData());
            data = str.split(":");
            if (data[0].equals("Username accepted")){

                onlineUserWindow.setVisible(true);
                t = new Thread(new SocketThread(Integer.parseInt(data[1].trim()), chatWindow.getResultField()));
                t.start();
            }
            else if (data[0].trim().equals("Username already taken")){
                System.out.println("denied");
                chatWindow.setVisible(false);
                nameDeniedWindow = new NameDenied();
                nameDeniedWindow.setVisible(true);
            }
        }
        
    }
    public void connectUser() throws Exception{
        ds = new DatagramSocket();
        b = ("connect:"+myName).getBytes();// This loads the packet with the keyword "Connect" as well as the users name
        ia = InetAddress.getLocalHost();
        dp = new DatagramPacket(b,b.length,ia, 1025);
        ds.send(dp); // The packet is sent to the server which will then make sense of it

        //This chunk of code waits to recieve a response from the server
        b1 = new byte[1024];
        dp2 = new DatagramPacket(b1, b1.length);
        ds.receive(dp2);// Server response is recieved here

    }
    
    public class NameDenied extends JFrame implements ActionListener{
        private static final long serialVersionUID = 1L;
        public static final int WIDTH = 300;
        public static final int HEIGHT = 150;
        public static final int NUMBER_OF_CHAR = 10;
        //public static String operand = "";

        private JTextField operandField;
        private JTextArea resultField;

        public NameDenied(){
            setTitle("Username denied");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            JPanel buttonPanel= new JPanel();
            buttonPanel.setBackground(Color.GRAY);
            buttonPanel.setLayout(new FlowLayout());
            JButton retryButton = new JButton("Retry");
            retryButton.addActionListener(this);
            buttonPanel.add(retryButton);
            add(buttonPanel, BorderLayout.CENTER);

        }
        public void actionPerformed(ActionEvent e){
            String actionCommand = e.getActionCommand();
            if (actionCommand.equals("Retry")){
                clientWindow.setVisible(true);
            }
        }

    } 
    
    public class UserNonExistant extends JFrame implements ActionListener {
        private static final long serialVersionUID = 1L;
        public static final int WIDTH = 300;
        public static final int HEIGHT = 150;
        public static final int NUMBER_OF_CHAR = 10;

        private JTextField operandField;
        private JTextArea resultField;

        public UserNonExistant(){
            setTitle("User offline");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.GRAY);
            buttonPanel.setLayout(new FlowLayout());
            JButton retryButton = new JButton("Retry");
            retryButton.addActionListener(this);
            buttonPanel.add(retryButton);
            add(buttonPanel, BorderLayout.CENTER);

        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if (actionCommand.equals("Retry")) {
                onlineUserWindow.setVisible(true);
            }
        }

    }

    public class OnlineUser extends JFrame implements ActionListener{
        private static final long serialVersionUID = 1L;
        public static final int WIDTH = 300;
        public static final int HEIGHT = 150;
        public static final int NUMBER_OF_CHAR = 10;

        private JTextField operandField;
        private JTextArea resultField;
        
        public OnlineUser(){
            setTitle("Online Chats");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            JPanel buttonPanel= new JPanel();
            buttonPanel.setBackground(Color.GRAY);
            buttonPanel.setLayout(new FlowLayout());

            JPanel navPanel = new JPanel();
            navPanel.setBackground(Color.GRAY);
            navPanel.setLayout(new BorderLayout());

            JPanel navPanel2 = new JPanel();
            navPanel2.setBackground(Color.GRAY);
            navPanel2.setLayout(new FlowLayout());

            JButton backButton = new JButton("Back");
            backButton.addActionListener(this);
            navPanel2.add(backButton);
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(this);
            navPanel2.add(exitButton);

            navPanel.add(navPanel2, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            JLabel nameLabel= new JLabel("Who would you like to message?");
            buttonPanel.add(nameLabel);

            operandField = new JTextField(NUMBER_OF_CHAR);
            operandField.setBackground(Color.LIGHT_GRAY);
            operandField.setEditable(true);
            buttonPanel.add(operandField);

            JButton selectButton = new JButton("Select");
            selectButton.addActionListener(this);
            buttonPanel.add(selectButton);

            add(buttonPanel, BorderLayout.CENTER);

        }
        public void actionPerformed(ActionEvent e) {
            try {
                userSelector(e);
            } catch (Exception e2) {
                operandField.setText("Error: Could not run userSelector GUI.");
            }
        }
        public void userSelector(ActionEvent e) throws Exception{
            String actionCommand = e.getActionCommand();
            if (actionCommand.equals("Select")){
                System.out.println(myName);

                //Use a better value in place of empty quotes!
                if (!(chatWindow.recipeint.equals(""))){
                    chatWindow.setVisible(true);
                }

                chatWindow.recipeint = operandField.getText();
                chatWindow.userNameField.setText("Chat with "+chatWindow.recipeint);
                System.out.println(chatWindow.recipeint);

                b = ("send:"+chatWindow.recipeint).getBytes();
                ia = InetAddress.getLocalHost();
                dp = new DatagramPacket(b,b.length,ia, 1025);
                ds.send(dp);//The server is sent a packet with the name of the recip

                b1 = new byte[1024];
                dp2 = new DatagramPacket(b1, b1.length);
                ds.receive(dp2);//The user recieves confirmation from the server as 
                str = new String(dp2.getData());

            //This is where we finally send a message to a recipient if he exist
                System.out.println(str.trim());
                if (str.trim().equals("confirmed")){
                    onlineUserWindow.setVisible(false);

                    chatWindow.setVisible(true);
                    System.out.println(data[0]);
                    }
            }
            else if(actionCommand.equals("Back")){
                onlineUserWindow.setVisible(false);
                clientWindow.setVisible(true);

            }
        }        

    } 
  
    public class Chat extends JFrame implements ActionListener {

        private static final long serialVersionUID = 1L;
        public static final int WIDTH = 500;
        public static final int HEIGHT = 490;
        public static final int NUMBER_OF_CHAR = 10;

        public JTextField operandField;
        public JTextField userNameField;
        private JTextArea resultField;
        public String recipeint = "";
        public JTextArea getResultField(){
            return resultField;
        }
        
        public Chat() {
            setTitle("Chat");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());

            JMenu chatsMenu = new JMenu("Chats Online");
            JMenuItem rendaChoice = new JMenuItem("Renda");
            rendaChoice.addActionListener(this);
            chatsMenu.add(rendaChoice);

            JMenuBar bar = new JMenuBar();
            bar.add(chatsMenu);
            setJMenuBar(bar);

            JPanel navPanel = new JPanel();
            navPanel.setBackground(Color.GRAY);
            navPanel.setLayout(new BorderLayout());

            JPanel navPanel2 = new JPanel();
            navPanel2.setBackground(Color.GRAY);
            navPanel2.setLayout(new FlowLayout());

            JButton backButton = new JButton("Back");
            backButton.addActionListener(this);
            navPanel2.add(backButton);
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(this);
            navPanel2.add(exitButton);

            navPanel.add(navPanel2, BorderLayout.EAST);
            add(navPanel, BorderLayout.NORTH);

            JPanel fieldsPanel = new JPanel();
            fieldsPanel.setBackground(Color.GRAY);
            fieldsPanel.setLayout(new FlowLayout());

            userNameField= new JTextField(NUMBER_OF_CHAR);
            userNameField.setBackground(Color.GRAY);
            userNameField.setEditable(false);
            fieldsPanel.add(userNameField);

            resultField = new JTextArea(15, 45);
            resultField.setBackground(Color.LIGHT_GRAY);
            resultField.setEditable(false);
            fieldsPanel.add(resultField);

            add(fieldsPanel, BorderLayout.CENTER);

            JScrollPane scrolledText = new JScrollPane(resultField);
            scrolledText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrolledText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            fieldsPanel.add(scrolledText);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.GRAY);
            buttonPanel.setLayout(new FlowLayout());

            JLabel messageLabel = new JLabel("Enter Message");
            buttonPanel.add(messageLabel);
            operandField = new JTextField(NUMBER_OF_CHAR);
            operandField.setBackground(Color.LIGHT_GRAY);
            operandField.setEditable(true);
            buttonPanel.add(operandField);

            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(this);
            buttonPanel.add(sendButton);

            add(buttonPanel, BorderLayout.SOUTH);

        }
        public void actionPerformed(ActionEvent e) {
        try {
            chatInterface(e);
        } catch (Exception e2) {
            operandField.setText("Error: Could not run client.");
        }
    }

        public void chatInterface(ActionEvent e) throws Exception{
            String actionCommand = e.getActionCommand();
            if (actionCommand.equals("Send")) {
                sendReceive();

            }
            else if ( actionCommand.equals("Renda")){
                recipeint = "Renda";

            }
            else if(actionCommand.equals("Back")){
                chatWindow.setVisible(false);
                onlineUserWindow.setVisible(true);

            }
            else if(actionCommand.equals("Exit")){
                t.stop();
                b = ("kill:"+myName).getBytes();//This is a message sent to the server to let it know the user is leaving.
                ia = InetAddress.getLocalHost();
                dp = new DatagramPacket(b,b.length,ia, 1025);
                ds.send(dp);
                System.exit(0);
            }
        }
        public void sendReceive() throws Exception{
            
            String text = operandField.getText();
            operandField.setText("");
            resultField.setText(resultField.getText()+"\nYou"+": "+text);
        
            b = ("message:"+recipeint+":"+myName+":"+text).getBytes();
            ia = InetAddress.getLocalHost();
            dp = new DatagramPacket(b,b.length,ia, 1025);
            ds.send(dp);
        }

    }
}


