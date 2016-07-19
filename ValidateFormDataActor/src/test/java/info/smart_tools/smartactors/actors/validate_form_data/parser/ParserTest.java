package info.smart_tools.smartactors.actors.validate_form_data.parser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserTest {
    @Test
    public void shouldRightValidateCompulsoryField() throws Exception {
        String rules = "обязательное";
        assertTrue(new Parser(rules, "qwerty").validate());
        assertFalse(new Parser(rules, "").validate());
    }

    @Test
    public void shouldRightValidateRussianField() throws Exception {
        String rules = "русский";
        assertTrue(new Parser(rules, "йцукен").validate());
        assertFalse(new Parser(rules, "qwerty").validate());
    }

    @Test
    public void shouldRightValidateAndTerms() throws Exception {
        String rules = "русский && обязательное";
        assertFalse(new Parser(rules, "").validate());
        assertFalse(new Parser(rules, "qwerty").validate());
        assertTrue(new Parser(rules, "йцукен").validate());
    }


}
