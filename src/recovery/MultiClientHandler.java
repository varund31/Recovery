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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static recovery.Server.ClientMap;

/**
 *
 * @author Varundeep and Vibhav
 */
public class MultiClientHandler extends Thread
{
    final DataInputStream din;
    final DataOutputStream dout; 
    final Socket socket; 
    String ClientName;
    //final String FileName;
    int ClientMoney;
    BufferedReader kboard_reader;
    Utilities util;
    ClientDetails cd;
    //String cname;
    
    MultiClientHandler(Socket socket , DataInputStream din , DataOutputStream dout)
    {
        this.din = din;
        this.dout = dout;
        this.socket = socket;
        //this.ClientName = ClientCounter;
        //this.FileName = FileName;
        //ClientMoney = 50000+ 10*ClientName;
        this.kboard_reader = new BufferedReader(new InputStreamReader(System.in));
        util = new Utilities();
            
    }
    
    public String getMenu()
    {
        return "1.Withdraw\n2.Deposit\n3.Send Log\n4.Notification\n5.Exit\n";
    }

    String getNotification()
    {

    	String[] msgs = ClientMap.get(ClientName).checkNotification().split("|");
    	System.out.println(ClientName + " - Notifications : ");
    	String retval="\n";
    	int i;
    	// msg[0] is always blank that is why started  with 1.
    	for(i =1;i<msgs.length;i++)
        {
    		System.out.print(msgs[i]);
    		retval = retval + msgs[i];
    	}
    	return retval;
    }
    

    
    public void run()
    {
        String Received; 
        String username;
        
        Timestamp ts1 = new Timestamp(System.currentTimeMillis());
        Timestamp prevTimestamp = new Timestamp(System.currentTimeMillis());
        
        int rc;
        
        try 
        {
            System.out.println(din);
            Received = din.readUTF(); // s1 //Username
            ClientName = Received;
            
            String FileName = "s-Client_"+ ClientName + "_log.txt";
            File NewFile = new File(FileName);
            boolean IsCreated = NewFile.createNewFile();

            if(IsCreated)
            {
                System.out.println("Successfully created new file, path:%s"+ NewFile.getCanonicalPath()); 
            }
            else
            {
                System.out.println("File Already Exists");
            }

            if(ClientMap.containsKey(ClientName) && ClientMap.get(ClientName).IsBlocked == false)
            {
                cd = ClientMap.get(ClientName);
            }
            else
            {
                cd = new ClientDetails( ClientName , 50000 , false );
                ClientMap.put(ClientName , cd );
            }
            
            
            if(true)  
            {
                while(true)
                {    
                    FileName = "s-Client_"+ClientName+"_log";
                    dout.writeUTF(getMenu());//s2
                    //System.out.println("Success");
                    rc =Integer.parseInt( din.readUTF() ); // Receive Option Chose by Client //s3
                    System.out.println(rc);
                    
                    if(rc == 1)
                    {
                        dout.writeUTF("Enter Amount you want to Withdraw"); //s4
                        Received = din.readUTF(); //s5
                        int WithdrawMoney = Integer.parseInt(Received);
                        //System.out.println(cd.ClientMoney);
                        
                        if( WithdrawMoney > 0 && WithdrawMoney <= cd.ClientMoney )
                        {
                            cd.ClientMoney -= WithdrawMoney;
                            dout.writeUTF("Amount has been succesfully withdrawn"+ WithdrawMoney); //s6
                            Timestamp ts = new Timestamp(System.currentTimeMillis());
                            String TimeStamp = ts.toString();
                            
                            util.AddLogEntry( FileName +".txt", TimeStamp , "Debit" , WithdrawMoney ,cd.ClientMoney );
                        }
                        else
                        {
                            dout.writeUTF("You dont have Enough Amount");  //s6
                        }
                    }
                    else if(rc == 2)
                    {
                        dout.writeUTF("Enter the Amount you want to Deposit"); //s4
                        Received = din.readUTF(); //s5
                        int DepositMoney = Integer.parseInt(Received);
                        cd.ClientMoney += DepositMoney;
                        dout.writeUTF("Client has Deposited : "+ DepositMoney ); //s6
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        String TimeStamp = ts.toString();
                        
                        util.AddLogEntry(FileName+".txt" ,  TimeStamp , "Credit" , DepositMoney , cd.ClientMoney );
                    }
                    else if( rc == 3 )
                    {

                        //Log Option
                        System.out.println("Sending Client Log File");
                        File fsend = new File(FileName+".txt");
                        System.out.print(fsend.length());
                        dout.writeUTF(Long.toString(fsend.length()));
                        //util.sendLogFile("serverFiles/"+username+"-server",dos);
                        util.SendFile(FileName+".txt", dout); //LogFileTransferFunction();

                        String received = din.readUTF();
                     
                        if(received.equalsIgnoreCase("over"))
                        {
                            Received = din.readUTF(); // To Receive Log File back  // receivedfilesize
                            System.out.println("Received File Size"+Received);
                            util.SaveFile(FileName+"-temp"+".txt", din , Integer.parseInt(Received));

                            boolean CheckFile = util.FileCompare(FileName+"-temp"+".txt" , FileName+".txt" );
                            System.out.println("Before checkFile");
                            if(CheckFile == true)
                            {
                                System.out.println("Putting Checkoint");
                                Timestamp ts = new Timestamp(System.currentTimeMillis());
                                prevTimestamp = ts;
                              
                                //PutCheckPoint();
                            }
                            else
                            {
                                //Recover all operations from log file, 
                                //having Timestamp greater than last checkpoint
                                //AlertOtherFunction();
                                cd.IsBlocked = true;
                                ClientMap.put(ClientName , cd);
                                String fname= FileName+".txt";
                                System.out.println(fname);
                                //FileReader fr=
                                System.out.println("File fr");
                                BufferedReader br=new BufferedReader(new FileReader(fname));
                                System.out.println("buffer br");
                                String overrideFile = "";
                                String currTimestamp = "dummy";
                                System.out.println("enter do while");
                                String logLine = br.readLine();
                                String[] logEntry= logLine.split("\\t");
                                currTimestamp = logEntry[0];
                                ts1 = Timestamp.valueOf(currTimestamp);  
                                while(prevTimestamp.compareTo(ts1) > 0)
                                {
                                    overrideFile = overrideFile + logLine + "\n";
                                    System.out.println("checkpoint do while enter");
                                    logLine = br.readLine();
                                  //  System.out.println("checkpoint do while 1");
                                    
                                  //  System.out.println("checkpoint do while 2");                                    
                                    logEntry= logLine.split("\\t");
                                  //  System.out.println("checkpoint do while 3");
                                    currTimestamp = logEntry[0];
                                   // System.out.println("checkpoint do while 4");
                                    System.out.println(logEntry[0]+  " and " + logEntry[1]);
                                  //  System.out.println("TimeStamp : "+currTimestamp);
                                    ts1 = Timestamp.valueOf(currTimestamp);  
                                    
                                    System.out.println("override : " + overrideFile);
                                 
                                    
                                }
                                br.close();
                               // fr.close();
                                FileWriter fw=new FileWriter(FileName+".txt");
                                fw.write(overrideFile);
                                fw.close();
                                System.out.println("Error");
                                
                                for(Map.Entry m:ClientMap.entrySet())
                                {   
                                         if (!m.getKey().equals(ClientName))
                                         {
                                                ClientMap.get(m.getKey()).Notify(ClientName + " has been blocked\n ");
                                         }

                                } 
                            }
                            File f = new File(FileName+"-temp.txt");
                            f.delete();
                        }
                        System.out.println("End of Log Function");
                    }
                    else if(rc == 4)
                    {
                        String toSend = getNotification();
                        dout.writeUTF(toSend);
                    }
                    else if(rc==5)
                    {
                        System.out.println("Client Number"+ClientName + " has Exited");
                        this.din.close(); 
                        this.dout.close(); 
                        break;
                    }
                    else
                    {
                        dout.writeUTF("You have Entered wrong choice\nPress Any Key to Continue");
                        Received = din.readUTF();
                                
                        
                    }
                }
            }
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MultiClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

