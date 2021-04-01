public class User
{
    private String userName;
    private int portNum;
    public User(String name, int num)
    {
         userName = name;
         portNum = num;
    }
    
    public String getName()
    {
        return userName;
    }
    public int getPort()
    {
       return portNum;
    }
}