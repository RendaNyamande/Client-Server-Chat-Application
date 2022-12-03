package src;

import java.util.ArrayList;
import java.util.List;
public class UserStore
{
    private List<User> storage;
    
    public UserStore()
    {
        storage = new ArrayList<>();
    }
    
    public void add(User user)
    {
        storage.add(user);
    }
    
    public boolean find(String userName)
    {
        boolean flag = false;
        for (User name : storage)
        {
            if (name.getName().trim().equals(userName.trim()))
            {
                flag = true;
                break;
            }
        }
        return flag;
    }
    
    public boolean findPassword(String userName, String password) {
        boolean flag = false;
        for (User name : storage) {
            if (name.getName().trim().equals(userName.trim())) {
                if(name.getPassword().trim().equals(password.trim())){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    public int findNum(String userName)
    {
        int portNum = 0;
        for (User name : storage)
        {
            if (name.getName().trim().equals(userName.trim()))
            {
                portNum = name.getPort();
                break;
            }
        }
        return portNum;
    }
    
}