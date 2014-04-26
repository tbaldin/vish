class Variavel{
	public String nome;
	public double valor;

	public Variavel(String s){
		this.nome = s.trim();
	}

	public boolean igual(String name){
		return this.nome.matches(name);
	}
}