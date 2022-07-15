/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import java.io.Serializable;

/**
 * Define os objetos que simulam as cartas que são usadas no jogo Uno
 * guardando valor, cor, etc.
 * 
 * @author Dariãn
 */
public class Carta implements Serializable {
    private Integer numero;
    private String simbolo;
    private String cor;
    private String descricao;
    private String corDeCompra;
    
    public Carta () {
        numero = null;
        simbolo = "";
        cor = "";
        corDeCompra = "";
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }
    
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCorDeCompra() {
        return corDeCompra;
    }

    public void setCorDeCompra(String corDeCompra) {
        this.corDeCompra = corDeCompra;
    }
}
