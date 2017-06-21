package base;

/**
 * Palavras reservadas do interpretador.
 * @author Luiz Peres.
 */
public class PalavraReservada 
{
	/**
	 * Os operadores da linguagem. 
	 */
	public static final String Operadores = "+-*/%=<>!";
	
	/**
	 * Os caracteres que podem estar no inicio de uma palavra.
	 */
	public static final String InicioPalavra = "_&%#";
	
	/**
	 * Os caracteres terminais da linguagem. Eles so podem estar sozinhos.
	 */
	public static final String CaracteresTerminais = ",;{}[]().?:";
	
	/**
	 * Os comentarios de linha unica, por exemplo: // isso e' um comentario.
	 */
	public static final String ComentarioLinhaUnica = "//";
	
	/**
	 * Os caracteres que informam que e' um inicio de comentario de linhas multiplas.
	 */
	public static final String InicioComentarioLinhasMultiplas = "/*";
	
	/**
	 * Os caracteres que informam que e' um fim de comentario de linhas multiplas.
	 */
	public static final String FimComentarioLinhasMultiplas = "*/";
	
	/**
	 * Os caracteres especiais que podem estar no meio de uma palavra.
	 */
	public static final String CaracterEspecialMeioPalavra = "_";
	
	/**
	 * Os caracteres que representam o inicio de uma cadeia constante, por exemplo: "isso e' uma cadeia constante".
	 */
	public static final String InicioCadeiaConstanteCaracter = "\'\"";
	
	/**
	 * O caracter de escape.
	 */
	public static final Character CaracterEscape = '\\';
	
	/**
	 * O caracter responsavel por separar numeros flutuantes.
	 */
	public static final Character CaracterSeparadorNumeroFlutuante = '.';
	
	/**
	 * O caracter responsavel por enderecamento de memoria das variaveis, como por exemplo &x.
	 */
	public static final Character CaracterEnderecoMemoria = '&';
	
	/**
	 * O caracter responsavel por identificar o fim de uma linha de comando.
	 */
	public static final Character CaracterFimDeComando = ';';
	
	/**
	 * O caracter responsavel por separar as variaveis.
	 */
	public static final Character CaracterSeparadorVariavel = ',';
	
	/**
	 * O caracter responsavel por iniciar uma definicao de expressao.
	 */
	public static final Character CaracterInicioDefinicaoExpressao = '(';
	
	/**
	 * O caracter responsavel por finalizar uma definicao de expressao.
	 */
	public static final Character CaracterFimDefinicaoExpressao = ')';
	
	/**
	 * A extensao aceita pela solucao.
	 */
	public static final String ExtensaoArquivo = ".bra";
	
	/**
	 * Representante de espaco da solucao. Entende-se por espaco:
	 * \s = \t \n entre outros
	 */
	public static final String Espaco = "\\s";
	
	/**
	 * Os generos dos tokens da linguagem.
	 */
	public enum GeneroToken
	{
		GT_PALAVRA,
		GT_NUMERO,
		GT_OPERADOR,
		GT_CARACTER_TERMINAL
	}
	
	/**
	 * Os tipos dos tokens da linguagem.
	 */
	public enum TipoToken
	{
		TT_INCLUSAO, // include
		TT_EXTENSAO_BIBLIOTECA, // .
		TT_NOME_BIB_INCLUIR, // o nome da biblioteca a ser incluida
		TT_CONSTANTE, // define
		TT_DECLARACAO_CONSTANTE, // a declaracao de uma constante.
		TT_FUNCAO, // funcao
		TT_DECLARACAO_FUNCAO, // a declaracao de uma funcao.
		TT_PROCEDIMENTO, // procedimento
		TT_TIPO_DE_DADOS, // inteiro, real
		TT_RETORNO, // retorna
		TT_CONDICAO, // se
		TT_CONDICAO_SENAO, // senao
		TT_ENQUANTO, // enquanto
		TT_FACA_ENQUANTO, // faca.. enquanto
		TT_PARA, // para
		TT_ABRE_PARENTESE, // (
		TT_FECHA_PARENTESE, // )
		TT_ABRE_CHAVE, // {
		TT_FECHA_CHAVE, // }
		TT_ABRE_COLCHETE, // [
		TT_FECHA_COLCHETE, // ]
		TT_FIM_COMANDO, // ponto-e-virgula
		TT_SEPARA_VARIAVEL, // virgula
		TT_OPERADOR_LOGICO, // e, ou
		TT_OPERADOR_RELACIONAL, // ==, !=, > etc.
		TT_OPERADOR_MATEMATICO_1, // + -
		TT_OPERADOR_MATEMATICO_2, // * / %
		TT_ENDERECO_VARIAVEIS, // &
		TT_VARIAVEL_DENTRO_CONSTANTE, // %d %f
		TT_ESCREVA, // a funcao escreva
		TT_LEIA, // a funcao leia
		TT_LOGICO, // verdadeiro ou falso
		TT_ASPAS_DUPLAS, // "
		TT_ASPAS_SIMPLES, // '
		TT_ATRIBUICAO, // =
		TT_CALCULO_E_ATRIBUICAO, // += -=
		TT_INCREMENTO, // ++ --
		TT_TERNARIO, // ?
		TT_DECLARACAO_VARIAVEL, // declara uma variavel, por exemplo: inteiro x;
		TT_CHAMA_VARIAVEL, // chama uma variavel declarada.
		TT_PALAVRA_CONSTANTE, // cadeia de caracteres constantes "uma palavra", por exemplo.
		TT_CHAMA_FUNCAO, // chama uma funcao. calcula(), por exemplo.
		TT_CARACTER_CONSTANTE, // caracter constante 'a', por exemplo.
		TT_NUMERO_CONSTANTE, // qualquer numero.
		TT_FUNCAO_PRINCIPAL, // a funcao principal do algoritmo
		TT_ESCOLHA, // escolha a variavel
		TT_CASO, // caso valor variavel
		TT_DOIS_PONTOS, // dois pontos
		TT_PADRAO, // o valor padrao do escolha
		TT_PULA, // sai da escolha ou o laco
		TT_CONTINUA, // vai para proxima identacao do laco
		TT_ALEATORIO, // aleatorio
		TT_LIMPA_TELA, // limpatela
		TT_OPERADOR_LOGICO_NEGACAO // !
	}
}