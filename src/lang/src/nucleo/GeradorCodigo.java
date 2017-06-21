package nucleo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import resources.PalavrasConhecidasJavaResources;
import resources.PalavrasConhecidasResources;

import util.ExcecoesUtil;
import util.PalavrasConhecidasJavaUtil;

import base.Token;
import base.PalavraReservada.TipoToken;

/**
 * Gerencia, analisa e traduz de brazuka script para java. 
 * @author Luiz Peres.
 */
public class GeradorCodigo 
{
	/**
	 * O nome do arquivo .java.
	 */
	String nomeArquivo;
	
	/**
	 * Path do arquivo .java.
	 */
	String pathArquivo;
	
	/**
	 * O metodo que sera executado.
	 */
	final String metodoExecutar = "main";
	
	/**
	 * A extensao do arquivo java.
	 */
	final String extJava = ".java";
	
	/**
	 * O buffer de escrita do arquivo.
	 */
	BufferedWriter bwOut;
	
	/**
	 * flag que verifica se esta dentro do metodo main.
	 */
	boolean flagIsDentroMain = false;
	
	/**
	 * Cria um arquivo java a partir de um analisador de tokens com uma lista.
	 * @param path O caminho do arquivo a quem os tokens pertencem.
	 * @param arquivo O nome do arquivo.
	 * @param objAnalisadorToken O analisador de token.
	 * @return O caminho das referencias dos arquivos .java para poderem ser executados posteriormente.
	 * @throws Exception Erro inesperado.
	 */
	public String[] getArquivoJava(String path, String arquivo, AnalisadorToken objAnalisadorToken) throws Exception
	{
		ArrayList<Token> listaTokens = objAnalisadorToken.getListaTokens();
		int nLinha = 0;
		Token token;
		int nChaves = 1;
		int nParenteses = 0;
		this.getCabecalhoLinguagem(path, arquivo);
		for(int i = 0; i < listaTokens.size(); i++)
		{
			token = listaTokens.get(i);
			nChaves = this.getNChaves(token, nChaves);
			nParenteses = this.getNParenteses(token, nParenteses);
			nLinha = this.ValidaNovaLinha(nLinha, token, nChaves);
			Object lexicoJava = this.getValorJava(token);
			i = this.PrintObjetoJava(lexicoJava, token.getValorToken(), listaTokens, i, nChaves, nParenteses);
			/*if (i < listaTokens.size() - 1)
			{
				nChaves = this.ValidaMetodoMainInsereException(token, listaTokens.get(i + 1), nChaves);
			}*/
		}
		
		this.getRodapeLinguagem(); 
		
		return new String[] 
		{ 
			this.pathArquivo, 
			this.nomeArquivo.substring(0, this.nomeArquivo.indexOf(this.extJava)), 
			this.metodoExecutar 
		};
	}

	/*private int ValidaMetodoMainInsereException(Token token, Token proxToken, int nChaves) throws IOException 
	{
		String barraT = this.getTab(nChaves, true);
		if (token.getTipoToken() == TipoToken.TT_FUNCAO_PRINCIPAL)
		{
			this.flagIsDentroMain = true;
		}
		else if (this.flagIsDentroMain && nChaves == 2 && token.getTipoToken() == TipoToken.TT_ABRE_CHAVE)
		{
			this.bwOut.write("\n\t" + barraT + "try\n");
			this.bwOut.write(barraT + "\t{");
			return nChaves + 1;
		}
		else if (this.flagIsDentroMain && nChaves == 3 && proxToken.getTipoToken() == TipoToken.TT_FECHA_CHAVE)
		{
			this.flagIsDentroMain = false;
			this.bwOut.write("\n" + barraT + "}\n");
			this.bwOut.write(barraT + "catch (IllegalStateException e)\n"); 
			this.bwOut.write(barraT + "{\n");
			this.bwOut.write(barraT + "\tthrow new Exception(\"Os valores da entrada de dados estao incorretos!\");\n");
			this.bwOut.write(barraT + "}\n");
			this.bwOut.write(barraT + "catch (NumberFormatException e)\n");
			this.bwOut.write(barraT + "{\n");
			this.bwOut.write(barraT + "\tthrow new Exception(\"A entrada de dados estava errada ou muito grande para o tipo de dados alocado.\");\n");
			this.bwOut.write(barraT + "}");
			return nChaves - 1;
		}
		
		return nChaves;
	}*/

