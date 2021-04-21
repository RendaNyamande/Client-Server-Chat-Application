import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.lang.Thread;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import java.awt.GridLayout;

public class UDPClient extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 150;
    public static final int NUMBER_OF_CHAR = 10;
    public static String operand = "";
    public static String myName;
    public static String password;

    private JTextField operandField;
    private JTextField operandField1;
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
    private  UserNonExistant userNonExistantWindow;
    private NamePasswordDenied namePasswordDeniedWindow;

    public UDPClient(){
        setTitle("Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(3,1));

        JLabel nameLabel= new JLabel("Name");
        JLabel passwordLabel= new JLabel("Password");

        JPanel namePanel = new JPanel();
        namePanel.setBackground(Color.GRAY);
        namePanel.setLayout(new FlowLayout());
        
        JPanel passwordPanel = new JPanel();
        passwordPanel.setBackground(Color.GRAY);
        passwordPanel.setLayout(new FlowLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.GRAY);
        buttonPanel.setLayout(new FlowLayout());

        operandField = new JTextField(NUMBER_OF_CHAR);
        operandField.setBackground(Color.GRAY);
        operandField.setEditable(true);
        namePanel.add(nameLabel);
        namePanel.add(operandField);

        operandField1 = new JTextField(NUMBER_OF_CHAR);
        operandField1.setBackground(Color.GRAY);
        operandField1.setEditable(true);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(operandField1);

        JButton registerButton= new JButton("Register");
        registerButton.addActionListener(this);
        buttonPanel.add(registerButton);

        JButton loginButton= new JButton("Login");
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);
        add(namePanel);
        add(passwordPanel);
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
        if (actionCommand.equals("Register")){
            
            myName = operandField.getText();
            password = operandField1.getText();

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
                chatWindow.setVisible(false);
                nameDeniedWindow = new NameDenied();
                nameDeniedWindow.setVisible(true);
            }
        }
        else if (actionCommand.equals("Login")){
            myName = operandField.getText();
            password = operandField1.getText();

            connectUser1();

            clientWindow.setVisible(false);
            onlineUserWindow = new OnlineUser();

            chatWindow = new Chat();
            str = new String(dp2.getData());
            data = str.split(":");
            if (data[0].equals("login successful")){

                onlineUserWindow.setVisible(true);
                t = new Thread(new SocketThread(Integer.parseInt(data[1].trim()), chatWindow.getResultField()));
                t.start();
            }
            else if (data[0].trim().equals("login failed")){
                chatWindow.setVisible(false);
                namePasswordDeniedWindow = new NamePasswordDenied();
                namePasswordDeniedWindow.setVisible(true);
            }

        }
        
    }
    public void connectUser() throws Exception{
        ds = new DatagramSocket();
        b = ("connect:"+myName+":"+password).getBytes();// This loads the packet with the keyword "Connect" as well as the users name
        ia = InetAddress.getLocalHost();
        dp = new DatagramPacket(b,b.length,ia, 1025);
        ds.send(dp); // The packet is sent to the server which will then make sense of it

        //This chunk of code waits to recieve a response from the server
        b1 = new byte[1024];
        dp2 = new DatagramPacket(b1, b1.length);
        ds.receive(dp2);// Server response is recieved here

    }
    public void connectUser1() throws Exception{
        ds = new DatagramSocket();
        b = ("login:"+myName+":"+password).getBytes();// This loads the packet with the keyword "Connect" as well as the users name
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
                nameDeniedWindow.setVisible(false);
                clientWindow.setVisible(true);
            }
        }

    } 
    public class NamePasswordDenied extends JFrame implements ActionListener{
        private static final long serialVersionUID = 1L;
        public static final int WIDTH = 300;
        public static final int HEIGHT = 150;
        public static final int NUMBER_OF_CHAR = 10;

        public NamePasswordDenied(){
            setTitle("Error");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());
            JPanel buttonPanel= new JPanel();
            buttonPanel.setBackground(Color.GRAY);
            buttonPanel.setLayout(new FlowLayout());
            JLabel errorLabel= new JLabel("Error! Incorrect Username and/or password");
            add(errorLabel, BorderLayout.SOUTH);
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
                userNonExistantWindow.setVisible(false);
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

                //Use a better value in place of empty quotes!
                if (!(chatWindow.recipeint.equals("hjkllkjh"))){
                    onlineUserWindow.setVisible(false);
                    chatWindow.setVisible(true);
                }

                chatWindow.recipeint = operandField.getText();

                b = ("send:"+chatWindow.recipeint).getBytes();
                ia = InetAddress.getLocalHost();
                dp = new DatagramPacket(b,b.length,ia, 1025);
                ds.send(dp);//The server is sent a packet with the name of the recip

                b1 = new byte[1024];
                dp2 = new DatagramPacket(b1, b1.length);
                ds.receive(dp2);//The user recieves confirmation from the server as 
                str = new String(dp2.getData());

            //This is where we finally send a message to a recipient if he exist
                if (str.trim().equals("confirmed")){
                    onlineUserWindow.setVisible(false);
                    chatWindow.userNameField.setText("Chat with "+chatWindow.recipeint);
                    chatWindow.setVisible(true);
                }
                else if(str.trim().equals("denied")){
                    onlineUserWindow.setVisible(false);
                    userNonExistantWindow = new UserNonExistant();
                    userNonExistantWindow.setVisible(true);

                }
            }
            else if(actionCommand.equals("Back")){
                clientWindow.operandField.setText(myName);
                onlineUserWindow.setVisible(false);
                clientWindow.setVisible(true);
            }
            else if(actionCommand.equals("Exit")){
                System.exit(0);
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
        public String recipeint = "hjkllkjh";
        public JTextArea getResultField(){
            return resultField;
        }
        
        public Chat() {
            setTitle("Chat");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(WIDTH, HEIGHT);
            setLayout(new BorderLayout());

            // JMenu chatsMenu = new JMenu("Chats Online");
            // JMenuItem rendaChoice = new JMenuItem("Renda");
            // rendaChoice.addActionListener(this);
            // chatsMenu.add(rendaChoice);

            // JMenuBar bar = new JMenuBar();
            // bar.add(chatsMenu);
            // setJMenuBar(bar);

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
                userNameField.setText("Chat with " + recipeint);

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
            if(text.trim().compareTo("")!=0)
            {
                operandField.setText("");
                resultField.setText(resultField.getText()+"\nYou"+": "+text);
        
                b = ("message:"+recipeint+":"+myName+":"+text).getBytes();
                ia = InetAddress.getLocalHost();
                dp = new DatagramPacket(b,b.length,ia, 1025);
                ds.send(dp);
            }
        }

    }
}


