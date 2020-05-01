/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recovery;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Varundeep and Vibhav
 */
public class Client1 
{
    private Socket socket = null;
    private DataInputStream din = null;
    private DataOutputStream dout = null;
    
    private BufferedReader server_reader = null;
    private BufferedReader kboard_reader = null;
    
    String toSend , toReceive;
    
    Client1(String address , int port)
    {
        try
        {
            System.out.println("Client is Trying to Connect");
            socket = new Socket(address,port);
            System.out.println("Connected");
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            server_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            kboard_reader = new BufferedReader(new InputStreamReader(System.in));
            ClientTask();
            //ConnCloser();
        }
        catch(Exception e)
        {
            
        }
    }
    
    private void ClientTask() throws IOException
    {
        toSend = kboard_reader.readLine();
        dout.writeUTF(toSend); //c1
        System.out.println(din.readUTF());
        System.out.println(din.readUTF()); //c2
        toSend = kboard_reader.readLine();
        dout.writeUTF(toSend);  //c3
        
        if(toSend.equalsIgnoreCase("3"))
        {
            
        }
        else if(toSend.equalsIgnoreCase("1") || toSend.equalsIgnoreCase("2"))
        {
            System.out.println(din.readUTF()); //c4
             
            toSend = kboard_reader.readLine();
            dout.writeUTF(toSend ); //c5
            
            System.out.println(din.readUTF()); //c6
        }
        
        
        
        
    }
    
    public static void main(String args[])
    {
        Client1 client = new Client1("127.0.0.1",2020); 
    
    }
    
}