	/**
	 * Forma o rodape da linguagem.
	 * @throws Exception Excecao de escrita.
	 */
	private void getRodapeLinguagem() throws Exception 
	{
		try
		{
			this.bwOut.write("\n");
			this.bwOut.write(PalavrasConhecidasResources.fechCha + "\n");
			this.bwOut.close();
		}
		catch (IOException e) 
		{
			ExcecoesUtil.GeraExcecao("ErroEscritaArquivo");
		}
	}

	/**
	 * Forma o cabecalho da linguagem.
	 * @param O path inicial do arquivo .bra.
	 * @param arquivo O nome do arquivo.
	 * @throws Exception Erro caso nao consiga criar diretorio ou arquivo ou excecao de escrita.
	 */
	private void getCabecalhoLinguagem(String path, String arquivo) throws Exception 
	{
		String nomeClasse = arquivo.substring(0, arquivo.indexOf('.')).trim().toUpperCase();
		this.pathArquivo = path + "bin/";
		this.nomeArquivo = nomeClasse + this.extJava;
		
		File fArquivo = this.CriaDiretorioEArquivo(this.pathArquivo, this.nomeArquivo);

		try
		{
			this.bwOut = new BufferedWriter(new FileWriter(fArquivo));
			this.bwOut.write("import java.util.Scanner;" + "\n");
			this.bwOut.write("import java.util.regex.MatchResult;" + "\n");
			this.bwOut.write(PalavrasConhecidasJavaResources.javaClass + " " + nomeClasse + "\n");
			this.bwOut.write(PalavrasConhecidasResources.abreCha + "\n");
			this.bwOut.write("\tstatic Scanner _____leitura = new Scanner(System.in);" + "\n");
			this.bwOut.write("\tstatic Scanner _____s;" + "\n");
			this.bwOut.write("\tstatic String _____input;" + "\n");
			this.bwOut.write("\tstatic MatchResult _____result;" + "\n");
		}
		catch (IOException e) 
		{
			ExcecoesUtil.GeraExcecao("ErroEscritaArquivo");
		}
	}

	private File CriaDiretorioEArquivo(String caminhoJava, String arqJava) throws Exception 
	{
		File fPath = new File(caminhoJava);
		File fArq = new File(caminhoJava + arqJava);
		
		if (!fPath.exists())
		{
			if (!fPath.mkdir())
			{
				ExcecoesUtil.GeraExcecao("ErroCriarDiretorio");
			}
		}
		
		if (fArq.exists())
		{
			fArq.delete();
		}
		
		if (!fArq.createNewFile())
		{
			ExcecoesUtil.GeraExcecao("ErroCriarArquivo");
		}
		
		return fArq;
	}

