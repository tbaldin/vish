/**
 * Interpretador das linhas
 *
 * Classe que interpreta cada linha do arquivo aberto.
 * 
 * Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */

import java.util.Arrays;
import java.util.Scanner;
class Interpretador {
	private String linhas[];
	private Variavel vars[];
	private Ula ula;

	/***
	* Construtor que instancia a ULA para auxiliar nas operações
	*/
	public Interpretador(){
		this.ula = new Ula();
	}
	

	/***
	* Método que interpreta as linhas do arquivo e controla as variáveis
	*
	* @param l: linhas para serem interpretadas
	*
	* @param variaveis: vetor de variáveis da interpretação. Serve para quando existir um laço ou um
	*					condicional que instanciará um novo interpretador e chamará este método para
	*					executar o escopo.
	*
	* @return 0 se executou corretamente ou o número da linha se houver algum problema na interpretação.
	*/
	public int interpreta(String[] l, Variavel[] variaveis) {
        int token,op,j,k,n,ret;
        
        RetornoOperacao retorno;
        	
        Interpretador escopo;
        
        String arr[],dim[],str;

        this.vars = variaveis;
        this.linhas = l;

        for(int i=0;i<this.linhas.length;i++) {
            if(this.linhas[i]!=null && this.linhas[i].trim().length()>0 && !this.linhas[i].trim().substring(0,1).equals("'")){
    			//Se a linha não for nula ou comentada

				token = checkToken(Tokens.mainTokens,this.linhas[i]);	//Verifica o token no incio da linha
				
				// Se identificou algum token, remove ele do inicio e joga o restante pra str
				str = (token>=0)?removeToken(this.linhas[i],token):"";
				
				switch(token){

					//------------------------------------------------------------------------------------------------------------------//
					//----------------------------------------- OPERAÇÃO DE DESVIO CONDICIONAL -----------------------------------------\\
					//------------------------------------------------------------------------------------------------------------------//

					case 0:
						// str = Linha atual tirando o 'if' já identificado do inicio
						if(str.substring(str.length()-Tokens.condTokens[3].length(),str.length()).toLowerCase().equals(Tokens.condTokens[3])){
							// Se tem o 'then' no final da linha	

							// str = linha atual sem o 'if' do início e sem o 'then' já identificado no final e sem os parenteses se existiam
							str = removeParenteses(str.substring(0,str.length()-Tokens.condTokens[3].length()).trim());
							
							if(str==null) return i+1;

							op = this.ula.checkOperation(str); //Verifica o operador existente na expressão

							if(op>=0 && op<=5){
								// se é uma operação condicional

								n = 1;				// n é a quantidade de 'end if' a encontrar 
								
								int elseL = -1; 	// elseL é a linha do 'else' se houver
								int endL;	 	 	// endL  é a linha do 'end if', precisa existir
								
								for (endL=i+1;this.linhas[endL]!=null;endL++){
									// for primeira linha do escopo até o final do arquivo
									if(checkToken(Tokens.mainTokens,this.linhas[endL])==0) n++; // se encontrar mais um if, ignora o próximo end if
									if(this.linhas[endL].trim().toLowerCase().equals(Tokens.condTokens[2])) n--; // se encontrar um end if, diminui o contador
									
									if(n==1 && elseL<0 && Tokens.condTokens[4].length()<=this.linhas[endL].trim().length())
										// se está no escopo do if em questão e ainda não encontrou um else, procura por um.
										if(this.linhas[endL].trim().toLowerCase().substring(0,Tokens.condTokens[4].length()).equals(Tokens.condTokens[4])) elseL=endL;

									if(n==0) break; // n == 0 significa que encontrou o end if do escopo, sai fora do for.
								}

								if(n>0){
									// Foi até o final do arquivo e não achou o end if do if
									System.out.println("Cara, se tu abriu um 'if' tu tem que especificar um 'end if', como vou adivinhar onde termina?");
									return i+1;
								}

								escopo 	=	new Interpretador();	// instancia um novo interpretador que executará as linhas dentro do escopo do 'if'
								arr 	=	new String[200];		// novo vetor de Strings que conterá as linhas do 'if'

								// Se tem else, faz o for ir até ele, senão até o endif
								if(elseL>0) k = elseL;
								else 		k = endL;
								
								// Resolve a expressão booleana
								retorno = this.ula.resolveOperacao(str,this);

								if(retorno.success&&retorno.result==1.0){	
									// se a condição do 'if' for verdadeira
									for(j=i+1;j<k;j++) // prepara o novo vetor de linhas para ser interpretado
										arr[j-i-1]=this.linhas[j];
								}else if(!retorno.success){
									retorno.imprimeErro();
									return (i+1);
								}else if(elseL>0){
									// se a condição do 'if' falhar e houver um 'else'
									
									// remove o 'else' do início da linha pro caso de haver um outro 'if' após ele na mesma linha
									this.linhas[elseL]=this.linhas[elseL].trim().substring(Tokens.condTokens[4].length(),this.linhas[elseL].trim().length()).trim();
									
									if(this.linhas[elseL].length()>0) endL++; // Se existir algo a mais que o else na linha inclui o 'end if' no escopo

									for(j=elseL;j<endL;j++) // prepara as linhas do escopo do else para serem interpretadas
										arr[j-i-1]=this.linhas[j];
								}

								ret=escopo.interpreta(arr,this.vars); // manda executar o escopo
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

					//------------------------------------------------------------------------------------------------------------------//
					//----------------------------------- OPERAÇÃO DE DECLARAÇÃO FORMAL DE VARIÁVEIS -----------------------------------\\
					//------------------------------------------------------------------------------------------------------------------//

					case 1:
						// str = linha sem o token do incio
						// dim = vetor de variáveis a serem declaradas.
						dim = str.split(",");
						for(k=0;k<dim.length;k++){
							if(expressaoComVariavel(dim[k])==null)
								return i+1;
						}
						break;

					//------------------------------------------------------------------------------------------------------------------//
					//-------------------------------------------- TRATAMENTO DO LAÇO WHILE --------------------------------------------\\
					//------------------------------------------------------------------------------------------------------------------//

					case 2:
						// str = linha sem o token, tira os parenteses também, servem pra nada.
						str = removeParenteses(str);

						if(str==null) return i+1;

						//System.out.println("WHILE: "+str);
						op = this.ula.checkOperation(str); //Verifica o operador existente na expressão

						if(op>=0&&op<=5){
							// se é uma operação condicional
							n = 1;				// n é a quantidade de 'end if' a encontrar 
							
							for (k=i+1;this.linhas[k]!=null;k++){
								// for primeira linha do escopo até o final do arquivo
								if(checkToken(Tokens.mainTokens,this.linhas[k])==2) n++; // se encontrar mais um if, ignora o próximo end if
								if(this.linhas[k].trim().toLowerCase().equals(Tokens.condTokens[5])) n--; // se encontrar um end if, diminui o contador

								if(n==0) break; // n == 0 significa que encontrou o end if do escopo, sai fora do for.
							}

							if(n>0){
								// Foi até o final do arquivo e não achou o end if do if
								System.out.println("Cara, se tu abriu um 'while' tu tem que especificar um 'wend', como vou adivinhar onde termina?");
								return i+1;
							}

							escopo 	=	new Interpretador(); 	// instancia um novo interpretador que executará as linhas dentro do escopo do 'while'
							arr 	=	new String[200];		// novo vetor de Strings que conterá as linhas do 'while'
							
							retorno = this.ula.resolveOperacao(str,this);

							if(retorno.result==1.0&&this.vars[Tokens.nBreakFlag].valor==0.0){	
								// se a condição do 'while' for verdadeira
								for(j=i+1;j<k;j++){ // prepara o novo vetor de linhas para ser interpretado
									arr[j-i]=this.linhas[j];
								}
								i--; // volta pra linha anterior pra verificar de novo o while
							}else if(!retorno.success){
								retorno.imprimeErro();
								return (i+1);
							}else{
								//pula o while
								i=k;
								this.vars[Tokens.nBreakFlag].valor = 0.0;
							}
							ret = escopo.interpreta(arr,this.vars); // manda executar o escopo
							if(ret!=0) return ret+i+1; //se houve um erro na execução do escopo
						}else{
							System.out.println("Cara, tudo bem ter compulsão por laços infinitos, mas até pra isso tu tem que por uma CONDIÇÃO. Cadê a condição?");
							return i+1;
						}
						break;

					//-------------------------------------------------------------------------------------------------------------------//
					//------------------------------------------ OPERAÇÃO DE IMPRESSÃO NA TELA ------------------------------------------\\
					//-------------------------------------------------------------------------------------------------------------------//

					case 3:
						// str = linha sem o print do inicio
						// Separa a concatenação se houver
						arr = str.split("&");

						// processa e imprime todas as partes
						for(k=0;k<arr.length;k++){
							if(arr[k].length()>0){
								if(arr[k].substring(0,1).equals("\"")){
									// se é pra imprimir uma string
									if(arr[k].substring(arr[k].length()-1,arr[k].length()).equals("\"")){
										// se as aspas foram fechadas corretamente
										arr[k] = arr[k].substring(1,arr[k].length()-1);
										System.out.print(arr[k]); // imprime o valor dentro das aspas
									}else{
										System.out.println("Fechar as aspas nunca né?");
										return i+1;
									}

								}else if(arr[k].length()>1 && arr[k].substring(0,2).equals("\\n")){ // valor \n imprime nova linha
									System.out.println("");
								}else{
									// se é pra imprimir um valor
									retorno = this.ula.resolveOperacao(arr[k],this); // resolve a expressão
									if(retorno.success){
										// se conseguiu resolver a expressão
										System.out.print(retorno.result);
									}else{
										retorno.imprimeErro();
										return i+1;
									}
								}
							}
						}
						break;

					//--------------------------------------------------------------------------------------------------------------------//
					//------------------------------------------- OPERAÇÃO DE ENTRADA DE DADOS -------------------------------------------\\
					//--------------------------------------------------------------------------------------------------------------------//

					case 4:
						// str = linha sem o token, ou seja, variavel que recebera a entrada de dados
						Scanner value = new Scanner(System.in);
						if(!atribuicao(str,value.nextLine())) return i+1;
						break;

					//--------------------------------------------------------------------------------------------------------------------//
					//--------------------------------------------- COMANDO BREAK PARA LAÇOS ---------------------------------------------\\
					//--------------------------------------------------------------------------------------------------------------------//

					case 5:
						this.vars[Tokens.nBreakFlag].valor = 1.0;
						break;

					//--------------------------------------------------------------------------------------------------------------------//
					//-------------------------------------- OPERAÇÕES NÃO IDENTIFICADAS COMO TOKENS--------------------------------------\\
					//-------------------------------- atribuição de valor à variáveis declaradas, no caso--------------------------------//

					default:
						if(this.linhas[i].contains(Tokens.varSintax[0])){
							// se há um sinal de igualdade indicando atribuição
							arr = this.linhas[i].split(Tokens.varSintax[0],2); // divide em antes e depois da igualdade

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

	/***
	* Método que verifica o que a linha atual deve fazer
	*
	* @param tokens: Vetor de tokens para verificar
	* 
	* @param part: String para procurar por algum dos tokens
	*
	* @return a posição do token no vetor passado ou -1 se não encontrar nenhuma correspondência
	*/
	private int checkToken(String[] tokens, String part){
		int i;
		for(i=0;i<tokens.length;i++){
			if(tokens[i].length()<=part.replaceAll("^\\s+", "").length()){
				if(tokens[i].equals(part.replaceAll("^\\s+", "").substring(0,tokens[i].length()).toLowerCase())){
					return i;
				}
			}
		}
		return -1;
	}

	/***
	* Método que busca a variável correspondente ao nome passado
	*
	* @param name: variável que deseja consultar o valor
	*
	* @return o objeto Variavel que possui o nome consultado ou null se não existir
	*/
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

	/***
	* Busca a posição para a próxima variável
	*
	* @return a posição no vetor de variávels do interpretador disponível para ser
	*		  utilizada.
	*/
	private int nextEmptyVar(){
		int i=0;
		while(this.vars[i]!=null) i++;
		return i;
	}


	/***
	* Executa uma atribuição a uma variável
	*
	* @param varName: nome da variável que deseja atribuir um valor
	*
	* @param operacao: String contendo um número ou uma operação matemática com variáveis ou não para ser atribuido a variável
	*
	* @return se a operação foi executada com sucesso ou não
	*/
	private boolean atribuicao(String varName, String operacao){
		String arr[];
		RetornoOperacao retorno;
		Variavel v=getVariable(varName.trim());
		double value;
		if(v!=null){
			retorno = this.ula.resolveOperacao(operacao,this);
			if(retorno.success){ // 0.88072879 é erro de operação
				v.valor = retorno.result;
			}else{
				retorno.imprimeErro();
				return false;
			}
		}else{
			// Se a variável não foi encontrada, cria ela com a expressão
			if(expressaoComVariavel(varName+"="+operacao)==null){
				System.out.println("Dafuq '"+varName.trim()+"'?");
				return false;
			}
		}
		return true;
	}

	/***
	* Os parenteses não servem pra nada, mas se for utilizado esse método verifica se foram utilizados da forma correta,
	* ou seja, parenteses aberto e depois fechado. Após isso remove.
	*
	* @param str: String que possivelmente contém parenteses
	*
	* @return a string sem os parenteses ou null se alguem não os utilizou da maneira correta
	*/

	private String removeParenteses(String str){
		if(str.substring(0,1).equals(Tokens.condTokens[0])){
			// se inicia com parenteses
			if(str.substring(str.length()-Tokens.condTokens[1].length(),str.length()).equals(Tokens.condTokens[1])){
				// se termina com parenteses
				return str.substring(1,str.length()-1); // str = condição sem os parenteses, não preciso deles
			}else{
				System.out.println("Parenteses aberto sem fechamento. Que vergonha hein.");
				return null;
			}
		}else if(str.substring(str.length()-Tokens.condTokens[1].length(),str.length()).equals(Tokens.condTokens[1])){
			System.out.println("E o inicio desse parenteses aberto, enfio onde?");
			return null;
		}
		return str; // se não tinha parenteses
	}


	/***
	* Remove o token já encontrado no início da linha repassada. Após checar qual token precisa o interpretador
	* não precisa mais dele no início da String correspondente à linha, fica só o restante do comando
	* 
	* @param linha: linha atual do interpretador para remover o token do início
	*
	* @param op: 	código do token identificado que será removido
	*/
	private String removeToken(String linha,int op){
		return linha.trim().substring(Tokens.mainTokens[op].length(),linha.trim().length()).trim();
	}


	/***
	* Recebe uma expressão com uma nova variável recebendo ou não algum valor. Se a variável já existir dá erro.
	* Se não exsitir, cria e atribui seu respectivo valor.
	*
	* @param expressao: expressão com a variável. Por exemplo: a=j+1, criará a variável a, se não existir, e irá
	*											  atribuir a ela o valor de j+1. j deve existir.
	*
	* @return a variável criada se a operação for executada com sucesso, null se der algum problema
	*/
	private Variavel expressaoComVariavel(String expressao){
		String str,arr[];
		int n;
		if(expressao.contains(Tokens.varSintax[0])){
			// nome da variável se for declaração de variável com atribuição
			str = expressao.split(Tokens.varSintax[0],2)[0];
		}else{
			// nome da variável se for declaração de variável sem atribuição
			str = expressao;
		}

		if(getVariable(str)==null){
			// se não existe essa variável
			
			n=nextEmptyVar(); // próxima posição livre no vetor de variáveis

			if(expressao.contains(Tokens.varSintax[0])){
				// se existe uma atribuição de valor
				arr = expressao.split(Tokens.varSintax[0],2); // divide str em um vetor de duas posições: antes e depois da igualdade
				
				this.vars[n] = new Variavel(arr[0]); // cria a variável com o nome à esquerda da igualdade
				
				if(!atribuicao(this.vars[n].nome,arr[1].substring(0,arr[1].length()))){
					return null;
				}
			}else{
				// Se é uma declaração simples sem atribuição de valor só cria a variável com o nome.
				this.vars[n] = new Variavel(str);
			}
		}else{
			System.out.println("Vish... ou tu usou coisa loca no início do nome ou essa variável já foi declarada cara...");
			return null;
		}
		return this.vars[n];
	}
}