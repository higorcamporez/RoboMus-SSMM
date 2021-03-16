/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import com.illposed.osc.OSCBundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import robomus.instrument.Instrument;
import robomus.server.Server;

/**
 * Classe para teste do BongoBot v2 com o SSMM (sem utilizar redes neurais)
 * @author Higor
 */
public class TestDelayRaspberry {
    private Server server;
    
    public TestDelayRaspberry() {
        
        this.server = new Server(1234);
        server.receiveMessages();
    }
    /**
     * Envia mensagens ao dos robôs BongoBot
     * @param nMsgs quantidade de mensagens
     */
    public void msgsWithoutDelay(int nMsgs){
        Random rand = new Random();
        Instrument instrument1 = server.findInstrument("/BongoBot");
        Instrument instrument2 = server.findInstrument("/BongoBot2");
        

        long timeRef = System.currentTimeMillis() + 3000;
        int tempo = 500;
        Timer timer = new Timer();
        long t;
        for (int i = 0; i < nMsgs; i++) {
            
            t = timeRef + (i+1)*tempo;
            if(instrument1 != null) {
                //instrument1.setCalculateDelay(Boolean.FALSE);
                OSCMessage oscMessage1 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage1.addArgument((long)i); //id
                //oscMessage1.addArgument(rand.nextInt(2)); //solenoide 0 -> 1
                //oscMessage1.addArgument(rand.nextFloat()); //velocity
                oscMessage1.addArgument((int)0); //solenoide 0 -> 1
                oscMessage1.addArgument((float)1); //velocity
                
                Date date = new Date(t);
                
                OSCBundle oscBundle1 =  new OSCBundle();
                oscBundle1.addPacket(oscMessage1);
                oscBundle1.setTimestamp(date);

                server.addMessage(oscBundle1,i);
            }
           if(instrument2 != null) {
       
                //instrument2.setCalculateDelay(Boolean.FALSE);
                OSCMessage oscMessage2 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                
                oscMessage2.addArgument((long)i); //id
                //oscMessage2.addArgument(rand.nextInt(2)); //solenoide 0->1
                //oscMessage2.addArgument(rand.nextFloat()); // velocity
                oscMessage2.addArgument((int)0); //solenoide 0->1
                oscMessage2.addArgument((float)1); // velocity
                
                Date date2 = new Date(t);
                //System.out.println("t = " + date2.getTime());
                OSCBundle oscBundle2 =  new OSCBundle();
                oscBundle2.addPacket(oscMessage2);
                oscBundle2.setTimestamp(date2);

                server.addMessage(oscBundle2,i);
            }
            
        } 
        /*
        t = timeRef + 10000 + (nMsgs)*500;
        if(instrument1 != null) {
            instrument1.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage1 = new OSCMessage(
                    instrument1.getOscAddress()+"/show"
            );
             
            Date date = new Date(t);

            OSCBundle oscBundle1 =  new OSCBundle();
            oscBundle1.addPacket(oscMessage1);
            oscBundle1.setTimestamp(date);

            server.addMessage(oscBundle1,1234);
        }
       if(instrument2 != null) {

            //instrument2.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage2 = new OSCMessage(
                    instrument2.getOscAddress()+"/show"
            );

            Date date2 = new Date(t);

            OSCBundle oscBundle2 =  new OSCBundle();
            oscBundle2.addPacket(oscMessage2);
            oscBundle2.setTimestamp(date2);

            server.addMessage(oscBundle2,1234);
        }
        */
            
    }
    

