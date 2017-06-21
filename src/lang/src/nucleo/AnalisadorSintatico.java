package nucleo;

import java.util.ArrayList;

import util.ExcecoesUtil;

import base.PalavraReservada.TipoToken;
import base.Token;

/**
 * Gerencia e analisa sintaticamente os tokens gerados pelo analisador lexico. 
 * @author Luiz Peres.
 */
public class AnalisadorSintatico 
{
	/**
	 * O contador de chaves para poder verificar as ocorrencias de repeticoes e condicoes.
	 */
	private int contadorChaves = 0;
	
	/**
	 * Contador referente aos parenteses abertos.
	 */
	int contParenteseAberto = 0; 
	
	/**
	 * Contador referente aos parenteses fechados.
	 */
	int contParenteseFechado = 0;
	
	/**
	 * Contador referente aos colchetes abertos.
	 */
	int contColcheteAberto = 0; 
	
	/**
	 * Contador referente aos colchetes fechados.
	 */
	int contColcheteFechado = 0;
	
	/**
	 * A flag para saber se pode haver um incremento posterior. Ex: var++
	 */
	boolean flagIncremento = false;
	
	/**
	 * A lista do numero do bloco em que esta a chave do se.
	 */
	ArrayList<Integer> listaBlocoSe;
	
	/**
	 * A lista do numero do bloco em que esta a chave do enquanto.
	 */
	ArrayList<Integer> listaBlocoEnquanto;
	
	/**
	 * A lista do numero do bloco em que esta a chave do faca enquanto.
	 */
	ArrayList<Integer> listaBlocoFacaEnquanto;
	
	/**
	 * A lista do numero do bloco em que esta a chave do para.
	 */
	ArrayList<Integer> listaBlocoPara;
	
	/**
	 * A lista do numero do bloco em que esta a chave do escolha.
	 */
	ArrayList<Integer> listaBlocoEscolha;
	
	/**
	 * A flag para saber se existe uma condicao anterior e se pode utilizar o senao.
	 */
	boolean flagSenao = false;
	
	/**
	 * A flag para saber em qual posicao do para estamos.
	 */
	byte posDentroPara = 0;
	
	/**
	 * A flag para saber se podemos atribuir ou nao.
	 */
	boolean flagAtribuicao = false;
	
	/**
	 * A flag para saber se podemos usar o retorna ou nao.
	 * A primeira posicao e' para sabermos se podemos utilizar o retorna e a segunda posicao
	 * e' para saber qual o proxEstado da condicao.
	 */
	boolean flagRetorna[] = new boolean[2];
	
	/** 
	 * O metodo construtor. 
	 * */
	public AnalisadorSintatico()
	{
		this.listaBlocoSe = new ArrayList<Integer>();
		this.listaBlocoEnquanto = new ArrayList<Integer>();
		this.listaBlocoFacaEnquanto = new ArrayList<Integer>();
		this.listaBlocoPara = new ArrayList<Integer>();
		this.listaBlocoEscolha = new ArrayList<Integer>();
		this.flagRetorna[0] = false;
		this.flagRetorna[1] = false;
	}
	
