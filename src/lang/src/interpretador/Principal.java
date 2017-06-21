package interpretador;

import nucleo.Gerenciador;

/**
 * Le um arquivo do tipo *.bra para posterior interpretacao.
 * @author Luiz Peres.
 */
public class Principal 
{
	public static void main(String args[]) throws Exception
	{
		String pathCompleto = args[0]; //"C:\\Users\\net\\workspace\\Projeto Brazuka\\arq1.bra"; ///home/luiz/Desktop/Brazuka/Programas/arq3.bra";
		
		// Unix
		String caminhoArq = pathCompleto.substring(0, pathCompleto.lastIndexOf('/') + 1);
		// Windows
		caminhoArq = caminhoArq.length() == 0 ? pathCompleto.substring(0, pathCompleto.lastIndexOf('\\') + 1) : caminhoArq;
		
		// Unix
		String nomeArq = pathCompleto.substring(pathCompleto.lastIndexOf('/') + 1, pathCompleto.length());
		// Windows
		nomeArq = nomeArq.length() == pathCompleto.length() ? pathCompleto.substring(pathCompleto.lastIndexOf('\\') + 1, pathCompleto.length()) : nomeArq;
		
		// Se não for nem windows nem unix, ele irá gerar um erro posteriormente.
		
		Gerenciador gerenciador = new Gerenciador(caminhoArq, nomeArq);
		gerenciador.exec();
	}
}
