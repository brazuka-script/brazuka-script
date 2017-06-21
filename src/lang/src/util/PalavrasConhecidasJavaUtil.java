package util;

import java.util.ResourceBundle;

/**
 * Classe que guarda utilidades referentes as palavras conhecidas JAVA da solucao.
 * @author Luiz Peres
 *
 */
public class PalavrasConhecidasJavaUtil 
{
	/**
	 * Recupera o valor do lexico em java.
	 * @param lexicoConhecido A chave de resource do lexico conhecido.
	 * @return O valor do lexico em java. 
	 * @throws Exception Erro inesperado.
	 */
	public static Object getValorJava(final String lexicoConhecido) throws Exception 
	{
		try
		{
			return ResourceBundle.getBundle("resources.PalavrasConhecidasJavaResources").getObject(lexicoConhecido);
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
