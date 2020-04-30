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
    int ClientAmount;
    
    MultiClientHandler(Socket socket , DataInputStream din , DataOutputStream dout, int ClientCounter , String FileName)
    {
        this.din = din;
        this.dout = dout;
        this.socket = socket;
        this.ClientNumber = ClientCounter;
        this.FileName = FileName;
        ClientAmount = 50000;
    }
    
    public void run()
    {
        String Received; 
        String ToReturn;
        String TimeStamp = "";
        try 
        {
            System.out.println(din);
            Received = din.readUTF();
            if(Received.equals("Start"))
            {
                System.out.println("Success");
                Received = din.readUTF(); // Receive Option Chose by Client
                if(Received.equalsIgnoreCase("withdraw"))
                {
                    dout.writeUTF("Enter Amount you want to Withdraw");
                    Received = din.readUTF();
                    int received_amount = Integer.parseInt(Received);
                    
                    if(received_amount > 0 && received_amount > ClientAmount)
                    {
                        dout.writeUTF("Client has Requested to Withdraw Money "+received_amount);
                        ClientAmount -= received_amount;
                        AddLogEntry(Timestamp , -1 , ClientAmount );
                    }
                    else
                    {
                        dout.writeUTF("YOu dont have Enough Amount"); 
                    }
                }
                else if(Received.equalsIgnoreCase("deposit"))
                {
                    
                }
                else
                {
                    //Log Option
                }
                /*FileReader inputFile = new FileReader("Client_1_log.txt");
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
                }*/            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