	/**
	 * Verifica a sintaxe atraves dos tokens gerados do analisador de tokens e lexico.
	 * @param objAnalisadorToken O objeto que representa o analisador de tokens.
	 */
	public void VerificaSintaxe(AnalisadorToken objAnalisadorToken) throws Exception
	{
		// anda em todos os tokens para gerar o lexico.
		ArrayList<Token> listaTokens = objAnalisadorToken.getListaTokens();
		// validador sintatico da maquina de estados.
		boolean resultado = true;
		// os estados possiveis.
		byte estado = 0;
		// o token atual.
		Token token = null;
		// o valor do token com erro.
		String tokenErro = "";
		// O estado anterior da maquina de estados.
		byte estadoAnterior = 0;
		
		for(int i = 0; resultado && i < listaTokens.size(); i++)
		{
			token = listaTokens.get(i);
			if (this.IsChaves(token) && estado != 11)
			{
				this.SetContadorChaves(token.getTipoToken());
			}
			
			switch (estado) 
			{
				// ============== Estado Inicial
				case 0:
				{
					this.ValidaParenteses(token.getnLinha());
					this.ValidaColchetes(token.getnLinha());
					estado = this.isTokenPrimeiroEstado(token);
					break;
				}
				// ============== Inclusai
				case 1:
				{
					// i ao fim do metodo tera sido incrementado.
					i = this.isInclusao(listaTokens, i);
					estado = 0;
					break;
				}
				// ============== Constante
				case 2:
				{
					i = this.isConstante(listaTokens, i);
					estado = 0;
					break;
				}
				// ============== Declaracao de variavel
				case 3:
				{
					estado = this.isDeclaracaoVariavel(token);
					// se não for declaracao de variavel, faz a ultima vericacao, para ver se a virgula era da expressao..
					if (estado == -1)
					{
						estado = this.getEstadoExpressao(token);
					}
					
					break;
				}
				// ============== Declaracao de Funcao
				case 4:
				{
					estadoAnterior = estado;
					estado = this.getDeclaracaoFuncao(token);
					break;
				}
				// ============== Retorna
				case 5:
				{
					estado = this.getProxEstadoRetorna(token);
					if (estado == 17)
					{
						// Voltamos "i" para ele poder entrar na proxima maquina de estados.
						i--;
					}
					break;
				}
				// ============== Atribuicao
				case 6:
				{
					estado = this.getEstadoAtribuicao(token);
					break;
				}
				// ============== Chama Funcao
				case 7:
				{
					estadoAnterior = estado;
					estado = 22;
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Se
				case 8:
				{
					estadoAnterior = estado;
					estado = 22;
					listaBlocoSe.add(this.contadorChaves);
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Enquanto
				case 9:
				{
					estadoAnterior = estado;
					estado = 22;
					this.listaBlocoEnquanto.add(this.contadorChaves);
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Para
				case 10:
				{
					estadoAnterior = estado;
					estado = 22;
					this.posDentroPara = 1;
					this.listaBlocoPara.add(this.contadorChaves);
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Faca...Enquanto
				case 11:
				{
					estadoAnterior = estado;
					estado = 26;
					this.listaBlocoFacaEnquanto.add(this.contadorChaves);
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;	
				}
				// ============== Escolha
				case 12:
				{
					estadoAnterior = estado;
					estado = 22;
					this.listaBlocoEscolha.add(this.contadorChaves);
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Leia
				case 13:
				{
					estadoAnterior = estado;
					estado = 22;
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Aleatorio
				case 14:
				{
					estadoAnterior = estado;
					estado = 22;
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Limpa tela
				case 15:
				{
					estadoAnterior = estado;
					estado = 22;
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				case 16:
				{
					estado = this.getEstadoDeclaracaoVar(token);
					break;
				}
				case 17:
				{
					estado = this.getEstadoExpressao(token);
					break;
				}
				case 18:
				{
					estado = this.getEstadoExpressaoNumerica(token);
					break;
				}
				case 19:
				{
					estado = this.isChamaVariavel(token.getTipoToken()) ? (byte)18 : -1;
					break;
				}
				
				// ============== Inicio de Estado de declaracao de Funcao
				
				case 20:
				{
					// procedimento.
					estadoAnterior = estado;
					estado = (this.isDeclaracaoFuncao(token));
					break;
				}
				
				case 21:
				{
					estadoAnterior = estado;
					estado = (this.isDeclaracaoFuncao(token));
					break;
				}
				case 22:
				{
					estado = this.getAbreParentese(token, estadoAnterior);
					break;
				}
				case 23:
				{
					estado = this.getTipoParametro(token);
					break;
				}
				case 24:
				{
					estadoAnterior = estado;
					estado = this.getNomeParametro(token);
					break;
				}
				case 25:
				{
					estado = this.getProxEstadoFuncao(token, estadoAnterior);
					break;
				}
				case 26:
				{
					estado = this.getAbreChave(token); 
					break;
				}
				
				// =============== Fim de estado de declaracao de funcao
				
				// =============== Fim de Comando.
				case 27:
				{
					estado = this.getFimComando(token);
					break;
				}
				
				// =============== Incremento
				case 28:
				{
					estado = this.isChamaVariavel(token.getTipoToken()) ? (byte)27 : -1;
					break;
				}
				
				// ============== Constante de texto
				
				case 29:
				{
					// Verificador de estado. 
					// Descobre se ele esta vindo do case 13 (leia) ou case 31 (escreva) e redireciona a maquina de estados.
					byte proxEstado29 = -1;
					proxEstado29 = estadoAnterior == 13 ? 25 : proxEstado29;
					proxEstado29 = estadoAnterior == 31 ? 18 : proxEstado29;
					
					estadoAnterior = estado;
					estado = this.isTipoConstanteTexto(token.getTipoToken()) ? proxEstado29 : -1;
					break;
				}

				// ============== Verificacao de chamada de variavel dentro de funcao				
				case 30:
				{
					estado = this.isChamaVariavel(token.getTipoToken()) ? (byte) 25 : -1;
					break;
				}
				
				// ============== Escreva				
				case 31:
				{
					estadoAnterior = estado;
					estado = 22;
					// Voltamos "i" para ele poder entrar na proxima maquina de estados.
					i--;
					break;
				}
				// ============== Senao
				case 32:
				{
					// Modifica o estadoAnterior por referencia.
					estado = this.getEstadoSenao(token);
					// Nessa linha abaixo, estamos simulando o case 8 (case do "SE").
					if (estado == 22)
					{
						estadoAnterior = 8;
					}
					
					break;
				}
				// ============== PULA, CONTINUA, RETORNA
				case 33:
				{
					
					estado = this.getProxEstadoBreak(token);
					break;
				}
				// ============== FECHA CHAVE DOS COMANDOS PULA, CONTINUA, RETORNA
				case 34:
				{
					byte valorEstadoEscolha = this.getEstadoEscolha(token); 
					if (this.IsDentroBlocoPrincipalEscolha() && (valorEstadoEscolha == 37 || valorEstadoEscolha == 38 || valorEstadoEscolha == 0))
					{
						// se for -1 entao e' referente a chave fechada.
						estado = valorEstadoEscolha != -1 ? valorEstadoEscolha : 36;
					}
					else
					{
						byte fechaChave = this.getFechaChave(token);
						estado = fechaChave == 0 ? this.ValidaCondicoesELoopings() : -1;
					}
					
					break;
				}
				// ============== COMPLEMENTO DO FACA...ENQUANTO
				case 35:
				{
					estadoAnterior = estado;
					estado = this.getComplementoFacaEnquanto(token);
					break;
				}
				// ============== COMPLEMENTO ESCOLHA
				case 36:
				{
					estado = this.getEstadoEscolha(token);
					break;
				}
				case 37:
				{
					estado = this.getConstanteEscolha(token);
					break;
				}
				case 38:
				{
					estado = this.getDoisPontos(token);
					break;
				}
				// ============== COMPLEMENTO PARA
				case 39:
				{
					estado = this.getComplementoPara(token);
					break;
				}
				case 40:
				{
					estado = this.getFechaParentese(token, estadoAnterior);
				}
			}
			
			resultado = (estado == -1 || i == -1) ? false : true; 
			tokenErro = token.getValorToken();
		}
		
		if (!resultado)
		{
			ExcecoesUtil.GeraExcecao("Proximo ao identificador \'" + tokenErro + "\'", "ErroSintaxe", token.getnLinha());
		}
		else if (this.contadorChaves != 0)
		{
			ExcecoesUtil.GeraExcecao("FaltaChaves");
		}
	}

	/**
	 * Validador se os parenteses estao corretos.
	 * @param nLinha O numero da linha posterior ao erro.
	 * @throws Exception Gera excecao caso o numero de parenteses esteja diferente.
	 */
	private void ValidaParenteses(int nLinha) throws Exception
	{
		if (this.contParenteseAberto != this.contParenteseFechado)
		{
			nLinha = nLinha != 0 ? nLinha - 1 : nLinha;
			ExcecoesUtil.GeraExcecao("", "FaltaParenteses", nLinha);
		}
		else
		{
			this.contParenteseAberto = 0;
			this.contParenteseFechado = 0;
		}
	}
	
	/**
	 * Validador se os colchetes estao corretos.
	 * @param nLinha O numero da linha posterior ao erro.
	 * @throws Exception Gera excecao caso o numero de colchetes esteja diferente.
	 */
	private void ValidaColchetes(int nLinha) throws Exception
	{
		if (this.contColcheteAberto != this.contColcheteFechado)
		{
			nLinha = nLinha != 0 ? nLinha - 1 : nLinha;
			ExcecoesUtil.GeraExcecao("", "FaltaColchetes", nLinha);
		}
		else
		{
			this.contColcheteAberto = 0;
			this.contColcheteFechado = 0;
		}
	}

	/**
	 * Case 0.
	 * Verifica se o token e' um token de estado inicial.
	 * @param token O token.
	 * @return Retorna o proximo estado do token e -1 se o token não for identicado.
	 */
	private byte isTokenPrimeiroEstado(Token token) 
	{
		boolean senaoValido = this.flagSenao;
		// Toda vez que entrar na expressao, a flag tem que receber falso.
		this.flagSenao = false;
		
		if (token.getTipoToken() == TipoToken.TT_INCLUSAO && this.contadorChaves == 0)
		{
			return 1;
		}
		else if (token.getTipoToken() == TipoToken.TT_CONSTANTE && this.contadorChaves == 0)
		{
			return 2;
		}
		else if (token.getTipoToken() == TipoToken.TT_TIPO_DE_DADOS)
		{
			return 3;
		}
		else if (token.getTipoToken() == TipoToken.TT_FUNCAO && this.contadorChaves == 0)
		{
			return 4;
		}
		else if (token.getTipoToken() == TipoToken.TT_PROCEDIMENTO && this.contadorChaves == 0)
		{
			return 20;
		}
		else if (token.getTipoToken() == TipoToken.TT_RETORNO && this.flagRetorna[0] && this.contadorChaves > 0)
		{
			return 5;
		}
		else if (token.getTipoToken() == TipoToken.TT_CHAMA_VARIAVEL && this.contadorChaves > 0)
		{
			return 6;
		}
		else if (token.getTipoToken() == TipoToken.TT_CHAMA_FUNCAO && this.contadorChaves > 0)
		{
			return 7;
		}
		else if (token.getTipoToken() == TipoToken.TT_CONDICAO && this.contadorChaves > 0)
		{
			return 8;
		}
		else if (token.getTipoToken() == TipoToken.TT_ENQUANTO && this.contadorChaves > 0)
		{
			return 9;
		}
		else if (token.getTipoToken() == TipoToken.TT_PARA && this.contadorChaves > 0)
		{
			return 10;
		}
		else if (token.getTipoToken() == TipoToken.TT_FACA_ENQUANTO && this.contadorChaves > 0)
		{
			return 11;
		}
		else if (token.getTipoToken() == TipoToken.TT_ESCOLHA && this.contadorChaves > 0)
		{
			return 12;
		}
		else if (token.getTipoToken() == TipoToken.TT_LEIA && this.contadorChaves > 0)
		{
			return 13;
		}
		else if (token.getTipoToken() == TipoToken.TT_ESCREVA && this.contadorChaves > 0)
		{
			return 31;
		}
		else if (token.getTipoToken() == TipoToken.TT_LIMPA_TELA && this.contadorChaves > 0)
		{
			return 15;
		}
		else if (token.getTipoToken() == TipoToken.TT_INCREMENTO && this.contadorChaves > 0)
		{
			return 28;
		}
		else if (token.getTipoToken() == TipoToken.TT_FECHA_CHAVE)
		{
			return this.ValidaCondicoesELoopings();
		}
		else if (token.getTipoToken() == TipoToken.TT_CONDICAO_SENAO && senaoValido && this.contadorChaves > 0)
		{
			// A flag do senao e' resetada.
			this.flagSenao = false;
			return 32;
		}
		else if (token.getTipoToken() == TipoToken.TT_PULA && this.contadorChaves > 0)
		{
			return 33;
		}
		else if (token.getTipoToken() == TipoToken.TT_CONTINUA && this.contadorChaves > 0 && !this.IsDentroBlocoPrincipalEscolha() 
				&& (this.listaBlocoEnquanto.size() != 0 || this.listaBlocoFacaEnquanto.size() != 0 || this.listaBlocoPara.size() != 0))
		{
			return 33;
		}
		else if (token.getTipoToken() == TipoToken.TT_CASO && this.IsDentroBlocoPrincipalEscolha())
		{
			return 37;
		}
		else if (token.getTipoToken() == TipoToken.TT_PADRAO && this.IsDentroBlocoPrincipalEscolha())
		{
			return 38;
		}
		else if (token.getTipoToken() == TipoToken.TT_ABRE_CHAVE 
				&& this.listaBlocoEscolha.size() > 0 && this.listaBlocoEscolha.get(this.listaBlocoEscolha.size() - 1) + 2 == this.contadorChaves)
		{
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Verifica se esta dentro do bloco principal do comando escolha.
	 * @return Verdadeiro se estiver dentro do bloco principal do comando escolha.
	 */
	private boolean IsDentroBlocoPrincipalEscolha() 
	{
		return this.listaBlocoEscolha.size() > 0 && this.listaBlocoEscolha.get(this.listaBlocoEscolha.size() - 1) + 1 == this.contadorChaves;
	}

	/**
	 * Valida as condicoes e loopings da linguagem.
	 * @return Retorna o proximo estado da maquina.
	 */
	private byte ValidaCondicoesELoopings() 
	{
		// -------------------------------------
		// FAZ VERIFICACAO PRIMEIRO, DEPOIS RETORNA PARA A CONDICAO INICIAL.
		// -------------------------------------	
		if (this.listaBlocoSe.size() > 0 && this.listaBlocoSe.get(this.listaBlocoSe.size() - 1) == this.contadorChaves)
		{
			this.listaBlocoSe.remove(this.listaBlocoSe.size() - 1);
			this.flagSenao = true;
		}
		// O ESCOLHA VEM AQUI!
		else if (this.listaBlocoEnquanto.size() > 0 && this.listaBlocoEnquanto.get(this.listaBlocoEnquanto.size() - 1) == this.contadorChaves)
		{
			this.listaBlocoEnquanto.remove(this.listaBlocoEnquanto.size() - 1);
		}
		else if (this.listaBlocoFacaEnquanto.size() > 0 && this.listaBlocoFacaEnquanto.get(this.listaBlocoFacaEnquanto.size() - 1) == this.contadorChaves)
		{
			this.listaBlocoFacaEnquanto.remove(this.listaBlocoFacaEnquanto.size() - 1);
			return 35;
		}
		else if (this.listaBlocoPara.size() > 0 && this.listaBlocoPara.get(this.listaBlocoPara.size() - 1) == this.contadorChaves)
		{
			this.listaBlocoPara.remove(this.listaBlocoPara.size() - 1);
		}
		else if (this.listaBlocoEscolha.size() > 0 && this.listaBlocoEscolha.get(this.listaBlocoEscolha.size() - 1) == this.contadorChaves)
		{
			this.listaBlocoEscolha.remove(this.listaBlocoEscolha.size() - 1);
		}
		else if(this.contadorChaves == 0)
		{
			this.flagRetorna[0] = false;
			this.flagRetorna[1] = false;
		}
		
		return 0;
	}

	/**
	 * Case 1.
	 * Verifica se a inclusao esta sintaticamente correta. 
	 * @param listaTokens A lista de tokens.
	 * @param i O indice da lista.
	 * @return Retorna a posicao de i ou -1 se nao estiver nos padroes.
	 */
	private int isInclusao(ArrayList<Token> listaTokens, final int i) 
	{
		if (i + 4 <= listaTokens.size())
		{
			if (listaTokens.get(i).getTipoToken() == TipoToken.TT_OPERADOR_RELACIONAL && listaTokens.get(i + 1).getTipoToken() == TipoToken.TT_NOME_BIB_INCLUIR
				&& listaTokens.get(i + 2).getTipoToken() == TipoToken.TT_EXTENSAO_BIBLIOTECA && listaTokens.get(i + 3).getTipoToken() == TipoToken.TT_EXTENSAO_BIBLIOTECA
				&& listaTokens.get(i + 4).getTipoToken() == TipoToken.TT_OPERADOR_RELACIONAL)
			{
				return i + 4;
			}
		}
		
		return -1;
	}
	
	/**
	 * Case 2.
	 * Verifica se a constante esta sintaticamente correta.
	 * @param listaTokens A lista de tokens.
	 * @param i O indice da lista.
	 * @return Retorna a posicao de i ou -1 se nao estiver nos padroes.
	 */
	private int isConstante(ArrayList<Token> listaTokens, final int i) 
	{
		if (i + 1 <= listaTokens.size())
		{
			if (listaTokens.get(i).getTipoToken() == TipoToken.TT_DECLARACAO_CONSTANTE 
				&& this.isTipoConstante(listaTokens.get(i + 1).getTipoToken()))
			{
				return i + 1;
			}
		}
		
		return -1;
	}
	
	/**
	 * Case 3.
	 * Verifica se o token e' uma declaracao de variavel. 
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte isDeclaracaoVariavel(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_DECLARACAO_VARIAVEL)
		{
			return 16;
		}
		
		return -1;
	}
	
	/**
	 * Case 4.
	 * Verifica se o token e' uma declaracao de funcao.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getDeclaracaoFuncao(Token token) 
	{
		if (this.isTipoDeDados(token.getTipoToken()))
		{
			this.flagRetorna[0] = true;
			return 21;
		}
		else if (token.getTipoToken() == TipoToken.TT_FUNCAO_PRINCIPAL)
		{
			return 22;
		}
		
		return -1;
	}

	/**
	 * Case 5.
	 * Verifica o proximo estado do retorna.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token.
	 */
	private byte getProxEstadoRetorna(Token token) 
	{
		if (this.getFimComando(token) == 0)
		{
			return 34;
		}
		else 
		{
			this.flagRetorna[1] = true;
			return 17;
		}
	}
	
	/**
	 * Case 6.
	 * Verifica se o token e' uma atribuicao ou afins.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getEstadoAtribuicao(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_ATRIBUICAO)
		{
			return 17;
		}
		else if (token.getTipoToken() == TipoToken.TT_CALCULO_E_ATRIBUICAO)
		{
			return 17;
		}
		else if(token.getTipoToken() == TipoToken.TT_INCREMENTO)
		{
			return 27;
		}
		else if (token.getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL && this.posDentroPara > 0 && this.posDentroPara <= 3)
		{
			//return 17;
		}
		else if (this.isAbreColchete(token))
		{
			this.flagAtribuicao = true;
			return 17;
		}
		
		return -1;
	}
	
	/**
	 * Case 16.
	 * Verifica o proximo estado de uma declaracao de variavel. 
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getEstadoDeclaracaoVar(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL)
		{
			return 3;
		}
		else if (token.getTipoToken() == TipoToken.TT_ATRIBUICAO)
		{
			return 17;
		}
		else if (token.getTipoToken() == TipoToken.TT_FIM_COMANDO)
		{
			return 0;
		}
		else if (this.isAbreColchete(token))
		{
			this.flagAtribuicao = true;
			return 17;
		}
		
		return -1;
	}
	
	/**
	 * Case 17.
	 * Verifica o proximo estado de uma expressao.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getEstadoExpressao(Token token) 
	{
		if (this.isAbreParentese(token.getTipoToken()))
		{
			this.contParenteseAberto++;
			return 17;
		}
		else if (this.isFechaParentese(token.getTipoToken()))
		{
			this.contParenteseFechado++;
			return 18;
		}
		else if (this.isTipoConstante(token.getTipoToken()))
		{
			return 18;
		}
		else if (this.isChamaVariavel(token.getTipoToken()))
		{
			this.flagIncremento = true;
			return 18;
		}
		else if (this.isChamaFuncao(token.getTipoToken()))
		{
			return 17;
		}
		// Verifica se e' incremento anterior ++n1;
		else if (this.isIncremento(token.getTipoToken()))
		{
			return 19;
		}
		else if (this.isNegacao(token.getTipoToken()))
		{
			return 17;
		}
		else if (this.isOperadorMatematicoTipo1(token.getTipoToken()))
		{
			return 17;
		}
		else if (this.isAleatorio(token.getTipoToken()))
		{
			return 17;
		}
		else if (this.posDentroPara > 0 && this.posDentroPara < 3 && this.getFimComando(token) == 0)
		{
			this.posDentroPara++;
			return 17;
		}
		else if (this.posDentroPara == 3 && this.isFechaParentese(token.getTipoToken()))
		{
			this.contParenteseFechado++;
			return 18;
		}
		
		return -1;
	}

	/**
	 * Case 18.
	 *Verifica o proximo estado de uma expressao numerica.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getEstadoExpressaoNumerica(Token token) 
	{
		boolean incrementoValido = this.flagIncremento;
		// Toda vez que entrar na expressao, a flag tem que receber falso.
		this.flagIncremento = false;
		
		if(this.isFechaParentese(token.getTipoToken()))
		{
			this.contParenteseFechado++;
			return 18;
		}
		else if (this.isOperadorMatematico(token.getTipoToken()))
		{
			return 17;
		}
		else if (this.isOperadorLogico(token.getTipoToken()))
		{
			return 17;
		}
		else if (this.isOperadorRelacional(token.getTipoToken()))
		{
			return 17;
		}
		else if (token.getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL)
		{
			if (this.posDentroPara == 1 && this.contParenteseAberto == 1)
			{
				return 39;
			}
			else if (this.posDentroPara == 3 && this.contParenteseAberto == 1)
			{
				return 17;
			}
			
			return 3;
		}
		else if (token.getTipoToken() == TipoToken.TT_FIM_COMANDO)
		{
			if(this.flagRetorna[1])
			{
				this.flagRetorna[1] = false;
				return 34;
			}
			else if (this.posDentroPara >= 1 && this.posDentroPara < 3)
			{
				this.posDentroPara++;
				return 17;
			}
			
			return 0;
		}
		else if (incrementoValido && this.isIncremento(token.getTipoToken()))
		{
			return 18;
		}
		// Nao pode ter operador ternario dentro de funcoes.
		else if (this.isOperadorTernario(token.getTipoToken()) && (this.contParenteseAberto - this.contParenteseFechado) == 0)
		{
			return 17;
		}
		else if (this.IsEscolha(token))
		{
			return 36;
		}
		else if (this.IsFacaEnquanto(token))
		{
			return 35;
		}
		else if (this.IsCondicao(token) || this.IsEnquanto(token) || this.IsPara(token))
		{
			return 0;
		}
		else if (this.posDentroPara == 3 && token.getTipoToken() == TipoToken.TT_CALCULO_E_ATRIBUICAO)
		{
			return 17;
		}
		else if (this.isAbreColchete(token))
		{
			return 17;
		}
		else if (this.isFechaColchete(token))
		{
			return 18;
		}
		else if (this.flagAtribuicao && token.getTipoToken() == TipoToken.TT_ATRIBUICAO)
		{
			this.flagAtribuicao = false;
			return 17;
		}
		
		return -1;
	}

	/**
	 * Case 21.
	 * Verifica se o token e' uma declaracao de funcao. 
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte isDeclaracaoFuncao(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_DECLARACAO_FUNCAO)
		{
			return 22;
		}
		
		return -1;
	}
	
	/**
	 * Case 22.
	 * Verifica se o token e' uma abertura de parentese de funcao. 
	 * @param token O token atual.
	 * @param estadoAnterior O estado anterior da maquina.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getAbreParentese(Token token, byte estadoAnterior) 
	{
		if (this.isAbreParentese(token.getTipoToken()))
		{
			if (estadoAnterior == 4)
			{
				this.contParenteseAberto++;
				return 40;
			}
			else if (estadoAnterior == 7)
			{
				this.contParenteseAberto++;
				return 17;
			}
			else if (estadoAnterior == 8)
			{
				this.contParenteseAberto++;
				return 17;
			}
			else if (estadoAnterior == 9)
			{
				this.contParenteseAberto++;
				return 17;
			}
			else if (estadoAnterior == 10)
			{
				this.contParenteseAberto++;
				return 39;
			}
			else if (estadoAnterior == 12)
			{
				this.contParenteseAberto++;
				return 17;
			}
			else if (estadoAnterior == 13)
			{
				return 29;
			}
			else if (estadoAnterior == 14)
			{
				this.contParenteseAberto++;
				return 17;
			}
			else if (estadoAnterior == 15)
			{
				this.contParenteseAberto++;
				return 40;
			}
			else if (estadoAnterior == 20)
			{
				return 23;
			}	
			else if (estadoAnterior == 21)
			{
				return 23;
			}	
			else if (estadoAnterior == 31)
			{
				this.contParenteseAberto++;
				return 29;
			}
			else if (estadoAnterior == 35)
			{
				this.contParenteseAberto++;
				return 17;
			}
		}
		
		return -1;
	}
	
	/**
	 * case 23.
	 * Verifica o tipo do parametro.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getTipoParametro(Token token) 
	{
		if (this.isFechaParentese(token.getTipoToken()))
		{
			return 26;
		}
		else if (this.isTipoDeDados(token.getTipoToken()))
		{
			return 24;
		}
		
		return -1;
	}
	
	/**
	 * Case 24.
	 * Verifica o nome do parametro.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getNomeParametro(Token token) 
	{
		if(this.isDeclaracaoVariavel(token) != -1)
		{
			return 25;
		}
		
		return -1;
	}
	
	/**
	 * Case 25.
	 * Verifica se separa o parametro ou termina.
	 * @param token O token atual.
	 * @param estadoAnterior O estado anterior da maquina.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getProxEstadoFuncao(Token token, byte estadoAnterior) 
	{
		if (token.getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL)
		{
			if (estadoAnterior == 24)
			{
				return 23;
			}
			else if (estadoAnterior == 29)
			{
				return 30; 
			}
		}
		else if (this.isFechaParentese(token.getTipoToken()))
		{
			if (estadoAnterior == 24)
			{
				return 26;
			}
			else if (estadoAnterior == 29)
			{
				return 27;
			}
		}
		
		return -1;
	}
	
	/**
	 * Case 26.
	 * Verifica se e' abertura de chave.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getAbreChave(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_ABRE_CHAVE)
		{
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Case 27. 
	 * Verifica se e' fim de comando
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getFimComando(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_FIM_COMANDO)
		{
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Case 32.
	 * Verifica e retorna o proximo estado da estrutura senao.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getEstadoSenao(Token token) 
	{	
		if (this.getAbreChave(token) == 0)
		{
			return 0;
		}
		else if (token.getTipoToken() == TipoToken.TT_CONDICAO)
		{
			listaBlocoSe.add(this.contadorChaves);
			return 22;
		}
		
		return -1;
	}

	/**
	 * Case 33.
	 * Verifica e retorna o proximo estado da estrutura pula, continua ou retorna.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getProxEstadoBreak(Token token) 
	{
		if (this.getFimComando(token) == 0 && this.listaBlocoEnquanto.size() > 0)
		{
			return 34;
		}
		else if (this.getFimComando(token) == 0 && this.listaBlocoFacaEnquanto.size() > 0)
		{
			return 34;
		}
		else if (this.getFimComando(token) == 0 && this.listaBlocoEscolha.size() > 0)
		{
			return 34;
		}
		else if (this.getFimComando(token) == 0 && this.listaBlocoPara.size() > 0)
		{
			return 34;
		}
		
		return -1;
	}
	
	/** 
	 * Case 34. 
	 * Verifica se e' fechamento de chave.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getFechaChave(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_FECHA_CHAVE)
		{
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Case 35. 
	 * Verifica se e' um complemento do faca..enquanto.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getComplementoFacaEnquanto(Token token) 
	{
		if(token.getTipoToken() == TipoToken.TT_ENQUANTO)
		{
			return 22;
		}
		
		return -1;
	}

	/**
	 * Case 36. 
	 * Verifica o proximo estado do escolha.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getEstadoEscolha(Token token) 
	{
		if(token.getTipoToken() == TipoToken.TT_CASO || token.getTipoToken() == TipoToken.TT_PADRAO)
		{
			return token.getTipoToken() == TipoToken.TT_CASO ? (byte)37 : (byte)38;
		}
		else if (token.getTipoToken() == TipoToken.TT_FECHA_CHAVE 
				&& this.listaBlocoEscolha.size() > 0 && this.listaBlocoEscolha.get(this.listaBlocoEscolha.size() - 1) == this.contadorChaves)
		{
			this.ValidaCondicoesELoopings();
			return 0;
		}
		
		return -1;
	}
	
	/**
	 * Case 37. 
	 * Verifica se e' uma constante do escolha.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getConstanteEscolha(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_NUMERO_CONSTANTE || token.getTipoToken() == TipoToken.TT_CARACTER_CONSTANTE)
		{
			return 38;
		}
		
		return -1;
	}
	
	/**
	 * Case 38. 
	 * Verifica se e' dois pontos.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getDoisPontos(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_DOIS_PONTOS)
		{
			return 0;
		}
		
		return -1;
	}

	/**
	 * Case 39. 
	 * Verifica se e' um complemento do comando para.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getComplementoPara(Token token) 
	{
		if (token.getTipoToken() == TipoToken.TT_TIPO_DE_DADOS)
		{
			return 3;
		}
		else if (token.getTipoToken() == TipoToken.TT_CHAMA_VARIAVEL)
		{
			return 6;
		}
		else if (token.getTipoToken() == TipoToken.TT_CHAMA_FUNCAO)
		{
			return 7;
		}
		else if (this.posDentroPara > 0 && this.posDentroPara < 3 && this.getFimComando(token) == 0)
		{
			this.posDentroPara++;
			return 17;
		}
		
		return -1;
	}
	
	/**
	 * Case 40.
	 * Verifica se e' um fechamento de parentese.
	 * @param token O token atual.
	 * @param estadoAnterior O estado anterior.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private byte getFechaParentese(Token token, byte estadoAnterior) 
	{
		if (this.isFechaParentese(token.getTipoToken()) && estadoAnterior == 4)
		{
			this.contParenteseFechado++;
			return 26;
		}
		else if (this.isFechaParentese(token.getTipoToken()) && estadoAnterior == 15)
		{
			this.contParenteseFechado++;
			return 27;
		}
		
		return -1;
	}
	
	/**
	 * Seta o contador de chaves. 
	 * Incrementa se for chave de abertura e decrementa se for chave de fechamento.
	 * @param tipoToken O tipo do token.
	 */
	private void SetContadorChaves(TipoToken tipoToken) 
	{
		if(tipoToken == TipoToken.TT_ABRE_CHAVE)
		{
			this.contadorChaves++;
		}
		else if(tipoToken == TipoToken.TT_FECHA_CHAVE)
		{
			this.contadorChaves--;
		}
	}
	
	/**
	 * Verifica se e' uma condicao.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private boolean IsCondicao(Token token) 
	{
		if (this.listaBlocoSe.size() > 0 && (this.contParenteseAberto - this.contParenteseFechado) == 0 && this.getAbreChave(token) == 0
				&& this.listaBlocoSe.get(this.listaBlocoSe.size() - 1) == this.contadorChaves - 1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se e' um laco enquanto.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private boolean IsEnquanto(Token token) 
	{
		if (this.listaBlocoEnquanto.size() > 0 && (this.contParenteseAberto - this.contParenteseFechado) == 0 && this.getAbreChave(token) == 0
				&& this.listaBlocoEnquanto.get(this.listaBlocoEnquanto.size() - 1) == this.contadorChaves - 1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se e' um laco faca..enquanto.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private boolean IsFacaEnquanto(Token token) 
	{
		if (this.listaBlocoFacaEnquanto.size() > 0 && (this.contParenteseAberto - this.contParenteseFechado) == 0 && this.getAbreChave(token) == 0
				&& this.listaBlocoFacaEnquanto.get(this.listaBlocoFacaEnquanto.size() - 1) == this.contadorChaves - 1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se e' um laco para.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private boolean IsPara(Token token) 
	{
		if (this.listaBlocoPara.size() > 0 && (this.contParenteseAberto - this.contParenteseFechado) == 0 && this.getAbreChave(token) == 0
				&& this.listaBlocoPara.get(this.listaBlocoPara.size() - 1) == this.contadorChaves - 1 && this.posDentroPara == 3)
		{
			this.posDentroPara = 0;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se e' um escolha.
	 * @param token O token atual.
	 * @return Retorna o proximo estado do token, ou -1 se ele for invalido.
	 */
	private boolean IsEscolha(Token token) 
	{
		if (this.listaBlocoEscolha.size() > 0 && (this.contParenteseAberto - this.contParenteseFechado) == 0 && this.getAbreChave(token) == 0
				&& this.listaBlocoEscolha.get(this.listaBlocoEscolha.size() - 1) == this.contadorChaves - 1)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se e' operador matematico.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro, se for operador matematico.
	 */
	private boolean isOperadorMatematico(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_OPERADOR_MATEMATICO_1 || tipoToken == TipoToken.TT_OPERADOR_MATEMATICO_2;
	}
	
	/**
	 * Verifica se e' operador matematico do tipo 1.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro, se for operador matematico do tipo 1.
	 */
	private boolean isOperadorMatematicoTipo1(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_OPERADOR_MATEMATICO_1;
	}
	
	/**
	 * Verifica se e' operador logico.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro, se for operador logico.
	 */
	private boolean isOperadorLogico(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_OPERADOR_LOGICO;
	}
	
	/**
	 * Verifica se e' operador relacional.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro, se for operador relacional.
	 */
	private boolean isOperadorRelacional(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_OPERADOR_RELACIONAL;
	}
	
	/**
	 * Verifica se e' a chamada de uma variavel.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro, se for chamada de uma variavel.
	 */
	private boolean isChamaVariavel(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_CHAMA_VARIAVEL;
	}
	
	/**
	 * Verifica se e' a chamada de uma funcao.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro, se for chamada de uma funcao.
	 */
	private boolean isChamaFuncao(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_CHAMA_FUNCAO;
	}

	/**
	 * Verifica se o token e' uma abertura de parentese.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro se for abertura de parentese.
	 */
	private boolean isAbreParentese(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_ABRE_PARENTESE;
	}

	/**
	 * Verifica se o token e' uma fechamento de parentese.
	 * @param tipoToken O token atual.
	 * @return Verdadeiro se for fechamento de parentese.
	 */
	private boolean isFechaParentese(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_FECHA_PARENTESE;
	}
	
	/**
	 * Verifica se o token e' de algum tipo de constante.
	 * @param tipoToken O tipo do token.
	 * @return Verdadeiro se o token for um tipo de constante.
	 */
	private boolean isTipoConstante(TipoToken tipoToken) 
	{
		if (tipoToken == TipoToken.TT_PALAVRA_CONSTANTE || tipoToken == TipoToken.TT_NUMERO_CONSTANTE || tipoToken == TipoToken.TT_CARACTER_CONSTANTE
			|| tipoToken == TipoToken.TT_LOGICO)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se o token e' de algum tipo de constante de texto.
	 * @param tipoToken O tipo do token.
	 * @return Verdadeiro se o token for um tipo de constante de texto.
	 */
	private boolean isTipoConstanteTexto(TipoToken tipoToken)
	{
		if (tipoToken == TipoToken.TT_PALAVRA_CONSTANTE)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se o token e' de algum tipo de incremento.
	 * @param tipoToken O tipo do token.
	 * @return Verdadeiro se o token for um tipo de incremento.
	 */
	private boolean isIncremento(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_INCREMENTO;
	}
	
	/**
	 * Verifica se o token e' de algum tipo de operador ternário.
	 * @param tipoToken O tipo do token.
	 * @return Verdadeiro se o token for um tipo de operador ternário.
	 */
	private boolean isOperadorTernario(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_TERNARIO || tipoToken == TipoToken.TT_DOIS_PONTOS;
	}
	
	/**
	 * Verifica se o token e' de algum tipo de negação.
	 * @param tipoToken O tipo do token.
	 * @return Verdadeiro se o token for um tipo de negação.
	 */
	private boolean isNegacao(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_OPERADOR_LOGICO_NEGACAO;
	}
	
	/**
	 * Verifica se o token e' de algum tipo de dados.
	 * @param tipoToken O tipo do token.
	 * @return Verdadeiro se o token for um tipo de dados.
	 */
	private boolean isTipoDeDados(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_TIPO_DE_DADOS;
	}
	
	/**
	 * Verifica se e' chave de abertura ou de fechamento.
	 * @param token O token.
	 * @return Verdadeiro se for chave de abertura ou fechamento.
	 */
	private boolean IsChaves(Token token) 
	{
		return token.getTipoToken() == TipoToken.TT_ABRE_CHAVE || token.getTipoToken() == TipoToken.TT_FECHA_CHAVE;
	}
	
	/**
	 * Verifica se e' um comando aletorio.
	 * @param token O tipo do token.
	 * @return Verdadeiro se for um comando aleatorio.
	 */
	private boolean isAleatorio(TipoToken tipoToken) 
	{
		return tipoToken == TipoToken.TT_ALEATORIO;
	}
	
	/**
	 * Verifica se e' colchete de abertura.
	 * @param token O token.
	 * @return Verdadeiro se for colchete de abertura.
	 */
	private boolean isAbreColchete(Token token)
	{
		if (token.getTipoToken() == TipoToken.TT_ABRE_COLCHETE)
		{
			this.contColcheteAberto++;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se e' colchete de fechamento.
	 * @param token O token.
	 * @return Verdadeiro se for colchete de fechamento.
	 */
	private boolean isFechaColchete(Token token)
	{
		if (token.getTipoToken() == TipoToken.TT_FECHA_COLCHETE)
		{
			this.contColcheteFechado++;
			return true;
		}
		
		return false;
	}
}

