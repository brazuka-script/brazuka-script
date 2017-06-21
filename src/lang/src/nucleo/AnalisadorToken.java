package nucleo;

import java.util.ArrayList;

import util.ExcecoesUtil;

import base.PalavraReservada;
import base.PalavraReservada.GeneroToken;
import base.Token;

/**
 * Gerencia e analisa os tokens passados atraves de textos utilizando o metodo Tokeniza.
 * @author Luiz Peres.
 */
public class AnalisadorToken 
{
	/**
	 * A lista de tokens do arquivo.
	 */
	private ArrayList<Token> listaTokens;
	
	/**
	 * Verificador que informa se e' ou nao para ignorar uma sequência de caracteres.
	 */
	private Boolean ignorarCaracter = false;

	/**
	 * A primeira posicao verifica o numero de aberturas de comentarios multiplos do arquivo.
	 * A segunda posicao informa o numero da linha do arquivo.
	 */
	private int[] nAberturasComentariosMultiplos = new int[2];
	
	/**
	 * A primeira posicao verifica o numero de fechamentos de comentarios multiplos do arquivo.
	 * A segunda posicao informa o numero da linha do arquivo.
	 */
	private int[] nFechamentosComentariosMultiplos = new int[2];
	
	/** 
	 * O metodo construtor.
	 * */
	public AnalisadorToken() 
	{
		this.listaTokens = new ArrayList<Token>();
		this.nAberturasComentariosMultiplos[0] = 0;
		this.nFechamentosComentariosMultiplos[0] = 0;
	}
	
	/** 
	 * Separa os tokens do sistema e os adiciona a uma lista de tokens.
	 * Esse metodo nada mais e' do que uma maquina de estados para a separacao de tokens.
	 * @param expressao O valor da linha do arquivo.
	 * @param nLinha O numero da linha do arquivo.
	 * @author Luiz Peres 
	 * @throws Exception Gera excecoes caso a entrada nao esteja correta.
	 * */
	public void Tokeniza(final String expressao, int nLinha) throws Exception 
	{
		int i = 0;
		while (i < expressao.length())
		{
			// Essa maquina de estado ignorarCaractere ignora os caracteres quando estiver dentro de um comentario de linhas multiplas.
			if (!this.ignorarCaracter)
			{
				// Verifica se e' inicio de comentario de linha multipla.
				if (i < expressao.length() - 1 && this.isInicioComentarioLinhasMultiplas(expressao.charAt(i), expressao.charAt(i + 1)))
				{
					// Ira ignorar os caracteres ate que encontre o fim '*/' de comentario.
					this.ignorarCaracter = true;
					// Incrementa a abertura de comentario de linhas multiplas.
					this.nAberturasComentariosMultiplos[0]++;
					this.nAberturasComentariosMultiplos[1] = nLinha;
				}
				// Verifica se e' comentario de linha unica, se for comentario com "//", ignora o resto da linha.
				else if (i < expressao.length() - 1 && this.isComentarioLinhaUnica(expressao.charAt(i), expressao.charAt(i + 1)))
				{
					// Sai do laco, ignora a linha.
					break;
				}
				// Verifica se e' uma cadeia de caracteres constante, podendo ser um char 'a' ou mais "constantes"
				else if(this.isInicioCadeiaConstanteCaracter(expressao.charAt(i)))
				{
					i = this.getCadeiaConstanteCaracter(expressao, i, nLinha);
				}
				// Verifica se e' uma palavra valida.
				else if(Character.isLetter(expressao.charAt(i)) || this.isCharEspecialInicioPalavra(expressao.charAt(i)))
				{
					/*  Passamos i por parâmetro porque andaremos posicoes na string "expressao", 
					 *  ou seja, ao final, i tera um valor diferente(se a palavra for maior que um caracter).
					 */
					i = this.getPalavra(expressao, i, nLinha);
				}
				// Verifica se e' um numero valido.
				else if(Character.isDigit(expressao.charAt(i)))
				{
					i = this.getNumero(expressao, i, nLinha);
				}
				// Verifica se e' um operador valido.
				else if(this.isOp(expressao.charAt(i)))
				{
					i = this.getOp(expressao, i, nLinha);
				}
				// Verifica se e' um Caracter Terminal (Caracter unico).
				else if(isCharTerminal(expressao.charAt(i)))
				{
					i = this.getTerminal(expressao, i, nLinha);
				}
				else
				{
					Character c = expressao.charAt(i);
					// Verifica se nao e' espaco, nem tabulacao e etc, gerando o erro, caso o caracter seja desconhecido.
					if(!c.toString().matches(PalavraReservada.Espaco))
					{
						ExcecoesUtil.GeraExcecao("\'" + expressao.charAt(i) + "\' ", "CaracterDesconhecido", nLinha);
					}
				}
			}
			else
			{
				// Verifica se e' fim de comentario de linha multipla.
				if(i < expressao.length() - 1 && this.isFimComentarioLinhasMultiplas(expressao.charAt(i), expressao.charAt(i + 1)))
				{
					this.ignorarCaracter = false;
					// Incrementa o fechamento de comentario de linhas multiplas.
					this.nFechamentosComentariosMultiplos[0]++;
					this.nFechamentosComentariosMultiplos[1] = nLinha;
					// Esse incremento no i, e' para pular a ultima barra(para o sistema nao inseri-la como token '/').
					i++;
				}
			}
			
			i++;
		}
	}

