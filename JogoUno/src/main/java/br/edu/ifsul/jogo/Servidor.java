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
    private static Baralho baralho = new Baralho();
    private static boolean jogoComecou = false;

    public Servidor (Jogador j) {
        jogador = j;
    }
    
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream()));
            PrintStream saida = new PrintStream(jogador.getSocket().getOutputStream());
            jogador.setSaida(saida);
            boolean jogoPodecomecar = true;
            
            do {
                nomeJogador = entrada.readLine();
            } while (nomeJogador == null);
            jogador.setNome(nomeJogador);
            sendTo(saida, jogador.getNome() + ", aguarde, o jogo já vai começar! ", jogador.getIp());
            
            for (Jogador j : jogadores) {
                if (j.getNome() == null) {
                    jogoPodecomecar = false;
                }
            }
            if (jogadores.size() == 2 && jogoPodecomecar) {
                jogoComecou = true;
                darCartas();
            }
                            
            if (jogoComecou) {
                String mensagemInicioDoJogo = "O jogo esta começando, os jogadores são: ";
                for (Jogador j : jogadores) {
                    mensagemInicioDoJogo += j.getNome() + "; ";
                }
                sendToAll("", mensagemInicioDoJogo);
                
                PrintStream saida2 = new PrintStream(jogadores.get(0).getSocket().getOutputStream());
                Carta c = baralho.getBaralho().get(0);
                baralho.getBaralho().remove(0);
                baralho.getLixo().add(c);
                jogadores.get(0).setSaida(saida2);
                jogadores.get(0).setVezDeJogar(true);
                sendTo(saida2, jogadores.get(0).getNome() + ", você é o primeiro a jogar, pressione enter para fazer a sua jogada. ", jogadores.get(0).getIp());
            }

            while (!jogador.getTerminarConexao()) {
                entrada.readLine();
                if (jogoComecou && jogador.getVezDeJogar()) {
                    String mao = montaMao();
                    jogada(mao);
                } else {
                    if (!jogoComecou) {
                        sendTo(saida, jogador.getNome() + ", aguarde, o jogo ainda não começou ", jogador.getIp());
                    } else if (!jogador.getVezDeJogar()) {
                        sendTo(saida, jogador.getNome() + ", ainda não é a sua vez de jogar ", jogador.getIp());
                    }
                }
            }
            jogadores.remove(saida);
            jogador.getSocket().close();
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    
    public void sendToAll(String acao, String texto) throws IOException {
        Iterator<Jogador> iter = jogadores.iterator();
        while (iter.hasNext()) {
            Jogador j = iter.next();
            PrintStream chat = (PrintStream) j.getSaida();
            chat.println(acao + texto);
        }
    }
    
    public void sendTo(PrintStream saida, String texto, String id_client) throws IOException {

        Iterator<Jogador> iter = jogadores.iterator();
        while (iter.hasNext()) {
            Jogador j = iter.next();
            if (j.getIp().equals(id_client)) {
                PrintStream chat = (PrintStream) j.getSaida();
                chat.println(texto);
            }
        }
    }
    
    public String montaMao () {
        String mao = "";
        mao += "0 - Sair\n";
        mao += "1 - Comprar uma carta\n";
        mao += "2 - Falar Uno\n";
        int cont = 3;
        
        for (Carta c : jogador.getMao()) {
            mao += cont + " - " + c.getDescricao() + "\n";
            cont++;
        }
        return mao;
    }
    
    public void jogada (String mao) {
        try {
            boolean jogadaValida;
            String escolha;
            int opcao = 0;
            
            BufferedReader entrada = new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream()));
            Carta cartaDaMesa = baralho.getLixo().get(baralho.getLixo().size()-1);
            
            String stringSaida = "É a sua vez de jogar, "
                    + "a carta que esta na mesa é: " + cartaDaMesa.getDescricao() + "\n Escolha uma das opções:\n" + mao;
            
            sendTo(jogador.getSaida(), stringSaida, jogador.getIp());
            do {
                escolha = entrada.readLine();
                jogadaValida = true;
                
                if (escolha == null || escolha.trim().equals("")) {
                    sendTo(jogador.getSaida(), "A jogada efetuada não é valida, por favor informe um valor!", jogador.getIp());
                    jogadaValida = false;
                } else if (!(escolha.matches("[+-]?\\d*(\\.\\d+)?"))) {
                    sendTo(jogador.getSaida(), "A jogada efetuada não é valida, por favor informe um número!", jogador.getIp());
                    jogadaValida = false;
                } else if (Integer.parseInt(escolha) < 0 || Integer.parseInt(escolha) > jogador.getMao().size()+2) {
                    sendTo(jogador.getSaida(), "A jogada efetuada não é valida, por favor escolha uma das opções acima!", jogador.getIp());
                    jogadaValida = false;
                }
                
                if (jogadaValida) {
                    opcao = Integer.parseInt(escolha);
                    if (opcao >= 3) {
                        if (jogador.getMao().get(opcao-3).getNumero() != null) {
                            if (cartaDaMesa.getNumero() != null && (
                                !(jogador.getMao().get(opcao-3).getCor().equals(cartaDaMesa.getCor())) || jogador.getMao().get(opcao-3).getNumero() != cartaDaMesa.getNumero())) {
                                sendTo(jogador.getSaida(), "A jogada efetuada não é valida, a carta jogada deve possuir o mesmo número ou cor da carta da mesa!", jogador.getIp());
                                jogadaValida = false;
                            } else if (!cartaDaMesa.getSimbolo().equals("") && jogador.getMao().get(opcao-3).getNumero() != cartaDaMesa.getNumero()) {
                                sendTo(jogador.getSaida(), "A jogada efetuada não é valida, a carta jogada deve possuir a mesma cor da carta da mesa!", jogador.getIp());
                                jogadaValida = false;
                            }
                        }
                    }
                }
                
            } while (!jogadaValida);
            
            Carta c = jogador.getMao().get(opcao-3);
            jogador.getMao().remove(opcao-3);
            baralho.getLixo().add(c);
            
            int index = 0;
            for (Jogador j : jogadores) {
                if (j.getIp().equals(jogador.getIp())) {
                    jogador.setVezDeJogar(false);
                    index++;
                    if (index == jogadores.size()) {
                        index = 0;
                    }
                    jogadores.get(index).setVezDeJogar(true);
                    PrintStream saida = new PrintStream(jogadores.get(index).getSocket().getOutputStream());
                    sendTo(saida, jogadores.get(index).getNome() + ", é a sua vez, pressione enter para fazer a sua jogada: ", jogadores.get(index).getIp());
                    break;
                }
                index++;
            }
          
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
    
    public static void darCartas () {
        Iterator<Jogador> iter = jogadores.iterator();
        baralho.embaralhar();
        while (iter.hasNext()) {
            Jogador j = iter.next();
            for (int i = 0; i < 7; i++) {
                Carta c = new Carta();
                c = baralho.getBaralho().get(0);
                baralho.getBaralho().remove(0);
                j.getMao().add(c);
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