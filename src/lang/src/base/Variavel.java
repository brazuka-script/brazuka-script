package base;

/**
 * Classe referente ao processo para traduzir as variaveis do arquivo *.bra
 * para java, podendo assim manipula-las.
 * @author Luiz Peres.
 */
public class Variavel 
{
	/** 
	 * O nome da variavel declarada no arquivo.
	 * */
	private String nome;
	
	/** 
	 * A variavel que estara em memoria.
	 * */
	private Object valor;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}
}
