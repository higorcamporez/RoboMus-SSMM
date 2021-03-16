/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.IOException;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.util.Note;
import robomus.util.Notes;
import robomus.util.PrintOSCMessage;

        
/**
 * Classe que representa um instrumento RoboMus
 * @author Higor
 */

public class Instrument{
    
    protected String name; // nome do instrumento   
    protected int polyphony; // quantidade de notas
    protected String OscAddress; //endereço do OSC do instrumento
    protected int sendPort; // porta para envio msgOSC
    protected int receivePort; // porta pra receber msgOSC
    protected String FamilyType; //tipo do instrumento
    protected String specificProtocol; //procolo especifico do robo
    protected String ip; //ip do instrumento
    protected int threshold;
    protected List<Action> actions; //lista de ações do instrumentos
    protected OSCPortOut sender = null; //instancia para enviar mensagens OSC
    
    public Instrument(){
        this.actions = new ArrayList<Action>();
    }

    public Instrument(String OscAddress) {
        this();
        this.OscAddress = OscAddress;
        
    }
    
    public Instrument(String name, int polyphony, String OscAddress,
            int sendPort, int receivePort,
            String FamilyType, String specificProtocol,
            String ip, int threshold) {
        
        this.name = name;
        this.polyphony = polyphony;
        this.OscAddress = OscAddress;
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        this.FamilyType = FamilyType;
        this.specificProtocol = specificProtocol;
        this.ip = ip;
        this.threshold = threshold;
        this.actions = new ArrayList<Action>();
        try {
            this.sender = new OSCPortOut(InetAddress.getByName(this.getIp()), this.getReceivePort());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {  
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setActions();
        
    }
    /**
     * Metodo que faz a conversão do protocolo específico do instrumento em 
     * string para uma lista de ações
     */
    public void setActions(){
        String strActions[] = this.specificProtocol.split(">");
        List actionParametersName = new ArrayList<String>();
       
        for (String strAct : strActions){  
            
            Action action = new Action();
            //retira '<'
            strAct = strAct.substring(1, strAct.length());
           
            //separando os parametros da ação
            String strArgs[] = strAct.split(";");
            
            //System.out.println("args"+strArgs.length +strArgs[0]);
            //adicionando o nome da a��o
            action.setActionAddress(strArgs[0]);
            
            //lista de argumentos
            List argsList = new ArrayList<>();
            
            for (int i  = 1; i < strArgs.length; i++) {
                 
                String name = strArgs[i].split("_")[0];
                String type = strArgs[i].split("_")[1];

                    
                if(type.equals("n")){ //se for do tipo note(n)
                    Argument argument = new Argument(name, 'n');
                    argsList.add(argument);
                }else if(type.equals("i")){ //se for do tipo inteiro(i)
                    Argument argument = new Argument(name, 'i');
                    argsList.add(argument);
                }else if(type.equals("f")){ //se for do tipo float(s)
                    Argument argument = new Argument(name, 'f');
                    argsList.add(argument);
                }else if(type.equals("s")){ //se for do tipo string(s)
                    Argument argument = new Argument(name, 's');
                    argsList.add(argument);
                }
             
            }
            action.setArguments(argsList);
            this.actions.add(action);

        }
        
    }
    
    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }
    
    public String getIp() {
        return this.ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPolyphony() {
        return polyphony;
    }

    public void setPolyphony(int polyphony) {
        this.polyphony = polyphony;
    }

    public String getOscAddress() {
        return OscAddress;
    }

    public void setOscAddress(String OscAddress) {
        this.OscAddress = OscAddress;
    }

    public int getSendPort() {
        return sendPort;
    }

    public void setSendPort(int sendPort) {
        this.sendPort = sendPort;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public String getFamilyType() {
        return FamilyType;
    }

    public void setFamilyType(String FamilyType) {
        this.FamilyType = FamilyType;
    }

    public String getSpecificProtocol() {
        return specificProtocol;
    }

    public void setSpecificProtocol(String specificProtocol) {
        this.specificProtocol = specificProtocol;
        this.setActions();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Instrument{" + "name=" + name +
                ", polyphony=" + polyphony +
                ", OscAddress=" + OscAddress +
                ", sendPort=" + sendPort +
                ", receivePort=" + receivePort +
                ", typeFamily=" + FamilyType +
                ", specificProtocol=" + specificProtocol +
                ", ip=" + ip +
                ", threshold=" + threshold + '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instrument other = (Instrument) obj;
        if (!Objects.equals(this.OscAddress, other.OscAddress)) {
            return false;
        }
        return true;
    }
    /**
     * Classe que envia um Bundle do padrão OSC
     * @param oscBundle Bundle a ser enviado
     */ 
    public void send(OSCBundle oscBundle){
        if(this.sender == null){
            try {
                this.sender = new OSCPortOut(InetAddress.getByName(this.getIp()), this.getReceivePort());
            } catch (UnknownHostException ex) {
                Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {  
                Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            sender.send(oscBundle);
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Metodo que retorna uma lista com os tipo dos 
     * @param oscMessage
     * @return 
     */
    /*
    public List getArgumentsType(OSCMessage oscMessage){
        Action a = new Action();
        String[] dividedAddress = divideAddress(oscMessage.getAddress());
        String actionAddress = '/'+dividedAddress[1];
        a.setActionAddress(actionAddress);

        List types = null;

        int index = this.actions.indexOf(a);

        if(index != -1){
            types = this.actions.get(index).getArgumentsType();
        }

        return types;
    }
    */
    
    /**
     * Metodo para dividir um endereço osc com base no /
     * @param address Endereço OSC
     * @return O endereço OSC separado
     */
    public String[] divideAddress(String address){
        String aux = address;
        if (aux.startsWith("/")) {
            aux = address.substring(1);
        }

        String[] split = aux.split("/", -1);

        return split;
    }

    
}
