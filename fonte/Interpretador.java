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
        int token,op,v,j,k,n;
        Variavel var;
        double a,b;
        Interpretador escopo;
        String[] mainTokens = {"if","var ","while","print "};
        String[] condTokens = {"(",")","end if","then"};
    	String[] endOfLines = {"{","}"};
    	String[] varSintax = {";","="};
        String temp,arr[],str;
        this.linhas = l;
        for(int i = 0; i < this.linhas.length; i++) {
            if(this.linhas[i] != null) {
            	//System.out.println("<"+(i+1)+">");
				token = checkToken(mainTokens,this.linhas[i]);
				if(token>=0){
					switch(token){
						case 0: // verificação de sintaxe se for condicional
							// linha tirando o IF do inicio, não interessa mais.
							str = this.linhas[i].trim().substring(mainTokens[0].length(),this.linhas[i].trim().length()).trim();

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
								//Agora claro, verifica se de fato existe um verificador pra condição na expressão.
								op = this.ula.checkOperation(str);
								if(op>=5){
									n=1;
									
									//Busca pelo end if do escopo
									for (k=i+1;this.linhas[k]!=null;k++){
										// Se encontrar mais um IF ignora o primeiro end if que encontrar
										if(checkToken(mainTokens,this.linhas[k])==0){
											n++;
										}

										// Quando encontrar um end if diminui n end ifs a encontrar
										if(this.linhas[k].trim().equals(condTokens[2])){
											n--;
										}

										// Se encontrou o end if do escopo sai fora
										if(n==0) break;
									}

									if(n>0){
										System.out.println("if sem end if.");
										return -1;
									}

									//Separa antes e depois da operação
									arr = str.split("\\"+this.ula.opULA[op],2);

									//Busca os valores para a operação lógica
									if(this.ula.tryParse(arr[0])){
										a=Double.parseDouble(arr[0]);
									}else if(getVariable(arr[0])!=null){
										a=getVariable(arr[0]).valor;
									}else{
										System.out.println("Não foi possível identificar '"+arr[0]+"'");
										return -1;
									}
									if (this.ula.tryParse(arr[1])){
										b=Double.parseDouble(arr[1]);
									}else if(getVariable(arr[1])!=null){
										b=getVariable(arr[1]).valor;
									}else{
										System.out.println("Não foi possível identificar '"+arr[1]+"'");
										return -1;
									}

									// Se o resultado da condição for true
									if(this.ula.execOP(a,b,op)==1.0){
										escopo = new Interpretador();
										arr = new String[1000];
										for(j=i+1;j<k;j++){
											arr[j-i-1]=this.linhas[j];
										}
										escopo.interpreta(arr);
									}
									i=k;
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
							str = this.linhas[i].substring(mainTokens[1].length(),this.linhas[i].length()).trim();
							if(str.contains(varSintax[1])){
								temp = str.split(varSintax[1],2)[0];
							}else{
								temp = str;
							}//temp conterá o nome da variável a ser declarada.

							//Verifica se já existe uma variável com este nome.
							if(getVariable(temp)==null){
								
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
								//System.out.println("----- OK. Variável '"+vars[v].nome+"' criada com valor "+vars[v].valor);
							}else{
								System.out.println("ERRO: Vish... ou tu usou coisa loca no início do nome ou essa variável já foi declarada cara...");
								return -1;
							}
							break;
						case 2: // verificação de sintaxe se for laço
							System.out.println("Laço while");
							break;
						case 3:
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
									return -1;
								}

							// Se não for string, é constante ou variável.
							}else{
								
								//Verifica se é um número
								if(this.ula.tryParse(str)){
									System.out.println(str.trim());
								
								// Se for variável testa se existe e imprime seu valor.
								}else{ 
									var = getVariable(str);
									
									// Se existe a variável, v a posição no vetor de variáveis.
									if(var!=null)
										System.out.println(var.valor);
									else{ // Se a variável não existe aborta
										System.out.println("Variável '"+str.trim().substring(0,str.trim().length())+"' não encontrada");
										return -1;
									}
								}
							}
							break;
						default: break;
					}

				// Se não for nenhum dos tokens, é atribuição de valor a uma variável.
				// Testa se tem o sinal de igualdade
				}else if(this.linhas[i].contains(varSintax[1])){
						
						// Divide em antes e depois da igualdade
						arr = this.linhas[i].split(varSintax[1],2);
	
						//Tenta fazer a atribuição
						if(!atribuicao(arr[0],arr[1].substring(0,arr[1].length()))){
							System.out.println("Falha na atribuição de valor");
							return -1;
						}
				
				// Se não é token, nem atribuição de variável.
				}else{
					System.out.println("Comando não identificado.");
					return -1;
				}
			}
		}
		return 0;
	}

	private int checkToken(String[] tokens, String part){
		int i;
		for(i=0;i<tokens.length;i++){
			//System.out.println("----> Compare: '"+tokens[i]+"' com '"+part.replaceAll("^\\s+", "").substring(0,tokens[i].length())+"'");
			if(tokens[i].equals(part.replaceAll("^\\s+", "").substring(0,tokens[i].length()))){
				return i;
			}
		}
		return -1;
	}

	private Variavel getVariable(String name){
		int i=0;
		String permitidos = "abcdefghijklmnopqrstuvxyz_";
		if(permitidos.contains(name.trim().substring(0,1).toLowerCase())){
			while(this.vars[i]!=null){
				if(this.vars[i].igual(name)){
					return this.vars[i];
				}
				i++;
			}
		}else{
			return null;
		}
		return null;
	}

	private int nextEmptyVar(){
		int i=0;
		while(this.vars[i]!=null) i++;
		return i;
	}

	private boolean atribuicao(String varName, String operacao){
		String arr[];
		int op;
		Variavel v=getVariable(varName.trim());
		if(v!=null){
			// Verifica se existe uma operação matemática no outro lado da igualdade da String
			op=this.ula.checkOperation(operacao);
			
			// -1 significa que não há operação, neste caso é uma atribuição simples.
			if(op==-1||op>4){
				if(this.ula.tryParse(operacao.trim())){
					v.valor = Double.parseDouble(operacao.trim());
				}else{
					System.out.println("-- Atribuição contendo variável. Ainda não implementado");
				}
			
			// Atribuição com operação entre dois números
			}else{
				// Quebra a operação em um vetor de duas posições: antes e depois do operando
				arr = operacao.substring(0,operacao.length()).trim().split("\\"+this.ula.opULA[op],2);
				
				//Verifica se os dois operandos são números
				if(this.ula.tryParse(arr[0])&&this.ula.tryParse(arr[1])){
					// Joga para o valor da variável o retorno do método ULA que recebeu os dois operandos e o número da operação
					v.valor = this.ula.execOP(Double.parseDouble(arr[0]),Double.parseDouble(arr[1]),op);
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