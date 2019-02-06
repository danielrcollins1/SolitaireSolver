import java.io.*; 
import java.util.*;

/******************************************************************************
*  Simple example of reading & writing a text file.
*
*  @author   Daniel R. Collins (dcollins@superdan.net)
*  @since    2016-07-16
*  @version  1.1
******************************************************************************/

public class TestFileIO {

	/**
	*  Read a text file into a String array list.
	*/
	public static void readFileToList (String filename, ArrayList<String> list) 
			throws IOException {

		File file = new File(filename);
		Scanner scan = new Scanner(file);
		while (scan.hasNext()) {
			String s = scan.nextLine();
			list.add(s);
		}
		scan.close();
	}

	/**
	*  Write a String array list to a text file.
	*/
	public static void writeListToFile (String filename, ArrayList<String> list)
			throws IOException {

		File file = new File(filename);
		PrintWriter printer = new PrintWriter(file, "UTF-8");
		for (String s: list) {
			printer.println(s);		
		}
		printer.close();
	}

	/**
	*  Main test function.
	*/
	public static void main (String[] args) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("\u2660");
		list.add("\u2661");
		list.add("\u2662");
		list.add("\u2663");
		writeListToFile("output.txt", list); 

/*		
		System.out.println("\u00a5123");		
		
		// Desperate!
		for (int i = 0; i < 512; i++) {
			System.out.print(Character.toChars(i));
			if (i % 32 == 31)
				System.out.println();
		}
		System.out.println();
		
		System.out.println("\u2660");
		System.out.println(Character.toString((char)0x2660));
		System.out.println(Character.toChars(0x2660));
		
		int codePoint = 0x2660;
		char[] charPair = Character.toChars(codePoint);
		String symbol = new String(charPair);
		System.out.println(symbol);
		
		for (int i = 0x2660; i < 0x2664; i++) {
			System.out.print(Character.toChars(i));
		}
		System.out.println();
*/		
	}
}

