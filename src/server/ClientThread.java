/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author root
 */
public class ClientThread extends Thread {
    
    
    private String clientName = null;
    //private DataInputStream is = null;
    //private PrintStream os = null;
    
    public BufferedInputStream is = null;
    public BufferedOutputStream os = null;
  
    public Socket clientSocket = null;
  
    public final ClientThread[] threads;
  
    private int maxClientsCount;

    
    //Constructor
    public ClientThread( Socket clientSocket, ClientThread[] threads ){
     
        this.clientSocket = clientSocket;
        this.threads = threads;
        
        maxClientsCount = threads.length;
    
        /*
            try{
                //this.is = new DataInputStream(clientSocket.getInputStream());
                //this.os = new PrintStream(clientSocket.getOutputStream());
            }
            catch(Exception e){
                System.out.println("fsafa");
            }
        */
        
    }//End of constructor ClientThread
 
    
    
    //Run method of Thread 
    
    @Override
    public void run() {
       
        int maxClientsCount = this.maxClientsCount;
        
        ClientThread[] threads = this.threads;
    
        System.out.println(" Start recording test.....");
        
        try {
            
            is = new BufferedInputStream(clientSocket.getInputStream());
            os = new BufferedOutputStream(clientSocket.getOutputStream());
        
            /*
             * Create input and output streams for this client.
             */

            //is = new DataInputStream(clientSocket.getInputStream());
            //os = new PrintStream(clientSocket.getOutputStream());
        
            String name;
            /*
                while (true) {
                    os.println("Enter your name.");
                    name = is.readLine().trim();

                    if (name.indexOf('@') == -1) {
                        break;
                    } else {
                        os.println("The name should not contain '@' character.");
                    }
                }
            * /
          
            /* Welcome the new the client. */
            
            //os.println("Welcome " + name
            //  + " to our chat room.\nTo leave enter /quit in a new line.");
            
            /*
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] == this) {
                            clientName = "@" + threads[i].getName();
                            break;
                        }
                    }
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this) {
                            threads[i].os.println("*** A new user " + "name"
                            + " entered the chat room !!! ***");
                        }
                    }
                }
            */
            
            byte[] audioData = new byte[ 512 * 10 ];
           
            /* Start the conversation. */
        
            while( is.read( audioData ) != -1 ) {

                // String line = is.;
                /*
                    if (line.startsWith("/quit")) {
                      break;
                    }
                * /
            
                /* The message is public, broadcast it to all other clients. */
                for(int i=0 ; i<threads.length;i++){
                    //Thread write_Thread = new Thread(new write_Thread(audioData));
                    //write_Thread.start();
                }//End of for loop
          
                synchronized (this) {
                //public static  data_Sending_Thread[] data_Send = new data_Sending_Thread[threads.length()];
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != this && threads[i] != null ) {

                        // threads[i].os.write(audioData);
                        //threads[i].os.flush();

                        Thread write_Th = new Thread(new WriteThread(audioData,threads[i]));
                        write_Th.start();
                        
                        }
                    }
                }//End of Synchronization
                
            }//End of While loop
      
            /*   
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                      if (threads[i] != null && threads[i] != this
                          && threads[i].clientName != null) {
                        threads[i].os.println("*** The user " + name
                            + " is leaving the chat room !!! ***");
                      }
                    }
                }
                os.println("*** Bye " + name + " ***");    
            */
        
            /*
             * Clean up. Set the current thread variable to null so that a new client
             * could be accepted by the server.
             */
            
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
      
            /*
            * Close the output stream, close the input stream, close the socket.
            */
        
            is.close();
            os.close();
            clientSocket.close();
            
        } catch ( IOException e ) {
            //Catch the exception of client thread run method.
        }
    }
}
