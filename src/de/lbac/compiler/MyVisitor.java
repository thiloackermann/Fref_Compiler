package de.lbac.compiler;

import de.lbac.parser.frefBaseVisitor;

public class MyVisitor extends frefBaseVisitor {

	@Override
	protected Object aggregateResult(Object aggregate, Object nextResult) {
		if(aggregate == null){
			return nextResult;
		}
		if(nextResult == null){
			return aggregate;
		}
		return aggregate + "\n" + nextResult;
	}

}
