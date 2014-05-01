package de.lbac.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import de.lbac.parser.frefLexer;
import de.lbac.parser.frefParser;

public class Main {
	
	public static void main(String[] args) throws Exception {
		ANTLRInputStream input = new ANTLRFileStream("code.demo");
		
		String compiledCode = compile(input);
		System.out.println(compiledCode);
		File f = new File("test.j");
		FileWriter fs = new FileWriter(f);
		fs.write(compiledCode);
		fs.close();
	}
	
	public static String compile(ANTLRInputStream input){
		frefLexer lexer = new frefLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		frefParser parser = new frefParser(tokens);
		
		ParseTree tree = parser.start();
		return createJasminFile((String)new MyVisitor().visit(tree));
		
	}
	
	private static String createJasminFile(String code){
		return ".class public Fref\n" + 
				".super java/lang/Object\n" + 
				"\n" + 
				".method public static out(I)V\n" +
				"\n" +
				"	.limit stack 2\n" +
				"	.limit locals 2\n" +
				"	iload 0\n" +
				"	getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
				"	swap\n" +
				"	invokevirtual java/io/PrintStream/println(I)V\n" + 
				"	return\n" + 
				"\n" +
				".end method\n" +
				//".method public static main([Ljava/lang/String;)V\n" + 
				//"	.limit stack 100\n" + 
				//"	.limit locals 100\n" + 
				//"\n" +
				code //+ 
				//"	return\n" + 
				//"\n" +
				//".end method\n" 
				;
	}

}
