/*
 * Copyright 2013-2014 Colby Skeggs
 * 
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 * 
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package ccre.testing;

import ccre.util.CArrayUtils;
import ccre.util.CList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A base class for testing the various kinds of lists in the CCRE.
 *
 * @author skeggsc
 */
public abstract class BaseTestList extends BaseTest {

    /**
     * Test if the specified CList works properly.
     *
     * @param a The CList&lt;String&gt; to test.
     * @throws TestingException If the test fails.
     */
    protected void runTest(CList<String> a) throws TestingException {
        // isEmpty
        assertTrue(a.isEmpty(), "Bad isEmpty!");
        // size
        assertEqual(a.size(), 0, "Bad size!");
        // add
        a.add("Alpha");
        assertFalse(a.isEmpty(), "Bad isEmpty!");
        assertEqual(a.size(), 1, "Bad size!");
        // add, indexed
        a.add(0, "Beta");
        try {
            a.add(-1, "Test");
            assertFail("Should have thrown IndexOutOfBoundsException!");
        } catch (IndexOutOfBoundsException e) {
            // Correct!
        }
        try {
            a.add(3, "Test");
            assertFail("Should have thrown IndexOutOfBoundsException!");
        } catch (IndexOutOfBoundsException e) {
            // Correct!
        }
        assertFalse(a.isEmpty(), "Bad isEmpty!");
        assertEqual(a.size(), 2, "Bad size!");
        // addAll
        a.addAll(CArrayUtils.asList("10", "20", "30"));
        assertFalse(a.isEmpty(), "Bad isEmpty!");
        assertEqual(a.size(), 5, "Bad size!");
        // addAll, indexed
        a.addAll(3, CArrayUtils.asList("40", "40", "40"));
        try {
            a.addAll(-1, CArrayUtils.asList("40", "40"));
            assertFail("Should have thrown IndexOutOfBoundsException!");
        } catch (IndexOutOfBoundsException e) {
            // Correct!
        }
        try {
            a.addAll(9, CArrayUtils.asList("40", "40"));
            assertFail("Should have thrown IndexOutOfBoundsException!");
        } catch (IndexOutOfBoundsException e) {
            // Correct!
        }
        // get
        assertEqual(a.get(0), "Beta", "Bad element!");
        assertEqual(a.get(1), "Alpha", "Bad element!");
        assertEqual(a.get(2), "10", "Bad element!");
        assertEqual(a.get(3), "40", "Bad element!");
        assertEqual(a.get(4), "40", "Bad element!");
        assertEqual(a.get(5), "40", "Bad element!");
        assertEqual(a.get(6), "20", "Bad element!");
        assertEqual(a.get(7), "30", "Bad element!");
        try {
            a.get(-1);
            assertFail("IndexOutOfBoundsException not thrown!");
        } catch (IndexOutOfBoundsException e) {
            // Correct!
        }
        try {
            a.get(8);
            assertFail("IndexOutOfBoundsException not thrown!");
        } catch (IndexOutOfBoundsException e) {
            // Correct!
        }
        // indexOf
        assertEqual(a.indexOf("Beta"), 0, "Bad index!");
        assertEqual(a.indexOf("Alpha"), 1, "Bad index!");
        assertEqual(a.indexOf("30"), 7, "Bad index!");
        assertEqual(a.indexOf("40"), 3, "Bad index!");
        assertEqual(a.indexOf("82"), -1, "Bad index!");
        // lastIndexOf
        assertEqual(a.lastIndexOf("Beta"), 0, "Bad index!");
        assertEqual(a.lastIndexOf("40"), 5, "Bad index!");
        assertEqual(a.lastIndexOf("30"), 7, "Bad index!");
        assertEqual(a.lastIndexOf("35"), -1, "Bad index!");
        // iterator
        Iterator<String> itr = a.iterator();
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "Beta", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "Alpha", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "10", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "40", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "40", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "40", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "20", "Bad element from iterator!");
        assertTrue(itr.hasNext(), "Bad result from iterator!");
        assertEqual(itr.next(), "30", "Bad element from iterator!");
        assertFalse(itr.hasNext(), "Bad result from iterator!");
        try {
            itr.next();
            assertFail("Should have thrown NoSuchElementException!");
        } catch (NoSuchElementException e) {
            // Correct!
        }
        // remove, indexed
        assertEqual(a.remove(1), "Alpha", "Bad remove result!");
        assertEqual(a.get(0), "Beta", "Bad element!");
        assertEqual(a.get(1), "10", "Bad element!");
        assertEqual(a.get(2), "40", "Bad element!");
        assertEqual(a.get(3), "40", "Bad element!");
        assertEqual(a.get(4), "40", "Bad element!");
        assertEqual(a.get(5), "20", "Bad element!");
        assertEqual(a.get(6), "30", "Bad element!");
        assertEqual(a.size(), 7, "Bad size!");
        assertFalse(a.isEmpty(), "Bad isEmpty!");
        // remove, valued
        assertTrue(a.remove("20"), "Bad remove!");
        assertFalse(a.remove("WHUT"), "Bad remove!");
        assertEqual(a.get(0), "Beta", "Bad element!");
        assertEqual(a.get(1), "10", "Bad element!");
        assertEqual(a.get(2), "40", "Bad element!");
        assertEqual(a.get(3), "40", "Bad element!");
        assertEqual(a.get(4), "40", "Bad element!");
        assertEqual(a.get(5), "30", "Bad element!");
        assertEqual(a.size(), 6, "Bad size!");
        // toString
        assertEqual(a.toString(), "[Beta, 10, 40, 40, 40, 30]", "Bad toString!");
        // set
        a.set(1, "Testing");
        assertEqual(a.get(0), "Beta", "Bad element!");
        assertEqual(a.get(1), "Testing", "Bad set!");
        assertEqual(a.get(2), "40", "Bad element!");
        // contains
        assertTrue(a.contains("40"), "Bad contains!");
        assertFalse(a.contains("42"), "Bad contains!");
        // containsAll
        assertTrue(a.containsAll(CArrayUtils.asList("30", "Testing", "40")), "Bad contains!");
        assertFalse(a.containsAll(CArrayUtils.asList("30", "Tester", "40")), "Bad contains!");
        // fillArray
        String[] target = new String[6];
        assertEqual(a.fillArray(target), 0, "Bad fillArray result!");
        for (int i = 0; i < target.length; i++) {
            assertEqual(target[i], a.get(i), "Bad filled array contents!");
        }
        target = new String[5];
        assertEqual(a.fillArray(target), 1, "Bad fillArray result!");
        for (int i = 0; i < target.length; i++) {
            assertEqual(target[i], a.get(i), "Bad filled array contents!");
        }
        target = new String[7];
        assertEqual(a.fillArray(target), -1, "Bad fillArray result!");
        for (int i = 0; i < target.length - 1; i++) {
            assertEqual(target[i], a.get(i), "Bad filled array contents!");
        }
        assertEqual(target[6], null, "Bad filled array contents!");
        // removeAll
        assertTrue(a.removeAll(CArrayUtils.asList("Testing", "40", "Wait...")), "Remove didn't cause modifications!");
        assertFalse(a.removeAll(CArrayUtils.asList("Nope", "Noep", "Ulp")), "Remove caused modifications!");
        assertEqual(a.get(0), "Beta", "Bad element!");
        assertEqual(a.get(1), "40", "Bad element!");
        assertEqual(a.get(2), "40", "Bad element!");
        assertEqual(a.get(3), "30", "Bad element!");
        assertEqual(a.size(), 4, "Bad size!");
        // retainAll
        assertFalse(a.retainAll(CArrayUtils.asList("Beta", "40", "30", "Extra!")), "Retain caused modifications!");
        assertTrue(a.retainAll(CArrayUtils.asList("Beta", "Noep", "30")), "Retain didn't cause modifications!");
        assertEqual(a.get(0), "Beta", "Bad element!");
        assertEqual(a.get(1), "30", "Bad element!");
        assertEqual(a.size(), 2, "Bad size!");
        // toArray
        Object[] o = a.toArray();
        assertEqual(o.length, a.size(), "Bad toArray length!");
        for (int i = 0; i < o.length; i++) {
            assertEqual(o[i], a.get(i), "Bad toArray!");
        }
        // clear
        a.clear();
        assertTrue(a.isEmpty(), "Supposed to be empty!");
        assertEqual(a.size(), 0, "Supposed to be 0-lengthed!");
    }
}
