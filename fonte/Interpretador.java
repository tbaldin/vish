/**
 * Interpretador das linhas
 *
 * Classe que interpreta cada linha do arquivo aberto.
 *
 * Originalmente Por Fernando Bevilacqua <fernando.bevilacqua@uffs.edu.br>
 * 
 * Por Régis Thiago Feyh <registhiagofeyh@gmail.com>
 */

class Interpretador {
    private String linhas[];
	private String tokens = "if while $";
    public void interpreta(String l[]) {
        this.linhas = l;
        
        for(int i = 0; i < this.linhas.length; i++) {
            if(this.linhas[i] != null) {
				String[] arr = this.linhas[i].split(" ",2);
			    String first = arr[0];
			    String resto = arr[1];
			    System.out.println(first);
			    if(1){ // condição para verificar se está nos tokens
			    	System.out.println("Token encontrado");	
			    } 
                // TODO: interpretar a linha
                System.out.println("Linha " + (i + 1) + ": " + this.linhas[i]);
            }
        }
    }
}