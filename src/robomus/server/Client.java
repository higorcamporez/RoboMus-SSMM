/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.server;

import java.util.Objects;

/**
 * Classe que representa os dados de um cliente (interface de controle) da
 * plataforma RoboMus
 * @author Higor
 */
public class Client {
    private String name;
    private String ipAdress;
    private String oscAdress;
    private int port;

    public Client(String name, String ipAdress, String oscAdress, int porta) {
        this.name = name;
        this.ipAdress = ipAdress;
        this.oscAdress = oscAdress;
        this.port = port;
    }

    

    public Client() {
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public String getOscAdress() {
        return oscAdress;
    }

    public void setOscAdress(String oscAdress) {
        this.oscAdress = oscAdress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Client{" + "name=" + name + ", ipAdress=" + ipAdress + ", oscAdress=" + oscAdress + ", port=" + port + '}';
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (!Objects.equals(this.oscAdress, other.oscAdress)) {
            return false;
        }
        return true;
    }
    
    
    
}
