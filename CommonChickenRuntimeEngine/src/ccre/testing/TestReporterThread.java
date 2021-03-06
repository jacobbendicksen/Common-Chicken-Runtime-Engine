/*
 * Copyright 2013-2015 Colby Skeggs
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

import ccre.concurrency.ReporterThread;

/**
 * Test the ReporterThread class.
 *
 * @author skeggsc
 */
public class TestReporterThread extends BaseTest {

    @Override
    public String getName() {
        return "ReporterThread Test";
    }

    @Override
    protected void runTest() throws TestingException, InterruptedException {
        final boolean[] wr = new boolean[1];
        ReporterThread tr = new TestThread("TestRT", wr);
        assertFalse(wr[0], "Expected no execution of the subthread!");
        boolean hadException = false;
        try {
            tr.run();
        } catch (IllegalStateException e) {
            hadException = true;
        }
        assertTrue(hadException, "Should have thrown an exception!");
        assertFalse(wr[0], "Expected no execution of the subthread!");
        assertTrue(tr.getName().startsWith("TestRT-"), "Expected the name to start with the specified prefix!");
        tr.start();
        tr.join();
        assertFalse(tr.isAlive(), "Thread should be done executing!");
        assertTrue(wr[0], "Expected execution of the subthread!");
    }

    private class TestThread extends ReporterThread {

        private boolean[] wr;

        TestThread(String name, boolean[] wr) {
            super(name);
            this.wr = wr;
        }

        @Override
        protected void threadBody() throws Throwable {
            wr[0] = true;
            boolean hadException = false;
            try {
                this.run();
            } catch (IllegalStateException e) {
                hadException = true;
            }
            assertTrue(hadException, "Self-run should have thrown an Exception!");
        }
    }
}