	/**
	 * Printa o lexico java na fonta de saida corrente.
	 * @param lexicoJava O lexico java.
	 * @param valorToken O lexico brazuka.
	 * @param listaToken A lista de tokens.
	 * @param i A posicao atual na lista de tokens.
	 * @param nChaves O numero do bloco de chaves atual.
	 * @param nParenteses O numero de parenteses abertos.
	 * @return A nova posicao na lista de tokens.
	 * @throws Exception Erro inesperado.
	 */
	private int PrintObjetoJava(Object lexicoJava, String valorToken, ArrayList<Token> listaToken, int i, int nChaves, int nParenteses) throws Exception 
	{
		int novaPos = i;
		StringBuilder sb = new StringBuilder(); 
		
		if (lexicoJava != null)
		{
			if (lexicoJava.toString().equals(PalavrasConhecidasJavaResources.getEscreva))
			{
				novaPos = this.getEscreva(sb, listaToken, i, nChaves);
				lexicoJava = sb.toString();
			}
			else
			{
				novaPos = this.auxPrintObjeto(lexicoJava, sb, listaToken, i, nChaves, nParenteses, false);
			}
			
			lexicoJava = sb.toString();
			this.bwOut.write(lexicoJava + " ");
		}
		else
		{
			if (listaToken.get(i).getTipoToken() == TipoToken.TT_NUMERO_CONSTANTE)
			{
				if (valorToken.contains(PalavrasConhecidasResources.ponto))
				{
					valorToken = "(float) " + valorToken;
				}
			}
			/*else if (listaToken.get(i).getTipoToken() == TipoToken.TT_DECLARACAO_FUNCAO)
			{
				novaPos = this.getFuncao(sb, listaToken, i);
				valorToken = sb.toString();
			}*/
			
			this.bwOut.write(valorToken + " ");
		}
		
		return novaPos;
	}

	/**
	 * Auxiliador do metodo Print Objeto.
	 * @param lexicoJava O lexico Java.
	 * @param sb A string builder.
	 * @param listaToken A lista de tokens.
	 * @param i O indice atual.
	 * @param nChaves O numero de chaves abertas.
	 * @param nParenteses O numero de parenteses abertos.
	 * @param isEscreva Verificador que informa se esta ou nao vindo do comando escreva.
	 * @return O retorno em java.
	 * @throws Exception Excecoes referentes as classes que foram incorporadas.
	 */
	private int auxPrintObjeto(Object lexicoJava, StringBuilder sb, ArrayList<Token> listaToken, int i, int nChaves, int nParenteses, boolean isEscreva) throws Exception 
	{
		this.verificarPrintObjeto(lexicoJava,listaToken.get(i), isEscreva);
		
		int novaPos = i;
		if(lexicoJava.toString().equals(PalavrasConhecidasJavaResources.inexistente))
		{
			sb.append("");
			novaPos += 5;
		}
		else if (lexicoJava.toString().equals(PalavrasConhecidasJavaResources.getConstante))
		{
			sb.append(this.getConstanteJava(listaToken, i));
			novaPos += 2;
		}
		else if (nChaves == 1 && nParenteses == 0 && listaToken.size() >= i + 1 && listaToken.get(i).getTipoToken() == TipoToken.TT_TIPO_DE_DADOS
				&& listaToken.get(i + 1).getTipoToken() == TipoToken.TT_DECLARACAO_VARIAVEL && !isEscreva)
		{
			sb.append(PalavrasConhecidasJavaResources.javaStatic + " " + lexicoJava.toString());
		}
		else if (listaToken.get(i).getTipoToken() == TipoToken.TT_TIPO_DE_DADOS && listaToken.get(i + 1).getTipoToken() == TipoToken.TT_DECLARACAO_VARIAVEL
				&& listaToken.get(i + 2).getTipoToken() == TipoToken.TT_ABRE_COLCHETE && !isEscreva)
		{ 
			novaPos = this.getVetor(sb, lexicoJava, listaToken, i);
		}
		else if (lexicoJava.toString().equals(PalavrasConhecidasJavaResources.getLeia))
		{
			novaPos = this.getLeia(sb, listaToken, i, nChaves);
		}
		else if (lexicoJava.toString().equals(PalavrasConhecidasJavaResources.getMain))
		{
			sb.append("void main(String[] args) throws Exception");
			novaPos += 2;
		}
		else if (lexicoJava.toString().equals(PalavrasConhecidasJavaResources.getLimpaTela))
		{
			sb.append("for (int _____j = 0; _____j < 100; ++_____j) System.out.println();");
			novaPos += 3;
		}
		else if (lexicoJava.toString().equals(PalavrasConhecidasJavaResources.getRandom))
		{
			novaPos = this.getRandom(sb, listaToken, i);
		}
		else
		{
			sb.append(lexicoJava.toString());
		}
		
		return novaPos;
	}

