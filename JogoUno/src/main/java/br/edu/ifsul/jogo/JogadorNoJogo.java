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
import javax.swing.JOptionPane;

/**
 * Conecta o cliente ao servidor
 * 
 * @author Darian & Elias
 */
public class JogadorNoJogo extends Thread {
    private static boolean terminarExecucao = false;
    private Socket conexao;
    
    public JogadorNoJogo (Socket s) {
        conexao = s;
    }
    
    /**
    * Informando as ações do jogo para o jogador em questão.
    * @authors Dariãn & Elias
    * @since 1.0
    */ 
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
    
    /**
    * Solicita uma conexão ao servidor, fica escutando o que o usuário digita
    * @authors Dariãn & Elias
    * @since 1.0
    */ 
    public static void main(String[] args) {
        try {
            Socket conexao = new Socket("127.0.0.1", 2222);
            PrintStream saida = new PrintStream(conexao.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Bem vindo ao jogo Uno, entre com o seu nome: ");
            String nomeJogador = teclado.readLine();
            saida.println(nomeJogador);
            
            Thread t = new JogadorNoJogo(conexao);
            t.start();
            String linha;
            
            while (true) {
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