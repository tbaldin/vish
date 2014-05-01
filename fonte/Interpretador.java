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
	private Variavel vars[];
	private String[] condTokens = {"(",")","end if","then","else","wend"};
	private Ula ula;

	public Interpretador(){
		this.ula = new Ula();
	}
	
	public int interpreta(String l[], Variavel[] variaveis) {
        int token,op,v,j,k,n,ret;
        double result;
        Variavel var;
        Interpretador escopo;
        String[] mainTokens = {"if","dim ","while","print "};
    	String[] endOfLines = {"{","}"};
    	String[] varSintax = {";","="};
        String temp,arr[],str;
        this.vars = variaveis;
        this.linhas = l;
        for(int i=0;i<this.linhas.length;i++) {
            if(this.linhas[i]!=null && this.linhas[i].length()>0 && !this.linhas[i].trim().substring(0,1).equals("'")) {
    			//Se a linha não for nula ou comentada

				token = checkToken(mainTokens,this.linhas[i]);//Verifica o token no incio da linha
				
				switch(token){
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					case 0: // CONDICIONAL
						
						// str = Linha atual tirando o 'if' já identificado do inicio
						str = this.linhas[i].trim().substring(mainTokens[0].length(),this.linhas[i].trim().length()).trim();

						if(str.substring(str.length()-this.condTokens[3].length(),str.length()).equals(this.condTokens[3])){
							// Se tem o 'then' no final da linha	

							// str = linha atual sem o 'if' do início e sem o 'then' já identificado no final e sem os parenteses se existiam
							str = removeParenteses(str.substring(0,str.length()-this.condTokens[3].length()).trim());
							
							if(str==null) return i+1;

							op = this.ula.checkOperation(str); //Verifica o operador existente na expressão

							if(op>=0&&op<=5){
								// se é uma operação condicional
								n=1; // n é a quantidade de 'end if' a encontrar 
								
								int elseL=-1; // elseL é a linha do 'else' se houver
								int endL;	  // endL  é a linha do 'end if', precisa existir
								
								for (endL=i+1;this.linhas[endL]!=null;endL++){
									// for primeira linha do escopo até o final do arquivo
									if(checkToken(mainTokens,this.linhas[endL])==0) n++; // se encontrar mais um if, ignora o próximo end if
									if(this.linhas[endL].trim().equals(this.condTokens[2])) n--; // se encontrar um end if, diminui o contador
									
									if(n==1 && elseL<0 && this.condTokens[4].length()<=this.linhas[endL].trim().length())
										// se está no escopo do if em questão e ainda não encontrou um else, procura por um.
										if(this.linhas[endL].trim().substring(0,this.condTokens[4].length()).equals(this.condTokens[4])) elseL=endL;

									if(n==0) break; // n == 0 significa que encontrou o end if do escopo, sai fora do for.
								}

								if(n>0){
									// Foi até o final do arquivo e não achou o end if do if
									System.out.println("Cara, se tu abriu um 'if' tu tem que especificar um 'end if', como vou adivinhar onde termina?");
									return i+1;
								}

								escopo = new Interpretador(); // instancia um novo interpretador que executará as linhas dentro do escopo do 'if'
								arr = new String[1000];		  // novo vetor de Strings que conterá as linhas do 'if'
								if(elseL>0) k=elseL;
								else k=endL;
								
								if(this.ula.resolveOperacao(str,this)==1.0){	
									// se a condição do 'if' for verdadeira
									for(j=i+1;j<k;j++) // prepara o novo vetor de linhas para ser interpretado
										arr[j-i-1]=this.linhas[j];

								}else if(elseL>0){
									// se a condição do 'if' falhar e houver um 'else'
									// remove o 'else' do início da linha pro caso de haver um outro 'if' após ele na mesma linha
									this.linhas[elseL]=this.linhas[elseL].trim().substring(this.condTokens[4].length(),this.linhas[elseL].trim().length()).trim();
									
									if(this.linhas[elseL].length()>0) endL++; // Se existir algo a mais que o else na linha inclui o 'end if' no escopo

									for(j=elseL;j<endL;j++) // prepara as linhas do escopo do else para serem interpretadas
										arr[j-i-1]=this.linhas[j];
								}

								ret = escopo.interpreta(arr,this.vars); // manda executar o escopo
								if(ret!=0) return ret+i+1; //se houve um erro na execução do escopo
								
								i=endL; // Continua a execução do escopo atual a partir da linha do 'end if'
							}else{
								System.out.println("Condicional IF sem condição. Você é uma piada hein!");
								return i+1;
							}
					
						}else{
							System.out.println("A sintaxe do condicional é: 'if(<condicao>) then'. Entendeu agora fera?");
							return i+1;
						}
						break;
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					case 1: // DECLARAÇÃO DE VARIÁVEL
						// linha tirando o var do inicio, não interessa mais.
						str = this.linhas[i].substring(mainTokens[1].length(),this.linhas[i].length()).trim();

						if(str.contains(varSintax[1])){
							// nome da variável se for declaração de variável com atribuição
							temp = str.split(varSintax[1],2)[0];
						}else{
							// nome da variável se for declaração de variável sem atribuição
							temp = str;
						}

						if(getVariable(temp)==null){
							// se não existe essa variável
							
							v=nextEmptyVar(); // próxima posição livre no vetor de variáveis

							if(str.contains(varSintax[1])){
								// se existe uma atribuição de valor
								arr = str.split(varSintax[1],2); // divide str em um vetor de duas posições: antes e depois da igualdade
								
								this.vars[v] = new Variavel(arr[0]); // cria a variável com o nome à esquerda da igualdade
								
								if(!atribuicao(this.vars[v].nome,arr[1].substring(0,arr[1].length()))){
									System.out.println("Falha ao atribuir valor à variável "+this.vars[v].nome);
									return i+1;
								}
							}else{
								// Se é uma declaração simples sem atribuição de valor só cria a variável com o nome.
								this.vars[v] = new Variavel(temp);
							}
						}else{
							System.out.println("Vish... ou tu usou coisa loca no início do nome ou essa variável já foi declarada cara...");
							return i+1;
						}
						break;
//-------------------------------------------------------------------------------------------------------------------------------------------\\			
					case 2: // LAÇO WHILE
						// tira o while do inicio e os parenteses se existirem, fica só a condição
						str = removeParenteses(linhas[i].trim().substring(mainTokens[2].length(),linhas[i].trim().length()).trim());

						//System.out.println("WHILE: "+str);
						op = this.ula.checkOperation(str); //Verifica o operador existente na expressão

						if(op>=0&&op<=5){
							// se é uma operação condicional
							for (k=i+1;this.linhas[k]!=null;k++){
								// for primeira linha do escopo até o final do arquivo
								if(this.linhas[k].trim().equals(this.condTokens[5])) break; // se encontrar um 'wend', sai fora
							}

							if(!this.linhas[k].trim().equals(this.condTokens[5])){
								// Foi até o final do arquivo e não achou o end if do if
								System.out.println("Cara, se tu abriu um 'while' tu tem que especificar um 'wend', como vou adivinhar onde termina?");
								return i+1;
							}

							escopo = new Interpretador(); // instancia um novo interpretador que executará as linhas dentro do escopo do 'while'
							arr = new String[1000];		  // novo vetor de Strings que conterá as linhas do 'while'
							
							if(this.ula.resolveOperacao(str,this)==1.0){	
								// se a condição do 'while' for verdadeira
								for(j=i+1;j<k;j++) // prepara o novo vetor de linhas para ser interpretado
									arr[j-i-1]=this.linhas[j];
								i--; // volta pra linha anterior pra verificar de novo o while
							}else
								//pula o while
								i=k+1;

							ret = escopo.interpreta(arr,this.vars); // manda executar o escopo
							if(ret!=0) return ret+i+1; //se houve um erro na execução do escopo
						}
						break;
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					case 3: // IMPRESSÃO NA TELA
						// Remove o token do inicio, não precisa mais.
						str = this.linhas[i].trim().substring(mainTokens[3].length(),this.linhas[i].trim().length()).trim();
						
						if(str.substring(0,1).equals("\"")){
							// se é pra imprimir uma string
							if(str.substring(str.length()-1,str.length()).equals("\"")){
								// se as aspas foram fechadas corretamente
								str = str.substring(1,str.length()-1);
								System.out.println(str); // imprime o valor dentro das aspas
							}else{
								System.out.println("Fechar as aspas nunca né?");
								return i+1;
							}

						}else{
							// se é pra imprimir um valor
							result = this.ula.resolveOperacao(str,this); // resolve a expressão
							if(result!=0.88072879){
								// se conseguiu resolver a expressão
								System.out.println(result);
							}else{
								System.out.println("Hã? O quê? Dafuq '"+str+"'?!");
								return i+1;
							}
						}
						break;
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					default:  // SE NÃO FOR TOKEN
						if(this.linhas[i].contains(varSintax[1])){
							// se há um sinal de igualdade indicando atribuição
							arr = this.linhas[i].split(varSintax[1],2); // divide em antes e depois da igualdade

							if(!atribuicao(arr[0],arr[1].substring(0,arr[1].length()))) return i+1; // se não conseguiu fazer a atribuição
						}else{
							// Se não é nada conhecido
							System.out.println("Cara... o que tu tentou fazer?");
							return i+1;
						}
						break;
				}
			}
		}
		return 0; // tudo ok.
	}

	// Método que verifica o que a linha atual deve fazer
	private int checkToken(String[] tokens, String part){
		int i;
		for(i=0;i<tokens.length;i++){
			if(tokens[i].length()<=part.replaceAll("^\\s+", "").length()){
				if(tokens[i].equals(part.replaceAll("^\\s+", "").substring(0,tokens[i].length()))){
					return i;
				}
			}
		}
		return -1;
	}

	// Método que busca o valor da variável correspondente ao nome passado
	public Variavel getVariable(String name){
		int i=0;
		String permitidos = "abcdefghijklmnopqrstuvxyz_";
		if(permitidos.contains(name.trim().substring(0,1).toLowerCase())){
			while(this.vars[i]!=null){
				if(this.vars[i].igual(name.trim())){
					return this.vars[i];
				}
				i++;
			}
		}else{
			return null;
		}
		return null;
	}

	// Retorna a posição no vetor de variáveis disponível para a próxima variável
	private int nextEmptyVar(){
		int i=0;
		while(this.vars[i]!=null) i++;
		return i;
	}

	// Método para executar a atribuição da operação na variável com o nome passado
	private boolean atribuicao(String varName, String operacao){
		String arr[];
		Variavel v=getVariable(varName.trim());
		double value;
		if(v!=null){
			value = this.ula.resolveOperacao(operacao,this);
			if(value!=0.88072879){ // 0.88072879 é erro de operação
				v.valor = this.ula.resolveOperacao(operacao,this);
			}else{
				return false;
			}
		}else{
			return false;
		}
		return true;
	}

	// Checa e remove os parenteses dos condicionais
	private String removeParenteses(String str){
		if(str.substring(0,1).equals(this.condTokens[0])){
			// se inicia com parenteses
			if(str.substring(str.length()-this.condTokens[1].length(),str.length()).equals(this.condTokens[1])){
				// se termina com parenteses
				return str.substring(1,str.length()-1); // str = condição sem os parenteses, não preciso deles
			}else{
				System.out.println("Parenteses aberto sem fechamento. Que vergonha hein.");
				return null;
			}
		}else if(str.substring(str.length()-this.condTokens[1].length(),str.length()).equals(this.condTokens[1])){
			System.out.println("E o inicio desse parenteses aberto, enfio onde?");
			return null;
		}
		return str; // se não tinha parenteses
	}
}