public class User
{
    private String userName;
    private String password;
    private int portNum;
    public User(String name, int num)
    {
         userName = name;
         portNum = num;
    }
    public User(String name, String password, int num)
    {
         userName = name;
         this.password = password;
         portNum = num;
    }
    
    public String getName()
    {
        return userName;
    }
    public String getPassword(){
        return password;
    }
    public int getPort()
    {
       return portNum;
    }
}