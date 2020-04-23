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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Varundeep
 */
public class Server 
{
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream din = null;
    private DataOutputStream dout = null;
    private BufferedReader client_reader = null;
    private BufferedReader kboard_reader = null;
    
    private Server(int port) throws Exception
    {
        System.out.println("Hello, Server is Starting");
        
        try
        {
            int  ClientCounter = 0;
            server = new ServerSocket(port);
            System.out.println("Server Started");
            
            System.out.println("Waiting for a Client...");
            try
            {
                while(true)
                { 
                    ClientCounter++;
                    socket = server.accept(); 
                    System.out.println("New Connection established");
                    System.out.println(" >> " + "Client No:" + ClientCounter + " at" + socket.getInetAddress());
                    
                    String FileName = "Client_"+ ClientCounter + "_log.txt";
                    File NewFile = new File(FileName);
                    boolean IsCreated = NewFile.createNewFile();
                    
                    if(IsCreated)
                    {
                        System.out.println("Successfully created new file, path:%s"+ NewFile.getCanonicalPath()); 
                    }
                    else
                    {
                        System.out.println("Some Error in Creation");
                    }
                     
                    dout  = new DataOutputStream(socket.getOutputStream());  // to send data to the client
                    client_reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // to read data coming from the client 
                    din= new DataInputStream(socket.getInputStream());
                    kboard_reader = new BufferedReader( new InputStreamReader(System.in)); 
                    
                    //System.out.println("Hello");
                    Thread t = new MultiClientHandler( socket, din , dout , ClientCounter , FileName );
                    t.start();
                }
            }
            catch(Exception e)
            { 
                e.printStackTrace(); 
            } 
            
        }
        catch(IOException e )
        {
            e.printStackTrace();
        }
       
        //ConClose();
    }
    
    private void ConClose()
    {
        System.out.println("Server] Connection terminated");
        try 
        {
                server.close();
                socket.close();
                dout.close();
                client_reader.close();
                kboard_reader.close();
        }
        catch(IOException e) 
        {
                System.err.println("IOException here!");
                e.printStackTrace();
        }
        catch(NullPointerException e) 
        {
                System.err.println("NullPointerException here!");
                e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        try 
        {
            Server server = new Server(2020);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
    

