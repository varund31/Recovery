/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recovery;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Varundeep
 */
public class MultiClientHandler extends Thread
{
    final DataInputStream din;
    final DataOutputStream dout; 
    final Socket socket; 
    
    MultiClientHandler(Socket socket , DataInputStream din , DataOutputStream dout)
    {
        this.din = din;
        this.dout = dout;
        this.socket = socket;
    }
    
    public void run()
    {
        String Received; 
        String ToReturn;
        
        try 
        {
            Received = din.readUTF();
            if(Received.equals("Start"))
            {
                System.out.println("Success");
            }
        
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
