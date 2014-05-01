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
	private Ula ula;

	public Interpretador(){
		this.ula = new Ula();
	}
	
	public int interpreta(String l[], Variavel[] variaveis) {
        int token,op,v,j,k,n,ret,elseL,endL;
        double result;
        Variavel var;
        Interpretador escopo;
        String[] mainTokens = {"if","var ","while","print "};
        String[] condTokens = {"(",")","end if","then","else"};
    	String[] endOfLines = {"{","}"};
    	String[] varSintax = {";","="};
        String temp,arr[],str;
        this.vars = variaveis;
        this.linhas = l;
        for(int i=0;i<this.linhas.length;i++) {
    
        	//Verifica se existe algo na linha
            if(this.linhas[i]!=null&&!this.linhas[i].substring(0,1).equals("'")) {
    
            	//Verifica o token no incio da linha
				token = checkToken(mainTokens,this.linhas[i]);
				
				switch(token){
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					case 0: // CONDICIONAL
						// str = Linha atual tirando o 'if' já identificado do inicio
						str = this.linhas[i].trim().substring(mainTokens[0].length(),this.linhas[i].trim().length()).trim();

						// Procura o 'then' no final da linha
						if(str.substring(str.length()-condTokens[3].length(),str.length()).equals(condTokens[3])){
							
							// str = linha atual sem o 'if' do início e sem o 'then' já identificado no final
							str = str.substring(0,str.length()-condTokens[3].length()).trim();
							
							// Verifica se inicia e termina com parenteses
							if(str.substring(0,1).equals(condTokens[0])){
								if(str.substring(str.length()-condTokens[1].length(),str.length()).equals(condTokens[1])){
									// str = condição sem os parenteses, não preciso deles
									str = str.substring(1,str.length()-1);
								}else{
									System.out.println("Parenteses aberto sem fechamento. Que vergonha hein.");
									return i+1;
								}
							}else if(str.substring(str.length()-condTokens[1].length(),str.length()).equals(condTokens[1])){
								System.out.println("E o inicio desse parenteses aberto, enfio onde?");
								return i+1;
							}

							//Agora claro, verifica se de fato existe um verificador pra condição na expressão.
							op = this.ula.checkOperation(str);

							// Verifica se o operador encontrado é um condicional
							if(op>=0&&op<=5){
								n=1; // n é o número de 'end if' a encontrar 
								
								elseL=-1; // elseL é a linha do else se houver
										  // endL  é a linha do end if
								
								//Busca pelo end if do escopo
								for (endL=i+1;this.linhas[endL]!=null;endL++){
									// Se encontrar mais um 'if' ignora o primeiro 'end if' que encontrar
									if(checkToken(mainTokens,this.linhas[endL])==0) n++;
									if(this.linhas[endL].trim().equals(condTokens[2])) n--;
									
									// procura se é um else
									if(n==1&&condTokens[4].length()<=this.linhas[endL].trim().length())
										if(this.linhas[endL].trim().substring(0,condTokens[4].length()).equals(condTokens[4])) elseL=endL;

									// Se encontrou o end if do escopo sai fora
									if(n==0) break;
								}

								if(n>0){
									System.out.println("Cara, se tu abriu um 'if' tu tem que especificar um 'end if', como vou adivinhar onde termina?");
									return i+1;
								}

								// Se o resultado da condição for true, str contém somente o condicional agora
								escopo = new Interpretador();
								arr = new String[1000];
								if(elseL>0) k=elseL;
								else k=endL;
								if(this.ula.resolveOperacao(str,this)==1.0){	
									for(j=i+1;j<k;j++){
										arr[j-i-1]=this.linhas[j];
									}	
								}else if(elseL>0){
									// Remove o else do início da linha pro caso de haver um outro if após ele na mesma linha
									this.linhas[elseL]=this.linhas[elseL].trim().substring(condTokens[4].length(),this.linhas[elseL].trim().length()).trim();
									if(this.linhas[elseL].length()==0) elseL++; // Se não houver mais nada quando remover o if, pula a linha
									for(j=elseL;j<endL;j++){
										arr[j-i-1]=this.linhas[j];
									}
								}

								// manda executar o escopo
								ret = escopo.interpreta(arr,this.vars);
								if(ret!=0) return ret+i+1; //Se houve um erro na execução do escopo
								
								i=endL; // Continua a execução do escopo atual a partir do end if
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
							// Nome da variável se for declaração de variável com atribuição
							temp = str.split(varSintax[1],2)[0];
						}else{
							// Nome da variável se for declaração de variável sem atribuição
							temp = str;
						}

						//Verifica se não existe uma variável com este nome
						if(getVariable(temp)==null){
							
							//Busca o próximo espaço livre para guardar a variável
							v=nextEmptyVar();

							//Verifica novamente se irá exisitir uma atribuição de valor
							if(str.contains(varSintax[1])){

								// Divide a String em um vetor de duas posições: antes e depois da igualdade
								arr = str.split(varSintax[1],2);
								
								// Cria uma variável com o nome localizado antes da igualdade
								this.vars[v] = new Variavel(arr[0]);
								
								// metodo para atribuição de valores
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
						System.out.println("Laço while");
						break;
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					case 3: // IMPRESSÃO NA TELA
						// Remove o token do inicio, não precisa mais.
						str = this.linhas[i].trim().substring(mainTokens[3].length(),this.linhas[i].trim().length()).trim();
						
						// Verifica se tem uma aspa no início, no caso de strings
						if(str.substring(0,1).equals("\"")){

							// Verifica se existe um fechamento das aspas
							if(str.substring(str.length()-1,str.length()).equals("\"")){
								str = str.substring(1,str.length()-1);
								System.out.println(str);
							}else{
								System.out.println("Fechar as aspas nunca né?");
								return i+1;
							}

						// Se não for string, é constante ou variável.
						}else{
							result = this.ula.resolveOperacao(str,this);
							if(result!=0.88072879){
								System.out.println(result);
							}else{
								System.out.println("Hã? O quê? Dafuq '"+str+"'?!");
								return i+1;
							}
						}
						break;
//-------------------------------------------------------------------------------------------------------------------------------------------\\
					default:  // SE NÃO FOR TOKEN
						// Verifica se há uma atribuição
						if(this.linhas[i].contains(varSintax[1])){	
							// Divide em antes e depois da igualdade
							arr = this.linhas[i].split(varSintax[1],2);

							//Tenta fazer a atribuição
							if(!atribuicao(arr[0],arr[1].substring(0,arr[1].length()))) return i+1;
						// Se não é token, nem atribuição de variável.
						}else{
							System.out.println("Cara... o que tu tentou fazer?");
							return i+1;
						}
						break;
				}
			}
		}
		return 0;
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
}