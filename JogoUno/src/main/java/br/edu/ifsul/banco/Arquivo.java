/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsul.banco;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Realiza as operações de leitura e escrita no banco 
 *
 * @author Darian & Elias
 */
public class Arquivo {
    /**
     * A função "Read" é utilizada para percorrer o arquivo texto utilizado como banco de dados.
     *
     * @authors Dariãn & Elias
     * @param caminho utilizado para chegar no arquivo.
     * @return Retorna uma String com todos os dados dentro do arquivo.
     * @since 1.0
     */
    public static String Read(String caminho){
        String conteudo = "";
        try {
            FileReader arq = new FileReader(caminho);
            BufferedReader lerArq = new BufferedReader(arq);
            String linha="";
            try {
                linha = lerArq.readLine();
                while(linha!=null){
                    conteudo += linha+"\n";
                    linha = lerArq.readLine();
                }
                arq.close();
                return conteudo;
            } catch (IOException ex) {
                System.out.println("Erro: Não foi possível ler o arquivo!");
                return "";
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro: Arquivo não encontrado!");
            return "";
        }
    }
    
    /**
     * A função "Write" é utilizada para esrever uma String no arquivo texto.
     *
     * @authors Dariãn & Elias
     * @param caminho utilizado para chegar no arquivo.
     * @param texto que será escrito no arquivo.
     * @return Retorna se foi possível escrever no arquivo.
     * @since 1.0
     */
    public static boolean Write(String caminho,String texto){
        try {
            FileWriter arq = new FileWriter(caminho);
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.println(texto);
            gravarArq.close();
            return true;
        }catch(IOException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
