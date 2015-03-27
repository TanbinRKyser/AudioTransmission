/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author root
 */

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author root
 */

public class DemoNewClient extends JFrame{
     
    public static final String SERVER_ADDRESS = "10.10.2.7";
    public static final int PORT = 2222;

    public static final int AUDIO_SIZE = 8192;
	
    AudioFormat audioFormat;

    TargetDataLine targetDataLine;
    SourceDataLine sourceDataLine;
    //DataLine.Info dataLineInfo;
         
    AudioInputStream audioInputStream;
    ByteArrayOutputStream byteArrayOutputStream;
        
    Socket clientSock = null;
    
    BufferedOutputStream out = null;
    BufferedInputStream in = null;
        
    public static boolean self_Acquire = false;
    private boolean isCapture = false;
       
    
    //constructor
    
    
    DemoNewClient (){
        
        this.setSize(300,200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new JLabel("Client is connected!"));
      
        //this.pack();
        
        this.setVisible(true);
		
            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            manager.addKeyEventDispatcher( new MyDispatcher() );
        
	try {   
            
            clientSock = new Socket(SERVER_ADDRESS, PORT);
            out = new BufferedOutputStream(clientSock.getOutputStream());
            in = new BufferedInputStream(clientSock.getInputStream());
            

            //Get Audio Format by sampleRate,     
            audioFormat = getAudioFormat();
            
        } catch (UnknownHostException ex) {
                Logger.getLogger(DemoNewClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(DemoNewClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    
    //Capturing the audio data
    
    
    public void capture_Audio(){          
        
        try {
            
            DataLine.Info dataLineInfo = new DataLine.Info( TargetDataLine.class, audioFormat );
            
            targetDataLine = ( TargetDataLine ) AudioSystem.getLine( dataLineInfo );                              
            targetDataLine.open( audioFormat );
            targetDataLine.start();
                
            Thread captureThread = new Thread( new CaptureThread() );
            captureThread.start();     
            
        } catch (LineUnavailableException ex) {
            Logger.getLogger(DemoNewClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    
    class CaptureThread extends Thread{            
        
        byte tempBuffer[] = new byte[5120];
        
        @Override
        public void run(){
            //byteArrayOutputStream = new ByteArrayOutputStream();
            while(true){                    
                while( isCapture ){
                //  System.out.println("V is pressed!!");
                    try {
                        int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                            if ( cnt > 0 ) {
                                out.write(tempBuffer, 0, cnt);
                            }
                    }catch (Exception e) {
                            System.out.println("Can't capture properly");
                    }//End of try-catch block
                }                               
            }
        }//End of Run method
    }
    
    
    //Playing the audio data
    
    
    public void play_Audio(){        
        
        try{
            DataLine.Info dataLineInfo_2 = new DataLine.Info( SourceDataLine.class, audioFormat );
            
            sourceDataLine = ( SourceDataLine ) AudioSystem.getLine( dataLineInfo_2 );
            sourceDataLine.open( audioFormat );
            sourceDataLine.start();
                
            Thread playThread = new Thread( new PlayThread() );
            playThread.start();
        
        }catch(Exception e){
            System.out.println( "Could not play properly" );
        }    
    }
    
        
    class PlayThread extends Thread {
        
        byte Buffer[] = new byte[5120];    
        
        @Override
        public void run(){
            while( true ){
                try{
                    int count = in.read( Buffer, 0, Buffer.length );
                    
                    if(count>0){
                        sourceDataLine.write( Buffer, 0, count );
                    } else {
                        sourceDataLine.drain();
                        sourceDataLine.close();
                    }    
                }catch(Exception e){
                    System.out.println( "playing problem " );
                }//End of try-catch block
            }
        } //End of Run method
    }
        
    
    //Main Method
    
    
    public static void main(String[] args) {
        
        DemoNewClient client = new DemoNewClient();
        client.capture_Audio();
        client.play_Audio();
        
        //tx.captureAudio();
    }
        
	
    //Key pressing
    
    
    private class MyDispatcher implements KeyEventDispatcher {
       
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        
            if( e.getID() == KeyEvent.KEY_PRESSED ) {
                //System.out.println("key pressed!");       
                if( !isCapture && !self_Acquire ){
                   // synchronized (this){
                    isCapture = true;
                    self_Acquire = true;
                    //}
                }
            
                System.out.println( "isCapture = " + isCapture );
                System.out.println( "self_Acquire = " + self_Acquire );
                
            } else if( e.getID() == KeyEvent.KEY_RELEASED ) {
      
                System.out.println("key released!");
                
                if( self_Acquire && isCapture ){
                   // synchronized (this){
                    isCapture = false;
                    self_Acquire = false;
                   // }
                }
                System.out.println( "isCapture---------"+ isCapture );
                System.out.println( "self_Acquire------" + self_Acquire );
                //isCapture = false;
            } else if ( e.getID() == KeyEvent.KEY_TYPED ) {
                    
            }
            return false;
        }
    }             
    
    
    //Audio data formating
    
    
    private AudioFormat getAudioFormat(){
        
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        
        
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian
        );
    }//end getAudioFormat
//===================================//
    
}

