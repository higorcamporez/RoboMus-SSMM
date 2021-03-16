/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.util;

import com.illposed.osc.OSCMessage;
import java.util.List;

/**
 * Classe imprimir mensagens OSC
 * @author higor
 */
public class PrintOSCMessage {

    public static void printMsg(OSCMessage oscMessage) {

        List<Object> l = oscMessage.getArguments();
        String s = oscMessage.getAddress() + " [";

        for (Object object : l) {
            s += object.toString() + " ";
        }
        s += "]";
        System.out.println(s);

    }
}
