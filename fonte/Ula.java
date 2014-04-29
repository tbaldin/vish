/**
 * Classe para realizar cálculos
 *
 * Executa as operações lógicas e aritméticas do interpretador
 *
 * 
 * Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */

class Ula{
	public String[] opULA = {"*","/","%","+","-","==","<=","<>",">=","<",">"};

	public boolean tryParse(String number){
		try{
			double a = Double.parseDouble(number);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}

	public int checkOperation(String part){
		int i;
		for(i=0;i<this.opULA.length;i++)
			if(part.contains(this.opULA[i])) return i;
		return -1;
	}

	public double execOP(double a, double b, int op){
		switch(op){
			case 0:
				return a*b;
			case 1:
				if(b==0.0){System.out.println("Bem vindo ao inferno, aqui você pode dividir por zero."); return -666.6;}
				return a/a;
			case 2:
				return a%b;
			case 3:
				return a+b;
			case 4:
				return a-b;
			case 5:
				return (a==b)?1.0:0.0;
			case 6:
				return (a<=b)?1.0:0.0;
			case 7:
				return (a!=b)?1.0:0.0;
			case 8:
				return (a>=b)?1.0:0.0;
			case 9:
				return (a<b)?1.0:0.0;
			case 10:
				return (a>b)?1.0:0.0;
			default: return -1.0;
		}
	}
}