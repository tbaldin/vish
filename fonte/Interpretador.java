/**
 * Interpretador das linhas
 *
 * Classe que interpreta cada linha do arquivo aberto.
 *
 * Originalmente Por Fernando Bevilacqua <fernando.bevilacqua@uffs.edu.br>
 * 
 * Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */
import java.util.Arrays;
class Interpretador {
    private String linhas[];
 
    public void interpreta(String l[]) {
        int token;
        String[] mainTokens = {"if","var ","while"};
    	String[] comparadores = {"==",">","<","<>",">=","<=","!="};
    	String[] endOfLines = {";","then","{","}"};
    	String[] varSintax = {";","="};
        String temp,aux,arr[];
        this.linhas = l;
        for(int i = 0; i < this.linhas.length; i++) {
            if(this.linhas[i] != null) {
				token = checkToken(mainTokens,linhas[i]);
				if(token>=0){
					switch(token){
						case 0: // verificação de sintaxe se for condicional
							System.out.println("Condicional if");
							break;
						case 1: // verificação de sintaze se for declaração de variável
							if(linhas[i].substring(mainTokens[1].length(),linhas[i].length()).trim().contains(varSintax[1])){
								System.out.println("Declaração de variável com atribuição");
							}else{
								System.out.println("Declaração de variável sem atribuição");
							}

							break;
						case 2: // verificação de sintaxe se for laço
							System.out.println("Laço while");
							break;
						default: break;
					}
				}else{
					System.out.println("Não é token, verifica se é variável");
				}
			}
		}
	}
	private int checkToken(String[] tokens, String part){
		//Arrays.asList(tokens).indexOf(part);
		int i;
		for(i=0;i<tokens.length;i++){
			//System.out.println("----> Compare: "+tokens[i]+" com "+part.substring(0,tokens[i].length()));
			if(tokens[i].equals(part.substring(0,tokens[i].length()))){
				return i;
			}
		}
		return -1;
	}
	private int checkIfVariableExists(String name){
		return 0;
	}

}