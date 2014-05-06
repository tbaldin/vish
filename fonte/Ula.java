/**
 * Classe para realizar cálculos
 *
 * Executa as operações lógicas e aritméticas do interpretador
 *
 * 
 * Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */

class Ula{
	// Verifica se é possível converter a String em número.
	public boolean tryParse(String number){
		try{
			double a = Double.parseDouble(number);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}

	// Verifica qual das operações está sendo executada
	public int checkOperation(String part){
		int i;
		for(i=0;i<Tokens.opULA.length;i++)
			if(part.contains(Tokens.opULA[i])) return i;
		return -1;
	}

	// Resolve a operação passada na String. O interpretador é necessário para buscar as variáveis através de seus métodos
	public RetornoUla resolveOperacao(String operacao, Interpretador p){
		double a,b;
		RetornoUla retorno = new RetornoUla();
		int op = checkOperation(operacao);
		
		//Se houver alguma operação matemática
		if(op!=-1){
			// Divide os operandos em um vetor de duas posições
			String arr[] = operacao.split("\\"+Tokens.opULA[op],2);
			arr[0]=arr[0].trim();
			arr[1]=arr[1].trim();

			//Busca os valores para a operação
			if(tryParse(arr[0])){
				a=Double.parseDouble(arr[0]);
			}else if(p.getVariable(arr[0])!=null){
				a=p.getVariable(arr[0]).valor;
			}else{
				retorno.error = 404;
				retorno.compl = arr[0];
				return retorno;
			}
			if (tryParse(arr[1])){
				b=Double.parseDouble(arr[1]);
			}else if(p.getVariable(arr[1])!=null){
				b=p.getVariable(arr[1]).valor;
			}else{
				retorno.error = 404;
				retorno.compl = arr[0];
				return retorno;
			}

			//Executa a operação que precisa ser realizada e retorna o resultado
			if(op==7&&b==0){ // divisão por zero
				retorno.error = 666;
				return retorno;
			}

			retorno.success = true;
			retorno.result = execOP(a,b,op);
			return retorno;
		// Se não houver atribuição
		}else{
			//Verifica se é número ou variável
			if(tryParse(operacao)){
				retorno.success = true;
				retorno.result = Double.parseDouble(operacao);
				return retorno;
			}else{
				//Verfica se a variável existe ou não
				if(p.getVariable(operacao)!=null){
					retorno.success = true;
					retorno.result = p.getVariable(operacao).valor;
					return retorno;
				}else{
					retorno.error = 404;
					retorno.compl = operacao.trim();
					return retorno;
				}
			}
		}
	}

	// Executa a operação lógica ou aritmética
	public double execOP(double a, double b, int op){
		switch(op){
			case 0:
				return (a==b)?1.0:0.0;
			case 1:
				return (a<=b)?1.0:0.0;
			case 2:
				return (a!=b)?1.0:0.0;
			case 3:
				return (a>=b)?1.0:0.0;
			case 4:
				return (a<b)?1.0:0.0;
			case 5:
				return (a>b)?1.0:0.0;
			case 6:
				return a*b;
			case 7:
				return a/b;
			case 8:
				return a%b;
			case 9:
				return a+b;
			case 10:
				return a-b;
			default: return 0.0;
		}
	}
}