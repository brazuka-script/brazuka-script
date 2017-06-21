package nucleo;

import java.util.ArrayList;

import util.ExcecoesUtil;
import util.PalavrasConhecidasUtil;

import base.PalavraReservada;
import base.PalavraReservada.TipoToken;
import base.Token;

//import base.PalavraReservada;

/**
 * Gerencia, analisa e informa o tipo dos tokens gerados pelo analisador de token. 
 * @author Luiz Peres.
 */
public class AnalisadorLexico 
{
	/**
	 * O contador de chaves para poder verificar as variaveis dentro dos blocos.
	 */
	private int contadorChaves;
	
	/**
	 * O verificador que indica que se foi aberto um parenteses ou nao.
	 */
	//private boolean isParenteses;
	
	/**
	 * O verificador que indica se foi incrementado o contador de chaves antes da chave.
	 * Serve especialmente para inicio de metodos, que podem ter declaracao de variaveis antes de chaves.
	 */
	private boolean isDeclaracaoFuncao;
	
	/**
	 * A lista de lexicos para variaveis globais.
	 */
	private ArrayList<ArrayList<Object>> listaVariaveis;
	
	/**
	 * A lista de lexicos para funcões.
	 */
	private ArrayList<String> listaFuncoes;
	
	/**
	 * A lista de tokens que estao sem um tipo.
	 */
	private ArrayList<Token> listaTokensSemTipo;
	
	/**
	 * O metodo construtor.
	 */
	public AnalisadorLexico() 
	{
		this.contadorChaves = 0;
		this.listaVariaveis = new ArrayList<ArrayList<Object>>();
		this.listaVariaveis.add(new ArrayList<Object>());
		this.listaVariaveis.add(new ArrayList<Object>());
		this.listaFuncoes = new ArrayList<String>();
		this.listaTokensSemTipo = new ArrayList<Token>();
	}
	
	/**
	 * Verifica os lexicos atraves dos tokens gerados do analisador de tokens.
	 * @param objAnalisadorToken O objeto que representa o analisador de tokens.
	 */
	public void VerificaLexicos(AnalisadorToken objAnalisadorToken) throws Exception
	{
		// anda em todos os tokens para gerar o lexico.
		ArrayList<Token> listaTokens = objAnalisadorToken.getListaTokens();
		for(int i = 0; i < listaTokens.size(); i++)
		{
			if (listaTokens.get(i).getTipoToken() == null)
			{
				// Verifica se e' um lexico pre-definido da linguagem.
				if(this.isLexicoConhecidoLinguagem(listaTokens.get(i).getValorToken()))
				{
					listaTokens.get(i).setTipoToken(this.getTipoLexicoConhecidoLinguagem(listaTokens.get(i).getValorToken()));
				}
				// Verifica se e' uma constante de caracter.
				else if(this.isCadeiaConstanteCaracter(listaTokens.get(i).getValorToken()))
				{
					listaTokens.get(i).setTipoToken(this.getTipoCadeiaConstanteCaracter(listaTokens.get(i).getValorToken()));
				}
				// Verifica se e' um numero.
				else if(this.isNumeroConstante(listaTokens.get(i).getValorToken()))
				{
					listaTokens.get(i).setTipoToken(this.getTipoNumeroConstante(listaTokens.get(i).getValorToken()));
				}
				// Verifica se e' uma declaracao de funcao.
				else if(this.isDeclaraFuncao(listaTokens, i))
				{
					listaTokens.get(i).setTipoToken(this.getTipoDeclaraFuncao(listaTokens, i));
				}
				// Verifica se esta chamando uma funcao.
				else if(this.isChamaFuncao(listaTokens.get(i).getValorToken()))
				{
					listaTokens.get(i).setTipoToken(this.getTipoChamaFuncao());
				}
				// Nao tem o tipo isDeclaraVariavel porque ele  e' reenchido no metodo PreencheLexicosAuxiliares.
				// Verifica se esta chamando uma variavel.
				else if(this.isChamaVarivel(listaTokens.get(i).getValorToken()))
				{
					listaTokens.get(i).setTipoToken(this.getTipoChamaVariavel(listaTokens.get(i)));
				}
				
				// Verificacao para nao gerar erro de null pointer.
				if (listaTokens.get(i).getTipoToken() != null)
				{
					this.PreencheLexicosAuxiliares(listaTokens, listaTokens.get(i).getTipoToken(), i);
					this.ControlaNumeroChaves(listaTokens, i);
				}
				else
				{
					/* Preenche a lista de tokens sem tipo para posterior verificacao, por exemplo, uma funcao pode ser chamada antes de ser
					 * declarada, por isso, guardamos a lista de tokens sem tipo e ao fim tentantos encontrar um tipo para o mesmo.
					 * Caso o lexico nao seja definido, ai sim emitiremos uma mensagem de erro ao usuario.
					 */
					this.listaTokensSemTipo.add(listaTokens.get(i));
				}
			}
		}
		
		// Deve-se procurar todos os tokens que ainda estejam vazios.
		// Primeiro passo e' verificar se eles nao estao na listaFuncoes (para o caso de funcões que sao chamadas antes de serem declaradas).
		// Caso ainda assim hajam tokens sem tipo, entao gera um erro ao usuario.
		this.UltimaVerificacaoTokensSemTipo();
	}

