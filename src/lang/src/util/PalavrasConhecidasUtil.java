package util;

import java.util.ResourceBundle;

/**
 * Classe que guarda utilidades referentes as palavras conhecidas da solucao.
 * @author Luiz Peres
 *
 */
public class PalavrasConhecidasUtil 
{
	/**
	 * Recupera o tipo do token de uma palavra conhecida (um lexico) do sistema.
	 * @param resourcePalavraConhecida A chave de resource da palavra conhecida.
	 * @return O tipo do token da palavra conhecida.
	 */
	public static Object getTipoToken(final String resourcePalavraConhecida) 
	{
		return ResourceBundle.getBundle("resources.PalavrasConhecidasResources").getObject(resourcePalavraConhecida);
	}
}