	/**
	 * Verifica se pode ou nao utilizar os comandos passados por parametros.
	 * @param lexicoJava O lexico Java.
	 * @param isEscreva Verificador se e' escreva ou nao.
	 * @throws Exception O unico comando que o escreva aceita e' o random.
	 */
	private void verificarPrintObjeto(Object lexicoJava, Token token, boolean isEscreva) throws Exception 
	{
		if ((lexicoJava.toString() == PalavrasConhecidasJavaResources.getConstante
				||
			lexicoJava.toString() == PalavrasConhecidasJavaResources.getLeia
				||
			lexicoJava.toString() == PalavrasConhecidasJavaResources.getLimpaTela
				|| 
			lexicoJava.toString() == PalavrasConhecidasJavaResources.getMain
				||
			lexicoJava.toString() == PalavrasConhecidasJavaResources.getEscreva
			)
			&&
			isEscreva)
		{
			ExcecoesUtil.GeraExcecao("\'" + token.getValorToken() + "\'", "TokenInvalidoCmdEscreva", token.getnLinha());
		}
	}

	private int getVetor(StringBuilder sb, Object lexicoJava, ArrayList<Token> listaToken, int i) throws Exception 
	{
		int pos = i;
		int nContColchete = 0;
		String strAux = "";
		ArrayList<String> lstValor = new ArrayList<String>();
		
		while(listaToken.get(pos).getTipoToken() != TipoToken.TT_FIM_COMANDO || (listaToken.get(pos).getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL
				&& nContColchete == 0))
		{
			Token token = listaToken.get(pos); 
			if (token.getTipoToken() == TipoToken.TT_ABRE_COLCHETE)
			{
				nContColchete++;
				strAux = "";
			}
			else if (token.getTipoToken() == TipoToken.TT_FECHA_COLCHETE)
			{
				nContColchete--;
				lstValor.add(strAux);
			}
			else
			{
				String vlToken = "";
				Object objLexicoJava = this.getValorJava(token);
				if (objLexicoJava != null)
				{
					sb.delete(0, sb.length());
					pos = this.auxPrintObjeto(objLexicoJava, sb, listaToken, pos, 0, 0, true);
					vlToken = sb.toString();
				}
				else
				{
					vlToken = token.getValorToken();
				}
				
				if (nContColchete > 0)
				{
					strAux += vlToken + " ";
				}
			}
			
			pos++;
		}
		
		String res = lexicoJava.toString() + " " + listaToken.get(i + 1).getValorToken() + " ";
		String aux1 = "", aux2 = "";
		for(int j = 0; j < lstValor.size(); j++)
		{
			aux1 += "[] ";
			aux2 += "[ " + lstValor.get(j) + " ] ";
		}
		
		res += aux1 + "= " + "new " + lexicoJava.toString() + " " + aux2 + " ;";
		
		sb = sb.delete(0, sb.length());
		sb.append(res);
		
		return pos;
	}

	/*private int getFuncao(StringBuilder sb, ArrayList<Token> listaToken, int i) throws Exception 
	{
		int pos = i;
		String vlToken = "";
		while (listaToken.get(pos).getTipoToken() != TipoToken.TT_ABRE_CHAVE)			
		{		
			Object objLexicoJava = this.getValorJava(listaToken.get(pos));
			if (objLexicoJava != null)
			{
				vlToken += objLexicoJava + " ";
			}
			else
			{
				vlToken += listaToken.get(pos).getValorToken() + " ";
			}
			
			pos++;
		}
		
		sb = sb.delete(0, sb.length());
		sb.append(vlToken + " throws Exception");
		
		// retorna pos - 1 por causa da abertura da chave.
		return pos - 1;
	}*/
	