	/**
	 * Faz a ultima verificacao dos tokens que ainda nao tem um tipo.
	 * @throws Exception Gera excecao caso o token nao seja enquadrado em nenhuma categoria.
	 */
	private void UltimaVerificacaoTokensSemTipo() throws Exception
	{
		for (Token token : listaTokensSemTipo) 
		{
			if (this.isChamaFuncao(token.getValorToken()))
			{
				// Nao ha nenhum problema em setar o tipo do token aqui, ja que a referência dele esta na listaTokens.
				token.setTipoToken(this.getTipoChamaFuncao());
			}
			else
			{
				ExcecoesUtil.GeraExcecao("\'" + token.getValorToken() + "\'", "TokenDesconhecido", token.getnLinha());
			}
		}
	}

	/**
	 * Retorna o tipo TT_CHAMA_VARIAVEL.
	 * @return O tipo do lexico.
	 */
	private TipoToken getTipoChamaVariavel(Token token)
	{
		if (token.getTipoToken() == null)
		{
			return TipoToken.TT_CHAMA_VARIAVEL;
		}
		else
		{
			return token.getTipoToken();
		}
	}

	/**
	 * Retorna o tipo TT_CHAMA_FUNCAO.
	 * @return O tipo do lexico.
	 */
	private TipoToken getTipoChamaFuncao() 
	{
		return TipoToken.TT_CHAMA_FUNCAO;
	}
	
	/**
	 * Verifica e retorna o tipo do lexico passado por parâmetro.
	 * @param listaTokens A lista de token.
	 * @param i O indice do token.
	 * @throws Exception Gera erro caso ja tenha sido declarado a funcao ou procedimento.
	 * @return O tipo do lexico.
	 */
	private TipoToken getTipoDeclaraFuncao(ArrayList<Token> listaTokens, int i) throws Exception 
	{
		// Verifica se e' possivel conferir o tipo da declaracao
		if((listaTokens.size() - 2 >= 0) && (i + 2 < listaTokens.size()))
		{
			boolean nomeExiste = false;
			// Verifica se e' um procedimento.
			if (listaTokens.get(i - 1).getTipoToken() == TipoToken.TT_PROCEDIMENTO)
			{
				nomeExiste = listaFuncoes.contains(listaTokens.get(i).getValorToken());
				if (!nomeExiste)
				{
					listaFuncoes.add(listaTokens.get(i).getValorToken());
					return TipoToken.TT_DECLARACAO_FUNCAO;
				}
			}
			// Verifica se e' uma funcao.
			else if (listaTokens.get(i - 1).getTipoToken() == TipoToken.TT_TIPO_DE_DADOS && listaTokens.get(i - 2).getTipoToken() == TipoToken.TT_FUNCAO)
			{
				nomeExiste = listaFuncoes.contains(listaTokens.get(i).getValorToken());
				if (!nomeExiste)
				{
					listaFuncoes.add(listaTokens.get(i).getValorToken());
					return TipoToken.TT_DECLARACAO_FUNCAO;
				}
			}
			
			// Verificador que informa se o nome ja existe na lista de funcões.
			if (nomeExiste)
			{
				ExcecoesUtil.GeraExcecao("\'" + listaTokens.get(i).getValorToken() + "\'", "FuncaoJaDeclarada", listaTokens.get(i).getnLinha());
			}
			// Verifica se o nome ja existe na lista de variaveis.
			else if(this.listaVariaveis.contains(listaTokens.get(i).getValorToken()))
			{
				ExcecoesUtil.GeraExcecao("\'" + listaTokens.get(i).getValorToken() + "\'", "FuncaoJaDeclaradaVariavel", listaTokens.get(i).getnLinha());
			}
		}
		
		return null;
	}
	
