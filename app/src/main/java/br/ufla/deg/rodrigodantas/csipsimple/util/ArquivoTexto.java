package br.ufla.deg.rodrigodantas.csipsimple.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Rodrigo on 6/24/16.
 */
public class ArquivoTexto {

    private String nomeDiretorio;
    private String nomeArquivo;

    public ArquivoTexto(String nomeDiretorio, String nomeArquivo){
        this.nomeDiretorio = Environment.getExternalStorageDirectory()+System.getProperty("file.separator")+nomeDiretorio;
        this.nomeArquivo = nomeArquivo;
    }

    public void gravar(String texto){
        File diretorio = new File(nomeDiretorio);
        if(!diretorio.exists())
            diretorio.mkdirs();

        //Quando o File() tem um parâmetro ele cria um diretório.
        //Quando tem dois ele cria um arquivo no diretório onde é informado.
        File fileExt = new File(nomeDiretorio, nomeArquivo);

        //Cria o arquivo
        fileExt.getParentFile().mkdirs();

        //Abre o arquivo
        FileOutputStream fosExt = null;
        try {
            fosExt = new FileOutputStream(fileExt);

            //Escreve no arquivo
            fosExt.write(texto.getBytes());

            //Obrigatoriamente você precisa fechar
            fosExt.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String lerArquivo(){
        File arq = new File(nomeDiretorio, nomeArquivo);
        if(!arq.exists()){
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(arq));
            String linha;
            String resultado = "";

            while ((linha = br.readLine()) != null) {
                resultado = resultado + linha;
            }
            return resultado;
        }catch(FileNotFoundException f){
            f.printStackTrace();
            return "";
        }catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public void destroy(){
        File arq = new File(nomeDiretorio, nomeArquivo);
        if(arq.exists()){
            arq.delete();
        }
    }
}
