package compilador;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * Classe que gerencia as excecoes do compilador.
 * @author Luiz Peres
 *
 */
public class GerenciadorExcecaoCompilador 
{
	/**
	 * O nome da classe do arquivo.
	 */
	private static String Classe;
	
	/**
	 * Le o arquivo e gera erro de compilacao para o mesmo.
	 * @param classeArquivo O nome da classe do arquivo.
	 * @throws Exception Erro de compilacao.
	 */
	public static void GeraErroCompilacao(String classeArquivo) throws Exception
	{
		String nomeArquivo = classeArquivo + Compilador.extensaoJava;
		
		Classe = classeArquivo;
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(Compilador.arqExcecao));
			String str;
			while (in.ready())
			{
				str = in.readLine();
				
				if (str.contains(nomeArquivo))
				{
					GerenciadorExcecaoCompilador.ValidaCabecalhoErro(in, str);
				}
			}
			
			in.close();
		}
		catch (IOException e) 
		{
		}
	}
	
	/**
	 * Le o arquivo e gera erro de compilacao para o mesmo.
	 * @param classeArquivo O nome da classe do arquivo.
	 * @throws Exception Erro de compilacao.
	 */
	public static void GeraErroCompilacao(String classeArquivo, ByteArrayOutputStream baos) throws Exception
	{	
		String nomeArquivo = classeArquivo + Compilador.extensaoJava;
		Classe = classeArquivo;
		String result = new String();
		try
		{
			//BufferedReader in = new BufferedReader(new FileReader(Compilador.arqExcecao));
			BufferedReader in = new BufferedReader(new StringReader(new String(baos.toByteArray())));
			
			String str;
			while (in.ready())
			{
				str = in.readLine();
				
				if (str.contains(nomeArquivo))
				{
					GerenciadorExcecaoCompilador.ValidaCabecalhoErro(in, str);
				}
			}
			
			in.close();
		}
		catch (IOException e) 
		{
		}
	}

	/**
	 * Valida o cabecalho do erro.
	 * @param in O ponteiro do arquivo.
	 * @param str A string da linha atual.
	 * @throws Exception Joga o erro tratado na tela.
	 */
	private static void ValidaCabecalhoErro(BufferedReader in, String str) throws Exception
	{
		String[] strDividida = str.split(":");
		if (strDividida.length >= 3)
		{
			int nLinha = GerenciadorExcecaoCompilador.isOSWindows() && str.charAt(1) == ':' ? Integer.parseInt(strDividida[2]) : Integer.parseInt(strDividida[1]);
			String strErro = GerenciadorExcecaoCompilador.isOSWindows() && str.charAt(1) == ':' ? strDividida[4] : strDividida[2];
			GerenciadorExcecaoCompilador.ThrowNewException(in, strErro, nLinha);
		}
	}

	/**
	 * Gerencia, trata e manipula as excecoes da tela.
	 * @param in O ponteiro do arquivo.
	 * @param strErro A string de erro.
	 * @param nLinha O numero da linha do erro.
	 * @throws Exception A excecao tratada.
	 */
	private static void ThrowNewException(BufferedReader in, String strErro, int nLinha) throws Exception 
	{
		String erro = "";
		String strFound = "";
		String strRequired = "";
		String strOperador = "";
		String[] strFindNLinha;
		strErro = strErro.trim();
		if (GerenciadorExcecaoCompilador.isOSWindows())
		{
			strFindNLinha = in.readLine().split("////Linha: ");
			if (strFindNLinha.length > 1)
			{
				nLinha = Integer.parseInt(strFindNLinha[1].trim());
			}
		}
		
		if (strErro.equals("illegal escape character"))
		{
			erro += "existe caracter de escape ilegal! Sao validos apenas: \\b, \\t, \\n, \\f, \\r, \\\", \\' e \\\\";
			
		}
		else if (strErro.equals("case, default, or '}' expected"))
		{
			erro += "e' esperado 'caso', 'padrao' ou '}'.";
		}
		else if (strErro.equals("incompatible types"))
		{
			if (GerenciadorExcecaoCompilador.isOSWindows())
			{
				in.readLine();
				strFound = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
				strRequired = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
				in.readLine();
			}
			else
			{
				strFound = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
				strRequired = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
			}
			erro += "existe erro semantico! Tipos incompativeis: " + strFound + " e " + strRequired + ".";
		}
		else if (strErro.equals("illegal forward reference"))
		{
			erro += "nao existe a referencia ainda.";
		}
		else if (strErro.matches("operator .* cannot be applied to .*"))
		{
			strOperador = GerenciadorExcecaoCompilador.TraduzPortugol(strErro.split(" ")[1]);
			String strTipo = strErro.substring(strErro.indexOf("applied to ") + 11, strErro.length());
			String[] strTipos = strTipo.split(",");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < strTipos.length; i++)
			{
				sb.append(GerenciadorExcecaoCompilador.TraduzPortugol(strTipos[i]) + ",");
			}
			// tira a ultima virgula.
			sb.deleteCharAt(sb.length() - 1);
			
			erro += "existe erro semantico! O operador " + strOperador + " nao pode ser aplicado a " + sb.toString() + ".";
		}
		else if (strErro.equals("possible loss of precision"))
		{
			if (GerenciadorExcecaoCompilador.isOSWindows())
			{
				in.readLine();
				strFound = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
				strRequired = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
				in.readLine();
			}
			else
			{
				strFound = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
				strRequired = GerenciadorExcecaoCompilador.TraduzPortugol(in.readLine().split(":")[1]);
			}
			erro += "existe um erro de precisao! Esperava " + strRequired.trim() + ", porem foi encontrado " + strFound + ".";
		}
		else if (strErro.matches(".*(.*) in " + Classe + " cannot be applied to .*") || strErro.matches(".*(.*) in class " + Classe + " cannot be applied to .*"))
		{
			String nomeFuncao = GerenciadorExcecaoCompilador.isOSWindows() ? strErro.substring(7, strErro.indexOf(' ', 7)) : strErro.substring(0, strErro.indexOf('('));
			String[] strTiposUsados = new String[0];
			String[] strTipos = new String[0];
			if (GerenciadorExcecaoCompilador.isOSWindows())
			{
				in.readLine();
				String tipos = in.readLine();
				strTipos = tipos.substring(tipos.lastIndexOf(':') + 1, tipos.length()).split(",");
				tipos = in.readLine();
				strTiposUsados = tipos.substring(tipos.lastIndexOf(':') + 1, tipos.length()).split(",");
			}
			else
			{
				strTiposUsados = strErro.substring(strErro.indexOf('(') + 1, strErro.indexOf(')')).split(",");
			}
		
			StringBuilder sbUsado = new StringBuilder();
			for (int i = 0; i < strTiposUsados.length; i++)
			{
				sbUsado.append(GerenciadorExcecaoCompilador.TraduzPortugol(strTiposUsados[i]) + ",");
			}
			// tira a ultima virgula.
			sbUsado.deleteCharAt(sbUsado.length() - 1);
			
			if (!GerenciadorExcecaoCompilador.isOSWindows())
			{
				strTipos = strErro.substring(strErro.lastIndexOf('(') + 1, strErro.lastIndexOf(')')).split(",");
			}
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < strTipos.length; i++)
			{
				sb.append(GerenciadorExcecaoCompilador.TraduzPortugol(strTipos[i]) + ",");
			}
			// tira a ultima virgula.
			sb.deleteCharAt(sb.length() - 1);
			
			if (sb.toString().equals("()"))
					sb.delete(0, sb.length());
			if (sbUsado.toString().equals("()"))
					sbUsado.delete(0, sbUsado.length());
			
			erro += nomeFuncao + "(" + sbUsado.toString() + ") nao pode ser aplicado a (" + sb.toString() + ")";
		}
		else if (strErro.contains("main(java.lang.String[]) is already defined in " + Classe) || strErro.contains("main(String[]) is already defined in " + Classe))
		{
			erro += "esta duplicando a funcao principal!";
		}
		else if (strErro.equals("unreachable statement"))
		{
			erro += "existe um estado inacessivel!";
		}
		else if (strErro.equals("missing return statement"))
		{
			erro += "esta faltando o retorno da funcao!";
		}
		else if (strErro.trim().contains("integer number too large"))
		{
			erro += "contem um numero muito grande!";
		}
		else
		{
			erro += "contem um erro semantico!";
		}
		
		if (!GerenciadorExcecaoCompilador.isOSWindows())
		{
			strFindNLinha = in.readLine().split("////Linha: ");
			if (strFindNLinha.length > 1)
			{
				nLinha = Integer.parseInt(strFindNLinha[1].trim());
			}
		}
		
		String erroComeco = "Proximo a linha " + nLinha + " ";
		
		throw new Exception(erroComeco + erro);
	}

	/**
	 * Traduz para portugol alguns tokens especificos.
	 * @param string A string a ser traduzida.
	 * @return O token traduzido para portugol.
	 */
	private static String TraduzPortugol(String string) 
	{
		if (string.trim().equals("int"))
		{
			return "inteiro";
		}
		else if (string.trim().equals("java.lang.String") || string.trim().equals("String"))
		{
			return "texto";
		}
		else if (string.trim().equals("float"))
		{
			return "real";
		}
		else if (string.trim().equals("boolean"))
		{
			return "logico";
		}
		else if (string.trim().equals("char"))
		{
			return "caracter";
		}
		else if (string.trim().equals("&&"))
		{
			return "'e'";
		}
		else if (string.trim().equals("||"))
		{
			return "'ou'";
		}
		
		return string;
	}
	
	/**
	 * Verifica se é sistema operacional windows.
	 * @return Retorna verdadeiro se for windows.
	 */
	public static boolean isOSWindows() 
	{
		return System.getProperty("os.name").contains("Windows");
	}
}