	/**
	 * Recebe o tipo do lexico passado por parâmetro.
	 * @param valorToken O valor do token (que a partir de agora e' um lexico conhecido da linguagem). 
	 * @return O tipo do token do lexico conhecido.
	 */
	private TipoToken getTipoLexicoConhecidoLinguagem(String valorToken) 
	{
		return (TipoToken) PalavrasConhecidasUtil.getTipoToken(valorToken); 
	}
	
	/**
	 * Recebe o tipo do lexico de constante de caracter passado.
	 * @param valorToken O valor do token.
	 * @return O tipo do token de constante de caracter.
	 */
	private TipoToken getTipoCadeiaConstanteCaracter(String valorToken) 
	{
		// Verifica se e' char constante.
		if(valorToken.startsWith(String.valueOf(PalavraReservada.InicioCadeiaConstanteCaracter.charAt(0))))
		{
			return TipoToken.TT_CARACTER_CONSTANTE;
		}
		else
		{
			return TipoToken.TT_PALAVRA_CONSTANTE;
		}
	}
	
	/**
	 * Recebe o tipo do lexico de constante numerica.
	 * @param valorToken O valor do token.
	 * @return O tipo do token de constante numerica.
	 */
	private TipoToken getTipoNumeroConstante(String valorToken) 
	{
		return TipoToken.TT_NUMERO_CONSTANTE;
	}

	/**
	 * Verifica se e' um lexico conhecido da linguagem.
	 * @param valorToken O valor do token.
	 * @return Verdadeiro se for um lexico conhecido.
	 */
	private boolean isLexicoConhecidoLinguagem(String valorToken) 
	{
		boolean lexicoConhecidoLinguagem = false;
		try
		{
			lexicoConhecidoLinguagem = PalavrasConhecidasUtil.getTipoToken(valorToken) != null;
		}
		catch (Exception e) 
		{
			/*
			 *  Na excecao ele captura o erro mas emite na tela porque quando o lexico nao for da linguagem, ele sera recuperado
			 *  posteriormente, caso o lexico nao seja valido, o metodo VerificaLexicos sera quem ira emitir o erro ao usuario.
			 */
		}
		
		return lexicoConhecidoLinguagem;
	}
	
	/**
	 * Verifica se e' uma cadeia constante de caracter. Formato: 'a' ou "algo".
	 * @param valorToken O valor do token.
	 * @return Verdadeiro se for uma cadeia constante de caracter.
	 */
	private boolean isCadeiaConstanteCaracter(String valorToken) 
	{
		Character charConst = PalavraReservada.InicioCadeiaConstanteCaracter.charAt(0);
		Character cadeiaCharConst = PalavraReservada.InicioCadeiaConstanteCaracter.charAt(1);
		boolean isCharConst = valorToken.startsWith(charConst.toString()) && valorToken.endsWith(charConst.toString());
		boolean isCadeiaCharConst = valorToken.startsWith(cadeiaCharConst.toString()) && valorToken.endsWith(cadeiaCharConst.toString());
		return (valorToken.length() > 1 && (isCharConst || isCadeiaCharConst));
	}
	
