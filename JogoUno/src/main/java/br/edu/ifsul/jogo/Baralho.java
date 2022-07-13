/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Dariãn
 */
public class Baralho implements Serializable {
    private ArrayList<Carta> baralho;
    private ArrayList<Carta> lixo;
    
    public Baralho () {
        geraBaralho();
        lixo = new ArrayList<>();
    }
    
    public void geraBaralho () {
        String cores[] = {"Amarelo", "Azul", "Verde", "Vermelho"};
        baralho = new ArrayList<>();
        
        for (String cor : cores) {
            // Gera as cartas de compra +2
            Carta c = new Carta();
            c.setSimbolo("Compra4");
            c.setDescricao(" mais 4 ");
            baralho.add(c);
            
            // Gera as cartas de mudanca de cor
            c = new Carta();
            c.setSimbolo("MudaCor");
            c.setDescricao(" Muda a cor ");
            baralho.add(c);
            
            // Gera os números 0
            c = new Carta();
            c.setNumero(0);
            c.setCor(cor);
            c.setDescricao(" 0 " + cor + " ");
            baralho.add(c);
            
            for (int i = 0; i < 2; i++) {
                // Gera as cartas de 1 a 9
                for (int j = 1; j < 10; j++) {
                    c = new Carta();
                    c.setNumero(j);
                    c.setCor(cor);
                    c.setDescricao(" " + j + " " + cor + " ");
                    baralho.add(c);
                }
                            
                // gera as cartas de bloqueio
                c = new Carta();
                c.setSimbolo("Bloqueio");
                c.setCor(cor);
                c.setDescricao(" Bloqueio " + cor + " ");
                baralho.add(c);
                
                // Gera as cartas de inverter a ordem de jogo
                c = new Carta();
                c.setSimbolo("Inverte");
                c.setCor(cor);
                c.setDescricao(" Vira a ordem " + cor + " ");
                baralho.add(c);
                
                // Gera as cartas de compra +2
                c = new Carta();
                c.setSimbolo("Compra2");
                c.setCor(cor);
                c.setDescricao(" Mais 2 " + cor + " ");
                baralho.add(c);
            }
        }
    } 

    public void embaralhar () {
        Collections.shuffle(baralho);
    }
    
    public void resetarBaralho () {
        while (lixo.size() > 1) {
            baralho.add(lixo.get(0));
            lixo.remove(0);
        }
        embaralhar();
    }
    
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
