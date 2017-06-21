package resources;

import java.util.ListResourceBundle;

import base.PalavraReservada.TipoToken;

/**
 * Classe que guarda as resources de palavras conhecidas do sistema.
 * @author Luiz Peres
 *
 */
public class PalavrasConhecidasResources extends ListResourceBundle
{
	public static final String inclusao = "#incluir"; 
	public static final String constante = "#definir";
	public static final String funcao = "funcao";
	public static final String procedimento = "procedimento";
	public static final String vazio = "vazio";
	public static final String inteiro = "inteiro";
	public static final String real = "real";
	public static final String caracter = "caracter";
	public static final String texto = "texto";
	public static final String logico = "logico";
	public static final String retorna = "retorna";
	public static final String se = "se";
	public static final String senao = "senao";
	public static final String enquanto = "enquanto";
	public static final String faca = "faca";
	public static final String para = "para";
	public static final String abrePar = "(";
	public static final String fechPar = ")";
	public static final String abreCha = "{";
	public static final String fechCha = "}";
	public static final String abreCol = "[";
	public static final String fechCol = "]";
	public static final String fimCmd = ";";
	public static final String separaVar = ",";
	public static final String menor = "<";
	public static final String maior = ">";
	public static final String eComercial = "&";
	public static final String mais = "+";
	public static final String menos = "-";
	public static final String vezes = "*";
	public static final String dividir = "/";
	public static final String porcent = "%";
	public static final String leia = "leia";
	public static final String escreva = "escreva";
	public static final String porcentD = "%d";
	public static final String porcentF = "%f";
	public static final String porcentC = "%c";
	public static final String porcentS = "%s";
	public static final String verdadeiro = "verdadeiro";
	public static final String falso = "falso";
	public static final String aspasDuplas = "\"";
	public static final String aspasSimples = "'";
	public static final String incremento = "++";
	public static final String decremento = "--";
	public static final String maisIgual = "+=";
	public static final String menosIgual = "-=";
	public static final String vezesIgual = "*=";
	public static final String dividirIgual = "/=";
	public static final String porcentIgual = "%=";
	public static final String atribuicao = "=";
	public static final String igualIgual = "==";
	public static final String maiorIgual = ">=";
	public static final String menorIgual = "<=";
	public static final String e = "e";
	public static final String ou = "ou";
	public static final String negacao = "!";
	public static final String diferente = "!=";
	public static final String ponto = ".";
	public static final String interrogacao = "?";
	public static final String doisPontos = ":";
	public static final String principal = "principal";
	public static final String escolha = "escolha";
	public static final String caso = "caso";
	public static final String padrao = "padrao";
	public static final String pula = "pula";
	public static final String continua = "continua";
	public static final String aleatorio = "aleatorio";
	public static final String limpatela = "limpatela";
	
