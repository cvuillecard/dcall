package com.dcall.core.configuration.utils.parser.expression;

public abstract class Expression {
    final ExpressionType type;

    protected Expression(ExpressionType type) { this.type = type; }

    public ExpressionType getType() { return type; }
}
