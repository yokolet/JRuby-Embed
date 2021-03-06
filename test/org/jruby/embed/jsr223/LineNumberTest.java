/**
 * **** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2009 Yoko Harada <yokolet@gmail.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 * **** END LICENSE BLOCK *****
 */
package org.jruby.embed.jsr223;

import java.io.StringWriter;
import javax.script.ScriptContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Yoko Harada
 */
public class LineNumberTest {

    public LineNumberTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        System.setProperty("org.jruby.embed.localcontext.scope", "threadsafe");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of line numbers in exceptions.
     */
    @Test
    public void testInitialLineNumber() throws Exception {
        System.out.println("[Initial Line Number]");
        JRubyScriptEngineManager manager = new JRubyScriptEngineManager();
        JRubyEngine engine = (JRubyEngine) manager.getEngineByName("jruby");
        String script = "puts \"Hello World!!!\"\nputs \"Have a nice day!";
        StringWriter writer = new StringWriter();
        engine.getContext().setErrorWriter(writer);
        try {
            engine.eval(script);
        } catch (Exception e) {
            assertTrue(writer.toString().contains("<script>:2:"));
            System.out.println("ERROR: " + writer.toString());
        }
        engine.getContext().setAttribute("org.jruby.embed.linenumber", 1, ScriptContext.ENGINE_SCOPE);
        writer = new StringWriter();
        engine.getContext().setErrorWriter(writer);
        try {
            engine.eval(script);
        } catch (Exception e) {
            assertTrue(writer.toString().contains("<script>:3:"));
            System.out.println("ERROR: " + writer.toString());
        }
    }
}