	/**
	 * Verifica se nao existem comentarios de multiplas linhas que nao foram fechados corretamente.
	 * Essa funcao so deve ser chamada apos o termino de leitura do arquivo fonte.
	 * @throws Exception Gera Excecao caso os comentarios de multiplas linhas nao tenham sido fechados.
	 */
	public void TokensComentariosFechados() throws Exception 
	{
		/* Caso o numero de abertura de comentarios seja maior que o numero de fechamento de comentarios multiplos 
		 * significa que nao foi fechado corretamente os comentarios de multiplas linhas.
		 */
		if (this.nAberturasComentariosMultiplos[0] > this.nFechamentosComentariosMultiplos[0])
		{
			ExcecoesUtil.GeraExcecao("", "ComentarioLMSemFechamento", nAberturasComentariosMultiplos[1]);
		}
	}

	/**
	 * Gera um token de cadeia de constante de caracter e adiciona o token na lista de tokens.
	 * Nele esta incluso a maquina de estado de constante de caracter (no formato 'a') e a maquina de estado
	 * de cadeia de caracteres (no formato "cadeia de caracteres").
	 * @param expressao O valor da linha do arquivo.
	 * @param i A posicao do index na expressao.
	 * @param nLinha O numero da linha do token.
	 * @return A nova posicao do index.
	 */
	private int getCadeiaConstanteCaracter(String expressao, int i, int nLinha) throws Exception
	{
		int index = i;
				
		Token token = new Token();
		token.setGeneroToken(GeneroToken.GT_PALAVRA);
		token.setnLinha(nLinha);
		
		// a primeira posicao sempre e' o comeco da cadeia constante de caracter.
		String valorToken = String.valueOf(expressao.charAt(index));
		index++;
		
		// Verifica se e' uma constante de char, do tipo 'a', por exemplo.
		if (valorToken.charAt(0) == PalavraReservada.InicioCadeiaConstanteCaracter.charAt(0))
		{
			while (index < expressao.length())
			{ 
				valorToken += String.valueOf(expressao.charAt(index));
				// Verifica se e' um caracter normal ou com escape e gera erro caso nao seja um caracter valido.
				if ((valorToken.length() > 3 && valorToken.charAt(1) != PalavraReservada.CaracterEscape) || valorToken.length() > 4)
				{
					ExcecoesUtil.GeraExcecao(valorToken, "ConstanteCaracterIncorreto", nLinha);
				}
				
				if(expressao.charAt(index) == PalavraReservada.InicioCadeiaConstanteCaracter.charAt(0))
				{
					break;
				}
				
				index++;
			}
			
			// Sempre ira comecar com aspa simples, entao verificamos se termina com aspa simples tambem para garantir que e' um caracter.
			if (!valorToken.endsWith(String.valueOf(PalavraReservada.InicioCadeiaConstanteCaracter.charAt(0))))
			{
				ExcecoesUtil.GeraExcecao(valorToken, "ConstanteCaracterIncorreto", nLinha);
			}
		}
		// Verifica se e' uma constante de cadeia de char, do tipo "cadeia de char", por exemplo.
		else if (valorToken.charAt(0) == PalavraReservada.InicioCadeiaConstanteCaracter.charAt(1)) 
		{
			while (index < expressao.length())
			{ 
				valorToken += String.valueOf(expressao.charAt(index));
				// Sai do laco quando encontrar o fim da cadeia de caracteres.
				if(expressao.charAt(index) == PalavraReservada.InicioCadeiaConstanteCaracter.charAt(1))
				{
					break;
				}
				
				index++;
			}
			
			// Sempre ira comecar com aspa simples, entao verificamos se termina com aspa simples tambem para garantir que e' um caracter.
			if (!valorToken.endsWith(String.valueOf(PalavraReservada.InicioCadeiaConstanteCaracter.charAt(1))))
			{
				ExcecoesUtil.GeraExcecao(valorToken, "ConstanteCaracterIncorreto", nLinha);
			}
		}
		
		token.setValorToken(valorToken);
		this.listaTokens.add(token);
		
		return index;
	}
	
