/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recovery;

import java.sql.Timestamp;

/**
 *
 * @author Varundeep
 */
public class ClientDetails 
{
    String name;
    int ClientMoney;
    boolean IsBlocked;
    String Notification;
    
    ClientDetails(String name , int ClientMoney , boolean IsBlocked)
    {
        this.name = name;
        this.ClientMoney = ClientMoney;
        this.IsBlocked = IsBlocked;
    }

    public void BlockUser()
    {
        IsBlocked = true;
    }
    
    public String getName() 
    {
        return name;
    }

    public int getClientMoney() 
    {
        return ClientMoney;
    }

    public boolean isIsBlocked() 
    {
        return IsBlocked;
    }

    void Notify(String string) 
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.Notification += "|"+string +"\t"+ ts.toString();
    }
    
    String checkNotification()
    {
            if(this.Notification.equals(""))
            {
                    return "No new Transactions";
            }
            else
            {
                    String ret = this.Notification;
                    this.Notification = "";
                    return ret;
            }
    }
    
}
