/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsul.jogo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Dariãn
 */
public class JogadorNoJogo extends Thread {
    private static boolean terminarExecucao = false;
    private Socket conexao;
    
    public JogadorNoJogo (Socket s) {
        conexao = s;
    }
    
    public void run () {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            String linha;
            while (true) {
                linha = entrada.readLine();
                if (linha == null) {
                    System.out.println("Conexão encerrada!");
                    break;
                }
                System.out.println();
                System.out.println(linha);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        terminarExecucao = true;
    }
    
    // Aqui vai a função Jogada(), que vai verificar se o movimento que o jogador deseja fazer é algo válido;
    
    public static void main(String[] args) {
        try {
            Socket conexao = new Socket("127.0.0.1", 2222);
            PrintStream saida = new PrintStream(conexao.getOutputStream());
            BufferedReader teclado= new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Bem vindo ao jogo Uno, entre com o seu nome: ");
            String nomeJogador = teclado.readLine();
            saida.println(nomeJogador);
            
            Thread t = new JogadorNoJogo(conexao);
            t.start();
            String linha;
            
            while (true) {
                System.out.println("> ");
                linha = teclado.readLine();
                
                if (terminarExecucao) {
                    break;
                }
                saida.println(linha);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }
}
