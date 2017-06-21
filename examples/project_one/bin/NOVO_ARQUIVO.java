import java.util.Scanner;
import java.util.regex.MatchResult;
public class NOVO_ARQUIVO
{
	static Scanner _____leitura = new Scanner(System.in);
	static Scanner _____s;
	static String _____input;
	static MatchResult _____result;
 ////Linha: 6
	public static void main(String[] args) throws Exception  ////Linha: 7
	{  ////Linha: 8
		int x ;  ////Linha: 9
		int y ;  ////Linha: 11
		System.out.println( "Digite dois valores inteiros:\n" );  ////Linha: 12
		_____input = _____leitura.nextLine();
		_____s = new Scanner(_____input);
		_____s.findInLine("(\\d+) (\\d+)");
		_____result = _____s.match();
		x = Integer.parseInt(_____result.group(1).trim());
		y = Integer.parseInt(_____result.group(2).trim());
		_____s.close();  ////Linha: 14
		int soma = x + y ;  ////Linha: 15
		System.out.println( "A soma é: " + soma + "" );  ////Linha: 16
	} 
}
