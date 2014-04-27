class Ula{
	public String[] math = {"*","/","%","+","-"};

	public int checkOperation(String part){
		int i;
		for(i=0;i<this.math.length;i++)
			if(part.contains(this.math[i])) return i;
		return -1;
	}

	public boolean tryParse(String number){
		try{
			double a = Double.parseDouble(number);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}

	public double opUla(double a, double b, int op){
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
			default: return -1.0;
		}
	}
}