	/**
	 * Gera um token de caracter terminal.
	 * @param expressao O valor da linha do arquivo.
	 * @param i A posicao do index na expressao.
	 * @param nLinha O numero da linha do token.
	 * @return A nova posicao do index.
	 */
	private int getTerminal(final String expressao, final int i, final int nLinha) 
	{
		int index = i;
		Token token = new Token();
		token.setGeneroToken(GeneroToken.GT_CARACTER_TERMINAL);
		token.setnLinha(nLinha);
		
		// A primeira posicao sempre e' um numero.
		String valorToken = String.valueOf(expressao.charAt(index));
		index++;
		
		token.setValorToken(valorToken);
		this.listaTokens.add(token);
		
		// Retorna o index - 1 porque no proximo loop ele ira incrementar, neutralizando assim essa ocorrência.
		return index - 1;
	}

	/**
	 * Gera o token de um operador e adiciona o token na lista de tokens.
	 * @param expressao O valor da linha do arquivo.
	 * @param i A posicao do index na expressao.
	 * @param nLinha O numero da linha do token. 
	 * @return A nova posicao do index.
	 * @throws Exception Gera excecao quando o operador nao condiz com o suportado pela linguagem.
	 */
	private int getOp(final String expressao, final int i, final int nLinha) throws Exception 
	{
		int index = i;
		int contadorOp = 0;
		Token token = new Token();
		token.setGeneroToken(GeneroToken.GT_OPERADOR);
		token.setnLinha(nLinha);
		
		// A primeira posicao sempre e' um operador.
		String valorToken = String.valueOf(expressao.charAt(index));
		index++;
		contadorOp++;
		
		while (index < expressao.length())
		{ 
			if(this.isOp(expressao.charAt(index)))
			{
				valorToken += String.valueOf(expressao.charAt(index));
				contadorOp++;
			}
			else
			{
				break;
			}
			
			// Se o contador de operador for maior que 2, entao e' um erro.
			if(contadorOp > 2)
			{
				ExcecoesUtil.GeraExcecao("", "OperadorIncorreto", nLinha);
			}
			
			index++;
		}
		
		/* Verifica se e' fim de comentario, e se for gera erro porque e' um comentario de linha multipla que nao foi aberto */
		if(contadorOp == 2 && this.isFimComentarioLinhasMultiplas(valorToken.charAt(0), valorToken.charAt(1)))
		{
			ExcecoesUtil.GeraExcecao("", "ComentarioLMSemAbertura", nLinha);
		}
		
		token.setValorToken(valorToken);
		this.listaTokens.add(token);
		
		// Retorna o index - 1 porque no proximo loop ele ira incrementar, neutralizando assim essa ocorrência.
		return index - 1;
	}

