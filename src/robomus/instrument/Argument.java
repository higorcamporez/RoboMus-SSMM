/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import java.util.List;

/**
 * Classe que representa um agumento de uma ação de um instrumento RoboMus
 * @author higor
 */
public class Argument {
    private String name; //nome do argumento
    private char type; //tipo do argumento. inteiro, float, note e etc...
    private Float minValue; //valor mínimo que pode assumir
    private Float maxValue; //valor máximo que pode assumir
    private List object;    //lista de valores que pode assumir
    
    /**
     * Construtor para a classe argumento
     * @param name nome do argumento
     * @param type tipo do argumento. inteiro, float, note e etc...
     */
    public Argument(String name, char type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * 
     * @param name nome do argumento
     * @param type tipo do argumento. inteiro (i), float (f), string (s),
     * note (n)
     * @param minValue valor mínimo que o argumento pode assumir
     * @param maxValue valor máximo que o argumento pode assumir
     */
    public Argument(String name, char type, Float minValue, Float maxValue) {
        this.name = name;
        this.type = type;
        this.minValue = minValue; 
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Float getMinValue() {
        return minValue;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }
    
         
}
