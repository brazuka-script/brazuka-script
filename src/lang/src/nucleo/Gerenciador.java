package nucleo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import compilador.Compilador;

import base.PalavraReservada;

import util.ExcecoesUtil;

/**
 * Escolhe qual a melhor opcao utilizar a partir de linhas de codigo de arquivos *.bra.
 * @author Luiz Peres.
 */
public class Gerenciador 
{	
	private AnalisadorToken analisadorToken;
	private AnalisadorLexico analisadorLexico;
	private AnalisadorSintatico analisadorSintatico;
	private GeradorCodigo geradorCodigo;
	private String path;
	private String arquivo;
	private String[] urlPathArquivoJava;
	
	/** 
	 * O metodo construtor.
	 * */
	public Gerenciador (String path, String arquivo) 
	{
		this.analisadorToken = new AnalisadorToken();
		this.analisadorLexico = new AnalisadorLexico();
		this.analisadorSintatico = new AnalisadorSintatico();
		this.geradorCodigo = new GeradorCodigo();
		this.path = path;
		this.arquivo = arquivo;
	}
	
	/** 
	 * Gerencia e trata arquivos do tipos *.bra.
	 * @throws Exception Gera erro caso a entensao nao seja conhecida.
	 * */
	public void exec() throws Exception
	{
		if (arquivo.endsWith(PalavraReservada.ExtensaoArquivo))
		{
			this.analisaTokens(this.analisadorToken, this.path + this.arquivo);
			this.analisaLexicos(this.analisadorLexico, this.analisadorToken);
			this.analisaSintaxe(this.analisadorSintatico, this.analisadorToken);
			// this.analisaSemantica
			// A analise semantica sera trabalho do java.
			this.urlPathArquivoJava = this.geradorCodigo.getArquivoJava(this.path, this.arquivo, this.analisadorToken);
			
			try
			{
				Compilador.main(this.urlPathArquivoJava);
			}
			catch (Exception e) 
			{
				throw new Exception(e.getMessage());
			}
		}
		else 
		{
			ExcecoesUtil.GeraExcecao("ExtensaoNaoConhecida");
		}
	}

	/** 
	 * Analisa e seta os gêneros dos tokens de um determinado arquivo *.bra.
	 * @param objAnalisadorToken O objeto onde estara a lista de tokens.
	 * @param arq O arquivo *.bra a ser lido. 
	 * @throws Exception Gera erro caso nao consiga abrir o arquivo.
	 * */
	private void analisaTokens(AnalisadorToken objAnalisadorToken, String arq) throws Exception 
	{	
		try 
		{
	        BufferedReader in = new BufferedReader(new FileReader(arq));
            String str;
            for (int linha = 1; in.ready(); linha++)
            {
            	str = in.readLine();
                objAnalisadorToken.Tokeniza(str, linha);
            }
            in.close();
            
            // Confere se todos os tokens de comentarios multiplos foram fechados corretamente.
            objAnalisadorToken.TokensComentariosFechados();
	    } 
		catch (IOException e) 
		{
			ExcecoesUtil.GeraExcecao("ImpossivelAbrirArquivo");
	    }
	}
	
	/**
	 * Analisa e seta os tipos dos tokens gerados pelo analisador de tokens e gera erro caso o lexico nao seja conhecido.
	 * @param objAnalisadorLexico O objeto que representa o analisador lexico.
	 * @param objAnalisadorToken O objeto que representa o analisador de tokens.
	 * @throws Exception Gera excecao caso o lexico nao seja conhecido.
	 */
	private void analisaLexicos(AnalisadorLexico objAnalisadorLexico, AnalisadorToken objAnalisadorToken) throws Exception 
	{
		objAnalisadorLexico.VerificaLexicos(objAnalisadorToken);
	}
	
	/**
	 * Analisa e verifica se a sintaxe dos tokens (que ja passou pelo analisador lexico) sao valido de acordo com as definicoes da linguagem.
	 * @param objAnalisadorSintatico O objeto que representa o analisador sintatico.
	 * @param objAnalisadorToken O objeto que representa o analisador de tokens.
	 * @throws Exception Gera excecao caso a sintaxe nao esteja de acordo com a definida da solucao.
	 */
	private void analisaSintaxe(AnalisadorSintatico objAnalisadorSintatico,	AnalisadorToken objAnalisadorToken) throws Exception 
	{
		objAnalisadorSintatico.VerificaSintaxe(objAnalisadorToken);
	}
}
