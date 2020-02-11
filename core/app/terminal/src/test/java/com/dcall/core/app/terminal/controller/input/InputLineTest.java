package com.dcall.core.app.terminal.controller.input;

import com.dcall.core.app.terminal.controller.output.InputLine;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InputLineTest {
    private static InputLine line;
    private final String word = "Line";

    @BeforeClass
    public static void init() {
        InputLineTest.line = new InputLine()
                .add(String.valueOf('L'))
                .add(String.valueOf('i'))
                .add(String.valueOf('n'))
                .add(String.valueOf('e'));
    }

    @Test
    public void should_return_word_toString() {
        this.line.removeAt(line.size() - 1);

        Assert.assertTrue(this.line.size() < word.length());

        this.line.add(String.valueOf('e'));

        Assert.assertEquals(word.length(), this.line.size());
        Assert.assertEquals(word, line.toString());
    }

    @Test
    public void should_clear_buffer_clear() {
        Assert.assertEquals(0, this.line.clear().size());
    }
}
