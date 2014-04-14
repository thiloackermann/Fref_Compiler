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
import de.lbac.parser.frefParser.EQCondContext;
import de.lbac.parser.frefParser.EmptyContext;
import de.lbac.parser.frefParser.ExpressionWithoutStatementContext;
import de.lbac.parser.frefParser.FnctcallContext;
import de.lbac.parser.frefParser.FnctnContext;
import de.lbac.parser.frefParser.FuncCallContext;
import de.lbac.parser.frefParser.FunctionParameterContext;
import de.lbac.parser.frefParser.GTCondContext;
import de.lbac.parser.frefParser.IfclauseContext;
import de.lbac.parser.frefParser.IfelseclauseContext;
import de.lbac.parser.frefParser.LTCondContext;
import de.lbac.parser.frefParser.MultDivisionContext;
import de.lbac.parser.frefParser.NumberContext;
import de.lbac.parser.frefParser.StartContext;
import de.lbac.parser.frefParser.SubtractionContext;
import de.lbac.parser.frefParser.VariableContext;

import java.util.HashMap;
import java.util.Map;
 
public class MyVisitor extends frefBaseVisitor<Object> {
	
	Map<String, Integer> variables = new HashMap<String, Integer>();   //Variablen Map
	Map<String, HashMap<String, Integer>> functions = new HashMap<String, HashMap<String, Integer>>();   //Funktionsmap
	int labelCounter;
	
	public MyVisitor(){
		labelCounter = 0;
	}
	
	
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
		if(ctx.getText().equals("==")){
			return "ifeq ";
		}
		
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
		return "lb" + labelCounter + ":\n" + visit(ctx.docode) + visit(ctx.cond) + "lb" + labelCounter++ + "\n";
		//label: docode
		//W1
		//W2
		//IFEQ <label>
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
		String callType;
		if (ctx.functionname.getText().equals("Out")){
			callType = "invokestatic";
		} else {
			callType = "invokevirtual";
		}
		return visitChildren(ctx) + callType + " Fref" + "." + ctx.functionname.getText() + "(I)V\n";
	}
	
	@Override
	public Object visitFnctn(FnctnContext ctx) {
		String ret = ".method public " + ctx.functionname + "(";
		if(!ctx.fp.isEmpty()){
			ret += "I";
		}
		ret += ")";
		if (ctx.ret.getText().equals("String")){
			ret += "Ljava/lang/String\n";
		} else if (ctx.ret.getText().equals("Number")){
			ret += "I\n";
		} else {
			ret += "V\n";
		}
		ret += "\n" +
				"	.limit stack 20\n" +
				"	.limit locals 20\n";
		visitCode(ctx.funcode);
		if (ctx.ret.getText().equals("String")){
			//ret += STRING RETURN;
			ret += "return\n";
		} else if (ctx.ret.getText().equals("Number")){
			ret += "ireturn\n";
		} else {
			ret += "return\n";
		}
		return ret;
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
		return visit(ctx.cond) + "lb" + labelCounter++ + "\ngoto lb" + labelCounter + "\nlb" + (labelCounter-1) + ":\n" + visit(ctx.ifcode) + "lb" + labelCounter++ +":\n";
	}														//[code]
															//goto Label2
															//Label1: [code]
															//Label2: ...	
	@Override
	public Object visitIfelseclause(IfelseclauseContext ctx) {
		return visit(ctx.cond) + "lb" + labelCounter++ + "\n" + visit(ctx.elsecode) + "goto lb" + labelCounter + "\nlb" + (labelCounter-1) + ":\n" + visit(ctx.ifcode) + "lb" + labelCounter++ +":\n";
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
	public Object visitEQCond(EQCondContext ctx) {
		return visitChildren(ctx) + "isub\nifeq ";
	}
	
	@Override
	public Object visitGTCond(GTCondContext ctx) {
		return visitChildren(ctx) + "isub\nifgt ";
	}
	
	@Override
	public Object visitLTCond(LTCondContext ctx) {
		return visitChildren(ctx) + "isub\niflt ";
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
