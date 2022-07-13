/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Dariãn
 */
public class Jogador implements Serializable {
    private String nome;
    private String ip;
    private Socket socket;
    private PrintStream saida;
    private ArrayList<Carta> mao;
    private Boolean vezDeJogar;
    private boolean terminarConexao;
    private boolean host;
    private boolean falouUno;
    private boolean comprouUmaCarta;

    public Jogador () {
        this.setVezDeJogar(false);
        this.mao = new ArrayList<>();
        this.terminarConexao = false;
        this.host = false;
        this.falouUno = false;
        this.comprouUmaCarta = false;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintStream getSaida() {
        return saida;
    }

    public void setSaida(PrintStream saida) {
        this.saida = saida;
    }

    public ArrayList<Carta> getMao() {
        return mao;
    }

    public void setMao(ArrayList<Carta> mao) {
        this.mao = mao;
    }

    public Boolean getVezDeJogar() {
        return vezDeJogar;
    }

    public void setVezDeJogar(Boolean vezDeJogar) {
        this.vezDeJogar = vezDeJogar;
    }

    public boolean getTerminarConexao() {
        return terminarConexao;
    }

    public void setTerminarConexao(boolean terminarConexao) {
        this.terminarConexao = terminarConexao;
    }

    public boolean getHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public boolean getFalouUno() {
        return falouUno;
    }

    public void setFalouUno(boolean falouUno) {
        this.falouUno = falouUno;
    }

    public boolean getComprouUmaCarta() {
        return comprouUmaCarta;
    }

    public void setComprouUmaCarta(boolean comprouUmaCarta) {
        this.comprouUmaCarta = comprouUmaCarta;
    }

    
}