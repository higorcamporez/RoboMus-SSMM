/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import com.illposed.osc.OSCMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa uma ação de um instrumento
 * @author higor
 */
public class Action {
    private String actionAddress; //endereço osc da ação
    private List<Argument> arguments; //argumentos da ação
    private OSCMessage oscMessage;  //mensagem osc pronta para essa ação

    public Action() {
    }
    
    /**
     * Contrutor para Classe Action
     * @param actionAddress endereço OSC da ação
     */
    public Action(String actionAddress) {
        this.actionAddress = actionAddress;
    }
    /**
     * Contrutor para Classe Action
     * @param actionAddress endereço OSC da ação
     * @param params Lista de argumentos da ação
     */
    public Action(String actionAddress, List<Argument> params) {
        this.actionAddress = actionAddress;
        this.arguments = params;
    }

    public String getActionAddress() {
        return actionAddress;
    }

    public void setActionAddress(String actionAddress) {
        this.actionAddress = actionAddress;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public List getArgumentsType(){
        List types = new ArrayList<>();

        for (Argument arg: arguments ) {
            types.add(arg.getType());
        }
        return types;
    }

    public OSCMessage getOscMessage() {
        return oscMessage;
    }

    public void setOscMessage(OSCMessage oscMessage) {
        this.oscMessage = oscMessage;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(actionAddress, action.actionAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionAddress);
    }
}
