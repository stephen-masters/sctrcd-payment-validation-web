package com.sctrcd.beans;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BeanMatcherTest {

    // We use the concrete implementation because we wish to test some of the
    // protected methods, which are not available on the public interface.
    BeanMatcher matcher = new BeanMatcher();

    @Test
    public void shouldLowercaseFirstLetter() {
        assertEquals("camelCase", matcher.lowercaseFirstLetter("CamelCase"));
        assertEquals("camelCase", matcher.lowercaseFirstLetter("camelCase"));
        assertEquals("", matcher.lowercaseFirstLetter(""));
        assertEquals(null, matcher.lowercaseFirstLetter(null));
    }

    @Test
    public void shouldMatchAnythingWithEmptyFilter() {
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {}),
                "An empty filter should always return a match.");
    }

    @Test
    public void shouldMatchIfAllFilterPropertesInBean() {
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"foo=foo", "bar=bar"}),
                "Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.");
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"bar=bar"}),
                "Expected 'getBar' to return 'bar' and to be sufficient for a match.");
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {}),
                "An empty filter should always return true.");
    }

    @Test
    public void shouldNotMatchIfFilterPropertyNotInBean() {
        assertFalse(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"uncle=bob"}),
                "There is no bean property 'uncle', so this should not match.");
    }

    @Test
    public void shouldNotMatchIfFilterPropertyNotEqualToBeanProperty() {
        assertFalse(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"foo=bar"}),
                "Bean property 'foo' has value 'foo', not 'bar'.");
    }

    @Test
    public void shouldMatchPropertiesUsingVarArgs() {
        assertTrue(
                matcher.matches(
                        new StubBean("foo", "bar"),
                        new BeanPropertyFilter("foo", "foo"),
                        new BeanPropertyFilter("bar", "bar")),
                "Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.");
        assertTrue(
                matcher.matches(
                        new IntegerBean(Integer.valueOf(3)), new BeanPropertyFilter("prop", Integer.valueOf(3))),
                "Expected 'getBar' to return '3' and to be sufficient for a match.");
        assertFalse(
                matcher.matches(
                        new IntegerBean(Integer.valueOf(3)), new BeanPropertyFilter("prop", Integer.valueOf(4))),
                "Expected 'getBar' to return '3' and to not match 4.");
    }

    @Test
    public void shouldCompareObjects() {
        String o1 = "hello";
        String o2 = "hello";
        assertTrue(matcher.isEquivalent(o1, o2));

        Boolean o3 = Boolean.FALSE;
        boolean o4 = false;
        assertTrue(matcher.isEquivalent(o3, o4));
        o4 = true;
        assertFalse(matcher.isEquivalent(o3, o4));
    }

    public class StubBean {
        private String foo, bar;

        public StubBean(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }

    public class IntegerBean {
        private Integer prop = null;

        public IntegerBean(Integer prop) {
            this.prop = prop;
        }

        public Integer getProp() {
            return prop;
        }

        public void setProp(Integer prop) {
            this.prop = prop;
        }
    }
}