	/**
	 * Traduz o escreva de brazuka para Java. 
	 * @param strBuilder A string builder.
	 * @param listaToken A lista de tokens.
	 * @param i O indice atual.
	 * @return Os comandos de escreva. 
	 * @throws Exception Gera excecao caso o numero de parametros esteja diferente.
	 */
	private int getEscreva(StringBuilder sb, ArrayList<Token> listaToken, int i, int nChaves) throws Exception 
	{
		int pos = i;
		ArrayList<String> lstVar = new ArrayList<String>();
		// estamos pulando os tokens 'escreva' e '('.
		pos += 2;
		String strPadrao = listaToken.get(pos).getValorToken();
		// Pulamos a str padrao.
		pos++;
		String strAux = "";
		int nContParentese = 0;
		
		while ((listaToken.get(pos).getTipoToken() != TipoToken.TT_FECHA_PARENTESE && listaToken.get(pos + 1).getTipoToken() != TipoToken.TT_FIM_COMANDO))			
		{
			if (listaToken.get(pos).getTipoToken() == TipoToken.TT_ABRE_PARENTESE)
			{
				nContParentese++;
			}
			else if (listaToken.get(pos).getTipoToken() == TipoToken.TT_FECHA_PARENTESE)
			{
				nContParentese--;
			}
			
			if (listaToken.get(pos).getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL)
			{
				if (!strAux.equals("") && nContParentese == 0)
				{
					lstVar.add(strAux);
					strAux = "";
				}
			}
			else
			{
				String vlToken = "";
				Object objLexicoJava = this.getValorJava(listaToken.get(pos));
				if (objLexicoJava != null)
				{
					sb.delete(0, sb.length());
					pos = this.auxPrintObjeto(objLexicoJava, sb, listaToken, pos, nChaves, nContParentese, true);
					vlToken = sb.toString();
				}
				else
				{
					vlToken = listaToken.get(pos).getValorToken();
				}
				
				strAux += vlToken + " ";
			}
			
			pos++;
		}
		
		if (!strAux.equals(""))
		{
			lstVar.add(strAux);
		}
		// Incrementando a pos por causa do toke ';'
		pos++;
		
		sb = sb.delete(0, sb.length());
		sb.append(this.getExpressaoEscrita(strPadrao,  lstVar, listaToken.get(i).getnLinha()));
		
		return pos;
	}

	/**
	 * Recupera a expressao de escrita no padrao java.
	 * @param strPadrao A string padrao.
	 * @param lstVar A lista de variaveis.
	 * @param nLinha O numero da linha do comando.
	 * @return A expressao modificada.
	 * @throws Exception Gera excecao caso o numero de variaveis seja diferente.
	 */
	private String getExpressaoEscrita(String strPadrao, ArrayList<String> lstVar, int nLinha) throws Exception 
	{
		String str = strPadrao;
		ArrayList<String> lstTipoVar = new ArrayList<String>();
		for (int i = 0; i < str.length() - 1; i++)
		{
			if (str.charAt(i) == '%')
			{
				if (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'f' || str.charAt(i + 1) == 'c' || str.charAt(i + 1) == 's')
				{
					lstTipoVar.add(str.substring(i, i + 2));
					i++;
				}
			}
		}
		
		if (lstTipoVar.size() != lstVar.size())
		{
			ExcecoesUtil.GeraExcecao("", "NumeroParametrosDiferentes", nLinha);
		}
		
		String strCmd = "System.out.println( ";
		for (int i = 0; i < lstTipoVar.size(); i++)
		{
			str = str.replaceFirst(lstTipoVar.get(i), "\" + " + lstVar.get(i) + "+ \"");
		}
		
		strCmd += str + " );"; 
		return strCmd;
	}

