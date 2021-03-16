/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.server;

import robomus.instrument.Instrument;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.util.PrintOSCMessage;

/**%
 * Classe principal que representa o SSMM 
 * (Servidor de Sincronização de Mensagens Multimidia)
 * @author Higor
 */
public class Server {
    private int port; //porta para conexão OSC
    private List<Instrument> instruments; // lista de instrumentos (Robôs)
    private List<Client> clients; // lista de cliente (interface de controle)
    FileWriter arq, arq2, arq3; 
    PrintWriter laplapArq, laplap2Arq, infoSaidaArq;
    public long id; //controlador de identificador das mensagens
    private String oscAdress; //endereço osc do servidor
    private String name; //nome do servidor
    private SimpleDateFormat dateFormat; //
    private int lastIdReceived;
    private volatile Buffer buffer; //buffer para "armazenar" as mensagens antes de enviar aos robôs
    private int networkDelay; //atraso médio de rede
    
    /**
     * Contrutor para o SSMM
     * @param port porta de comunicação para ser usada pelo servidor
     */
    public Server(int port) {
        this.port = port;
        this.oscAdress = "/server";
        this.name = "server";
        this.clients = new ArrayList<>();
        this.instruments = new ArrayList<>();
        this.lastIdReceived = -1;       
        this.networkDelay = 2000; //ms
        this.id = 0;

        //thread do buffer de mensagens
        this.buffer =  new Buffer();
        this.buffer.setMessages(new ArrayList<RoboMusMessage>());
        this.buffer.start();
            
        //

        System.out.println("server started");
    }
    