	/**
	 * Verifica se e' numero constante.
	 * @param valorToken O valor do token.
	 * @return Verdadeiro se for numero.
	 */
	private boolean isNumeroConstante(String valorToken) 
	{
		boolean numero = false;
		try
		{
			Double.parseDouble(valorToken);
			numero = true;
		}
		catch (Exception e) 
		{
			// nao faz nada, nem gera erro porque esse metodo e' so verificacao se o token e' ou nao um numero constante.
		}
		
		return numero;
	}
	
	/**
	 * Verifica se e' uma declaracao de funcao.
	 * @param listaTokens A lista de tokens.
	 * @param i O indice do token atual.
	 * @return Verdadeiro se for declaracao de funcao.
	 */
	private boolean isDeclaraFuncao(ArrayList<Token> listaTokens, int i) 
	{
		// Verificando se pode olhar os tokens 2 posicões anteriores.
		if(listaTokens.size() - 2 >= 0 && i >= 2)
		{
			if((listaTokens.get(i - 1).getTipoToken() == TipoToken.TT_TIPO_DE_DADOS 
					&& listaTokens.get(i - 2).getTipoToken() == TipoToken.TT_FUNCAO) 
					|| listaTokens.get(i - 1).getTipoToken() == TipoToken.TT_PROCEDIMENTO)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Verifica se o usuario esta chamando uma funcao.
	 * @param valorToken O valor do token.
	 * @return Verdadeiro se o usario estiver chamando uma funcao.
	 */
	private boolean isChamaFuncao(String valorToken) 
	{
		return this.listaFuncoes.contains(valorToken);
	}
	
	/**
	 * Verifica se o usuario esta chamando uma variavel.
	 * @param valorToken O valor do token.
	 * @return Verdadeiro se o usuario estiver chamando uma variavel.
	 */
	private boolean isChamaVarivel(String valorToken) 
	{
		valorToken = valorToken.charAt(0) == PalavraReservada.CaracterEnderecoMemoria ? valorToken.substring(1) : valorToken;
		return this.listaVariaveis.get(0).contains(valorToken);
	}

	/**
	 * Controla o numero de chaves abertas. Ao fim do progama, a variavel contadorChaves deve ser zero.
	 * @param listaTokens A lista de token.
	 * @param i O indice do token atual.
	 */
	private void ControlaNumeroChaves(ArrayList<Token> listaTokens, int i) 
	{
		/* Por exemplo, toda vez que for uma funcao, iremos incrementar o contador de chaves e depois iremos pular a primeira chave,
		 * porque pode ser que exista declaracao de variaveis dentro do escopo do metodo.
		 * */
		if (listaTokens.get(i).getTipoToken() == TipoToken.TT_DECLARACAO_FUNCAO)
		{
			this.contadorChaves++;
			this.isDeclaracaoFuncao = true;
		}
		else if (listaTokens.get(i).getTipoToken() == TipoToken.TT_ABRE_CHAVE)
		{
			// so pode incrementar o contador de chaves se nao tiver que pular a chave.
			if (!this.isDeclaracaoFuncao)
			{
				this.contadorChaves++;
			}
			else
			{
				this.isDeclaracaoFuncao = false;
			}
		}
		else if (listaTokens.get(i).getTipoToken() == TipoToken.TT_FECHA_CHAVE)
		{
			this.contadorChaves--;
			
			// aqui tem que excluir da lista todos os maiores que o contador.
			while (this.listaVariaveis.get(0).size() - 1 > 0 && 
					Integer.parseInt(this.listaVariaveis.get(1).get(this.listaVariaveis.get(1).size() - 1).toString()) > this.contadorChaves)
			{
				this.listaVariaveis.get(0).remove(this.listaVariaveis.get(0).size() - 1);
				this.listaVariaveis.get(1).remove(this.listaVariaveis.get(1).size() - 1);
			}
		}
		/*
		// Verifica se o token e' uma abertura de chave ou uma abertura de parêntese(somente se ele vier depois de uma funcao ou do para).
		if(listaTokens.get(i).getTipoToken() == TipoToken.TT_ABRE_CHAVE || 
				(listaTokens.get(i).getTipoToken() == TipoToken.TT_ABRE_PARENTESE &&
				(listaTokens.get(i - 1).getTipoToken() == TipoToken.TT_DECLARACAO_FUNCAO ||
				 listaTokens.get(i - 1).getTipoToken() == TipoToken.TT_PARA
				)))
		{
			this.contadorChaves++;
			if (listaTokens.get(i).getTipoToken() == TipoToken.TT_ABRE_PARENTESE)
			{
				this.isParenteses = true;
			}
		}
		else if(listaTokens.get(i).getTipoToken() == TipoToken.TT_FECHA_CHAVE)
		{
			this.contadorChaves--;
			
			// Se abriu um parênteses, entao temos que decrementar mais um quando estivermos saindo da funcao.
			if (this.isParenteses)
			{
				this.contadorChaves--;
				this.isParenteses = false;
			}
			
			// aqui tem que excluir da lista todos os maiores que o contador.
			while (this.listaVariaveis.get(0).size() - 1 > 0 && 
					Integer.parseInt(this.listaVariaveis.get(1).get(this.listaVariaveis.get(1).size() - 1).toString()) > this.contadorChaves)
			{
				this.listaVariaveis.get(0).remove(this.listaVariaveis.get(0).size() - 1);
				this.listaVariaveis.get(1).remove(this.listaVariaveis.get(1).size() - 1);
			}
		}*/
	}
	
	/**
	 * Preenche os lexicos auxiliares.
	 * Lexicos auxiliares sao os lexicos necessarios para determinada acao, por exemplo, os lexicos "stdio" e "h"
	 * sao auxiliares da expressao "#incluir <stdio.h>"
	 * @param listaTokens A lista de tokens.
	 * @param tipoToken O tipo do token atual.
	 * @param i O indice do token atual.
	 * @throws Exception Gera excecao se existir variavel com o mesmo nome.
	 */
	private void PreencheLexicosAuxiliares(ArrayList<Token> listaTokens, TipoToken tipoToken, int i) throws Exception
	{
		switch (tipoToken) 
		{
			case TT_INCLUSAO:
			{
				if (listaTokens.size() >= i + 4)
				{
					this.setLexicosAuxiliaresBib(listaTokens, i);
				}
			}
			
			break;
			case TT_CONSTANTE:
			{
				if (listaTokens.size() >= i + 1)
				{
					this.setLexicosAuxiliaresDeclaracaoConst(listaTokens, i);
					this.SetVariaveisGeral(listaTokens, i + 1);
				}
			}
			
			break;
			case TT_TIPO_DE_DADOS:
			{
				// Verifica se esta dentro da declaracao de uma funcao.
				if (this.isDeclaracaoFuncao && listaTokens.size() >= i + 1)
				{
					this.SetVariaveisGeral(listaTokens, i + 1);
				}
				// Representam os lexicos de declaracao de variavel.
				else if(   listaTokens.size() >= 3 && 
						   ( 
						     listaTokens.get(i - 1).getTipoToken() != TipoToken.TT_FUNCAO &&
						     listaTokens.get(i - 1).getTipoToken() != TipoToken.TT_PROCEDIMENTO &&
						     listaTokens.get(i - 1).getTipoToken() != TipoToken.TT_FUNCAO_PRINCIPAL
						   )
				  )
				{
					this.setLexicosAuxiliaresDeclaracaoVar(listaTokens, i);
				}
			}
			
			break;
		}
	}

	/**
	 * Seta os lexicos auxiliares de declaracao de variaveis.
	 * @param listaTokens A lista de tokens.
	 * @param i O indice atual do token.
	 * @throws Exception Gera excecao se existir variavel ou funcao com o mesmo nome.
	 */
	private void setLexicosAuxiliaresDeclaracaoVar(ArrayList<Token> listaTokens, int i) throws Exception
	{
		// se o contador de parenteses for maior que zero, significa que ele esta dentro de uma expressao.
		int contParenteses = 0;
		for(int j = i + 1; j < listaTokens.size() && (!PalavraReservada.CaracterFimDeComando.toString().equals((listaTokens.get(j).getValorToken()))); j++)
		{
			if (PalavraReservada.CaracterInicioDefinicaoExpressao.toString().equals(listaTokens.get(j).getValorToken()))
			{
				contParenteses++;
			}
			else if (PalavraReservada.CaracterFimDefinicaoExpressao.toString().equals(listaTokens.get(j).getValorToken()))
			{
				contParenteses--;
			}
			
			// Verifica se e' uma declaracao de variavel valida.
			boolean isSeparador = PalavraReservada.CaracterSeparadorVariavel.toString().equals(listaTokens.get(j - 1).getValorToken());
			if(((listaTokens.get(j - 1).getTipoToken() == TipoToken.TT_TIPO_DE_DADOS && listaTokens.get(j - 2).getTipoToken() != TipoToken.TT_FUNCAO) 
					|| isSeparador) && contParenteses == 0)
			{
				this.SetVariaveisGeral(listaTokens, j);
			}
		}
	}

	/**
	 * Seta os lexicos auxiliares de declaracao de variaveis, verificando se eles ja estao sendo utilizados e gerando erro, caso estejam.
	 * @param listaTokens A lista de tokens.
	 * @param indice O indice atual do token.
	 * @throws Exception Gera excecao se existir variavel ou funcao com o mesmo nome.
	 */
	private void SetVariaveisGeral(ArrayList<Token> listaTokens, int indice) throws Exception 
	{
		// Verifica se a variavel ja foi declarada.
		if(this.listaVariaveis.get(0).contains(listaTokens.get(indice).getValorToken()))
		{
			ExcecoesUtil.GeraExcecao("\'" + listaTokens.get(indice).getValorToken() + "\'", "VariavelJaDeclarada", listaTokens.get(indice).getnLinha());
		}
		// Verifica se a variavel foi declarada como funcao.
		else if (this.listaFuncoes.contains(listaTokens.get(indice).getValorToken()))
		{
			ExcecoesUtil.GeraExcecao("\'" + listaTokens.get(indice).getValorToken() + "\'", "VariavelJaDeclaradaFuncao", listaTokens.get(indice).getnLinha());
		}
		else
		{
			if (listaTokens.get(indice).getTipoToken() == null)
			{
				listaTokens.get(indice).setTipoToken(TipoToken.TT_DECLARACAO_VARIAVEL);
			}
			
			this.listaVariaveis.get(0).add(listaTokens.get(indice).getValorToken());
			this.listaVariaveis.get(1).add(this.contadorChaves);
		}
	}
	
	/**
	 * Seta os lexicos auxiliares de declaracao de constante.
	 * @param listaTokens A lista de tokens.
	 * @param i O indice atual do token.
	 */
	private void setLexicosAuxiliaresDeclaracaoConst(ArrayList<Token> listaTokens, int i) 
	{
		// representando o lexico de nome da constante.
		listaTokens.get(i + 1).setTipoToken(TipoToken.TT_DECLARACAO_CONSTANTE);
	}

	/**
	 * Seta os lexicos auxiliares de biblioteca.
	 * @param listaTokens A lista de tokens.
	 * @param i O indice atual do token.
	 */
	private void setLexicosAuxiliaresBib(ArrayList<Token> listaTokens, int i) 
	{
		// representando o lexico auxiliar "stdio"					
		listaTokens.get(i + 2).setTipoToken(TipoToken.TT_NOME_BIB_INCLUIR);
		// representando o lexico auxiliar "h"
		listaTokens.get(i + 4).setTipoToken(TipoToken.TT_EXTENSAO_BIBLIOTECA);
	}
}
