package com.sctrcd.beans;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sctrcd.beans.BeanMatcher;
import com.sctrcd.beans.BeanPropertyFilter;


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
        assertTrue("An empty filter should always return a match.",
        		matcher.matches(new StubBean("foo", "bar"), new String[] {}));
    }
    
    @Test
    public void shouldMatchIfAllFilterPropertesInBean() {
        assertTrue("Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.", 
        		matcher.matches(new StubBean("foo", "bar"), new String[] {"foo=foo", "bar=bar"}));
        assertTrue("Expected 'getBar' to return 'bar' and to be sufficient for a match.",
        		matcher.matches(new StubBean("foo", "bar"), new String[] {"bar=bar"}));
        assertTrue("An empty filter should always return true.",
        		matcher.matches(new StubBean("foo", "bar"), new String[] {}));
    }
    
    @Test
    public void shouldNotMatchIfFilterPropertyNotInBean() {
        assertFalse("There is no bean property 'uncle', so this should not match.",
        		matcher.matches(new StubBean("foo", "bar"), new String[] {"uncle=bob"}));
    }
    
    @Test
    public void shouldNotMatchIfFilterPropertyNotEqualToBeanProperty() {
        assertFalse("Bean property 'foo' has value 'foo', not 'bar'.",
        		matcher.matches(new StubBean("foo", "bar"), new String[] {"foo=bar"}));
    }
	
	@Test
	public void shouldMatchPropertiesUsingVarArgs() {
        assertTrue("Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.", 
        		matcher.matches(new StubBean("foo", "bar"), new BeanPropertyFilter("foo", "foo"), new BeanPropertyFilter("bar", "bar") ));
        assertTrue("Expected 'getBar' to return '3' and to be sufficient for a match.",
        		matcher.matches(new IntegerBean(new Integer(3)), new BeanPropertyFilter("prop", new Integer(3))));
        assertFalse("Expected 'getBar' to return '3' and to not match 4.",
        		matcher.matches(new IntegerBean(new Integer(3)), new BeanPropertyFilter("prop", new Integer(4))));
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
            this.foo = foo; this.bar = bar;
        }
        public String getFoo() { return foo; }
        public String getBar() { return bar; }
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
