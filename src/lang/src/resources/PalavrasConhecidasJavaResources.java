package resources;

import java.util.ListResourceBundle;

/**
 * Classe que guarda as resources de palavras JAVA conhecidas do sistema.
 * @author Luiz Peres
 *
 */
public class PalavrasConhecidasJavaResources extends ListResourceBundle 
{
	public static final String inexistente = "inexistente";
	public static final String getEscreva = "getEscreva";
	public static final String getLeia = "getLeia";
	public static final String getMain = "getMain";
	public static final String getRandom = "getRandom";
	public static final String getLimpaTela = "getLimpaTela";
	public static final String getConstante = "getConstante";
	public static final String constante = "static final";
	public static final String javaInt = "int";
	public static final String javaBool = "boolean";
	public static final String javaChar = "char";
	public static final String javaFloat = "float";
	public static final String javaStr = "String";
	public static final String javaStatic = "static";
	public static final String javaClass = "public class";
	
	/**
	 * As palavras reservadas da linguagem (palavras conhecidas) para java..
	 */
	
	static final Object[][] ConhecidasJava = 
	{
		{ PalavrasConhecidasResources.inclusao , PalavrasConhecidasJavaResources.inexistente },
		{ PalavrasConhecidasResources.constante, PalavrasConhecidasJavaResources.getConstante },
		{ PalavrasConhecidasResources.funcao, "public static" },
		{ PalavrasConhecidasResources.procedimento, "public static void" },
		{ PalavrasConhecidasResources.vazio, "void" },
		{ PalavrasConhecidasResources.inteiro, PalavrasConhecidasJavaResources.javaInt },
		{ PalavrasConhecidasResources.real, PalavrasConhecidasJavaResources.javaFloat },
		{ PalavrasConhecidasResources.caracter, PalavrasConhecidasJavaResources.javaChar },
		{ PalavrasConhecidasResources.texto, PalavrasConhecidasJavaResources.javaStr },
		{ PalavrasConhecidasResources.logico, PalavrasConhecidasJavaResources.javaBool },
		{ PalavrasConhecidasResources.retorna, "return" },
		{ PalavrasConhecidasResources.se, "if" },
		{ PalavrasConhecidasResources.senao, "else" },
		{ PalavrasConhecidasResources.enquanto, "while" },
		{ PalavrasConhecidasResources.faca, "do" },	
		{ PalavrasConhecidasResources.para, "for" },
		{ PalavrasConhecidasResources.abrePar, "(" },
		{ PalavrasConhecidasResources.fechPar, ")" },
		{ PalavrasConhecidasResources.abreCha, "{" },
		{ PalavrasConhecidasResources.fechCha, "}" },
		{ PalavrasConhecidasResources.abreCol, "[" },
		{ PalavrasConhecidasResources.fechCol, "]" },
		{ PalavrasConhecidasResources.fimCmd, ";" },
		{ PalavrasConhecidasResources.separaVar, "," },
		{ PalavrasConhecidasResources.menor, "<" },
		{ PalavrasConhecidasResources.maior, ">" },
		{ PalavrasConhecidasResources.eComercial, "&" },
		{ PalavrasConhecidasResources.mais, "+" },
		{ PalavrasConhecidasResources.menos, "-" },
		{ PalavrasConhecidasResources.vezes, "*" },
		{ PalavrasConhecidasResources.dividir, "/" },
		{ PalavrasConhecidasResources.porcent, "%" },
		{ PalavrasConhecidasResources.leia, PalavrasConhecidasJavaResources.getLeia },
		{ PalavrasConhecidasResources.escreva, PalavrasConhecidasJavaResources.getEscreva },
		{ PalavrasConhecidasResources.porcentD, "%d" }, 
		{ PalavrasConhecidasResources.porcentF, "%f" },
		{ PalavrasConhecidasResources.porcentC, "%c" },
		{ PalavrasConhecidasResources.porcentS, "%s" },
		{ PalavrasConhecidasResources.verdadeiro, "true" },
		{ PalavrasConhecidasResources.falso, "false" },
		{ PalavrasConhecidasResources.aspasDuplas, "\"" },
		{ PalavrasConhecidasResources.aspasSimples, "'" },
		{ PalavrasConhecidasResources.incremento, "++" },
		{ PalavrasConhecidasResources.decremento, "--" },
		{ PalavrasConhecidasResources.maisIgual, "+=" },
		{ PalavrasConhecidasResources.menosIgual, "-=" },
		{ PalavrasConhecidasResources.vezesIgual, "*=" },
		{ PalavrasConhecidasResources.dividirIgual, "/=" },
		{ PalavrasConhecidasResources.porcentIgual, "%=" },
		{ PalavrasConhecidasResources.atribuicao, "=" },
		{ PalavrasConhecidasResources.igualIgual, "==" },
		{ PalavrasConhecidasResources.maiorIgual, ">=" },
		{ PalavrasConhecidasResources.menorIgual, "<=" },
		{ PalavrasConhecidasResources.e, "&&" },
		{ PalavrasConhecidasResources.ou, "||" },
		{ PalavrasConhecidasResources.negacao, "!" },
		{ PalavrasConhecidasResources.diferente, "!=" },
		{ PalavrasConhecidasResources.ponto, "." }, 
		{ PalavrasConhecidasResources.interrogacao, "?" },
		{ PalavrasConhecidasResources.doisPontos, ":" },
		{ PalavrasConhecidasResources.principal, PalavrasConhecidasJavaResources.getMain },
		{ PalavrasConhecidasResources.escolha, "switch" },
		{ PalavrasConhecidasResources.caso, "case" },
		{ PalavrasConhecidasResources.padrao, "default" },
		{ PalavrasConhecidasResources.pula, "break" },
		{ PalavrasConhecidasResources.continua, "continue" },
		{ PalavrasConhecidasResources.aleatorio, PalavrasConhecidasJavaResources.getRandom },
		{ PalavrasConhecidasResources.limpatela, PalavrasConhecidasJavaResources.getLimpaTela }
	};

	/**
	 * Retorna o conteudo da string de palavras conhecidas JAVA.
	 */
	@Override
	protected Object[][] getContents() 
	{
		return ConhecidasJava;
	}

}

