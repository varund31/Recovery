/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recovery;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
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
    final int ClientNumber;
    final String FileName;
    MultiClientHandler(Socket socket , DataInputStream din , DataOutputStream dout, int ClientCounter , String FileName)
    {
        this.din = din;
        this.dout = dout;
        this.socket = socket;
        this.ClientNumber = ClientCounter;
        this.FileName = FileName;
        
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
                try
                {
                    FileReader inputFile = new FileReader("Client_1_log.txt");
                    try
                    {
                        Scanner parser = new Scanner(inputFile);
                        while (parser.hasNextLine())
                        {
                                        String line = parser.nextLine();
                                String[] att = line.split(",");
                                    System.out.println(att[0]);
                                        System.out.println(att[1]);
                                        System.out.println(att[2]);

                        }

                    }
                    finally
                    {
                        inputFile.close();
                    }
                }
                catch(FileNotFoundException exception)
                {
                        System.out.println(" not found");
                }
                catch(IOException exception)
                {
                        System.out.println("Unexpected I/O error occured.");
                }
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
