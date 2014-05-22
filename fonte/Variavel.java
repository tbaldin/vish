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

	/**
	* Construtor da classe que instancia com o nome da variável removendo espaços
	* dos dois lados.
	*/
	public Variavel(String s){
		this.nome = s.trim();
	}


	/***
	* Verifica se o nome passado é igual ao nome da variavel
	*
	* @param name: o nome para verificar(avá?)
	*
	* @return se o nome é igual ao parametro ou não.
	*/
	public boolean igual(String name){
		return this.nome.equals(name.trim());
	}
}