	/**
	 * Gera o token de um numero e adiciona o token na lista de tokens.
	 * @param expressao O valor da linha do arquivo.
	 * @param i A posicao do index na expressao.
	 * @param nLinha O numero da linha do token.
	 * @return A nova posicao do index.
	 * @throws Exception Gera excecao quando o numero contem mais de uma virgula.
	 */
	private int getNumero(final String expressao, final int i, final int nLinha) throws Exception 
	{
		int index = i;
		int contVirgulas = 0;
		Token token = new Token();
		token.setGeneroToken(GeneroToken.GT_NUMERO);
		token.setnLinha(nLinha);
		
		// A primeira posicao sempre e' um numero.
		String valorToken = String.valueOf(expressao.charAt(index));
		index++;
		
		while (index < expressao.length())
		{
			if(Character.isDigit(expressao.charAt(index)) || expressao.charAt(index) == PalavraReservada.CaracterSeparadorNumeroFlutuante)
			{
				if(expressao.charAt(index) == PalavraReservada.CaracterSeparadorNumeroFlutuante)
				{
					if((index + 1 < expressao.length()) && Character.isDigit(expressao.charAt(index + 1)) && contVirgulas == 0)
					{
						valorToken += String.valueOf(expressao.charAt(index));
					}
					
					contVirgulas++;
					if (contVirgulas > 1)
					{
						ExcecoesUtil.GeraExcecao("", "NumeroIncorreto", nLinha);
					}
				}
				else
				{
					valorToken += String.valueOf(expressao.charAt(index));
				}
			}
			else
			{
				break;
			}
			
			index++;
		}
		
		token.setValorToken(valorToken);
		this.listaTokens.add(token);
		
		// Retorna o index - 1 porque no proximo loop ele ira incrementar, neutralizando assim essa ocorrência.
		return index - 1;
	}

	/**
	 * Gera o token de uma palavra e adiciona o token na lista de tokens.
	 * @param expressao O valor da linha do arquivo.
	 * @param i A posicao do index na expressao.
	 * @param nLinha O numero da linha do token.
	 * @return A nova posicao do index.
	 */
	private int getPalavra(final String expressao, final int i, final int nLinha) 
	{
		int index = i;
		Token token = new Token();
		token.setGeneroToken(GeneroToken.GT_PALAVRA);
		token.setnLinha(nLinha);
		
		// A primeira posicao sempre e' um caracter para palavras.
		String valorToken = String.valueOf(expressao.charAt(index));
		index++;
		
		while (index < expressao.length())
		{
			if(Character.isLetterOrDigit(expressao.charAt(index)) || this.isCaracterEspecialMeioPalavra(expressao.charAt(index)))
			{
				valorToken += String.valueOf(expressao.charAt(index));
			}
			else // Nao e' caracter ou numero ou underline, entao acabou a palavra.
			{
				break;
			}
			
			index++;
		}
		
		token.setValorToken(valorToken);
		this.listaTokens.add(token);
		
		// Retorna o index - 1 porque no proximo loop ele ira incrementar, neutralizando assim essa ocorrência.
		return index - 1;
	}
	
	/**
	 * Verifica se e' um inicio de cadeira constante de caracter.
	 * @param oChar O caracter a ser verificado.
	 * @return Verdadeiro se for um inicio de cadeia de caracter.
	 */
	private boolean isInicioCadeiaConstanteCaracter(char oChar) 
	{
		return PalavraReservada.InicioCadeiaConstanteCaracter.contains(String.valueOf(oChar));
	}

