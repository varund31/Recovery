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
    BufferedReader kboard_reader;
    Utilities util;
    
    
    MultiClientHandler(Socket socket , DataInputStream din , DataOutputStream dout, int ClientCounter , String FileName)
    {
        this.din = din;
        this.dout = dout;
        this.socket = socket;
        this.ClientNumber = ClientCounter;
        this.FileName = FileName;
        ClientMoney = 50000;
        this.kboard_reader = new BufferedReader(new InputStreamReader(System.in));
            
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
            
            
            if(Received.equalsIgnoreCase("start"))  
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
                        util.AddLogEntry( TimeStamp , "Debit" , WithdrawMoney ,ClientMoney );
                        
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
                    util.AddLogEntry( TimeStamp , "Credit" , DepositMoney , ClientMoney );
                }
                else if( rc == 3 )
                {
                    
                    //Log Option
                    System.out.println("Sending Client Log File");
                    
                    util.SendFile("Client_1_log.txt" , dout); //LogFileTransferFunction();
                    //SendLogFile("Client_1_log.txt");
                    /*
                    //DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    FileInputStream fis = new FileInputStream("Client_1_log.txt");
                    byte[] buffer = new byte[4096];

                    while (fis.read(buffer) > 0) 
                    {
                            dout.write(buffer);
                    }

                    fis.close();*/
                    System.out.println("Wirte Here");
                    String temp = kboard_reader.readLine();
                    dout.writeUTF(temp);
                    System.out.println(temp);
                    Received = din.readUTF(); // To Receive Log File back //s7
                    if(Received.equalsIgnoreCase("over"))
                    {
                        //ReceiveLogFile("NewFile.txt");
                        //SaveFile(socket);//LogFileReceiveFunction();
                        boolean CheckFile = util.FileCompare("receivedfile.txt" , "Client_1_log.txt");
                    
                        if(CheckFile == true)
                        {
                            System.out.println("Putting Checkoint");
                            //PutCheckPoint();
                        }
                        else
                        {
                            //Recover all operations from log file, 
                            //having Timestamp greater than last checkpoint
                            //AlertOtherFunction();
                        }
                    }
                    
                    
                    
                }
                else
                {
                    
                }
             }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

