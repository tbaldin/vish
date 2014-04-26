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
        String[] mainTokens = {"if","var","while"};
    	String[] comparadores = {"==",">","<","<>",">=","<=","!="};
    	String[] endOfLines = {";","then","{","}"};
        String first,resto,arr[];
        this.linhas = l;
        for(int i = 0; i < this.linhas.length; i++) {
            if(this.linhas[i] != null) {
				arr = this.linhas[i].split(" ",2);
				first = arr[0];
				resto = arr[1];

				token = checkToken(mainTokens,first);
				if(token>=0){
					switch(token){
						case 0: // verificação de sintaxe se for condicional
							System.out.println("Condicional if");
							break;
						case 1: // verificação de sintaze se for declaração de variável
							System.out.println("Declaração de variável");
							break;
						case 2: // verificação de sintaxe se for laço
							System.out.println("Laço while");
							break;
						default: break;
					}
				}else{
					System.out.println("Não é token, verifica se é variável");
				}
				//System.out.println("Linha " + (i + 1) + ": " + this.linhas[i]);
			}
		}
	}
	private int checkToken(String[] tokens, String part){
		//Arrays.asList(tokens).indexOf(part);
		int i=-1;
		for(i=0;i<tokens.length;i++){
			if(tokens[i].equals(part.substring(0,tokens[i].length()))){
				return i;
			}
		}
		return i;
	}
	private int checkIfVariableExists(String name){
		return 0;
	}

}