package util;

import java.util.ResourceBundle;

/**
 * Classe que guarda utilidades referentes as excecoes da solucao.
 * @author Luiz Peres
 *
 */
public class ExcecoesUtil 
{
	/**
	 * Gera uma excecao a partir de um resource de excecao.
	 * @param resourceExcecao A chave do resource de excecao.
	 * @throws Exception A excecao de acordo com o resource passado por parâmetro.
	 */
	public static void GeraExcecao(String resourceExcecao) throws Exception
	{
		throw new Exception(ResourceBundle.getBundle("resources.ExcecoesResources").getString(resourceExcecao));
	}
	
	/**
	 * Gera uma excecao a partir de um resource de excecao e uma string inicial mostrando a linha de ocorrencia do erro.
	 * @param strInicial A string que vem antes do erro.
	 * @param resourceExcecao A chave do resource de excecao.
	 * @param nLinha O numero da linha em que ocorreu o erro.
	 * @throws Exception A excecao de acordo com o resource passado por parâmetro.
	 */
	public static void GeraExcecao(String strInicial, String resourceExcecao, int nLinha) throws Exception
	{
		throw new Exception(strInicial + " " + ResourceBundle.getBundle("resources.ExcecoesResources").getString(resourceExcecao) + " Linha: "+ nLinha);
	}
}
