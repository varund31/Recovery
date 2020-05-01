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
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;
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
    
    public void AddLogEntry(String Timestamp , String flag , int Money , int TotalMoney)
    {
        try
        {
            FileWriter writer = new FileWriter("Client_1_log.txt",true);
            writer.write(Timestamp+"\t"+Money+"\t"+flag+"\t"+TotalMoney+"\n");
            writer.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run()
    {
        String Received; 
        String ToReturn;
        int rc;
        //String TimeStamp = "";
        try 
        {
            System.out.println(din);
            Received = din.readUTF(); // s1
            
            
            if(Received.equals("Start"))  
            {
                dout.writeUTF("1.Withdraw\n2.Deposit\n3.Send Log\n4.Exit\n");//s2
                //Received = din.readUTF();
            
                System.out.println("Success");
                rc =Integer.parseInt( din.readUTF() ); // Receive Option Chose by Client //s3
                System.out.println(rc);
                if(rc == 1)
                {
                    dout.writeUTF("Enter Amount you want to Withdraw"); //s4
                    Received = din.readUTF(); //s5
                    int WithdrawMoney = Integer.parseInt(Received);
                    System.out.println(ClientMoney);
                    if( WithdrawMoney > 0 && WithdrawMoney <= ClientMoney )
                    {
                        ClientMoney -= WithdrawMoney;
                        dout.writeUTF("Amount has been succesfully withdrawn"+ WithdrawMoney); //s6
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        String TimeStamp = ts.toString();
                        AddLogEntry( TimeStamp , "Debit" , WithdrawMoney ,ClientMoney );
                        
                    }
                    else
                    {
                        dout.writeUTF("You dont have Enough Amount");  //s6
                    }
                }
                else if(rc == 2)
                {
                    dout.writeUTF("Enter the Amoutn you want to Deposit"); //s4
                    Received = din.readUTF(); //s5
                    int DepositMoney = Integer.parseInt(Received);
                    ClientMoney += DepositMoney;
                    dout.writeUTF("Client has Deposited : "+ DepositMoney ); //s6
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    String TimeStamp = ts.toString();
                    AddLogEntry( TimeStamp , "Credit" , DepositMoney , ClientMoney );
                }
                else if( rc == 3 )
                {
                    
                    //Log Option
                    dout.writeUTF("Sending Client Log File");
                    LogFileTransferFunction();
                    
                    Received = din.readUTF(); // To Receive Log File back
                    LogFileReceiveFunction();
                    boolean CheckFile = CheckLogFile();
                    
                    if(CheckFile == true)
                    {
                        PutCheckPoint();
                    }
                    else
                    {
                        //Recover all operations from log file, 
                        //having Timestamp greater than last checkpoint
                        AlertOtherFunction();
                    }
                    
                    
                }
                else
                {
                    
                }*/
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
                }*/         }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

