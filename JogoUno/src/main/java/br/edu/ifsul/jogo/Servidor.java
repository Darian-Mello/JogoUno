/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Dariãn
 */
public class Servidor extends Thread {
    private static List<Jogador> jogadores;
    private Jogador jogador;
    private Socket conexao;
    private String nomeJogador;
    
    public Servidor (Jogador j) {
        jogador = j;
    }
    
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream()));
            PrintStream saida = new PrintStream(jogador.getSocket().getOutputStream());
            jogador.setSaida(saida);
            
            nomeJogador = entrada.readLine();
            
            if (nomeJogador == null) {
                return;
            }
            jogador.setNome(nomeJogador);

            String linha = entrada.readLine();
            while (linha != null && !(linha.trim().equals(""))) {
                sendToAll(saida, " disse: ", linha);
                linha = entrada.readLine();
            }
            sendToAll(saida, " saiu ", "do jogo!");
            jogadores.remove(saida);
            conexao.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
    
    public void sendToAll(PrintStream saida, String acao, String linha) throws IOException {

        Iterator<Jogador> iter = jogadores.iterator();
        while (iter.hasNext()) {
            Jogador outroCliente = iter.next();
            PrintStream chat = (PrintStream) outroCliente.getSaida();
            if (chat != saida) {
                chat.println(jogador.getNome() + " com IP: " + jogador.getSocket().getRemoteSocketAddress() + acao + linha);
            }
        }
    }
    
    public static void main(String args[]) {
        jogadores = new ArrayList<Jogador>();
        try {
            ServerSocket s = new ServerSocket(2222);
            while (true) {
                System.out.println("Esperando algum jogador se conectar...");
                Socket conexao = s.accept();
                Jogador jogador = new Jogador();
                jogador.setIp(conexao.getRemoteSocketAddress().toString());
                jogador.setSocket(conexao);

                jogadores.add(jogador);

                System.out.println(" Conectou!: " + conexao.getRemoteSocketAddress());

                Thread t = new Servidor(jogador);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
}
