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
	private Ula ula = new Ula();
	public int interpreta(String l[]) {
        int token,op,v;
        String[] mainTokens = {"if","var ","while","print "};
        String[] condTokens = {"(",")","end if","then"};
    	String[] endOfLines = {"{","}"};
    	String[] varSintax = {";","="};
        String temp,aux,arr[],eol,str;
        this.linhas = l;
        for(int i = 0; i < this.linhas.length; i++) {
            if(this.linhas[i] != null) {
            	System.out.println("#"+(i+1)+": '"+linhas[i]+"'");
				token = checkToken(mainTokens,linhas[i]);
				if(token>=0){
					switch(token){
						case 0: // verificação de sintaxe se for condicional
							// linha tirando o IF do inicio, não interessa mais.
							str = linhas[i].substring(mainTokens[0].length(),linhas[i].length()).trim();

							// procura o then no final da linha
							if(str.substring(str.length()-condTokens[3].length(),str.length()).equals(condTokens[3])){
								
								// remove o then do final, não interessa mais
								str = str.substring(0,str.length()-condTokens[3].length()).trim();
								
								// Verifica se inicia e termina com parenteses
								if(str.substring(0,1).equals(condTokens[0])){
									if(str.substring(str.length()-condTokens[1].length(),str.length()).equals(condTokens[1])){
										//remove os parenteses, não interessa mais. Fica só a condição.
										str = str.substring(1,str.length()-1);
									}else{
										System.out.println("Parenteses aberto na linha sem fechamento. Que vergonha hein.");
										return -1;
									}
								}else if(str.substring(str.length()-condTokens[1].length(),str.length()).equals(condTokens[1])){
									System.out.println("E o inicio desse parenteses aberto, enfio onde?");
									return -1;
								}
								System.out.println(str);	
								//Agora claro, verifica se de fato existe um verificador pra condição na expressão.
								if(this.ula.checkLogicOperation(str)>=0){
									





								}else{
									System.out.println("Condicional IF sem condição. Você é uma piada hein!");
									return -1;
								}
						
							}else{ // Se não encontrou o parenteses depois do IF
								System.out.println("A sintaxe do condicional é: 'if(<condicao>) then'. Entendeu agora fera?");
								return -1;
							}
							break;
						case 1: // DECLARAÇÃO DE VARIÁVEL
								// Verifica se na declaração existe uma atribuição de valor

							// linha tirando o var do inicio, não interessa mais.
							str = linhas[i].substring(mainTokens[1].length(),linhas[i].length()).trim();
							if(str.contains(varSintax[1])){
								temp = str.split(varSintax[1],2)[0];
							}else{
								temp = str;
							}//temp conterá o nome da variável a ser declarada.

							//Verifica se já existe uma variável com este nome.
							if(checkVarExists(temp.trim())==-1){
								
								//Busca o próximo espaço livre para guardar a variável
								v=nextEmptyVar();

								//Verifica novamente se irá exisitir uma atribuição de valor
								if(str.contains(varSintax[1])){

									// Divide a String em um vetor de duas posições: antes e depois da igualdade
									arr = str.split(varSintax[1],2);
									
									// Cria uma variável com o nome localizado antes da igualdade
									this.vars[v] = new Variavel(new String(arr[0]));
									
									// metodo para atribuição de valores
									if(!atribuicao(this.vars[v].nome,arr[1].substring(0,arr[1].length()))){
										System.out.println("Falha ao atribuir valor à variável "+this.vars[v].nome);
										return -1;
									}
								}else{
									this.vars[v] = new Variavel(new String(temp.trim().substring(0,temp.trim().length())));
								}
								System.out.println("----- OK. Variável '"+vars[v].nome+"' criada com valor "+vars[v].valor);
							}else{
								System.out.println("ERRO: Vish... ou tu usou coisa loca no início do nome ou essa variável já foi declarada cara...");
								return -1;
							}
							break;
						case 2: // verificação de sintaxe se for laço
							System.out.println("Laço while");
							break;
						case 3:
							arr = linhas[i].split(" ",2);
							if(this.ula.tryParse(arr[1])){ // Verifica se é um número
								System.out.println(arr[1].trim());
							}else{ // Se fo variável testa se existe e imprime seu valor.
								v = checkVarExists(arr[1].trim().substring(0,arr[1].trim().length()));
								if(v>=0)
									System.out.println(this.vars[v].valor);
								else{ // Se a variável não existe aborta
									System.out.println("Variável '"+arr[1].trim().substring(0,arr[1].trim().length())+"' não encontrada");
									return -1;
								}
							}
							break;
						default: break;
					}
				}else if(linhas[i].contains(varSintax[1])){
						arr = linhas[i].split(varSintax[1],2);
						if(!atribuicao(arr[0],arr[1].substring(0,arr[1].length()))){
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

	private int checkToken(String[] tokens, String part){
		//Arrays.asList(tokens).indexOf(part);
		int i;
		for(i=0;i<tokens.length;i++){
			//System.out.println("----> Compare: "+tokens[i]+" com "+part.substring(0,tokens[i].length()));
			if(tokens[i].equals(part.trim().substring(0,tokens[i].length()))){
				return i;
			}
		}
		return -1;
	}

	private int checkVarExists(String name){
		int i=0;
		String permitidos = "abcdefghijklmnopqrstuvxyz_";
		if(permitidos.contains(name.trim().substring(0,1).toLowerCase())){
			while(this.vars[i]!=null){
				if(this.vars[i].igual(name)){
					return i;
				}
				i++;
			}
		}else{
			System.out.println("Nome de variável inválido.");
			return -2;
		}
		return -1;
	}

	private int nextEmptyVar(){
		int i=0;
		while(this.vars[i]!=null) i++;
		return i;
	}

	private boolean atribuicao(String varName, String operacao){
		String arr[];
		int op,varPos=checkVarExists(varName.trim());
		Variavel v;
		if(varPos>=0){
			v=this.vars[varPos];
			// Verifica se existe uma operação matemática no outro lado da igualdade da String
			op=this.ula.checkOperation(operacao);
			
			// -1 significa que não há operação, neste caso é uma atribuição simples.
			if(op==-1){
				if(this.ula.tryParse(operacao.trim())){
					v.valor = Double.parseDouble(operacao.trim());
				}else{
					System.out.println("-- Atribuição contendo variável. Ainda não implementado");
				}
			
			// Atribuição com operação entre dois números
			}else{
				// Quebra a operação em um vetor de duas posições: antes e depois do operando
				arr = operacao.substring(0,operacao.length()).trim().split("\\"+this.ula.math[op],2);
				
				//Verifica se os dois operandos são números
				if(this.ula.tryParse(arr[0])&&this.ula.tryParse(arr[1])){
					// Joga para o valor da variável o retorno do método ULA que recebeu os dois operandos e o número da operação
					v.valor = this.ula.opMath(Double.parseDouble(arr[0]),Double.parseDouble(arr[1]),op);
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