/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Dariãn
 */
public class Baralho implements Serializable {
    private ArrayList<Carta> baralho;
    private ArrayList<Carta> lixo;
    
    public Baralho () {
        
    }
    
    // deve haver uma função para criar as cartas do baralho
    
    // deve haver uma função para embaralhar, ou resetar o baralho;

    public ArrayList<Carta> getBaralho() {
        return baralho;
    }

    public void setBaralho(ArrayList<Carta> baralho) {
        this.baralho = baralho;
    }

    public ArrayList<Carta> getLixo() {
        return lixo;
    }

    public void setLixo(ArrayList<Carta> lixo) {
        this.lixo = lixo;
    }
}