	/**
	 * Verifica se e' um comentario de linha unica. Por exemplo: //isso e' um comentario.
	 * e' passado dois caracteres porque e' um token multiplo (apenas o primeiro caracter nao e' suficiente para verificar se e' um comentario).
	 * @param oChar O caracter a ser verificado (primeira posicao).
	 * @param oChar2 O segundo caracter a ser verificado (primeira posicao).
	 * @return Verdadeiro se for um comentario de linha unica.
	 */
	private boolean isComentarioLinhaUnica(Character oChar, Character oChar2) 
	{
		return PalavraReservada.ComentarioLinhaUnica.equals(oChar.toString() + oChar2.toString());
	}
	
	/**
	 * Verifica se e' um fim de comentario de linhas multiplas.
	 * e' passado dois caracteres porque e' um token multiplo (apenas o primeiro caracter nao e' suficiente para verificar se e' um comentario).
	 * @param oChar O caracter a ser verificado (primeira posicao).
	 * @param oChar2 O segundo caracter a ser verificado (primeira posicao).
	 * @return Verdadeiro se for um fim de comentario de linhas multiplas.
	 */
	private boolean isFimComentarioLinhasMultiplas(Character oChar, Character oChar2) 
	{
		return PalavraReservada.FimComentarioLinhasMultiplas.equals(oChar.toString() + oChar2.toString());
	}

	/**
	 * Verifica se e' um inicio de comentario de linhas multiplas.
	 * e' passado dois caracteres porque e' um token multiplo (apenas o primeiro caracter nao e' suficiente para verificar se e' um comentario).
	 * @param oChar O caracter a ser verificado (primeira posicao).
	 * @param oChar2 O segundo caracter a ser verificado (primeira posicao).
	 * @return Verdadeiro se for um inicio de comentario de linhas multiplas.
	 */
	private boolean isInicioComentarioLinhasMultiplas(Character oChar, Character oChar2) 
	{
		return PalavraReservada.InicioComentarioLinhasMultiplas.equals(oChar.toString() + oChar2.toString());
	}
	
	/**
	 * Verifica se e' um caracter especial de inicio de palavra.
	 * @param oChar O caracter atual da expressao(linha do arquivo).
	 * @return Verdadeiro se for um caracter especial de inicio de palavra.
	 */
	private boolean isCharEspecialInicioPalavra(char oChar) 
	{
		return PalavraReservada.InicioPalavra.contains(String.valueOf(oChar));
	}

	/**
	 * Verifica se um caracter e' operador.
	 * @param oChar O caracter atual da expressao(linha do arquivo). 
	 * @return Verdadeiro se for um operador.
	 */
	private boolean isOp(char oChar)
	{
		return PalavraReservada.Operadores.contains(String.valueOf(oChar));
	}
	
	/**
	 * Verifica se um caracter e' terminal.
	 * @param oChar O caracter atual da expressao(linha do arquivo).
	 * @return Verdadeiro se for um caracter terminal.
	 */
	private boolean isCharTerminal(char oChar)
	{
		return PalavraReservada.CaracteresTerminais.contains(String.valueOf(oChar));
	}
	
	/**
	 * Verifica se um caracter especial pode estar no meio de uma palavra.
	 * @param oChar O caracter atual da expressao(linha do arquivo).
	 * @return Verdadeiro se for um caracter terminal.
	 */
	private boolean isCaracterEspecialMeioPalavra(char oChar)
	{
		return PalavraReservada.CaracterEspecialMeioPalavra.contains(String.valueOf(oChar));
	}

	public ArrayList<Token> getListaTokens() {
		return listaTokens;
	}

	public void setListaTokens(ArrayList<Token> listaTokens) {
		this.listaTokens = listaTokens;
	}
}
