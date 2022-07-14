/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsul.banco;

import br.edu.ifsul.jogo.Jogador;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author Elias Dalvite
 */
public class BancoTxt {
    public static void incluirNovoPlayer(String nomeJogador){
        String conteudo = "";
        String[] dados;
        Boolean existe = false;
        try {
            FileReader arq = new FileReader("banco.txt");
            BufferedReader lerArq = new BufferedReader(arq);
            String linha = "";
            String print;
            try {
                while(linha!=null){
                    linha = lerArq.readLine();
                    if(linha!=null){
                        dados = linha.split(";");
                        
                        if(dados[0].equals(nomeJogador))
                            existe = true;
                        
                        conteudo += linha+"\n";
                    } 
                }
                if(existe == false){
                    Jogador j = new Jogador();
                    j.setNome(nomeJogador);
                    String nome = j.getNome();
                    Integer vitorias = j.getVitorias();
                    Integer derrotas = j.getDerrotas();
                    Integer pontuacao = j.getPontuacao();
                    print = nome+";"+vitorias+";"+derrotas+";"+pontuacao+";";
                    conteudo = conteudo + print;
                    Arquivo.Write("banco.txt", conteudo);
                }
                arq.close();
            } catch (IOException ex) {
                System.out.println("Erro: Não foi possível ler o arquivo!");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro: Arquivo não encontrado!");
        }
    }
    
    public static void aumentaVitorias(String nomeJogador){
        String conteudo = "";
        String[] dados;
        Integer muda;
        
        try {
            FileReader arq = new FileReader("banco.txt");
            BufferedReader lerArq = new BufferedReader(arq);
            String linha="";
            try {
                while(linha!=null){
                    linha = lerArq.readLine();
                    if(linha!=null){                    
                    dados = linha.split(";");                      
                    //compara com o nome do cara passado por parametro
                    if(dados[0].equals(nomeJogador)){
                        muda = Integer.parseInt(dados[1]);//pega o dado
                        muda++;//aumenta uma vitoria
                        dados[1] = muda.toString();//devolve pro dado
                        muda = Integer.parseInt(dados[3]);//mesma coisa com pontuacao (adicionei 10 cada vitoria)
                        muda+=10;
                        dados[3] = muda.toString();
                        
                        linha = dados[0]+";"+dados[1]+";"+dados[2]+";"+dados[3]+";";
                            //coloca os dados novos na linha 
                            //pra depois guardar na variavel conteudo
                    }
                    conteudo += linha+"\n";
                    }
                }
                Arquivo.Write("banco.txt", conteudo); //escreve todos os dados dnv, com os dados atualizados
                arq.close();
            } catch (IOException ex) {
                System.out.println("Erro: Não foi possível ler o arquivo!");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro: Arquivo não encontrado!");
        }   
    }
    
    public static void aumentaDerrotas(String nomeJogador){
        String conteudo = "";
        String[] dados;
        Integer muda;
        
        try {
            FileReader arq = new FileReader("banco.txt");
            BufferedReader lerArq = new BufferedReader(arq);
            String linha="";
            try {
                while(linha!=null){
                    linha = lerArq.readLine();
                    if(linha!=null){
                    
                    dados = linha.split(";");                     
                    
                    if(dados[0].equals(nomeJogador)){
                        
                        muda = Integer.parseInt(dados[2]);
                        muda++;
                        dados[2] = muda.toString();
                        
                        linha = dados[0]+";"+dados[1]+";"+dados[2]+";"+dados[3]+";";
                    }
                    conteudo += linha+"\n";
                    }
                }
                Arquivo.Write("banco.txt", conteudo);
                System.out.println(conteudo);
                arq.close();
            } catch (IOException ex) {
                System.out.println("Erro: Não foi possível ler o arquivo!");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro: Arquivo não encontrado!");
        }   
    }
}

