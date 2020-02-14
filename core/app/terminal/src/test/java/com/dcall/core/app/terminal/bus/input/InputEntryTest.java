package com.dcall.core.app.terminal.bus.input;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class InputEntryTest {
    private static final char[] s1 = " Sed non risus.".toCharArray();
    private static final char[] s2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.".toCharArray();
    private static final char[] s3 = " Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi.".toCharArray();
    private static final char[] s4 = " Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat. Duis semper. Duis arcu massa, scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue. Ut in risus volutpat libero pharetra tempor. Cras vestibulum bibendum augue. Praesent egestas leo in pede. Praesent blandit odio eu enim. Pellentesque sed dui ut augue blandit sodales. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aliquam nibh. Mauris ac mauris sed pede pellentesque fermentum. Maecenas adipiscing ante non diam sodales hendrerit.".toCharArray();

    private static final InputEntry<String> entry = new InputEntry<>();

    @Before
    public void init() {
        entry.reset();
    }

    /** UTILS **/
    private static void addtoEntry(final char[] in) {
        for (int i = 0; i < in.length; i++) entry.add(String.valueOf(in[i]));
    }

    /** InputEntry::isAppend() + isValidPosition() **/
    @Test
    public void should_return_false_if_not_in_bounds_or_true_if_in_bounds_isAppend_with_isValidPosition() {
        final String finalString = String.valueOf(s1) + String.valueOf(s2) + String.valueOf(s3);
        int nbLines = finalString.length() / TermAttributes.getTotalLineWidth();
        int rest = finalString.length() % TermAttributes.getTotalLineWidth();
        int totalLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // state
        Assert.assertEquals(entry.posX(), entry.getBuffer().get(entry.maxNbLine()).size());
        Assert.assertEquals(entry.posY(), entry.maxNbLine());
        Assert.assertEquals(nbLines, entry.maxNbLine());
        Assert.assertEquals(totalLines, entry.nbLine());

        // when : set X out of bounds
        entry.setX(-1);
        entry.setY(0);

        addtoEntry(s3);

        // then : nothing append
        Assert.assertFalse(entry.isAppend());
        Assert.assertFalse(entry.isValidPosition());
        Assert.assertNotEquals(entry.posX(), entry.getBuffer().get(entry.maxNbLine()).size());
        Assert.assertNotEquals(entry.posY(), entry.maxNbLine());
        Assert.assertEquals(nbLines, entry.maxNbLine());
        Assert.assertEquals(totalLines, entry.nbLine());

        // when : set Y out of bounds
        entry.setX(0);
        entry.setY(totalLines + 1);

        addtoEntry(s3);

        // then : nothing append
        Assert.assertFalse(entry.isAppend());
        Assert.assertFalse(entry.isValidPosition());
        Assert.assertNotEquals(entry.posX(), entry.getBuffer().get(entry.maxNbLine()).size());
        Assert.assertNotEquals(entry.posY(), entry.maxNbLine());
        Assert.assertEquals(nbLines, entry.maxNbLine());
        Assert.assertEquals(totalLines, entry.nbLine());
    }

    /** InputEntry::add() + nbLine() + maxNbLine() **/
    @Test
    public void should_init_with_non_empty_buffer_add_nbLine_maxNbLine() {
        // when
        InputEntryTest.addtoEntry(s1);

        // then
        Assert.assertEquals(1, entry.nbLine());
        Assert.assertEquals(entry.getBuffer().size(), entry.nbLine());

        Assert.assertEquals(0, entry.maxNbLine());
        Assert.assertEquals(entry.getBuffer().size() - 1, entry.maxNbLine());
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

        // verify
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

    /** InputEntry::insert() **/
    @Test
    public void should_insert_element_keeping_size_of_other_line_buffer_add() {
        final String superStr = "<< oups ! une phrase s'est glissee, et en plus c'est du francais ! youpi ! >>";
        final String baseStr = String.valueOf(s1) + String.valueOf(s2) + String.valueOf(s3);
        final String newStrBetween = String.valueOf(s1) + superStr + String.valueOf(s2) + String.valueOf(s3);
        final String newStrAtEnd = newStrBetween + superStr;
        final int idxAfterS1Length = String.valueOf(s1).length();

        final int nbLinesBeforeInsert = baseStr.length() / TermAttributes.getTotalLineWidth();
        final int restBeforeInsert = baseStr.length() % TermAttributes.getTotalLineWidth();
        final int totalNbLinesBeforeInsert = nbLinesBeforeInsert + (restBeforeInsert > 0 ? 1 : 0);

        final int nbLinesAfterInsert = newStrBetween.length() / TermAttributes.getTotalLineWidth();
        final int restAfterInsert = newStrBetween.length() % TermAttributes.getTotalLineWidth();
        final int totalNbLinesAfterInsert = nbLinesAfterInsert + (restAfterInsert > 0 ? 1 : 0);

        final int nbLinesAfterInsert2 = newStrAtEnd.length() / TermAttributes.getTotalLineWidth();
        final int restAfterInsert2 = newStrAtEnd.length() % TermAttributes.getTotalLineWidth();
        final int totalNbLinesAfterInsert2 = nbLinesAfterInsert2 + (restAfterInsert2 > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // state
        Assert.assertEquals(nbLinesBeforeInsert, entry.maxNbLine());
        Assert.assertEquals(totalNbLinesBeforeInsert, entry.nbLine());
        Assert.assertEquals(baseStr, entry.toString());

        // when : we set posX at s1.length() (only for this test, cause we know s1.length() < getMaxLineWidth())
        entry.setX(idxAfterS1Length);
        entry.setY(0);


        addtoEntry(superStr.toCharArray());

        // then : superStr must have been inserted
        Assert.assertEquals(nbLinesAfterInsert, entry.maxNbLine());
        Assert.assertEquals(totalNbLinesAfterInsert, entry.nbLine());

        // then : max line buffer size must have been keeped (and the same) for each buffer line
        // before (getTotalLineWidth - 1) according getMaxLineWidth()
        IntStream.range(0, entry.maxNbLine()).forEach(y ->
                Assert.assertEquals(TermAttributes.getTotalLineWidth(), entry.getBuffer().get(y).size()));

        Assert.assertEquals(newStrBetween, entry.toString());

        // when : we try to insert at last line entry's idx to see if insert() acts like add when append another string
        entry.setX(entry.getBuffer().get(entry.maxNbLine()).size());
        entry.setY(entry.maxNbLine());

        addtoEntry(superStr.toCharArray());

        // then : we must have previous added string in line and the same new string at end of buffer entry
        Assert.assertEquals(nbLinesAfterInsert2, entry.maxNbLine());
        Assert.assertEquals(totalNbLinesAfterInsert2, entry.nbLine());

        // then : max line buffer size must have been keeped (and the same) for each buffer line
        // before (getTotalLineWidth - 1) according getMaxLineWidth()
        IntStream.range(0, entry.maxNbLine()).forEach(y ->
                Assert.assertEquals(TermAttributes.getTotalLineWidth(), entry.getBuffer().get(y).size()));

        Assert.assertEquals(newStrAtEnd, entry.toString());
    }

    /** InputEntry::remove() **/
    @Test
    public void should_remove_last_element_in_line_remove() {
        final String finalString = String.valueOf(s1);

        // init
        addtoEntry(s1);

        // when
        entry.remove();
        // then
        Assert.assertEquals(finalString.length() - 1, entry.getBuffer().get(entry.posY()).size());

        //verify
        Assert.assertEquals(finalString.substring(0, finalString.length() - 1), entry.toString());

        // when
        entry.add(String.valueOf('.'));

        // then
        Assert.assertEquals(finalString.length(), entry.getBuffer().get(entry.posY()).size());
        Assert.assertEquals(finalString, entry.toString());
    }

    @Test
    public void should_not_remove_when_buffer_line_empty_remove() {
        // init
        addtoEntry(s1);
        IntStream.range(0, s1.length).forEach(i -> entry.remove());

        // state
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(0, entry.getBuffer().get(entry.posY()).size());
        Assert.assertEquals(1, entry.nbLine());

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

        // init
        addtoEntry(s3);

        // state : we check what we have for next test
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

        // then : Now we must be on the first line at max line idx in line
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.getBuffer().get(entry.posY()).size());
        Assert.assertEquals(1, entry.nbLine());
    }

    /** InputEntry::moveAfterX() **/
    @Test
    public void should_not_move_posX_when_already_at_EOL_moveAfterX() {
        // init
        entry.add(String.valueOf('Z'));

        // state : check point - used by each test
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when
        entry.moveAfterX(0);

        // then : check point must be the result
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when
        entry.moveAfterX(-1);

        // then : check point must be the result : negative values are not valid
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when : trying to moveAt out of buffer range
        entry.moveAfterX(1);

        // then : check point must be the result
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when
        entry.moveAfterX(5);

        // then : check point must be the result
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());
    }

    @Test
    public void should_move_posX_at_new_pos_in_line_moveAfterX() {
        final char[] str = new char[]{'A', 'B', 'C', 'D', 'E'};
        final int strLength = str.length;

        // init
        addtoEntry(str);

        entry.setX(0);
        entry.setY(0);

        // state : expected -> 5 elements with 1 line with X = 0 / Y = 0
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());
        Assert.assertEquals(5, entry.getBuffer().get(entry.posY()).size());

        // when : trying to moveAt in line
        entry.moveAfterX(1);

        // then : expected -> X = 1 and Y = 0
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when : moving to last element of line
        entry.moveAfterX(3);

        // then : expected -> X = 4 last element in line
        Assert.assertEquals(4, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when : moving at futur next element position in line
        entry.moveAfterX(1);

        // then : expected to be on the next position entry in buffer line
        Assert.assertEquals(5, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when : trying to moveAt out of line bounds
        entry.moveAfterX(1);

        // then : we are already on the next position entry in line
        Assert.assertEquals(5, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());
    }

    @Test
    public void should_move_posX_to_next_line_if_exists_moveAfterX() {
        final int s1Length = s1.length; // 15
        final int s2Length = s2.length; // 56
        final int s3Length = s3.length; // 180
        final int totalLength = s1Length + s2Length + s3Length; // 251
        final int nbLines = totalLength / TermAttributes.getTotalLineWidth();
        final int rest = totalLength % TermAttributes.getTotalLineWidth();
        final int totalnbLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // for test only
        entry.setX(0);
        entry.setY(0);

        // state
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt at last element of line bounds
        entry.moveAfterX(TermAttributes.getMaxLineWidth());

        // then : We must be on the last element idx of current line
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt on the 5th element of next line
        entry.moveAfterX(5);

        // then : we must be at the 5th element idx(=4) of next line
        Assert.assertEquals(4, entry.posX());
        Assert.assertEquals(1, entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt out of entry buffer bounds
        entry.moveAfterX(totalLength);

        // then : we must be on the next element of last line
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(totalnbLines - 1, entry.posY());

        Assert.assertEquals(entry.maxNbLine(), entry.posY());
    }

    /** moveBeforeX() **/
    @Test
    public void should_not_move_posX_when_already_at_begining_of_first_line_moveBeforeX() {
        // init
        entry.add(String.valueOf('Z'));

        // state
        Assert.assertEquals(1, entry.nbLine());
        Assert.assertEquals(1, entry.getBuffer().get(entry.posY()).size());
        // check point - used by each test
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());

        // when
        entry.moveBeforeX(0);

        // then : check point must be the result
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());

        // when
        entry.moveBeforeX(15);

        // then : check point must be the result : positive values are not valid
        Assert.assertEquals(1, entry.posX());
        Assert.assertEquals(0, entry.posY());

        // when : trying to go back on first element of line
        entry.moveBeforeX(-1);

        // then : expected -> posX = 0
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());

        // when : trying to go back on first element of line
        entry.moveBeforeX(-15);

        // then : expected -> posX = 0
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
    }

    @Test
    public void should_move_posX_at_new_pos_in_line_moveBeforeX() {
        final char[] str = new char[]{'A', 'B', 'C', 'D', 'E'};

        // init
        addtoEntry(str);

        // only for test
        entry.setX(5);
        entry.setY(0);

        // state : expected -> 5 elements with 1 line with X = 5 (next idx entry) / Y = 0
        Assert.assertEquals(5, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());
        Assert.assertEquals(5, entry.getBuffer().get(entry.posY()).size());

        // when : moving to last element in line (we are on the next entry idx)
        entry.moveBeforeX(-1);

        // then : expected -> X = 4 (last element) and Y = 0
        Assert.assertEquals(4, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when : moving to first element of line
        entry.moveBeforeX(-4);

        // then : expected -> X = 0 first element in line
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());

        // when : trying to moveAt before first element of first line (out of bounds)
        entry.moveBeforeX(-1);

        // then : we are already on the first position entry in line
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(1, entry.nbLine());
    }

    @Test
    public void should_move_posX_to_previous_line_if_exists_moveBeforeX() {
        final int s1Length = s1.length; // 15
        final int s2Length = s2.length; // 56
        final int s3Length = s3.length; // 180
        final int totalLength = s1Length + s2Length + s3Length; // 251
        final int nbLines = totalLength / TermAttributes.getTotalLineWidth();
        final int rest = totalLength % TermAttributes.getTotalLineWidth();
        final int totalnbLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // state
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt at last element of line bounds
        entry.moveBeforeX(rest * -1);

        // then : We must be on the first element idx of current line (is also the last line)
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());

        // when : trying to moveAt on the 5th element of previous line from its EOL
        entry.moveBeforeX(-5);

        // then : we must be at the 5th element from EOL on previous line
        Assert.assertEquals(TermAttributes.getMaxLineWidth() - 5, entry.posX());
        Assert.assertEquals(nbLines - 1, entry.posY());

        // when : trying to moveAt out of entry buffer bounds
        entry.moveBeforeX(totalLength * -1);

        // then : we must be on the first line's first element
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
    }

    /** InputEntry::moveX() **/
    @Test
    public void should_move_posX_to_next_line_if_exists_moveX() {
        final int s1Length = s1.length; // 15
        final int s2Length = s2.length; // 56
        final int s3Length = s3.length; // 180
        final int totalLength = s1Length + s2Length + s3Length; // 251
        final int nbLines = totalLength / TermAttributes.getTotalLineWidth();
        final int rest = totalLength % TermAttributes.getTotalLineWidth();
        final int totalnbLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // for test only
        entry.setX(0);
        entry.setY(0);

        // state
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt at last element of line bounds
        entry.moveX(TermAttributes.getMaxLineWidth());

        // then : We must be on the last element idx of current line
        Assert.assertEquals(TermAttributes.getMaxLineWidth(), entry.posX());
        Assert.assertEquals(0, entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt on the 5th element of next line
        entry.moveX(5);

        // then : we must be at the 5th element idx(=4) of next line
        Assert.assertEquals(4, entry.posX());
        Assert.assertEquals(1, entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt out of entry buffer bounds
        entry.moveX(totalLength);

        // then : we must be on the next element of last line
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(totalnbLines - 1, entry.posY());

        Assert.assertEquals(entry.maxNbLine(), entry.posY());
    }

    @Test
    public void should_move_posX_to_previous_line_if_exists_moveX() {
        final int s1Length = s1.length; // 15
        final int s2Length = s2.length; // 56
        final int s3Length = s3.length; // 180
        final int totalLength = s1Length + s2Length + s3Length; // 251
        final int nbLines = totalLength / TermAttributes.getTotalLineWidth();
        final int rest = totalLength % TermAttributes.getTotalLineWidth();
        final int totalnbLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // state
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt at last element of line bounds
        entry.moveX(rest * -1);

        // then : We must be on the first element idx of current line (is also the last line)
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());

        // when : trying to moveAt on the 5th element of previous line from its EOL
        entry.moveX(-5);

        // then : we must be at the 5th element from EOL on previous line
        Assert.assertEquals(TermAttributes.getMaxLineWidth() - 5, entry.posX());
        Assert.assertEquals(nbLines - 1, entry.posY());

        // when : trying to moveAt out of entry buffer bounds
        entry.moveX(totalLength * -1);

        // then : we must be on the first line's first element
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());
    }

    @Test
    public void should_move_posX_before_or_after_moveX() {
        final int s1Length = s1.length; // 15
        final int s2Length = s2.length; // 56
        final int s3Length = s3.length; // 180
        final int totalLength = s1Length + s2Length + s3Length; // 251
        final int nbLines = totalLength / TermAttributes.getTotalLineWidth();
        final int rest = totalLength % TermAttributes.getTotalLineWidth();
        final int totalnbLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // state
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : trying to moveAt at last element of line bounds
        entry.moveX(rest * -1);

        // then : We must be on the first element idx of current line (is also the last line)
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());

        // when : trying to moveAt on the 5th element of previous line from its EOL
        entry.moveX(-5);

        // then : we must be at the 5th element from EOL on previous line
        Assert.assertEquals(TermAttributes.getMaxLineWidth() - 5, entry.posX());
        Assert.assertEquals(nbLines - 1, entry.posY());

        // when : trying to return at the last element of last line
        entry.moveX(rest + 5);

        // then : We must be on the last line's last element
        Assert.assertEquals(rest - 1, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());

        // when : trying to moveAt at next idx element entry on last line
        entry.moveX(1);

        // then : We must be at the end of last line on idx next entry
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());

        // when : trying to moveAt out of bounds before first line
        entry.moveX(totalLength * -1);

        // then : we must be on the first line's idx
        Assert.assertEquals(0, entry.posX());
        Assert.assertEquals(0, entry.posY());

        // when : trying to moveAt out of bounds after last line
        entry.moveX(totalLength);

        // then : we must be on the first line's idx
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());
    }

    /** InputEntry::moveY() **/
    @Test
    public void should_move_posY_position_before_or_after_posY_moveY() {
        final int s1Length = s1.length; // 15
        final int s2Length = s2.length; // 56
        final int s3Length = s3.length; // 180
        final int totalLength = s1Length + s2Length + s3Length; // 251
        final int nbLines = totalLength / TermAttributes.getTotalLineWidth();
        final int rest = totalLength % TermAttributes.getTotalLineWidth();
        final int totalnbLines = nbLines + (rest > 0 ? 1 : 0);

        // init
        addtoEntry(s1);
        addtoEntry(s2);
        addtoEntry(s3);

        // state : 4 lines
        Assert.assertEquals(rest, entry.posX());
        Assert.assertEquals(nbLines, entry.posY());
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());
        Assert.assertEquals(totalnbLines, entry.nbLine());

        // when : moveY(0) does nothing
        entry.moveY(0);

        // then : nothing changed
        Assert.assertEquals(nbLines, entry.posY());

        // when : moving cursor Y to last line
        entry.moveY(totalnbLines - 1); // identical to nbLines

        // then : expected - posY() = 3 -> is the last line of entry (4 lines)
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(nbLines, entry.posY());

        // when : We want to moveAt 2 lines before the last line (current line = posY())
        entry.moveY(-2);

        // then : expected - posY() = 1 ->  is the line 2
        Assert.assertEquals(totalnbLines - 3, entry.posY());
        Assert.assertEquals(entry.maxNbLine() - 2, entry.posY());

        // when : We want to moveAt 1 line after current line
        entry.moveY(1);

        // then : expected - posY() = 2 -> posY must be at line 3 (1 line from last line)
        Assert.assertEquals(totalnbLines - 2, entry.posY());
        Assert.assertEquals(entry.maxNbLine() - 1, entry.posY());

        // when : trying to go out of bounds after last line
        entry.moveY(totalnbLines);

        // then
        Assert.assertEquals(totalnbLines - 1, entry.posY());
        Assert.assertEquals(entry.maxNbLine(), entry.posY());

        // when : trying to go out of bounds before first line
        entry.moveY(totalnbLines * -1);

        // then
        Assert.assertEquals(0, entry.posY());
    }
}
