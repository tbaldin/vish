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

    public int interpreta(String l[]) {
        int token,op,v;
        String[] mainTokens = {"if","var ","while","print "};
    	String[] logical = {"==",">","<","<>",">=","<=","!="};
    	String[] endOfLines = {"then","{","}"};
    	String[] varSintax = {";","="};
        String temp,aux,arr[],eol;
        this.linhas = l;
        for(int i = 0; i < this.linhas.length; i++) {
            if(this.linhas[i] != null) {
            	System.out.println("#"+(i+1)+": '"+linhas[i]+"'");
				token = checkToken(mainTokens,linhas[i]);
				if(token>=0){
					switch(token){
						case 0: // verificação de sintaxe se for condicional
							System.out.println("Condicional if");
							break;
						case 1: // DECLARAÇÃO DE VARIÁVEL
							// Verifica se a linha termina com ';' respeitando a sintaxe.
							if(!linhas[i].substring(linhas[i].length()-1,linhas[i].length()).equals(varSintax[0])) {
								System.out.println("Vish... erro de sintaxe na linha: "+(i+1)+" (esperado: ';', encontrado: '"+linhas[i].substring(linhas[i].length()-1,linhas[i].length())+"')");
								return -1;	
							} 

							// Verifica se na declaração existe uma atribuição de valor
							if(linhas[i].substring(mainTokens[1].length(),linhas[i].length()).contains(varSintax[1])){
								temp = linhas[i].substring(mainTokens[1].length(),linhas[i].length()).split(varSintax[1],2)[0];
							}else{
								temp = linhas[i].substring(mainTokens[1].length(),linhas[i].length()-1);
							}//temp conterá o nome da variável a ser declarada.

							//Verifica se já existe uma variável com este nome.
							if(checkVarExists(temp.trim())==-1){
								
								//Busca o próximo espaço livre para guardar a variável
								v=nextEmptyVar();

								//Verifica novamente se irá exisitir uma atribuição de valor
								if(linhas[i].substring(mainTokens[1].length(),linhas[i].length()).trim().contains(varSintax[1])){

									// Divide a String em um vetor de duas posições: antes e depois da igualdade
									arr = linhas[i].substring(mainTokens[1].length(),linhas[i].length()).trim().split(varSintax[1],2);
									
									// Cria uma variável com o nome localizado antes da igualdade
									this.vars[v] = new Variavel(new String(arr[0]));
									
									// metodo para atribuição de valores
									if(!atribuicao(this.vars[v].nome,arr[1].substring(0,arr[1].length()-1))){
										System.out.println("Falha ao atribuir valor à variável "+this.vars[v].nome);
										return -1;
									}
								}else{
									System.out.println("-- Declaração de variável sem atribuição: "+temp);
									this.vars[v] = new Variavel(new String(temp.trim().substring(0,temp.length())));
								}
								System.out.println("----- OK. Variável '"+vars[v].nome+"' criada com valor "+vars[v].valor);
							}else{
								System.out.println("ERRO: Vish... essa variável já foi declarada cara...");
								return -1;
							}
							break;
						case 2: // verificação de sintaxe se for laço
							System.out.println("Laço while");
							break;
						case 3:
							arr = linhas[i].split(" ",2);
							v = checkVarExists(arr[1].substring(0,arr[1].length()-1));
							if(v>=0)
								System.out.println(this.vars[v].valor);
							else{
								System.out.println("Variável '"+arr[1].substring(0,arr[1].length()-1)+"' não encontrada");
								return -1;
							}
							break;
						default: break;
					}
				}else if(linhas[i].contains(varSintax[1])){
						arr = linhas[i].split(varSintax[1],2);
						if(!atribuicao(arr[0],arr[1].substring(0,arr[1].length()-1))){
							System.out.println("Falha na atribuição de valor");
							return -1;
						}
						
				}else{
					System.out.println("Comando não identificado.");
				}
			}
		}
		return 0;
	}

	public static boolean tryParse(String number){
		try{
			double a = Double.parseDouble(number);
			return true;
		}catch (NumberFormatException e) {
			return false;
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
			if(this.vars[i].igual(name)){
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

	private boolean atribuicao(String varName, String operacao){
		String[] math = {"+","-","*","/","%"};
		String arr[];
		int op,varPos=checkVarExists(varName.trim());
		Variavel v;
		if(varPos>=0){
			v=this.vars[varPos];
			// Verifica se existe uma operação matemática no outro lado da igualdade da String
			op=checkOperation(math,operacao);
			
			// -1 significa que não há operação, neste caso é uma atribuição simples.
			if(op==-1){
				System.out.println("-- Atribuição simples: '"+operacao.trim()+"'");
				if(tryParse(operacao.trim())){
					v.valor = Double.parseDouble(operacao.trim());
				}else{
					System.out.println("-- Atribuição contendo variável. Ainda não implementado");
				}
			
			// Atribuição com operação entre dois números
			}else{
				System.out.println("-- Atribuição com a operação ("+math[op]+") em: '"+operacao.substring(0,operacao.length()-1).trim()+"'");
				// Quebra a operação em um vetor de duas posições: antes e depois do operando
				arr = operacao.substring(0,operacao.length()-1).trim().split("\\"+math[op],2);
				
				//Verifica se os dois operandos são números
				if(tryParse(arr[0])&&tryParse(arr[1])){
					// Joga para o valor da variável o retorno do método ULA que recebeu os dois operandos e o número da operação
					v.valor = ULA(Double.parseDouble(arr[0]),Double.parseDouble(arr[1]),op);
				}else{
					System.out.println("-- Atribuição contendo operação com variáveis. Ainda não implementado");
				}
			}
		}else{
			System.out.println("Variável '"+varName+"'' não existe");
			return false;
		}
		return true;
	}
}