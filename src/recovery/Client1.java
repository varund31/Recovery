/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recovery;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
    Utilities util;
    
    Client1(String address , int port)
    {
        try
        {
            util = new Utilities();
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
        System.out.println("Enter 'Start'");
        toSend = kboard_reader.readLine();
        dout.writeUTF(toSend); //c1 Start
        //System.out.println(din.readUTF()); //
        while(true)
        {
            System.out.println(din.readUTF()); //c2 Menu
            toSend = kboard_reader.readLine();
            dout.writeUTF(toSend);  //c3

            if(toSend.equalsIgnoreCase("3"))
            {
                //ReceiveLogFile("testfile.txt");
                String fsize = din.readUTF();
                util.SaveFile("Test.txt" , din, Integer.parseInt(fsize));
                System.out.println("Type 'over' to return the log file back to Server");

                toSend = kboard_reader.readLine();
                dout.writeUTF(toSend); //c7

                //Sending File Size
                System.out.println("Sending Server Log File");
                File fsend = new File("test.txt");
                System.out.print(fsend.length());
                dout.writeUTF(Long.toString(fsend.length()));
                util.SendFile("test.txt", dout);


                //SendFile("testfile.txt");
                //SendLogFile("testfile.txt");
            }
            else if(toSend.equalsIgnoreCase("1") || toSend.equalsIgnoreCase("2"))
            {
                System.out.println(din.readUTF()); //c4

                toSend = kboard_reader.readLine();
                dout.writeUTF(toSend ); //c5

                System.out.println(din.readUTF()); //c6
            }
            else if(toSend.equals("4"))
            {
                break;
                
            }
            else
            {
                System.out.println("Wrong Choice");
            }
        }
        
        
        
    }
    
    public static void main(String args[])
    {
        Client1 client = new Client1("127.0.0.1",2020); 
    
    }
    
}