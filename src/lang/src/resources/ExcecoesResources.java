package resources;

import java.util.ListResourceBundle;

/**
 * Classe que guarda as resources de excecoes do sistema.
 * @author Luiz Peres
 *
 */
public class ExcecoesResources extends ListResourceBundle
{
	/**
	 * Resource de conteudo chave-valor.
	 */
	static final Object[][] contents = 
	{
		// Chaves pares de valor
		{ "NumeroIncorreto", "O numero nao esta em formato correto!" },
		{ "ImpossivelAbrirArquivo", "Impossivel abrir o arquivo!" },
		{ "ExtensaoNaoConhecida", "Extensao do arquivo nao conhecida!" },
		{ "OperadorIncorreto", "O operador nao esta em formato correto!" },
		{ "ComentarioLMSemFechamento", "O comentario de linha multipla nao esta sendo fechado!" },
		{ "ComentarioLMSemAbertura", "O comentario de linha multipla nao esta sendo aberto!" },
		{ "ConstanteCaracterIncorreto", "Constante(s) e/ou cadeia de caracter(es) incorreto(s)!" },
		{ "CaracterDesconhecido", "Caracter nao conhecido!" },
		{ "FuncaoJaDeclarada", "e' uma funçao que ja esta sendo utilizada!" },
		{ "VariavelJaDeclarada", "e' uma variavel que ja esta sendo utilizada!" },
		{ "VariavelJaDeclaradaFuncao", "foi declarada como uma funçao anteriormente!" },
		{ "FuncaoJaDeclaradaVariavel", "foi declarada como variavel anteriormente!" },
		{ "TokenDesconhecido", "nao e' um identificador valido no contexto!" },
		{ "ErroSintaxe", "foi verificado um erro sintatico!" },
		{ "FaltaParenteses", "O numero de parenteses abertos e' diferente do numero de parenteses fechados!" },
		{ "FaltaColchetes", "O numero de colchetes abertos e' diferente do numero de colchetes fechados!" },
		{ "FaltaChaves", "O numero de chaves abertas e' diferente do numero de chaves fechadas!"},
		{ "NumeroParametrosDiferentes", "O numero de parametros informados esta diferente do numero de parametros chamados!"},
		{ "ErroVarCmdAleatorio", "O comando aleatorio possui dois parametros somente!" },
		{ "TokenInvalidoCmdEscreva", "nao e' valido dentro do comando escreva!" },
		{ "ErroCriarDiretorio", "O diretorio 'bin/' nao pode ser criado! Verificar permissoes de escrita na pasta!" },
		{ "ErroCriarArquivo", "O arquivo de execucao nao pode ser criado! Verificar permissoes de escrita!" },
		{ "ErroEscritaArquivo", "Nao foi possivel escrever no arquivo de execucao!" }
	};

	@Override
	protected Object[][] getContents() 
	{
		// Retorna o conteudo de contents.
		return contents;
	}
}
