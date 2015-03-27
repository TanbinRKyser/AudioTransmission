/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author root
 */
public class DemoServer {
    
    static ServerSocket serverSocket = null;
    static Socket clientSocket = null;
    
    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    
    private static final ClientThread[] threads = new ClientThread[ maxClientsCount ];
  
    
    //Main method Goes here
    
    
    public static void main(String args[]) {    
        //Channel port no. for server socket
        int portNumber = 2222;
      
        try {
            serverSocket = new ServerSocket( portNumber );
            
            System.out.println( "LISTENING ON PORT: " + portNumber );
            
        } catch( IOException e ){
            System.out.println( e );
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                //clientSocket.setTcpNoDelay(true);
                
                System.out.println( "CONNECTION RECEIVED FROM PC: " + clientSocket.getRemoteSocketAddress() );
                
                int i = 0;
               
                //When server is empty
                
                for (i = 0; i < maxClientsCount; i++) { 
                    System.out.println( clientSocket.getInetAddress() );
                
                    if( threads[ i ] == null ) {
                        
                        ( threads[ i ] = new ClientThread( clientSocket, threads ) ).start();
                        break;
                    }
                }
                
                //Checking the server is busy or not.
                
                if ( i == maxClientsCount ) {
                    
                    PrintStream os = new PrintStream( clientSocket.getOutputStream() );
                    
                    os.println( "Server too busy. Try later." );
                    os.close();
                    clientSocket.close();
                }
                
            } catch (IOException e) {
                System.out.println("FDSFS");
            }
        }   

    }
}