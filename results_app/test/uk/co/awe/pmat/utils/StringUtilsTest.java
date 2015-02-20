package uk.co.awe.pmat.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class StringUtilsTest {

    public StringUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void repeat_char_should_return_a_string_containing_the_char_repeated_n_times() {
        String expString = "          ";
        String retString = StringUtils.repeatChar(' ', 10);
        
        assertThat(retString, equalTo(expString));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void repeat_char_should_throw_an_exception_when_given_negative_n() {
        StringUtils.repeatChar(' ', -10);
    }

    @Test
    public void joinshould_return_a_string_containing_each_given_string_seperated_by_the_deliminator() {

        String[] words = new String[] { "a", "collection", "of", "words" };
        String delim = " ";
        String expString = "a collection of words";

        assertThat(StringUtils.joinStrings(java.util.Arrays.asList(words), delim), equalTo(expString));

        delim = ", ";
        expString = "a, collection, of, words";

        assertThat(StringUtils.joinStrings(java.util.Arrays.asList(words), delim), equalTo(expString));

    }

    @Test
    public void capitaliseWords_should_capitalise_the_first_letter_of_each_word() {

        String word = "test";
        String expWord = "Test";

        assertThat(StringUtils.capitaliseWords(word), equalTo(expWord));

        String sentence = "a test sentence";
        String expSentence = "A Test Sentence";

        assertThat(StringUtils.capitaliseWords(sentence), equalTo(expSentence));
    }

    @Test
    public void capitaliseWords_should_do_nothing_to_the_empty_string() {

        String word = "";
        String expWord = "";

        assertThat(StringUtils.capitaliseWords(word), equalTo(expWord));
    }

    @Test
    public void normalise_camel_case_should_captilise_the_first_letter_of_a_word() {
        String word = "testing";
        String expWord = "Testing";
        assertThat(StringUtils.normaliseCamelCase(word), equalTo(expWord));
    }

    @Test
    public void normalise_camel_case_should_seperate_camel_case_with_spaces_and_capitalise() {
        String word = "testingWord";
        String expWord = "Testing Word";
        assertThat(StringUtils.normaliseCamelCase(word), equalTo(expWord));
    }

    @Test
    public void normalise_camel_case_should_do_nothing_to_an_already_normalised_word() {
        String word = "Testing Words";
        String expWord = "Testing Words";
        assertThat(StringUtils.normaliseCamelCase(word), equalTo(expWord));
    }

    @Test
    public void normalise_camel_case_should_not_seperate_a_word_which_is_all_caps() {
        String word = "WORD";
        String expWord = "WORD";
        assertThat(StringUtils.normaliseCamelCase(word), equalTo(expWord));
    }

    @Test
    public void normalise_camel_case_should_normalise_each_word_in_a_list() {
        String[] word = {"testing", "testingWord", "WORD"};
        String[] expWord = {"Testing", "Testing Word", "WORD"};
        assertThat(StringUtils.normaliseCamelCase(java.util.Arrays.asList(word)), equalTo(java.util.Arrays.asList(expWord)));
    }
    
    @Test
    public void the_closeness_of_two_identical_words_should_be_the_word_length() {
        String wordA = "test";
        String wordB = "test";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(wordA.replace(" ", "").length()));
        
        wordA = "another test word";
        wordB = "another test word";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(wordA.replace(" ", "").length()));
    }
    
    @Test
    public void the_closeness_of_two_completely_distinct_words_should_be_zero() {
        String wordA = "test";
        String wordB = "false";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(0));
        
        wordA = "some random sentence";
        wordB = "here be dragons";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(0));
    }
    
    @Test
    public void the_closeness_of_two_words_should_be_a_count_of_the_number_of_matching_letters() {
        String wordA = "test-A";
        String wordB = "test-B";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(4));
        
        wordA = "testing A";
        wordB = "test A";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(5));
        
        wordA = "A Test XYZ A";
        wordB = "testing A";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(6));
    }
    
    @Test
    public void the_closeness_of_two_words_should_be_commutative() {
        String wordA = "testing 123";
        String wordB = "a test";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(StringUtils.closeness(wordB, wordA)));
        
        wordA = "name-test-A";
        wordB = "n-test-B";
        
        assertThat(StringUtils.closeness(wordA, wordB), equalTo(StringUtils.closeness(wordB, wordA)));
    }

}