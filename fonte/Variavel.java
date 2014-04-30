/**
 * Classe de variáveis do interpretador
 *
 * Modelo de variável do interpretador
 * 
 * Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */

class Variavel{
	public String nome;
	public double valor;

	public Variavel(String s){
		this.nome = s.trim();
	}

	public boolean igual(String name){
		return this.nome.equals(name.trim());
	}
}