package base;

import base.PalavraReservada.GeneroToken;
import base.PalavraReservada.TipoToken;

/**
 * Classe que e' responsavel pela base dos tokens da linguagem.
 * @author Luiz Peres
 */
public class Token 
{
	/**
	 * O genero do Token.
	 */
	private GeneroToken generoToken;
	
	/**
	 * O tipo do Token.
	 */
	private TipoToken tipoToken;

	/**
	 * O valor do Token.
	 */
	private String valorToken;
	
	/**
	 * O numero da linha em que o token esta.
	 */
	private int nLinha;
	
	public String getValorToken() {
		return valorToken;
	}
	
	public void setValorToken(String valorToken) {
		this.valorToken = valorToken;
	}
	
	public GeneroToken getGeneroToken() {
		return generoToken;
	}

	public void setGeneroToken(GeneroToken generoToken) {
		this.generoToken = generoToken;
	}

	public int getnLinha() {
		return nLinha;
	}
	
	public void setnLinha(int nLinha) {
		this.nLinha = nLinha;
	}
	
	public TipoToken getTipoToken() {
		return tipoToken;
	}

	public void setTipoToken(TipoToken tipoToken) {
		this.tipoToken = tipoToken;
	}
}