	/**
	 * Traduz o random de brazuka para Java. 
	 * @param strBuilder A string builder.
	 * @param listaToken A lista de tokens.
	 * @param i O indice atual.
	 * @return O comando random. 
	 * @throws Exception Gera excecao caso o numero de parametros esteja errado.
	 */
	private int getRandom(StringBuilder sb, ArrayList<Token> listaToken, int i) throws Exception 
	{
		int pos = i;
		ArrayList<String> lstVar = new ArrayList<String>();
		String strAux = "";
		// estamos pulando os tokens 'aleatorio' e '('.
		pos += 2;
		int nContParentese = 1;
		while (nContParentese > 0)
		{
			if (listaToken.get(pos).getTipoToken() == TipoToken.TT_ABRE_PARENTESE)
			{
				nContParentese++;
			}
			else if (listaToken.get(pos).getTipoToken() == TipoToken.TT_FECHA_PARENTESE)
			{
				nContParentese--;
			}
			
			if (listaToken.get(pos).getTipoToken() == TipoToken.TT_SEPARA_VARIAVEL)
			{
				if (!strAux.equals("") && nContParentese == 1)
				{
					lstVar.add(strAux);
					strAux = "";
				}
			}
			else 
			{
				if (nContParentese > 0)
				{
					strAux += listaToken.get(pos).getValorToken() + " ";
				}
			}
			 
			if (nContParentese > 0)
			{
				pos++;
			}
		}
		
		if (!strAux.equals(""))
		{
			lstVar.add(strAux);
		}
		
		if (lstVar.size() != 2)
		{
			ExcecoesUtil.GeraExcecao("", "ErroVarCmdAleatorio", listaToken.get(i).getnLinha());
		}
		
		String min = lstVar.get(0);
		String max = lstVar.get(1);
		
		sb.append("(int) (Math.random() * ( " + max + " - " + min + " ) + " + min + ")");
		
		return pos;
	}

	/**
	 * Traduz o leia de brazuka para Java. 
	 * @param strBuilder A string builder.
	 * @param listaToken A lista de tokens.
	 * @param i O indice atual.
	 * @param nChaves O numero de chaves abertas.
	 * @return Os comandos de leitura. 
	 * @throws Exception Gera excecao caso o numero de parametros esteja diferente.
	 */
	private int getLeia(StringBuilder strBuilder, ArrayList<Token> listaToken, int i, int nChaves) throws Exception 
	{
		int pos = i;
		boolean isAbreChave = false;
		String tab = this.getTab(nChaves, isAbreChave);
		String strLexicoJava = "";
		ArrayList<String> lstVar = new ArrayList<String>();
		ArrayList<String> lstTipoVar = new ArrayList<String>();
		
		strLexicoJava = "_____input = _____leitura.nextLine();\n";
		strLexicoJava += tab + "_____s = new Scanner(_____input);\n";
		while(listaToken.get(pos).getTipoToken() != TipoToken.TT_FIM_COMANDO)
		{
			if (listaToken.get(pos).getTipoToken() == TipoToken.TT_PALAVRA_CONSTANTE)
			{
				strLexicoJava += tab + "_____s.findInLine(" + this.getExpressaoLeitura(listaToken.get(pos), lstTipoVar) + ");\n";
				strLexicoJava += tab + "_____result = _____s.match();\n";
			}
			else if (listaToken.get(pos).getTipoToken() == TipoToken.TT_CHAMA_VARIAVEL)
			{
				lstVar.add(listaToken.get(pos).getValorToken());
			}
			pos++;
		}
		
		if (lstTipoVar.size() != lstVar.size())
		{
			ExcecoesUtil.GeraExcecao("", "NumeroParametrosDiferentes", listaToken.get(i).getnLinha());
		}
		
		for (int j = 1; j <= lstVar.size(); j++) 
		{
			strLexicoJava += tab + this.getConversaoTipoJava(lstVar, lstTipoVar, j) + "\n";
		}
		
		strLexicoJava += tab + "_____s.close();";
		strBuilder.append(strLexicoJava);
		return pos;
	}

