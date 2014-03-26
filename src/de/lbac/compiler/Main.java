package de.lbac.compiler;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import de.lbac.parser.frefLexer;
import de.lbac.parser.frefParser;

public class Main {
	
	public static void main(String[] args) throws Exception{
		ANTLRInputStream input = new ANTLRFileStream("code.demo");
		frefLexer lexer = new frefLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		frefParser parser = new frefParser(tokens);
		
		ParseTree tree = parser.start();
		System.out.println(new MyVisitor().visit(tree));
		
	}

}
