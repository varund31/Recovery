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
    
    public boolean FileCompare(String file1, String file2) throws FileNotFoundException, IOException
    {
        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        BufferedReader reader2 = new BufferedReader(new FileReader(file2));
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();
        boolean areEqual = true;
        int lineNum = 1;
        while (line1 != null || line2 != null)
        {
            if(line1 == null || line2 == null)
            {
                areEqual = false;
                break;
            }
            else if(! line1.equalsIgnoreCase(line2))
            {
                areEqual = false;
                break;
            }
            line1 = reader1.readLine();
            line2 = reader2.readLine();
            lineNum++;
        }
        if(areEqual)
        {
            System.out.println("Two files have same content.");
            reader1.close();
            reader2.close();
            return true;
        }
        else
        {
            System.out.println("Two files have different content. They differ at line "+lineNum);
            System.out.println("File1 has "+line1+" and File2 has "+line2+" at line "+lineNum);
            reader1.close();
            reader2.close();
    
            return false;
        }

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
    
    public void SendFile(String file) throws IOException 
    {
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) 
                {
			dout.write(buffer);
		}
		
		fis.close();
		dout.close();	
    }
    
    
    private void SaveFile(Socket clientSock) throws IOException 
    {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fos = new FileOutputStream("receivedfile.txt");
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
        dis.close();
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
                    System.out.println("Sending Client Log File");
                    
                    SendFile("Client_1_log.txt"); //LogFileTransferFunction();
                    
                    //Received = din.readUTF(); // To Receive Log File back //s7
                    if(Received.equalsIgnoreCase("over"))
                    {
                        SaveFile(socket);//LogFileReceiveFunction();
                        boolean CheckFile = FileCompare("receivedfile.txt" , "Client_1_log.txt");
                    
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

