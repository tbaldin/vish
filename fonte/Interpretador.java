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
 	private Variavel[] vars = new Variavel[1000];

    public void interpreta(String l[]) {
        int token,op;
        String[] mainTokens = {"if","var ","while"};
    	String[] logical = {"==",">","<","<>",">=","<=","!="};
    	String[] math = {"+","-","*","/","%"};
    	String[] endOfLines = {"then","{","}"};
    	String[] varSintax = {";","="};
        String temp,aux,arr[],eol;
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
							if(checkVarExists(linhas[i].substring(mainTokens[1].length(),linhas[i].length()))==-1){
								if(linhas[i].substring(mainTokens[1].length(),linhas[i].length()).trim().contains(varSintax[1])){
									arr = linhas[i].substring(mainTokens[1].length(),linhas[i].length()).trim().split("=",2);
									System.out.println("Operação de atribuição: "+arr[0]+" recebe "+arr[1]);
									op=checkOperation(math,arr[1]);
									if(op==-1){
										System.out.println("Atribuição simples");
									}else{
										System.out.println("Atribuição com '"+math[op]+"'");
									}

								

								}else{
									System.out.println("Declaração de variável sem atribuição");
									

								}
							}else{
								System.out.println("ERRO: Vish... essa variável já foi declarada cara...");
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

	private int checkOperation(String[] tokens, String part){
		int i;
		for(i=0;i<tokens.length;i++)
			if(part.contains(tokens[i])) return i;
		return -1;
	}

	private int checkVarExists(String name){
		int i=0;
		while(this.vars[i]!=null){
			if(this.vars[i].equals(name)){
				return i;
			}
			i++;
		}
		return -1;
	}

	private int nextEmptyVar(){
		int i=0;
		while(this.vars[i]!=null) i++;
		return i;
	}

	private double ULA(double a, double b, int op){ //String[] math = {"+","-","*","/","%"};
		switch(op){
			case 0:
				return a+b;
			case 1:
				return a-b;
			case 2:
				return a*b;
			case 3:
				if(b==0.0) return -666.6;
				return a/a;
			case 4:
				return a%b;
			default: return -1.0;
		}
	}
}