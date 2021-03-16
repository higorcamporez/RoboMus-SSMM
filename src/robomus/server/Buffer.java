package robomus.server;

import java.util.Collections;
import java.util.List;

/**
 * Classe que representa uma buffer onde as mensagens são "armazenadas" antes de
 * serem enviadas aos robos
 * @author higor
 */
public class Buffer extends Thread{
    private volatile List<RoboMusMessage> messages;
    /**
     * Construtor
     */
    public void Buffer(){
    }

    public List<RoboMusMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<RoboMusMessage> messages) {
        this.messages = messages;
    }
    /**
     * Adiciona uma mensagem do tipo RoboMus ao buffer para que a mesma possa
     * ser distribuida ao robô
     * @param roboMusMessage Mensagem a ser entregue a um robô
     */
    public void addMessage(RoboMusMessage roboMusMessage){
        //System.out.println("add");
        this.messages.add(roboMusMessage);
        // ordenar
        //Collections.sort(this.messages);
    }
    
    /**
     * Thread que verifica se ainda há tempo hábil para enviar uma dada mensagem
     */
    @Override
    public void run() {

        while(true){
            synchronized(this){
                if(!this.messages.isEmpty()){
                    if((this.messages.get(0).getCompensatedTimestamp().getTime()) <
                        System.currentTimeMillis()) {

                        //enviar msg ao instrumento
                        this.messages.get(0).send();
                        System.out.println("buffer: enviou msg "+this.messages.get(0).getMessageId());
                        //retirar do buffer
                        this.messages.remove(0);
                        
                    }

                }
            }
        }
    }
}
