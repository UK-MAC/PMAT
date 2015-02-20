package uk.co.awe.pmat;

import java.util.Collection;
import java.util.Iterator;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author AWE Plc copyright 2013
 */
public final class TestUtils {

    private TestUtils() { }

    private static final class StartsWith <T> extends BaseMatcher<T> {

        private final Collection<?> collection;

        StartsWith(Collection collection) {
            this.collection = collection;
        }

        @Override
        public boolean matches(Object o) {
            if (!(collection instanceof Collection)) {
                return false;
            }
            if (!(o instanceof Collection)) {
                return false;
            }
            final Collection<?> coll = (Collection<?>) o;
            if (coll.size() < collection.size()) {
                return false;
            }
            final Iterator<?> iter = coll.iterator();
            for (Object el : collection) {
                if (!el.equals(iter.next())) { return false; }
            }
            return true;
        }

        @Override
        public void describeTo(Description d) {
            d.appendText("<[");
            String delim = "";
            for (Object el : collection) {
                d.appendText(delim + el.toString());
                delim = ", ";
            }
            d.appendText(", ...]>");
        }

    }

    public static <T extends Collection> Matcher<T> startsWith(T collection) {
        return new StartsWith<T>(collection);
    }

}
