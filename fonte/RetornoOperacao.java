class RetornoOperacao{
	public double result;
	public boolean success;
	public int error;
	public String compl;

	public RetornoOperacao(){
		result = 0.0;
		success = false;
		error = 0;
	}

	public void imprimeErro(){
		switch(error){
			case 404:
				System.out.println("Variável '"+compl+"' não encontrada.");
				break;
			case 666:
				System.out.println("Bem-vindo ao inferno. Aqui você pode dividir por zero a vontade!");
				break;
			default:
				System.out.println("Erro não identificado");
				break;
		}
	}
}