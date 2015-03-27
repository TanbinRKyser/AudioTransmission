/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.IOException;
import static java.lang.Thread.sleep;


/**
 *
 * @author root
 */


class WriteThread extends Thread {

    private volatile Thread blinker;
    
    ClientThread writeData;
    
    byte[] data;

    
    /*
    * Constructor of WriteThread
    */
    
    WriteThread( byte[] audioData, ClientThread clientThread ) {
        
        writeData = clientThread;
        data = audioData;
       // blinker = Thread.currentThread();
        
    }//End of constructor

    
    /*
    * sstop method
    */
    
    public void sstop(){
        
        blinker = null;
        //blinker.interrupt();    
    }
    
    
    /*
    * Overriding the run method
    */
    
    @Override
    public void run() {
   
        blinker = Thread.currentThread();
        //blinker = writData;
        
        try{
            writeData.os.write( data );
            //blinker.s
            sleep( 1111 );
            
        }catch( IOException | InterruptedException e ){
            System.out.println( "Con't write prolerly to " + writeData.clientSocket.getInetAddress() );
        }
        //repaint();
        finally{
            this.sstop();
        }
        
    }//End of Run method
    
}

