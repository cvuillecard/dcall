package com.dcall.core.configuration.generic.parser.expression.operand;

import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.ExpressionType;

public class Operand<T> extends Expression {
    private T value;

    public Operand() { super(ExpressionType.OPERAND); }
    public Operand(final T value) { super(ExpressionType.OPERAND); this.value = value; }

    // getter
    public T getValue() { return this.value; }

    // setter
    public Operand<T> setValue(final T value) { this.value = value; return this; }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