	/**
	 * Recupera as variaveis convertindo para o valor das mesmas.
	 * @param lstVar A lista de variaveis.
	 * @param lstTipoVar A lista de tipos de variaveis.
	 * @param j O indice do result group.
	 * @return A string convertida para codigo java.
	 */
	private String getConversaoTipoJava(ArrayList<String> lstVar, ArrayList<String> lstTipoVar, int j) 
	{
		String strConvertida = "";
		String strTipoConversao = "";
		if (lstTipoVar.get(j - 1).equals(PalavrasConhecidasJavaResources.javaInt))
		{
			strTipoConversao = "Integer.parseInt(_____result.group("+ j +").trim());";
		}
		else if (lstTipoVar.get(j - 1).equals(PalavrasConhecidasJavaResources.javaFloat))
		{
			strTipoConversao = "Float.parseFloat(_____result.group("+ j +").trim());";
		}
		else if (lstTipoVar.get(j - 1).equals(PalavrasConhecidasJavaResources.javaChar))
		{
			strTipoConversao = "_____result.group("+ j +").trim().charAt(0);";
		}
		else if (lstTipoVar.get(j - 1).equals(PalavrasConhecidasJavaResources.javaStr))
		{
			strTipoConversao = "_____result.group("+ j +").trim();";
		}
		
		strConvertida = lstVar.get(j - 1) + " = " + strTipoConversao;
		
		return strConvertida;
	}

	/**
	 * Recupera a expressao de leitura no padrao java.
	 * @param token O token atual.
	 * @return A nova expressao.
	 */
	private String getExpressaoLeitura(Token token, ArrayList<String> lstTipoVar) 
	{
		String valorToken = token.getValorToken();
		char[] cadeiaVlTk = valorToken.toCharArray();
		for (int i = 0; i < cadeiaVlTk.length - 1; i++)
		{
			if (cadeiaVlTk[i] == '%' && cadeiaVlTk[i + 1] == 'd')
			{
				lstTipoVar.add(PalavrasConhecidasJavaResources.javaInt);
			}
			else if (cadeiaVlTk[i] == '%' && cadeiaVlTk[i + 1] == 'f')
			{
				lstTipoVar.add(PalavrasConhecidasJavaResources.javaFloat);
			}
			else if (cadeiaVlTk[i] == '%' && cadeiaVlTk[i + 1] == 'c')
			{
				lstTipoVar.add(PalavrasConhecidasJavaResources.javaChar);
			}
			else if (cadeiaVlTk[i] == '%' && cadeiaVlTk[i + 1] == 's')
			{
				lstTipoVar.add(PalavrasConhecidasJavaResources.javaStr);
			}
		}
		
		valorToken = valorToken.replace(PalavrasConhecidasResources.porcentD, "(\\\\d+)");
		valorToken = valorToken.replace(PalavrasConhecidasResources.porcentF, "(\\\\d+.?\\\\d*)");
		valorToken = valorToken.replace(PalavrasConhecidasResources.porcentC, "(\\\\w)");
		valorToken = valorToken.replace(PalavrasConhecidasResources.porcentS, "(\\\\w+)");
		
		return valorToken;
	}

	/**
	 * Traduz uma constante de brazuka para Java.
	 * @param listaToken A lista de tokens.
	 * @param i O indice atual.
	 * @return A constante Java.
	 * @throws Exception Erro inesperado.
	 */
	private String getConstanteJava(ArrayList<Token> listaToken, int i) throws Exception 
	{
		String constante = "";
		
		if (listaToken.size() >= i + 2)
		{
			Token tokenPos1 = listaToken.get(i + 1), tokenPos2 = listaToken.get(i + 2);
			String complementoFloat = "";
			String valorToken2 = tokenPos2.getValorToken();
			
			constante += PalavrasConhecidasJavaResources.constante + " ";
			if (listaToken.get(i + 2).getTipoToken() == TipoToken.TT_NUMERO_CONSTANTE)
			{
				complementoFloat = "(float) ";
				constante += PalavrasConhecidasJavaResources.javaFloat; 
			}
			else if (listaToken.get(i + 2).getTipoToken() == TipoToken.TT_CARACTER_CONSTANTE)
			{
				constante += PalavrasConhecidasJavaResources.javaChar;
			}
			else if (listaToken.get(i + 2).getTipoToken() == TipoToken.TT_PALAVRA_CONSTANTE)
			{
				constante += PalavrasConhecidasJavaResources.javaStr;
			}
			else if (listaToken.get(i + 2).getTipoToken() == TipoToken.TT_LOGICO)
			{
				constante += PalavrasConhecidasJavaResources.javaBool;
				Object obj = this.getValorJava(listaToken.get(i + 2));
				if (obj != null)
				{
					valorToken2 = obj.toString();
				}
			}
			
			constante += " " + tokenPos1.getValorToken() + " = " + complementoFloat + valorToken2 + ";";
		}
		
		return constante;
	}

