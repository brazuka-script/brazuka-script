package compilador;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Classe para compilar e executar um codigo via reflection.
 * @author Luiz Peres
 *
 */
public class Compilador 
{
	final static String arqExcecao = "result.log";
	final static String extensaoJava = ".java";
	
	public static void main(String[] args) throws Exception 
	{
		try
		{
			String pathArquivo = args[0]; //"C:/Users/net/workspace/Projeto Brazuka/bin/"; //"/C:\\Users\\net\\Desktop\\Plugin IDE\\workspace\\"; //"/home/luiz/Desktop/Brazuka/Programas/bin/";
			if (GerenciadorExcecaoCompilador.isOSWindows())
			{
				pathArquivo = pathArquivo.charAt(0) == '/' ? pathArquivo.substring(1, pathArquivo.length()) : pathArquivo;
			}
				
			String classeArquivo = args[1]; //"ARQ1";
			String metodoExecutar = args[2]; //"main";
			String[] comandos = { pathArquivo + classeArquivo + extensaoJava };
			PrintWriter logger;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			logger = new PrintWriter(baos);
			//logger = new PrintWriter( new FileWriter(arqExcecao));
			int isCompilou = com.sun.tools.javac.Main.compile(comandos, logger);
			if (isCompilou != 0)
			{
				GerenciadorExcecaoCompilador.GeraErroCompilacao(classeArquivo, baos);
			}
			else 
			{
				//Esse era o m�todo antigo, que era executado via Reflection.
				//Compilador.executaMetodo(classeArquivo, pathArquivo, metodoExecutar, new Class[] { String[].class }, new Object[] { new String[] {} });
				Compilador.executaArquivo(classeArquivo, pathArquivo);
			}
        } 
		catch (MalformedURLException e) 
        {  
            e.printStackTrace();  
        } 
		catch (ClassNotFoundException e) 
        {  
            e.printStackTrace();  
        }     
		catch (InvocationTargetException e) 
		{
			throw new Exception("A entrada de dados estava errada ou muito grande para o tipo de dados alocado!");
		}
		catch (IllegalStateException e) 
		{
			throw new Exception("A entrada de dados estava errada ou muito grande para o tipo de dados alocado!");
		}
	}
	
	/**
	 * Metodo estatico que executa metodos a partir de uma determinada classe.
	 * @param nomeClasse Nome da classe que se deseja os metodos.
	 * @param camArquivo O caminho do arquivo.
	 * @param nomeMetodo Nome do metodo a ser invocado.
	 * @param argTypes Tipo dos parametros do metodo.
	 * @param args Parametros de entrada do metodo.
	 */
    public static void executaMetodo(String nomeClasse, String camArquivo, String nomeMetodo, Class[] argTypes, Object[] args) throws Exception
    {
    	try
    	{
	        // Criando um ClassLoader.   
	        ClassLoader cl = null;  
	        // Carregando a classe.  
	        Class cls = null;
	        
	    	if(camArquivo != null)
	    	{
	    		// Path para o local onde as classes se encontram (Sem as informações de package)   
	            URL url = new File(camArquivo).toURI().toURL();  
	              
	            // Array de URL formando um classpath.    
	            URL[] urls = new URL[]{url};
	            
	            // Carregando um ClassLoader.   
	            cl = new URLClassLoader(urls);
	            
	            // Carregando a classe.  	
	            cls = cl.loadClass(nomeClasse);
	    	}
	    	else 
	    	{
	    		 cls = Class.forName(nomeClasse);
	    	}
	    	
	        Object instance = cls.newInstance();
	        
	        Method method = cls.getDeclaredMethod(nomeMetodo, argTypes);
	        method.invoke(instance, args);
	    }
    	catch (NoSuchMethodException e) 
    	{
			throw new Exception("Funcao 'principal' nao foi encontrada no arquivo!");
		}
    }
    
    /**
	 * Metodo estatico que executa uma classe pelo prompt.
	 * @param nomeClasse Nome da classe que se deseja os metodos.
	 * @param camArquivo O caminho do arquivo.
	 */
    public static void executaArquivo(String nomeClasse, String camArquivo) throws Exception
    {
    	try
    	{
    		Runtime rt = Runtime.getRuntime();
    		String cmd = "";
    		
    		if (GerenciadorExcecaoCompilador.isOSWindows())
    		{
    			cmd = "cmd /c start cmd.exe /K \" " 
						+ " cd " + camArquivo
						+ " && java " + nomeClasse
						+ " && pause "
						+ " && exit "
						+ " \"";
    			
    			rt.exec(cmd);
    		}
    		else
    		{
    			// Executa o comando Linux.
    		}
    	}
    	catch (Exception e) 
    	{
    		throw new Exception("Ocorreu um erro ao tentar executar o arquivo!");
		}
    }
}