	/**
	 * As palavras reservadas da linguagem (palavras conhecidas).
	 */
	static final Object[][] Conhecidas = 
	{
		{ PalavrasConhecidasResources.inclusao , TipoToken.TT_INCLUSAO },
		{ PalavrasConhecidasResources.constante, TipoToken.TT_CONSTANTE },
		{ PalavrasConhecidasResources.funcao, TipoToken.TT_FUNCAO },
		{ PalavrasConhecidasResources.procedimento, TipoToken.TT_PROCEDIMENTO },
		{ PalavrasConhecidasResources.vazio, TipoToken.TT_TIPO_DE_DADOS },
		{ PalavrasConhecidasResources.inteiro, TipoToken.TT_TIPO_DE_DADOS },
		{ PalavrasConhecidasResources.real, TipoToken.TT_TIPO_DE_DADOS },
		{ PalavrasConhecidasResources.caracter, TipoToken.TT_TIPO_DE_DADOS },
		{ PalavrasConhecidasResources.texto, TipoToken.TT_TIPO_DE_DADOS },
		{ PalavrasConhecidasResources.logico, TipoToken.TT_TIPO_DE_DADOS },
		{ PalavrasConhecidasResources.retorna, TipoToken.TT_RETORNO },
		{ PalavrasConhecidasResources.se, TipoToken.TT_CONDICAO },
		{ PalavrasConhecidasResources.senao, TipoToken.TT_CONDICAO_SENAO },
		{ PalavrasConhecidasResources.enquanto, TipoToken.TT_ENQUANTO },
		{ PalavrasConhecidasResources.faca, TipoToken.TT_FACA_ENQUANTO },	
		{ PalavrasConhecidasResources.para, TipoToken.TT_PARA },
		{ PalavrasConhecidasResources.abrePar, TipoToken.TT_ABRE_PARENTESE },
		{ PalavrasConhecidasResources.fechPar, TipoToken.TT_FECHA_PARENTESE },
		{ PalavrasConhecidasResources.abreCha, TipoToken.TT_ABRE_CHAVE },
		{ PalavrasConhecidasResources.fechCha, TipoToken.TT_FECHA_CHAVE },
		{ PalavrasConhecidasResources.abreCol, TipoToken.TT_ABRE_COLCHETE },
		{ PalavrasConhecidasResources.fechCol, TipoToken.TT_FECHA_COLCHETE },
		{ PalavrasConhecidasResources.fimCmd, TipoToken.TT_FIM_COMANDO },
		{ PalavrasConhecidasResources.separaVar, TipoToken.TT_SEPARA_VARIAVEL },
		{ PalavrasConhecidasResources.menor, TipoToken.TT_OPERADOR_RELACIONAL },
		{ PalavrasConhecidasResources.maior, TipoToken.TT_OPERADOR_RELACIONAL },
		{ PalavrasConhecidasResources.eComercial, TipoToken.TT_ENDERECO_VARIAVEIS },
		{ PalavrasConhecidasResources.mais, TipoToken.TT_OPERADOR_MATEMATICO_1 },
		{ PalavrasConhecidasResources.menos, TipoToken.TT_OPERADOR_MATEMATICO_1 },
		{ PalavrasConhecidasResources.vezes, TipoToken.TT_OPERADOR_MATEMATICO_2 },
		{ PalavrasConhecidasResources.dividir, TipoToken.TT_OPERADOR_MATEMATICO_2 },
		{ PalavrasConhecidasResources.porcent, TipoToken.TT_OPERADOR_MATEMATICO_2 },
		{ PalavrasConhecidasResources.leia, TipoToken.TT_LEIA },
		{ PalavrasConhecidasResources.escreva, TipoToken.TT_ESCREVA },
		{ PalavrasConhecidasResources.porcentD, TipoToken.TT_VARIAVEL_DENTRO_CONSTANTE }, 
		{ PalavrasConhecidasResources.porcentF, TipoToken.TT_VARIAVEL_DENTRO_CONSTANTE },
		{ PalavrasConhecidasResources.porcentC, TipoToken.TT_VARIAVEL_DENTRO_CONSTANTE },
		{ PalavrasConhecidasResources.porcentS, TipoToken.TT_VARIAVEL_DENTRO_CONSTANTE },
		{ PalavrasConhecidasResources.verdadeiro, TipoToken.TT_LOGICO },
		{ PalavrasConhecidasResources.falso, TipoToken.TT_LOGICO },
		{ PalavrasConhecidasResources.aspasDuplas, TipoToken.TT_ASPAS_DUPLAS },
		{ PalavrasConhecidasResources.aspasSimples, TipoToken.TT_ASPAS_SIMPLES },
		{ PalavrasConhecidasResources.incremento, TipoToken.TT_INCREMENTO },
		{ PalavrasConhecidasResources.decremento, TipoToken.TT_INCREMENTO },
		{ PalavrasConhecidasResources.maisIgual, TipoToken.TT_CALCULO_E_ATRIBUICAO },
		{ PalavrasConhecidasResources.menosIgual, TipoToken.TT_CALCULO_E_ATRIBUICAO },
		{ PalavrasConhecidasResources.vezesIgual, TipoToken.TT_CALCULO_E_ATRIBUICAO },
		{ PalavrasConhecidasResources.dividirIgual, TipoToken.TT_CALCULO_E_ATRIBUICAO },
		{ PalavrasConhecidasResources.porcentIgual, TipoToken.TT_CALCULO_E_ATRIBUICAO },
		{ PalavrasConhecidasResources.atribuicao, TipoToken.TT_ATRIBUICAO },
		{ PalavrasConhecidasResources.igualIgual, TipoToken.TT_OPERADOR_RELACIONAL },
		{ PalavrasConhecidasResources.maiorIgual, TipoToken.TT_OPERADOR_RELACIONAL },
		{ PalavrasConhecidasResources.menorIgual, TipoToken.TT_OPERADOR_RELACIONAL },
		{ PalavrasConhecidasResources.e, TipoToken.TT_OPERADOR_LOGICO },
		{ PalavrasConhecidasResources.ou, TipoToken.TT_OPERADOR_LOGICO },
		{ PalavrasConhecidasResources.negacao, TipoToken.TT_OPERADOR_LOGICO_NEGACAO },
		{ PalavrasConhecidasResources.diferente, TipoToken.TT_OPERADOR_RELACIONAL },
		{ PalavrasConhecidasResources.ponto, TipoToken.TT_EXTENSAO_BIBLIOTECA }, 
		{ PalavrasConhecidasResources.interrogacao, TipoToken.TT_TERNARIO },
		{ PalavrasConhecidasResources.doisPontos, TipoToken.TT_DOIS_PONTOS },
		{ PalavrasConhecidasResources.principal, TipoToken.TT_FUNCAO_PRINCIPAL },
		{ PalavrasConhecidasResources.escolha, TipoToken.TT_ESCOLHA },
		{ PalavrasConhecidasResources.caso, TipoToken.TT_CASO },
		{ PalavrasConhecidasResources.padrao, TipoToken.TT_PADRAO },
		{ PalavrasConhecidasResources.pula, TipoToken.TT_PULA },
		{ PalavrasConhecidasResources.continua, TipoToken.TT_CONTINUA },
		{ PalavrasConhecidasResources.aleatorio, TipoToken.TT_ALEATORIO },
		{ PalavrasConhecidasResources.limpatela, TipoToken.TT_LIMPA_TELA }
	};

	/**
	 * Retorna o conteudo da string de palavras conhecidas.
	 */
	@Override
	protected Object[][] getContents() 
	{
		return Conhecidas;
	}

}
