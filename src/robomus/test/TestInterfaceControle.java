/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import java.util.Scanner;
import robomus.instrument.Instrument;
import robomus.server.Server;

/**
 * Classe para teste de comunicação com a interface de controle por linha de
 * comando. Nesse teste são criados 2 robôs fictícios para teste.
 * @author higor
 */
public class TestInterfaceControle {
    private Server server;
    
    public TestInterfaceControle() {
        this.server = new Server(1234);
        server.receiveMessages();
        // Criando dois instrumentos (robôs) fictícios para testes
        Instrument i1 = new Instrument();
        i1.setName("instr1");
        i1.setOscAddress("/instr1");
        i1.setSpecificProtocol("</play;time_i></stop;time_i></next;num_i>");
        Instrument i2 = new Instrument();
        i2.setName("instr2");
        i2.setOscAddress("/instr2");
        i2.setSpecificProtocol("</playNote;note_s;string_i></play;fret_i;sting1__i>");
        //add os robôs ao servidor
        server.getInstruments().add(i1);
        server.getInstruments().add(i2);
        
        System.out.println("TestInterfaceControle iniciado");
                
               
    }
    
    public static void main(String[] args) {
        
        TestInterfaceControle c = new TestInterfaceControle();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
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
                default:
                    System.out.println("Option not found\n");
            }
       
        }
        
    }
}
