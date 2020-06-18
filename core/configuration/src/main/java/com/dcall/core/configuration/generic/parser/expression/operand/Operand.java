package com.dcall.core.configuration.generic.parser.expression.operand;

import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.ExpressionType;

import java.util.function.Function;

public class Operand<T> extends Expression {
    private T value;
    private Function<CharSequence, T> mutator;

    public Operand() { super(ExpressionType.OPERAND); }
    public Operand(final T value) { super(ExpressionType.OPERAND); this.value = value; }

    public Operand(final T value, final Function<CharSequence, T> mutator) {
        super(ExpressionType.OPERAND);
        this.value = value;
        this.mutator = mutator;
    }

    public T mutate(final CharSequence seq) {
        if (mutator != null && seq != null && seq.length() > 0)
            this.value = mutator.apply(seq.toString());

        return this.value;
    }

    // getter
    public T getValue() { return this.value; }
    public Function<CharSequence, T> getMutator() { return mutator; }

    // setter
    public Operand<T> setValue(final T value) { this.value = value; return this; }
    public Operand<T> setMutator(final Function<CharSequence, T> mutator) { this.mutator = mutator; return this; }
}