	/**
	 * Validador de nova linha.
	 * @param nLinha O numero da linha anterior.
	 * @param token O token atual.
	 * @param nChaves O numero de blocos abertos por chave.
	 * @return O valor da nova linha.
	 * @throws Erro de escrita.
	 */
	private int ValidaNovaLinha(int nLinha, Token token, int nChaves) throws Exception
	{
		if (nLinha != token.getnLinha())
		{
			boolean isAbreChave = token.getTipoToken() == TipoToken.TT_ABRE_CHAVE;
			
			String barraT = this.getTab(nChaves, isAbreChave);
			try
			{
				this.bwOut.write(" ////Linha: " + (token.getnLinha() - 1));
				this.bwOut.write("\n");
				this.bwOut.write(barraT);
			}
			catch (IOException e)
			{
				ExcecoesUtil.GeraExcecao("ErroEscritaArquivo");
			}
			
			nLinha = token.getnLinha();
		}
		
		return nLinha;
	}

	/**
	 * Verificador de chaves do codigo.
	 * @param token O token atual.
	 * @param valorNChaves O valor do numero do bloco da chave aberta.
	 * @return O novo valor do bloco.
	 */
	private int getNChaves(Token token, int valorNChaves) 
	{
		if (token.getTipoToken() == TipoToken.TT_ABRE_CHAVE)
		{
			return valorNChaves + 1;
		}
		else if (token.getTipoToken() == TipoToken.TT_FECHA_CHAVE)
		{
			return valorNChaves - 1;
		}
		
		return valorNChaves;
	}
	
	/**
	 * Verificador de parenteses do codigo.
	 * @param token O token atual.
	 * @param valorNParenteses O valor do numero de parenteses abertos que nao possuem (ainda) fechamento.
	 * @return O novo valor da variavel nParentese.
	 */
	private int getNParenteses(Token token, int valorNParenteses) 
	{
		if (token.getTipoToken() == TipoToken.TT_ABRE_PARENTESE)
		{
			return valorNParenteses + 1;
		}
		else if (token.getTipoToken() == TipoToken.TT_FECHA_PARENTESE)
		{
			return valorNParenteses - 1;
		}
		return valorNParenteses;
	}
	
	/**
	 * Verificador que recebe o numero de chaves do bloco atual.
	 * @param nChaves O numero de chaves.
	 * @param isAbreChave Verifica se e' uma abertura de chaves.
	 * @return A quantidade de tabs.
	 */
	private String getTab(int nChaves, boolean isAbreChave)
	{
		int c = isAbreChave ? nChaves - 1 : nChaves;
		
		String barraT = "";
		for(int j = 0; j < c; j++)
		{
			barraT += "\t";
		}
		
		return barraT;
	}

	/**
	 * Recupera o valor em java de um token.
	 * @param token O token atual.
	 * @return O valor do token em java.
	 * @throws Exception Erro inesperado. 
	 */
	private Object getValorJava(Token token) throws Exception 
	{
		return PalavrasConhecidasJavaUtil.getValorJava(token.getValorToken());
	}
}
