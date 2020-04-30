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
    int ClientMoney;
    
    MultiClientHandler(Socket socket , DataInputStream din , DataOutputStream dout, int ClientCounter , String FileName)
    {
        this.din = din;
        this.dout = dout;
        this.socket = socket;
        this.ClientNumber = ClientCounter;
        this.FileName = FileName;
        ClientMoney = 50000;
    }
    
    public void AddLogEntry(String Timestamp , int flag , int Money , int TotalMoney)
    {
        
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
                    int WithdrawMoney = Integer.parseInt(Received);
                    
                    if( WithdrawMoney > 0 && WithdrawMoney > ClientMoney )
                    {
                        dout.writeUTF("Client has Requested to Withdraw Money "+ WithdrawMoney);
                        ClientMoney -= WithdrawMoney;
                        AddLogEntry( Timestamp , -1 , WithdrawMoney ,ClientMoney );
                    }
                    else
                    {
                        dout.writeUTF("You dont have Enough Amount"); 
                    }
                }
                else if(Received.equalsIgnoreCase("deposit"))
                {
                    dout.writeUTF("ENter the Amoutn you want to Deposit");
                    Received = din.readUTF();
                    int DepositMoney = Integer.parseInt(Received);
                    dout.writeUTF("Client has Requested to Withdraw Money "+ DepositMoney );
                    ClientMoney += DepositMoney;
                    AddLogEntry( Timestamp , 1 , DepositMoney , ClientMoney );
                }
                else if(Received.equalsIgnoreCase("Log"))
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
