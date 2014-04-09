package de.lbac.compiler;

import de.lbac.parser.frefBaseVisitor;
import de.lbac.parser.frefParser.AdditionContext;
import de.lbac.parser.frefParser.BracketsContext;
import de.lbac.parser.frefParser.CodeContext;
import de.lbac.parser.frefParser.CompareKeywordContext;
import de.lbac.parser.frefParser.DeclarationContext;
import de.lbac.parser.frefParser.DeclarationDefinitionContext;
import de.lbac.parser.frefParser.DefinitionContext;
import de.lbac.parser.frefParser.DowhileclauseContext;
import de.lbac.parser.frefParser.EmptyContext;
import de.lbac.parser.frefParser.ExpressionWithoutStatementContext;
import de.lbac.parser.frefParser.FnctcallContext;
import de.lbac.parser.frefParser.FnctnContext;
import de.lbac.parser.frefParser.FuncCallContext;
import de.lbac.parser.frefParser.FunctionParameterContext;
import de.lbac.parser.frefParser.IfclauseContext;
import de.lbac.parser.frefParser.IfelseclauseContext;
import de.lbac.parser.frefParser.MultDivisionContext;
import de.lbac.parser.frefParser.NumberContext;
import de.lbac.parser.frefParser.RelCondContext;
import de.lbac.parser.frefParser.StartContext;
import de.lbac.parser.frefParser.SubtractionContext;
import de.lbac.parser.frefParser.VariableContext;

import java.util.HashMap;
import java.util.Map;

public class MyVisitor extends frefBaseVisitor<Object> {
	
	Map<String, Integer> variables = new HashMap<String, Integer>();   //Variablen Map
	Map<String, String> functions = new HashMap<String, String>();   //Funktionsmap
	
	
	@Override
	public Object visitAddition(AdditionContext ctx) {
		return visitChildren(ctx) + "iadd\n";
	}
	
	@Override
	public Object visitBrackets(BracketsContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitCode(CodeContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitCompareKeyword(CompareKeywordContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitDeclaration(DeclarationContext ctx) {
		variables.put(ctx.name.getText(), variables.size());
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitDeclarationDefinition(DeclarationDefinitionContext ctx) {
		if(!variables.containsKey(ctx.name.getText()))
				variables.put(ctx.name.getText(), variables.size());
		return visitChildren(ctx) + "istore " + variables.get(ctx.name.getText()) + "\n";
	}
	
	@Override
	public Object visitDefinition(DefinitionContext ctx) {
		if(variables.get(ctx.name.getText()) != null)
			return visitChildren(ctx) + "istore " + variables.get(ctx.name.getText()) + "\n";
		return null;
		
	}
	
	@Override
	public Object visitDowhileclause(DowhileclauseContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitEmpty(EmptyContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitExpressionWithoutStatement(ExpressionWithoutStatementContext ctx) {
		return null;
	}
	
	@Override
	public Object visitFnctcall(FnctcallContext ctx) {
		return visitChildren(ctx) + "invokestatic " + "Fref" + "." + ctx.functionname.getText() + "(I)V\n";
	}
	
	@Override
	public Object visitFnctn(FnctnContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitFuncCall(FuncCallContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Object visitFunctionParameter(FunctionParameterContext ctx) {
		if(ctx.parent.getText().charAt(0) != ('#'))
			return "iload " + variables.get(ctx.name.getText()) + "\n";
		else{
			variables.put(ctx.name.getText(), variables.size());
			return "istore " + variables.get(ctx.name.getText()) + "\n";
		}
	}
	
	@Override
	public Object visitIfclause(IfclauseContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitIfelseclause(IfelseclauseContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitMultDivision(MultDivisionContext ctx) {
		
		if (ctx.operator.getText().equals("*"))
		{
			return visitChildren(ctx) + "imul\n";
		}
		else
		{
			return visitChildren(ctx) + "idiv\n";
		}
			
	}
	
	@Override
	public Object visitNumber(NumberContext ctx) {
		return "ldc " + ctx.getText() + "\n";
	}
	
	@Override
	public Object visitRelCond(RelCondContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitStart(StartContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitSubtraction(SubtractionContext ctx) {
		return visitChildren(ctx) + "isub\n";
	}

	@Override
	public Object visitVariable(VariableContext ctx) {
		return "iload " + variables.get(ctx.getText()) + "\n";
	}
	
	@Override
	protected Object aggregateResult(Object aggregate, Object nextResult) {
		if(aggregate == null){
			return nextResult;
		}
		if(nextResult == null){
			return aggregate;
		}
		return aggregate + "" + nextResult;
	}

}
