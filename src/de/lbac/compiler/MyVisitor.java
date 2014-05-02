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
import de.lbac.parser.frefParser.RetValueContext;
import de.lbac.parser.frefParser.StartContext;
import de.lbac.parser.frefParser.SubtractionContext;
import de.lbac.parser.frefParser.VariableContext;

import java.util.HashMap;
import java.util.Map;
 
public class MyVisitor extends frefBaseVisitor<Object> {
	
	String currentFunction = "";
	Map<String, Integer> variables = new HashMap<String, Integer>();   //Variablen Map
	Map<String, Map<String, Integer>> functions = new HashMap<String, Map<String, Integer>>();   //Funktionsmap
	int labelCounter;
	int maxStack;
	int stackSize;
	
	public MyVisitor(){
		labelCounter = 0;
		stackSize = 0;
		maxStack = 0;
	}
	
	
	@Override
	public Object visitAddition(AdditionContext ctx) {
		return visitChildren(ctx) + "        #?#iadd\n";
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
		return visitChildren(ctx) + "        #?#istore " + variables.get(ctx.name.getText()) + "\n";
	}
	
	@Override
	public Object visitDefinition(DefinitionContext ctx) {
		if(variables.get(ctx.name.getText()) != null)
			return visitChildren(ctx) + "        #?#istore " + variables.get(ctx.name.getText()) + "\n";
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
		String ret = visitChildren(ctx) + "        invokestatic" + " Fref" + "." + ctx.functionname.getText().toLowerCase() + "(";
		if(ctx.fp.getText().equals("Void")){
			ret += "V)";
		} else {
			ret += "I)#?#";
		}
		if(ctx.getParent().getParent().getClass().equals(DefinitionContext.class)||ctx.getParent().getParent().getClass().equals(DeclarationDefinitionContext.class)){
			if (stackSize>maxStack)
				maxStack = stackSize;
			ret += "I#!#\n";
		} else {
			ret += "V\n";
		}
		return ret;
	}
	
	@Override
	public Object visitFnctn(FnctnContext ctx) {
		stackSize = 0;
		maxStack = 0;
		if (!currentFunction.equals(""))
			functions.put(currentFunction, variables);
		currentFunction = ctx.functionname.getText().toLowerCase();
		variables = new HashMap<String, Integer>();
		
		
		String ret = ".method public static ";
		String code = "";
		ret += currentFunction + "(";
		if(currentFunction.equals("main")){
			ret += "[Ljava/lang/String;";
		} else if (!ctx.fp.isEmpty()){
			ret += "I";
		}
		ret += ")";
		if (ctx.ret.getText().equals("Number")){
			ret += "I\n";
		} else {
			ret += "V\n";
		}
		code += visitFunctionParameter((FunctionParameterContext) ctx.fp);
		code += visitCode(ctx.funcode);
		
		if (ctx.ret.getText().equals("Number")){
			code += visitRetValue(ctx.retValue());
		} else {
			code += "        return\n";
		}
		code = calcStack(code);
		ret += "\n" +
				"	.limit stack " + maxStack + "\n" +
				"	.limit locals " + variables.size() + "\n";
		ret += code + ".end method\n";
		return ret;
	}
	
	private String calcStack(String code) {
		String[] parts = code.split("#");
		String c = "";
		int i =0;
		while (i<parts.length){
			if (parts[i].equals("!")){
				stackSize++;
				if (stackSize>maxStack)
					maxStack = stackSize;
			} else if (parts[i].equals("?")){
				stackSize--;
			} else {
				c += parts[i];
			}
			i++;
		}
		return c;
	}


	@Override
	public Object visitFuncCall(FuncCallContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Object visitFunctionParameter(FunctionParameterContext ctx) {
		if(ctx.parent.getText().charAt(0) != ('#')){
			if (stackSize>maxStack)
				maxStack = stackSize;
			return "        #!#iload " + variables.get(ctx.name.getText()) + "\n";
		}else{
			variables.put(ctx.name.getText(), variables.size());
			return "";
		}
	}
	
	@Override
	public Object visitIfclause(IfclauseContext ctx) {
		return visit(ctx.cond) + "lb" + labelCounter++ + "\n        goto lb" + labelCounter + "\nlb" + (labelCounter-1) + ":\n" + visit(ctx.ifcode) + "lb" + labelCounter++ +":\n";
	}														//[code]
															//goto Label2
															//Label1: [code]
															//Label2: ...	
	@Override
	public Object visitIfelseclause(IfelseclauseContext ctx) {
		return visit(ctx.cond) + "lb" + labelCounter++ + "\n" + visit(ctx.elsecode) + "        goto lb" + labelCounter + "\nlb" + (labelCounter-1) + ":\n" + visit(ctx.ifcode) + "lb" + labelCounter++ +":\n";
	}
	
	@Override
	public Object visitMultDivision(MultDivisionContext ctx) {
		
		if (ctx.operator.getText().equals("*"))
		{
			return visitChildren(ctx) + "        #?#imul\n";
		}
		else
		{
			return visitChildren(ctx) + "        #?#idiv\n";
		}
			
	}
	
	@Override
	public Object visitNumber(NumberContext ctx) {
		return "        #!#ldc " + ctx.getText() + "\n";
	}
	
	@Override
	public Object visitEQCond(EQCondContext ctx) {
		return visitChildren(ctx) + "        #?#isub\n        ifeq ";
	}
	
	@Override
	public Object visitGTCond(GTCondContext ctx) {
		return visitChildren(ctx) + "        #?#isub\n        ifgt ";
	}
	
	@Override
	public Object visitLTCond(LTCondContext ctx) {
		return visitChildren(ctx) + "        #?#isub\n        iflt ";
	}
	
	@Override
	public Object visitStart(StartContext ctx) {
		return visitChildren(ctx);
	}
	
	@Override
	public Object visitSubtraction(SubtractionContext ctx) {
		return visitChildren(ctx) + "        #?#isub\n";
	}
	
	@Override
	public Object visitRetValue(RetValueContext ctx) {
		return "        #!#iload " + variables.get(ctx.name.getText()) + "\n        ireturn\n";
	}
	
	@Override
	public Object visitVariable(VariableContext ctx) {
		return "        #!#iload " + variables.get(ctx.getText()) + "\n";
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
