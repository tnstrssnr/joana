package ifc;

import static edu.kit.joana.ui.annotations.Level.*;


import edu.kit.joana.ui.annotations.EntryPoint;
import edu.kit.joana.ui.annotations.EntryPointKind;
import edu.kit.joana.ui.annotations.Sink;
import edu.kit.joana.ui.annotations.Source;


public class Main {

	public static int x,y;
	
	@EntryPoint(kind = EntryPointKind.SEQUENTIAL)
	public static void main(String[] argv) {
		
		int z  = 5; 
		
		print(z);
		
		x = inputPIN();
		if (x < 1234)
			print(1);
		if (x > 0) 
			z = 42;
		y = x;
		print(y);
		print(z);
		
	}
	
	@Source(level = HIGH, lineNumber = 34, columnNumber = 1)
	public static int inputPIN() { return 42; }
	
	@Sink(level = LOW, lineNumber = 37, columnNumber = 1)
	public static void print(int output) {}

}