    public void msgsWithRithymic(int nMsgs, int bpm){
        Instrument instrument1 = server.findInstrument("/BongoBot");
        if(instrument1 != null) {
            //instrument1.setCalculateDelay(Boolean.TRUE);
            //instrument1.loadModel();
        }        
        
        Instrument instrument2 = server.findInstrument("/BongoBot2");
        if(instrument2 != null) {
            //instrument2.setCalculateDelay(Boolean.TRUE);
            //instrument2.loadModel();
        }
        
        long timeRef = System.currentTimeMillis() + 5000;
        int tempo = (int)((60.0/bpm)*1000);
        System.out.println("tempo ="+tempo);
        long t = timeRef;
        for (int i = 0; i < nMsgs; i++) {
            
            
            if(instrument1 != null) {
                
                OSCMessage oscMessage1 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage1.addArgument((long)i+100);

                Date date = new Date(t);
                
                OSCBundle oscBundle1 =  new OSCBundle();
                oscBundle1.addPacket(oscMessage1);
                oscBundle1.setTimestamp(date);

                server.addMessage(oscBundle1,i+100);
                
                //----
                OSCMessage oscMessage2 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage2.addArgument((long)i+101);

                Date date2 = new Date(t+2*tempo);
                
                OSCBundle oscBundle2 =  new OSCBundle();
                oscBundle2.addPacket(oscMessage2);
                oscBundle2.setTimestamp(date2);

                server.addMessage(oscBundle2,i+101);
                
                //----
                OSCMessage oscMessage3 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage3.addArgument((long)i+102);

                Date date3 = new Date(t+3*tempo);
                
                OSCBundle oscBundle3 =  new OSCBundle();
                oscBundle3.addPacket(oscMessage3);
                oscBundle3.setTimestamp(date3);

                server.addMessage(oscBundle3,i+102);
                
                //----
                OSCMessage oscMessage4 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage4.addArgument((long)i+103);

                Date date4 = new Date(t+5*tempo);
                
                OSCBundle oscBundle4 =  new OSCBundle();
                oscBundle4.addPacket(oscMessage4);
                oscBundle4.setTimestamp(date4);

                server.addMessage(oscBundle4,i+103);
                
                //----
                OSCMessage oscMessage5 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage5.addArgument((long)i+104);

                Date date5 = new Date(t+6*tempo);
                
                OSCBundle oscBundle5 =  new OSCBundle();
                oscBundle5.addPacket(oscMessage5);
                oscBundle5.setTimestamp(date5);

                server.addMessage(oscBundle5,i+104);
               
            }
           if(instrument2 != null) {
       
                OSCMessage oscMessage1 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                oscMessage1.addArgument((long)i+100);

                Date date = new Date(t);
                
                OSCBundle oscBundle1 =  new OSCBundle();
                oscBundle1.addPacket(oscMessage1);
                oscBundle1.setTimestamp(date);

                server.addMessage(oscBundle1,i+100);
                
                //----
                OSCMessage oscMessage2 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                oscMessage2.addArgument((long)i+101);

                Date date2 = new Date(t+2*tempo);
                
                OSCBundle oscBundle2 =  new OSCBundle();
                oscBundle2.addPacket(oscMessage2);
                oscBundle2.setTimestamp(date2);

                server.addMessage(oscBundle2,i+101);
                
                //----
                OSCMessage oscMessage3 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                oscMessage3.addArgument((long)i+102);

                Date date3 = new Date(t+3*tempo);
                
                OSCBundle oscBundle3 =  new OSCBundle();
                oscBundle3.addPacket(oscMessage3);
                oscBundle3.setTimestamp(date3);

                server.addMessage(oscBundle3,i+102);
                
                //----
                OSCMessage oscMessage4 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                oscMessage4.addArgument((long)i+103);

                Date date4 = new Date(t+5*tempo);
                
                OSCBundle oscBundle4 =  new OSCBundle();
                oscBundle4.addPacket(oscMessage4);
                oscBundle4.setTimestamp(date4);

                server.addMessage(oscBundle4,i+103);
                
                //----
                OSCMessage oscMessage5 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                oscMessage5.addArgument((long)i+104);

                Date date5 = new Date(t+6*tempo);
                
                OSCBundle oscBundle5 =  new OSCBundle();
                oscBundle5.addPacket(oscMessage5);
                oscBundle5.setTimestamp(date5);

                server.addMessage(oscBundle5,i+104);
                
            }
            t = timeRef + (i+1)*8*tempo;
        }
        
        
        /*
        t = timeRef + 10000 + (nMsgs)*500;
        if(instrument1 != null) {
            instrument1.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage1 = new OSCMessage(
                    instrument1.getOscAddress()+"/show"
            );
             
            Date date = new Date(t);

            OSCBundle oscBundle1 =  new OSCBundle();
            oscBundle1.addPacket(oscMessage1);
            oscBundle1.setTimestamp(date);

            server.addMessage(oscBundle1,1234);
        }
       if(instrument2 != null) {

            instrument2.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage2 = new OSCMessage(
                    instrument2.getOscAddress()+"/show"
            );

            Date date2 = new Date(t);

            OSCBundle oscBundle2 =  new OSCBundle();
            oscBundle2.addPacket(oscMessage2);
            oscBundle2.setTimestamp(date2);

            server.addMessage(oscBundle2,1234);
        }
        */
    }
    
    public static String menuTests(){
        System.out.println("============= menu ===============");
        System.out.println("(0) Teste sem delay mec�nico compensado");
        System.out.println("(1) Teste ritimico");
        System.out.println("==================================");
        Scanner ler = new Scanner(System.in);
        String op = ler.nextLine();
        
        return op;
    }
    
    
    public static void main(String[] args) {
        
        TestDelayRaspberry c = new TestDelayRaspberry();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
            System.out.println("(2) Tests");
            System.out.println("==================================");
            Scanner ler = new Scanner(System.in);
            String op = ler.nextLine();
            
            switch(op){
                case "0":
                    c.server.printInstruments();
                    break;
                case "1":
                    c.server.printClients();
                    break;
                case "2":
                    String aux2 = TestDelayRaspberry.menuTests();
                    
                    switch (aux2) {
                        case "0":
                            System.out.println("Number of beats");
                            int nBeats = ler.nextInt(); 
                            c.msgsWithoutDelay(nBeats);
                            break;
                        case "1":
                            System.out.println("Number of beats");
                            nBeats = ler.nextInt(); 
                            c.msgsWithRithymic(nBeats,200);
                            break;
                    }
                break;
                
                default:
                    System.out.println("Option not found\n");
            }
       
        }
        
    }
    
}
