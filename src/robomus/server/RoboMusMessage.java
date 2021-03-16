package robomus.server;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import robomus.instrument.Instrument;

import java.util.Date;
/**
 * Classe que representa uma mensagem RoboMus 
 * @author higor
 */
public class RoboMusMessage implements Comparable<RoboMusMessage> {

    private Date originalTimestamp; // tempo absoluto da mensagem
    private Date compensatedTimestamp; //tempo compensado pelo atraso mecânico do robô (t_original - atraso)
    private OSCBundle oscBundle; // OSC Bundle
    private Instrument instrument; //Instrumento para ser enviado
    private long messageId; // Identificador da mensagem
    
    public RoboMusMessage() {
    }
    /**
     * Contrutor para classe RoboMusMessage
     * @param originalTimestamp tempo absoluto da mensagem
     * @param compensatedTimestamp tempo compensado pelo atraso mecânico do robô (t_original - atraso)
     * @param oscBundle OSC Bundle
     * @param instrument Instrumento para ser enviado
     * @param messageId Identificador da mensagem
     */
    public RoboMusMessage(Date originalTimestamp, Date compensatedTimestamp, 
            OSCBundle oscBundle, Instrument instrument, long messageId) {
        this.originalTimestamp = originalTimestamp;
        this.compensatedTimestamp = compensatedTimestamp;
        this.oscBundle = oscBundle;
        this.instrument = instrument;
        this.messageId = messageId;
    }

    public Date getOriginalTimestamp() {
        return originalTimestamp;
    }

    public void setOriginalTimestamp(Date originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public Date getCompensatedTimestamp() {
        return compensatedTimestamp;
    }

    public void setCompensatedTimestamp(Date compensatedTimestamp) {
        this.compensatedTimestamp = compensatedTimestamp;
    }

    public OSCBundle getOscBundle() {
        return oscBundle;
    }

    public void setOscBundle(OSCBundle oscBundle) {
        this.oscBundle = oscBundle;
    }

    
    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
    
    /**
     * Metodo para enviar a mensagem OSC
     */
    public void send() {
        instrument.send(oscBundle);
    }

    @Override
    public String toString() {
        return "RoboMusMessage{"
                + "originalTimestamp=" + originalTimestamp
                + ", compensatedTimestamp=" + compensatedTimestamp
                + ", instrument=" + instrument
                + '}';
    }

    @Override
    public int compareTo(RoboMusMessage roboMusMessage) {
        if (this.compensatedTimestamp.getTime() < roboMusMessage.getCompensatedTimestamp().getTime()) {
            return -1;
        } else if (this.compensatedTimestamp.getTime() < roboMusMessage.getCompensatedTimestamp().getTime()) {
            return 1;
        } else {
            return 0;
        }

    }
}
