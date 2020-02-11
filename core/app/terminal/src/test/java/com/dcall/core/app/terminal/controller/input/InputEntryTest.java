package com.dcall.core.app.terminal.controller.input;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class InputEntryTest {
    private static final char[] s1 = " Sed non risus.".toCharArray();
    private static final char[] s2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.".toCharArray();
    private static final char[] s3 = " Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi.".toCharArray();
    private static final char[] s4 = " Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat. Duis semper. Duis arcu massa, scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue. Ut in risus volutpat libero pharetra tempor. Cras vestibulum bibendum augue. Praesent egestas leo in pede. Praesent blandit odio eu enim. Pellentesque sed dui ut augue blandit sodales. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aliquam nibh. Mauris ac mauris sed pede pellentesque fermentum. Maecenas adipiscing ante non diam sodales hendrerit.".toCharArray();

    private static final InputEntry entry = new InputEntry();

    @Before
    public void init() {
        entry.reset();
    }

    /** UTILS **/
    private static void addtoEntry(final char[] in) {
        for (int i = 0; i < in.length; i++) entry.add(in[i]);
    }

    /** InputEntry::nbLine() **/
    @Test
    public void should_init_with_non_empty_buffer_nbLine() {
        // when
        InputEntryTest.addtoEntry(s1);
        // then
        Assert.assertEquals(1, entry.nbLine());
    }

    /** InputEntry::add() **/
    @Test
    public void should_add_s1_in_buffer_add() {
        // when
        InputEntryTest.addtoEntry(s1);

        // then
        Assert.assertEquals(String.valueOf(s1), entry.toString());

        Assert.assertEquals(s1.length, entry.posX());
        Assert.assertEquals(0, entry.posY());

        // when
        InputEntryTest.addtoEntry(s2);

        // then
        Assert.assertEquals(String.valueOf(s1) + String.valueOf(s2), entry.toString());
        Assert.assertEquals(s1.length + s2.length, entry.posX());

        Assert.assertEquals(0, entry.posY());
        Assert.assertTrue(entry.getBuffer().get(entry.posY()).size() < TermAttributes.getMaxLineWidth());
    }

    @Test
    public void should_add_new_line_when_current_line_buffer_size_exceed_max_line_width_add() {
        final String finalString = String.valueOf(s1) + String.valueOf(s2) + String.valueOf(s3);

        // when
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        int nbLines = finalString.length() / TermAttributes.getTotalLineWidth();
        int rest = finalString.length() % TermAttributes.getTotalLineWidth();
        int totalLines = nbLines + (rest > 0 ? 1 : 0);

        // then
        Assert.assertEquals(finalString, entry.toString());
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(totalLines - 1, entry.posY());
        Assert.assertEquals(totalLines, entry.nbLine());

        // verify
        Assert.assertEquals(finalString.length(), ((entry.nbLine() - 1) * TermAttributes.getTotalLineWidth()) + rest);

        IntStream.range(0, nbLines).forEach(noLine -> Assert.assertEquals(TermAttributes.getTotalLineWidth(), entry.getBuffer().get(noLine).size()));
        Assert.assertEquals(rest, entry.getBuffer().get(entry.nbLine() - 1).size());
    }

    /** InputEntry::remove() **/
    @Test
    public void should_remove_last_element_in_line_remove() {
        final String finalString = String.valueOf(s1);

        addtoEntry(s1);

        // when
        entry.remove();
        // then
        Assert.assertEquals(finalString.length() - 1, entry.getBuffer().get(entry.posY()).size());

        //verify
        Assert.assertEquals(finalString.substring(0, finalString.length() - 1), entry.toString());

        // when
        entry.add('.');

        // then
        Assert.assertEquals(finalString.length(), entry.getBuffer().get(entry.posY()).size());
        Assert.assertEquals(finalString, entry.toString());
    }

    @Test
    public void should_not_remove_when_buffer_line_empty_remove() {
        addtoEntry(s1);

        // when
        IntStream.range(0, s1.length).forEach(i -> entry.remove());

        // then
        Assert.assertEquals(0, entry.getBuffer().get(entry.posY()).size());

        // when
        entry.remove();
        entry.remove();
        entry.remove();

        // then
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(0, entry.getBuffer().get(entry.posY()).size());
        Assert.assertEquals(1, entry.nbLine());
    }

    @Test
    public void should_clear_last_line_when_remove_completly_last_line_remove() {
        final String str = String.valueOf(s3);
        final int nbLinesBeforeRemove = str.length() / TermAttributes.getTotalLineWidth();
        final int restBeforeRemove = str.length() % TermAttributes.getTotalLineWidth();
        final int totalNbLinesBeforeRemove = nbLinesBeforeRemove + (restBeforeRemove > 0 ? 1 : 0);

        final String stringAfterRemove = str.substring(0, TermAttributes.getTotalLineWidth() * nbLinesBeforeRemove);
        final int nbLinesAfterRemove = stringAfterRemove.length() / TermAttributes.getTotalLineWidth();
        final int restAfterRemove = stringAfterRemove.length() % TermAttributes.getTotalLineWidth();
        final int totalNbLinesAfterRemove = nbLinesAfterRemove + (restAfterRemove > 0 ? 1 : 0);

        // when : first we fill entry buffer
        addtoEntry(s3);
        // then : we check what we have for next test
        Assert.assertEquals(totalNbLinesBeforeRemove, entry.nbLine());
        Assert.assertEquals(restBeforeRemove, entry.posX());
        Assert.assertEquals(entry.nbLine() - 1, entry.posY());

        // when : we delete last line (3 lines at this point)
        IntStream.range(0, restBeforeRemove).forEach(i -> entry.remove()); // remove 24 elements (last line = 24)

        // then : we must be on the third line (3) but the line must be empty
        Assert.assertEquals(totalNbLinesBeforeRemove, entry.nbLine()); // last line is cleared but not deleted.
        Assert.assertEquals(0, entry.getBuffer().get(entry.posY()).size()); // line is cleared
        Assert.assertEquals(0, entry.posX()); // and we are on its first index

        // when : Now we remove the last element of the previous line (line 2), this must cause the current line's deletion (line 3)
        entry.remove();

        // then : We check if the operation is ok
        Assert.assertEquals(totalNbLinesBeforeRemove - 1, entry.nbLine()); // last line now has been deleted
        Assert.assertEquals(totalNbLinesAfterRemove, entry.nbLine());
        Assert.assertEquals(totalNbLinesAfterRemove - 1, entry.posY()); // no rest, now we are on last line
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.posX()); // we are on the last index of previous line before deletion
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.getBuffer().get(entry.posY()).size());

        // when : Now we remove the second line (2) plus one element to be sure that the line is deleted
        IntStream.range(0, TermAttributes.getTotalLineWidth()).forEach(i -> entry.remove()); // removing MaxLineWidth elements + 1
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.getBuffer().get(entry.posY()).size());
        Assert.assertEquals(1, entry.nbLine());
//        entry.remove();
//        entry.remove();
//
//        // then
//        Assert.assertEquals(0, entry.posX());
//        Assert.assertEquals(0, entry.posY());
//        Assert.assertEquals(0, entry.getBuffer().get(entry.posY()).size());
//        Assert.assertEquals(1, entry.nbLine());
    }
}
