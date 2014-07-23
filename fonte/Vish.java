/**
 * Programa principal
 *
 * Esse é o código principal do programa, é o programa exemplo postado pelo professor
 * com as devidas modificações.
 *
 * Originalmente Por Fernando Bevilacqua <fernando.bevilacqua@uffs.edu.br>
 * Modificado Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */

import java.util.Scanner;
import java.io.File;

class Vish{
	public static void main(String[] args){
		File f;
        Scanner s;
        Interpretador b;
        Variavel[] vars = new Variavel[1000]; // Código pode ter, no máximo, 1000 variáveis.
        String linhas[] = new String[2000]; // arquivo pode ter, no máximo, 2000 linhas.
        Funcion[] funcion = new Funcion[10]; // arquivo pode ter no máximo, 10 funções.
        int ret;
        
        try{
            // Referencia o arquivo. args[0] conterá os dados passados pela linha de comando.
            f = new File(args[0]);
            // Mandamos o Scanner ler a partir do arquivo.
            s = new Scanner(f);
            // Instanciamos o interpretador.
            b = new Interpretador();
            
            // Lemos todas as linhas do arquivo para dentro do
            // vetor "linhas". Ignorando linhas vazias.
            int i = 0;
            while(s.hasNext()) {
                linhas[i] = s.nextLine();
                i++;
            }

            // variável reservada utilizada para verificar break nos laços
            vars[Tokens.nBreakFlag] = new Variavel("break");
            
            // Inicializamos o interpretador com o vetor de linhas. A partir
            // desse ponto, o objeto "b" irá interpretar o código lido do arquivo.
            ret = b.interpreta(linhas,vars,funcion);
            if(ret!=0){
                System.out.println("Houston we have a problem na Linha '"+ret+"'. Abortar missão.");
            }            
        }catch (Exception e){
            System.out.println("Vish... não entendi o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
            System.out.println("Uso:");
            System.out.println("    java Vish /caminho/para/arquivo.vish");
            e.printStackTrace();
        }
	}
}