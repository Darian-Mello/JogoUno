/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import br.edu.ifsul.banco.BancoTxt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
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
    private static int totalJogadores = 0;
    private static boolean partidaAcabou = false;

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
            } while (nomeJogador.trim().equals(""));
            jogador.setNome(nomeJogador);
            
            if (jogador.getHost()) {
                defineTotalJogares();
            }
            
            sendTo(saida, jogador.getNome() + ", aguarde, o jogo já vai começar! ", jogador.getIp());
            
            for (Jogador j : jogadores) {
                if (j.getNome() == null) {
                    jogoPodecomecar = false;
                }
            }
            if (jogadores.size() != 0 && jogadores.size() == totalJogadores && jogoPodecomecar) {
                jogoComecou = true;
                darCartas();
                salvarJogadores();
            }
                            
            if (jogoComecou) {
                String mensagemInicioDoJogo = "O jogo esta começando, os jogadores são: ";
                for (Jogador j : jogadores) {
                    mensagemInicioDoJogo += j.getNome() + "; ";
                }
                sendToAll(mensagemInicioDoJogo);
                sendToAll(jogadores.get(0).getNome() + " Esta jogando.");
                
                PrintStream saida2 = new PrintStream(jogadores.get(0).getSocket().getOutputStream());
                jogadores.get(0).setSaida(saida2);
                jogadores.get(0).setVezDeJogar(true);
                sendTo(saida2, jogadores.get(0).getNome() + ", você é o primeiro a jogar, pressione enter para fazer a sua jogada. ", jogadores.get(0).getIp());
            }

            while (!jogador.getTerminarConexao() && !partidaAcabou) {
                entrada.readLine();
                if (jogoComecou && jogador.getVezDeJogar() && !jogador.getTerminarConexao() && !partidaAcabou) {
                    jogada();
                } else if (!jogador.getTerminarConexao() && !partidaAcabou) {
                    if (!jogoComecou) {
                        sendTo(saida, jogador.getNome() + ", aguarde, o jogo ainda não começou ", jogador.getIp());
                    } else if (!jogador.getVezDeJogar()) {
                        sendTo(saida, jogador.getNome() + ", ainda não é a sua vez de jogar ", jogador.getIp());
                    }
                }
            }
            if (!partidaAcabou) {
                finalizarPartidaSaidaJogador();
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    
    public void defineTotalJogares () throws IOException {
        BufferedReader entrada = new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream()));
        PrintStream saida = new PrintStream(jogador.getSocket().getOutputStream());
        sendTo(saida, jogador.getNome() + ", você entrou como Host, por favor defina quantos jogadores irão participar(entre 2 e 5): ", jogador.getIp());
        do {
            String total = entrada.readLine();
            if (!(total.matches("[+-]?\\d*(\\.\\d+)?")) || total.equals("")) {
                sendTo(saida, "Informe um número.", jogador.getIp());
            } else {
                totalJogadores = Integer.parseInt(total);
            } 
            if (totalJogadores < 2 || totalJogadores > 5) {
                sendTo(saida, "O total de jogadores deve estar entre 2 e 5.", jogador.getIp());
            } else if (totalJogadores < jogadores.size()) {
                totalJogadores = 0;
                sendTo(saida, "Já existem " + jogadores.size() + " jogadores conectados,"
                        + " por favor informe um número maior ou igual a esse.", jogador.getIp());
            }
        } while (totalJogadores < 2 || totalJogadores > 5);
    }
    
    public void sendToAll(String texto) throws IOException {
        Iterator<Jogador> iter = jogadores.iterator();
        while (iter.hasNext()) {
            Jogador j = iter.next();
            PrintStream chat = (PrintStream) j.getSaida();
            chat.println(texto);
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
     
    public void jogada () {
        try {
            boolean jogadaValida;
            String escolha;
            int opcao = 0;
            
            BufferedReader entrada = new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream()));
            Carta cartaDaMesa = baralho.getLixo().get(baralho.getLixo().size()-1);
            
            String stringSaida = "É a sua vez de jogar, Escolha uma das opções:\n" + montaMao();
            
            if (baralho.getBaralho().size() <= 5) {
                baralho.resetarBaralho();
            }
            
            sendTo(jogador.getSaida(), stringSaida, jogador.getIp());
            do {
                if (!cartaDaMesa.getCorDeCompra().equals("")) {
                    sendTo(jogador.getSaida(), "Você deve jogar uma carta da seguinte cor: " + cartaDaMesa.getCorDeCompra(), jogador.getIp());
                }
                sendTo(jogador.getSaida(), "A carta que esta na mesa é: " + cartaDaMesa.getDescricao() + "\nSua escolha: ", jogador.getIp());
                escolha = entrada.readLine();
                String mensagemJogadaInvalida = "";
                jogadaValida = true;
                
                if (escolha == null || escolha.trim().equals("")) {
                    mensagemJogadaInvalida = "A jogada efetuada não é valida, por favor informe um valor!";
                    jogadaValida = false;
                } else if (!(escolha.matches("[+-]?\\d*(\\.\\d+)?"))) {
                    mensagemJogadaInvalida = "A jogada efetuada não é valida, por favor informe um número!";
                    jogadaValida = false;
                } else if (Integer.parseInt(escolha) < 0 || Integer.parseInt(escolha) > jogador.getMao().size()+2) {
                    mensagemJogadaInvalida = "A jogada efetuada não é valida, por favor escolha uma das opções acima!";
                    jogadaValida = false;
                }
                
                if (jogadaValida) {
                    opcao = Integer.parseInt(escolha);
                    if (opcao >= 3) {
                        opcao = opcao - 3;
                        if (!cartaDaMesa.getCor().equals("")) {
                            if (cartaDaMesa.getNumero() != null) {
                                if (jogador.getMao().get(opcao).getNumero() != null) {
                                    if (!(jogador.getMao().get(opcao).getCor().equals(cartaDaMesa.getCor())) && jogador.getMao().get(opcao).getNumero() != cartaDaMesa.getNumero()) {
                                        mensagemJogadaInvalida = "A jogada efetuada não é valida, a carta jogada deve possuir o mesmo número ou cor da carta da mesa!";
                                        jogadaValida = false;
                                    }
                                } else if (!jogador.getMao().get(opcao).getCor().equals("") && !(jogador.getMao().get(opcao).getCor().equals(cartaDaMesa.getCor())) && 
                                        !jogador.getMao().get(opcao).getSimbolo().equals(cartaDaMesa.getSimbolo())) {
                                    mensagemJogadaInvalida = "A jogada efetuada não é valida, a carta jogada deve possuir a mesma cor da carta da mesa!";
                                    jogadaValida = false;
                                }
                            } else if (!jogador.getMao().get(opcao).getCor().equals("") && !(jogador.getMao().get(opcao).getCor().equals(cartaDaMesa.getCor())) &&
                                    !jogador.getMao().get(opcao).getSimbolo().equals(cartaDaMesa.getSimbolo())) {
                                mensagemJogadaInvalida = "A jogada efetuada não é valida, a carta jogada deve possuir a mesma cor da carta da mesa!";
                                jogadaValida = false;
                            }
                        } else if (cartaDaMesa.getSimbolo().equals("MudaCor") && !(jogador.getMao().get(opcao).getCor().equals(cartaDaMesa.getCorDeCompra()))) {
                            mensagemJogadaInvalida = "A jogada efetuada não é valida, a carta jogada deve possuir a cor solicitada!";
                            jogadaValida = false;
                        }
                    }
                }
                
                if (!jogadaValida) {
                    sendTo(jogador.getSaida(), mensagemJogadaInvalida, jogador.getIp());
                } else if (escolha.equals("1")) {
                    if (!jogador.getComprouUmaCarta()) {
                        comprar();
                        sendTo(jogador.getSaida(), "Você comprou uma carta, sua mão é:\n" + montaMao().replace("Comprar uma carta", "Passar a vez"), jogador.getIp());
                        jogadaValida = false;
                        jogador.setComprouUmaCarta(true);
                    } else {
                        sendTo(jogador.getSaida(), "Você passou a vez.", jogador.getIp());
                        jogador.setVezDeJogar(false);
                        int index = indexJogador();
                        index++;
                        if (index >= jogadores.size()) {
                            index = 0;
                        }
                        jogador.setComprouUmaCarta(false);
                        jogadores.get(index).setVezDeJogar(true);
                        sendToAll(jogadores.get(index).getNome() + " Esta jogando.");
                        sendTo(jogadores.get(index).getSaida(), "Pressione enter para fazer a sua jogada: ", jogadores.get(index).getIp());
                        return;
                    }
                } else if (jogador.getMao().size() != 1 && escolha.equals("2")) {
                    comprar();
                    comprar();
                    sendTo(jogador.getSaida(), "Você só deve falar \"Uno\" quando tiver apenas uma carta, por isso comprou mais duas cartas. Sua mão é:\n" + montaMao(), jogador.getIp());
                    jogadaValida = false;
                } else if (escolha.equals("0")) {
                    jogador.setTerminarConexao(true);
                    int index = indexJogador();
                    index++;
                    if (index >= jogadores.size()) {
                        index = 0;
                    }
                    jogadores.get(index).setVezDeJogar(true);
                    return;
                }
                
                if (jogador.getMao().size() == 1 && !escolha.equals("2") && !jogador.getFalouUno()) {
                    comprar();
                    comprar();
                    sendTo(jogador.getSaida(), "Você tinha apenas uma carta e não falou \"Uno\", por isso comprou mais duas cartas. Sua mão é:\n" + montaMao(), jogador.getIp());
                    jogadaValida = false;
                } else if (jogador.getMao().size() == 1 && escolha.equals("2")) {
                    sendTo(jogador.getSaida(), "Você disse \"Uno\" na hora certa, muito bem.", jogador.getIp());
                    jogadaValida = false;
                    jogador.setFalouUno(true);
                }
            } while (!jogadaValida);
            
            efetuarJogada(opcao);
            jogador.setFalouUno(false);
            jogador.setComprouUmaCarta(false);
           
            if (!cartaDaMesa.getCorDeCompra().equals("")) {
                baralho.getLixo().get(baralho.getLixo().size()-1).setCorDeCompra("");
            }
            
            if (jogador.getMao().size() == 0) {
                finalizarPartida();
            }
            
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
    
    public void finalizarPartida () throws IOException {
        partidaAcabou = true;
        sendToAll(jogador.getNome() + " Venceu o jogo!");
        while (jogadores.size() > 0) {
            jogadores.get(0).getSocket().close();
            jogadores.remove(0);
        }
    }
    
    public void finalizarPartidaSaidaJogador () throws IOException {
        jogadores.remove(indexJogador());
        if (jogadores.size() == 1) {
            sendToAll(jogadores.get(0).getNome() + " é o vencedor do jogo!");
            sendToAll("Todos os outros jogadores saíram do jogo.");
            jogadores.get(0).setTerminarConexao(true);
        } else if (jogadores.size() != 0) {
            while (jogador.getMao().size() > 0) {
                baralho.getBaralho().add(jogador.getMao().get(0));
                jogador.getMao().remove(0);
            }
            sendToAll(jogador.getNome() + " saiu do jogo!");
            int index = 0;
            for (Jogador j : jogadores) {
                if (j.getVezDeJogar()) {
                    break;
                }
                index++;
            }
            sendToAll(jogadores.get(index).getNome() + " Esta jogando.");
            sendTo(jogadores.get(index).getSaida(), "Pressione enter para fazer a sua jogada: ", jogadores.get(index).getIp());
        }
        jogador.getSocket().close();
    }
    
    public void comprar () {
        Carta c = baralho.getBaralho().get(0);
        baralho.getBaralho().remove(0);
        jogador.getMao().add(c);
    }
    
    public void efetuarJogada (int opcao) throws IOException {
        if (jogador.getMao().get(opcao).getSimbolo().equals("Inverte")) {
            Collections.reverse(jogadores);
        } else if (jogador.getMao().get(opcao).getSimbolo().equals("Compra2")) {
            int indexComprador = indexJogador();        
            indexComprador++;
            if (indexComprador >= jogadores.size()) {
                indexComprador = 0;
            }
            for (int i = 0; i < 2; i++) {
                Carta ca = new Carta();
                ca = baralho.getBaralho().get(0);  
                baralho.getBaralho().remove(0);
                jogadores.get(indexComprador).getMao().add(ca);
            }
            sendTo(jogadores.get(indexComprador).getSaida(), "Você recebeu um \"Mais 2\", por isso vai comprar mais duas cartas e não vai poder jogar nessa rodada.", jogadores.get(indexComprador).getIp());
        } else if (jogador.getMao().get(opcao).getSimbolo().equals("Compra4")) {
            int indexComprador = indexJogador();        
            indexComprador++;
            if (indexComprador >= jogadores.size()) {
                indexComprador = 0;
            }
            for (int i = 0; i < 4; i++) {
                Carta ca = new Carta();
                ca = baralho.getBaralho().get(0);
                baralho.getBaralho().remove(0);
                jogadores.get(indexComprador).getMao().add(ca);
            }
            sendTo(jogadores.get(indexComprador).getSaida(), "Você recebeu um \"Mais 4\", por isso vai comprar mais quatro cartas e não vai poder jogar nessa rodada.", jogadores.get(indexComprador).getIp());
        } else if (jogador.getMao().get(opcao).getSimbolo().equals("MudaCor")) {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(jogador.getSocket().getInputStream()));
            String cor = "0";
            String pergunta = "Selecione uma cor:\n"
                    + "1 - Amarelo\n"
                    + "2 - Azul\n"
                    + "3 - Verde\n"
                    + "4 - Vermelho\n"
                    + "Sua escolha: ";
            while (!cor.equals("1") && !cor.equals("2") && !cor.equals("3") && !cor.equals("4")) {
                sendTo(jogador.getSaida(), pergunta, jogador.getIp());
                cor = entrada.readLine();
            }
            if (cor.equals("1")) {
                jogador.getMao().get(opcao).setCorDeCompra("Amarelo");
            } else if (cor.equals("2")) {
                jogador.getMao().get(opcao).setCorDeCompra("Azul");
            } else if (cor.equals("3")) {
                jogador.getMao().get(opcao).setCorDeCompra("Verde");  
            } else if (cor.equals("4")) {
                jogador.getMao().get(opcao).setCorDeCompra("Vermelho");
            }
        }

        int index = indexJogador();
        sendTo(jogadores.get(index).getSaida(), "Sua jogada foi efetuada com sucesso. ", jogadores.get(index).getIp());
        jogadores.get(index).setVezDeJogar(false);

        index++;
        if (index >= jogadores.size()) {
            index = 0;
        }
        if (jogador.getMao().get(opcao).getSimbolo().equals("Bloqueio") || jogador.getMao().get(opcao).getSimbolo().equals("Compra4") || jogador.getMao().get(opcao).getSimbolo().equals("Compra2")) {
            index++;
        }
        if (index >= jogadores.size()) {
            index = 0;
        }

        Carta c = jogador.getMao().get(opcao);
        jogador.getMao().remove(opcao);
        baralho.getLixo().add(c);

        jogadores.get(index).setVezDeJogar(true);
        
        if (jogador.getMao().size() > 0) {
            sendToAll(jogadores.get(index).getNome() + " Esta jogando.");
            sendTo(jogadores.get(index).getSaida(), "Pressione enter para fazer a sua jogada: ", jogadores.get(index).getIp());
        }
    }
    
    public int indexJogador () {
        int index = 0; 
        for (Jogador j : jogadores) {
            if (j.getIp().equals(jogador.getIp())) {
                break;
            }
            index++;
        }
        return index;
    }
    
    public static void darCartas () {
        Iterator<Jogador> iter = jogadores.iterator();
        baralho.embaralhar();
        boolean selecionadaCartaInicio = false;
        while (iter.hasNext()) {
            Jogador j = iter.next();
            for (int i = 0; i < 7; i++) {
                Carta c = new Carta();
                c = baralho.getBaralho().get(0);
                baralho.getBaralho().remove(0);
                j.getMao().add(c);
            }
        }
        while (!selecionadaCartaInicio) {
            Carta c = baralho.getBaralho().get(0);
            baralho.getBaralho().remove(0);
            baralho.getLixo().add(c); 
            
            if (c.getNumero() != null) {
                selecionadaCartaInicio = true;
            } else {
                baralho.getBaralho().add(baralho.getLixo().get(0));
                baralho.getLixo().remove(0);
            }
        }
    }
    
    public static void salvarJogadores () {
        for (Jogador j : jogadores) {
            BancoTxt.incluirNovoPlayer(j.getNome());
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
                
                if (jogoComecou || jogadores.size() == 5) {
                    jogador.getSocket().close();
                    continue;
                }
                
                if (jogadores.size() == 0) {
                    jogador.setHost(true);
                }
                
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