    /**
     * Metodo que trata a mensagem de handshake de um dado instrumento (robô).
     * salvando esse robô na lista de instrumento do SSMM e retornando uma
     * mensagem ao mesmo contendo as informações do SSMM
     * @param message mensagem que o instrumento (robô) enviou
     */
    public void receiveHandshakeInstrument(OSCMessage message){
        //tratando as informações do novo instrumento
        Instrument instrument = new Instrument();
        List arguments = message.getArguments();
        
        instrument.setName((String)arguments.get(0));
        instrument.setOscAddress((String)arguments.get(1));
        instrument.setIp((String)arguments.get(2));
        instrument.setReceivePort((int)arguments.get(3));
        instrument.setPolyphony((int)arguments.get(4));
        instrument.setFamilyType((String)arguments.get(5));
        instrument.setSpecificProtocol((String)arguments.get(6));
        
        if(!this.instruments.contains(instrument)){
            //add o novo robô a lista
            this.instruments.add(instrument);
            
            //enviando o novo instrumento (robô) para as interfaces de controle
            sendInstrument(instrument);
        }
        
        //enviando informações do servidor ao instrumento (robô) que fez o handshake
        OSCMessage msg = new OSCMessage(instrument.getOscAddress()+"/handshake");
        //msg.addArgument(0);
        //msg.addArgument(1254);
        msg.addArgument(this.name);
        msg.addArgument(this.oscAdress);
        try {
            //System.out.println("server ip " + InetAddress.getLocalHost().getHostAddress());
            msg.addArgument(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addArgument(this.port);
        OSCPortOut sender = null;
        try {
            System.out.println(instrument.getIp()+ " "+ instrument.getReceivePort());
            sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getReceivePort());
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("enviou handshake");
    }
    
    /**
     * Metodo que trata a mensagem de handshake de um dado cliente (interface de controle)
     * @param message mensagem que o cliente (interface de controle) enviou
     */
    public void receiveHandshakeClient(OSCMessage message){
        Client client = new Client();
        List arguments = message.getArguments();
        
        client.setName(arguments.get(0).toString());
        client.setOscAdress(arguments.get(1).toString());
        client.setIpAdress(arguments.get(2).toString());
        client.setPort(Integer.parseInt(arguments.get(3).toString()));
        
        if(!this.clients.contains(client)){
            this.clients.add(client);
        } 
        
        //Enviado informações do SSMM para a nova interface de controle
        OSCMessage msg = new OSCMessage(client.getOscAdress()+"/handshake");
        msg.addArgument(this.name);
        msg.addArgument(this.oscAdress);
        try {
            msg.addArgument(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addArgument(this.port);
        OSCPortOut sender = null;
        try {       
            sender = new OSCPortOut(InetAddress.getByName(client.getIpAdress()), client.getPort());
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Metodo para dividir um endereço osc com base no /
     * exemplo: 
     *      entrada /guitar/play
     *      saída ["guitar","play"]
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
    
    /**
     * Encaminhador de mensagem. Quando uma mensagem chega de uma interface,
     * esse método adiciona a mensagem ao buffer 
     * @param oscMessage mensagem OSC
     */
    //OBS: aqui eu usei uma interface de controle básico que não tabalhava com 
    //     tempo, então aqui é add um tempo qualquer apenas para teste
    public void messageForwarder(OSCMessage oscMessage){
        
        String[] dividedAdress = divideAddress(oscMessage.getAddress());
        String instrumentAdress = dividedAdress[1];
        Instrument instrument = null;
        for (Instrument inst : instruments) {
            System.out.println(inst.getOscAddress()+" "+instrumentAdress);
            if(inst.getOscAddress().equals("/"+instrumentAdress)){
                instrument = inst;
                break;
            }
        }
        if(instrument != null){

            OSCMessage msg = new OSCMessage("/"+dividedAdress[1]+"/"+dividedAdress[2],oscMessage.getArguments());
            PrintOSCMessage.printMsg(msg);

            Date date2 = new Date(System.currentTimeMillis() + 3000);
            
            OSCBundle oscBundle2 =  new OSCBundle();
            oscBundle2.addPacket(msg);
            oscBundle2.setTimestamp(date2);
            this.id = this.id + 1;
            this.addMessage(oscBundle2, this.id);
                
        }
        
    }
    
    /**
     * Método para enviar as informações dos instrumentos (robôs) cadastrados
     * no SSMM para o cliente (interface de controle) que requisitou. 
     * O cliente é identificado pelo paramentro oscMessage
     * @param oscMessage Mensagem OSC que o cliente (interface de controle) 
     * envia requisitando as informações dos intrumentos (robôs)
     */
    public void sendInstruments(OSCMessage oscMessage){
        
        OSCPortOut sender = null;
        OSCMessage msg  = null;
            try {
                sender = new OSCPortOut(oscMessage.getIp(), 12345);
                if( !oscMessage.getArguments().isEmpty()){
                    System.out.println("end="+
                                       (String)oscMessage.getArguments().get(0)
                                       + "/instruments");
                    
                    OSCBundle oscBundle = new OSCBundle();
                    
                    for (Instrument inst : instruments) {
                        msg = new OSCMessage((String) oscMessage.getArguments().get(0)+"/instrument");
                        msg.addArgument(inst.getName());
                        msg.addArgument(inst.getPolyphony());
                        msg.addArgument(inst.getFamilyType());
                        msg.addArgument(inst.getSpecificProtocol());
                        msg.addArgument(inst.getOscAddress());
                        
                        oscBundle.addPacket(msg);
                        
                    }
                    try {
                        sender.send(oscBundle);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                     
                }else{
                    System.out.println("erro");
                    return;
                }

                
               
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

    }

    /**
     * Método para enviar as informações de instrumento (robô) para todas as 
     * interfaces de controle (clientes)
     * @param instrument Instrumento (robô) a ser enviado 
     */
    public void sendInstrument(Instrument instrument){
        
        OSCPortOut sender = null;
        OSCMessage msg  = null;
           
                    
        for (Client client : this.clients) {

            try {
                sender = new OSCPortOut(InetAddress.getByName(
                                         client.getIpAdress()),
                                        client.getPort());

                msg = new OSCMessage(client.getOscAdress()+"/instrument");
                msg.addArgument(instrument.getName());
                msg.addArgument(instrument.getPolyphony());
                msg.addArgument(instrument.getFamilyType());
                msg.addArgument(instrument.getSpecificProtocol());
                msg.addArgument(instrument.getOscAddress());


                sender.send(msg);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
    }        
    
    /**
     * Metodo que trata a mensagem de desconexão de um instrumento (robô) ou
     * cliente (interface de controle). Caso seja a desconexão de um robô, esse
     * metodo avisa todoas a interface a desconexão de tal robô.
     * @param oscMessage mensagem OSC de desconexão
     */
    public void disconnect(OSCMessage oscMessage){
        String[] dividedAdress = divideAddress(oscMessage.getAddress());
        if (dividedAdress.length == 3 && oscMessage.getArguments().size() >= 1) {
            //System.out.println("2: "+ dividedAdress[2]+ " a: "+oscMessage.getArguments().get(0).toString());
            //trata desconexão de uma interface de controle
            if(dividedAdress[2].equals("client")){
                Client client = new Client();
                client.setOscAdress(oscMessage.getArguments().get(0).toString());
                if(this.clients.remove(client)){
                    System.out.println("Client '"+
                            oscMessage.getArguments().get(0).toString()+
                            "' disconnected");
                }
            }
            //trata desconexão de um instrumento
            if(dividedAdress[2].equals("instrument")){ 
                Instrument instrument = new Instrument();
                instrument.setOscAddress(oscMessage.getArguments().get(0).toString());
                if ( this.instruments.remove(instrument) ){
                    System.out.println("Instrument '"+
                            oscMessage.getArguments().get(0).toString()+
                            "' disconnected");
                }
                // envia a desconexão de um instrumento as interfaces de 
                // controle conectadas
                sendInstrumentDisconnected(instrument);
            }
        }  
    }
    
    /**
     * Método que informa a todas a interfaces de controle (clientes) a
     * desconexão de um instrumento (robô)
     * @param instrument instrumento (robô) que se desconectou
     */
        public void sendInstrumentDisconnected(Instrument instrument){
        OSCPortOut sender = null;

        for (Client client : this.clients) {
            OSCMessage msg = new OSCMessage(client.getOscAdress()+"/disconnect/instrument");
            msg.addArgument(instrument.getOscAddress());
            try {
                sender = new OSCPortOut(InetAddress.getByName(client.getIpAdress()), client.getPort());
                try {
                    sender.send(msg);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Método que recebe um Bundle e converte em mensagem RoboMus e adiciona 
     * a mensagem no Buffer de saída. Esse método ainda adiciona o atraso de
     * rede no tempo de envio da mensagem.
     * @param oscBundle Bundle OSC
     * @param id Identificador da mensagem
     */
    public void addMessage(OSCBundle oscBundle, long id){
        
        OSCMessage oscMessage = (OSCMessage)oscBundle.getPackets().get(0);
        
        String[] dividedAddress = divideAddress(oscMessage.getAddress());
        String instrumentAddress = dividedAddress[0];
        Instrument instrument =  this.findInstrument("/"+instrumentAddress);

        if(instrument != null){
            // transforma a mensagem OSC em uma mensagem RoboMus
            RoboMusMessage roboMusMessage =  new RoboMusMessage();
            roboMusMessage.setOscBundle(oscBundle);
            roboMusMessage.setInstrument(instrument);
            roboMusMessage.setOriginalTimestamp(oscBundle.getTimestamp());
            roboMusMessage.setMessageId(id);
            

            int delay = 0; //atraso mecanico para tal mensagem. Não foi considerado aqui
            oscBundle.setTimestamp(new Date(oscBundle.getTimestamp().getTime() - delay));

            roboMusMessage.setCompensatedTimestamp(
                    new Date(
                        oscBundle.getTimestamp().getTime() - delay - this.networkDelay
                    )
            );
            synchronized(this){
                buffer.addMessage(roboMusMessage);
            }
        }else{
            System.out.println("nao achou instrumento");
        }
    }
    
    /**
     * Método que trata as mensagens OSC recebidos pelo SSMM
     */
    public void receiveMessages(){
        
        OSCPortIn receiver;
 
        try {
            receiver = new OSCPortIn(1234);
            OSCListener listener = new OSCListener() {
                @Override
                public void acceptMessage(java.util.Date time, OSCMessage message) {
                    //System.out.println("receive: "+message.getAddress());
                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
                        //System.out.println(dividedAdress[1]);
 
                        switch (dividedAdress[1]) {
                            /*case "handshake":
                                receiveHandshake(message);
                                break;*/
                            case "action":
                               // System.out.println("recebeu resposta: "
                               //         +message.getArguments().get(0)+" - "+ dateFormat.format(GregorianCalendar.getInstance().getTime()) );
                                
                                break;
                            case "getInstruments":
                                
                                sendInstruments(message);
                               
                                break;
                            case "disconnect":
                                
                                disconnect(message);
                               
                                break;
                            case "delay": 
                                //mensagem com delay gasto para executar uma ação
                                //era usado quando tinha rede neural
                                //receiveDelay(message);
                               
                                break;
                            default:
                                // quando é uma ação enviada de uma interface de
                                // controle par algum robô a msg cai aqui e é
                                // encaminhada ao mesmo
                                System.out.println("recebeu msg default");
                                messageForwarder(message);
                                break;

                        }
                        
                    }
                }
            };
            
            //trata as mensagens de handshake
            OSCListener listenerHandshake = new OSCListener() {
                @Override
                public void acceptMessage(java.util.Date time, OSCMessage message) {
    
                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
 
                        switch (dividedAdress[1]) {
                            case "client":
                                System.out.println("/handshake/client");
                                receiveHandshakeClient(message);
                                break;
                            case "instrument":
                                System.out.println("/handshake/instrument");
                                receiveHandshakeInstrument(message);
                                break;
                            default:
                                System.out.println("handshake unknown");
                                break;
                        }
                    }    
                    
                }
            };

            receiver.addListener("/handshake/*", listenerHandshake);
            // essas duas duas abaixo foram usada para que o receiver do OSC
            // consiga ler os subniveis do endereço OSC
            receiver.addListener("/server/*", listener); 
            receiver.addListener("/server/*/*", listener);
            
            receiver.startListening();
            
            
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }            
                
    }
    /**
     * Imprime as interfaces de controle
     */
    public void printClients(){
        if(this.clients.size() == 0){
            System.out.println("No client");
        }else{
            for (Client client : this.clients) {
                System.out.println(client.toString());
            }
        }
        
    }
    /**
     * Imprime os instrumentos cadastrado no SSMM
     */
    public void printInstruments(){
        if(this.instruments.size() == 0){
            System.out.println("No instrument");
        }else{
            for (Instrument instrument : this.instruments) {
                System.out.println(instrument.toString());
            }
        }
    }
    /**
     * Busca se o instrumento passado na mensagem OSC de entrada está cadastrado
     * no SSMM
     * @param oscAddress Mensagem OSC
     * @return Retorna a instância do instrumento caso encontrado. Caso não
     * encontrado retorna null
     */
    public Instrument findInstrument(String oscAddress){
        //buscando instrumento pelo nome
        int index = instruments.indexOf(new Instrument(oscAddress));
        if(index != -1){
            return (instruments.get(index));
        }else{
            return null;
        }
    }
    
    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }
    
        /*
    public void comparatorId(int id){
        if(this.lastIdReceived == -1){
            return;
        }else if(id != (this.lastIdReceived + 1) ){
            System.out.println("=========================> ERRO id = "+id);
        }else{
            this.lastIdReceived = id;
        }
    }
    */
                     
}
