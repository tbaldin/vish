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

	/***
	* Imprime na tela a mensagem correspondente ao c�digo do erro gerado
	*/
	public void imprimeErro(){
		switch(error){
			case 404:
				System.out.println("Vari�vel '"+compl+"' n�o encontrada.");
				break;
			case 666:
				System.out.println("Bem-vindo ao inferno. Aqui voc� pode dividir por zero a vontade!");
				break;
			default:
				System.out.println("Erro n�o identificado");
				break;
		}
	}
}