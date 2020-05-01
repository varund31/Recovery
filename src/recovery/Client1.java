/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recovery;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    
       public void SendLogFile(String filename) throws FileNotFoundException, IOException
    {
        FileInputStream fr = new FileInputStream(filename);
        byte []b = new byte[2002];
        fr.read(b, 0 , b.length);
        OutputStream os = this.socket.getOutputStream();
        os.write(b, 0 , b.length);
         
    }

    public void ReceiveLogFile(String filename) throws IOException
    {
        byte [] b = new byte[2002];
        InputStream is = this.socket.getInputStream();
        FileOutputStream fr = new FileOutputStream(filename);
        is.read(b, 0, b.length);
        fr.write(b, 0 ,b.length);
    }
    
    
    
    public void SendFile(String file) throws IOException 
    {
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) 
                {
			dout.write(buffer);
		}
		
		fis.close();
                return;
		//dout.close();	
    }
    
      private void SaveFile(Socket clientSock) throws IOException 
    {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fos = new FileOutputStream("testfile.txt");
        byte[] buffer = new byte[4096];

        int filesize = 15123; // Send file size in separate msg
        int read = 0;
        int totalRead = 0;
        int remaining = filesize;
        while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) 
        {
                totalRead += read;
                remaining -= read;
                System.out.println("read " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
        }

        fos.close();
        return;
        //dis.close();
    }
    
    
    private void ClientTask() throws IOException
    {
        toSend = kboard_reader.readLine();
        dout.writeUTF(toSend); //c1 Start
        //System.out.println(din.readUTF()); //
        System.out.println(din.readUTF()); //c2
        toSend = kboard_reader.readLine();
        dout.writeUTF(toSend);  //c3
        
        if(toSend.equalsIgnoreCase("3"))
        {
            //ReceiveLogFile("testfile.txt");
            //SaveFile(socket);
             
            
            FileOutputStream fos = new FileOutputStream("testfile.txt");
            byte[] buffer = new byte[4096];

            int filesize = 15123; // Send file size in separate msg
            int read = 0;
            int totalRead = 0;
            int remaining = filesize;
            while((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) 
            {
                    totalRead += read;
                    remaining -= read;
                    System.out.println("read " + totalRead + " bytes.");
                    fos.write(buffer, 0, read);
            }
            
            
            System.out.println("Type 'over' to return the log file back to Server");
            toSend = kboard_reader.readLine();
            dout.writeUTF(toSend); //c7
            SendFile("testfile.txt");
            //SendLogFile("testfile.txt");
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