package com.dcall.core.configuration.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class StringParserUtilsTest {

    @Test
    public void should_parse_all_key_values_with_quotes_or_double_quote() {
        final char equalsToken = '=';
        final Predicate<Character> isDelimiter = d -> d == '\'' || d == '"';
        final CharSequence seq0 = " === ";
        final CharSequence seq1 = " age='3 ans' code-postal=75015 toto \"\'\"\'region=\" Ile de France \" asd = ";
        final CharSequence seq2 = " age  =  '3 ans' code-postal  =  75015 'autre code postal'  =  94000 toto \"\'\"\'region=\" Ile de France \" ";
        final CharSequence seq3 = " 'age'  =  '3 ans'  \"ville\"   =  'Jungle-city'  ' name '=Jojo";

        final List<String> list0 = StringParserUtils.parseKeyValueToList(seq0, 0, seq0.length(), equalsToken, isDelimiter);
        final List<String> list1 = StringParserUtils.parseKeyValueToList(seq1, 0, seq1.length(), equalsToken, isDelimiter);
        final List<String> list2 = StringParserUtils.parseKeyValueToList(seq2, 0, seq2.length(), equalsToken, isDelimiter);
        final List<String> list3 = StringParserUtils.parseKeyValueToList(seq3, 0, seq3.length(), equalsToken, isDelimiter);
        final Map<String, String> map0 = StringParserUtils.parseKeyValueToMap(seq0, 0, seq0.length(), equalsToken, isDelimiter);
        final Map<String, String> map1 = StringParserUtils.parseKeyValueToMap(seq1, 0, seq1.length(), equalsToken, isDelimiter);
        final Map<String, String> map2 = StringParserUtils.parseKeyValueToMap(seq2, 0, seq2.length(), equalsToken, isDelimiter);
        final Map<String, String> map3 = StringParserUtils.parseKeyValueToMap(seq3, 0, seq3.length(), equalsToken, isDelimiter);


        // list 0
        Assert.assertEquals(1, list0.size());
        Assert.assertEquals("===", list0.get(0));

        Assert.assertEquals(1, map0.size());
        Assert.assertEquals("==", map0.get(""));

        // list 1
        Assert.assertEquals(3, list1.size());
        Assert.assertEquals("age=3 ans", list1.get(0));
        Assert.assertEquals("code-postal=75015", list1.get(1));
        Assert.assertEquals("toto \"'\"'region= Ile de France ", list1.get(2));

        Assert.assertEquals(3, map1.size());
        Assert.assertEquals("3 ans", map1.get("age"));
        Assert.assertEquals("75015", map1.get("code-postal"));
        Assert.assertEquals(" Ile de France ", map1.get("toto \"'\"'region"));

        // list 2
        Assert.assertEquals(4, list2.size());
        Assert.assertEquals("age  =3 ans", list2.get(0));
        Assert.assertEquals("code-postal  =75015", list2.get(1));
        Assert.assertEquals("autre code postal=94000", list2.get(2));
        Assert.assertEquals("toto \"'\"'region= Ile de France ", list2.get(3));

        Assert.assertEquals(4, map2.size());
        Assert.assertEquals("3 ans", map2.get("age  "));
        Assert.assertEquals("75015", map2.get("code-postal  "));
        Assert.assertEquals("94000", map2.get("autre code postal"));
        Assert.assertEquals(" Ile de France ", map2.get("toto \"'\"'region"));

        // list 3
        Assert.assertEquals(3, list3.size());
        Assert.assertEquals("age=3 ans", list3.get(0));
        Assert.assertEquals("ville=Jungle-city", list3.get(1));
        Assert.assertEquals(" name =Jojo", list3.get(2));

        Assert.assertEquals(3, map3.size());
        Assert.assertEquals("3 ans", map3.get("age"));
        Assert.assertEquals("Jojo", map3.get(" name "));
        Assert.assertEquals("Jungle-city", map3.get("ville"));

    }

    @Test
    public void should_parse_words_with_quote_or_double_quote() {
        final String seq0 = "   \"first word\"  ' second word ' third   word  ";
        final Predicate<Character> isDelimiter = d -> d == '\'' || d == '"';
        final List<String> list0 = StringParserUtils.parseWordToList(seq0, 0, seq0.length(), isDelimiter);

        Assert.assertEquals(4, list0.size());
        Assert.assertEquals("first word", list0.get(0));
        Assert.assertEquals(" second word ", list0.get(1));
        Assert.assertEquals("third", list0.get(2));
        Assert.assertEquals("word", list0.get(3));
    }

}
