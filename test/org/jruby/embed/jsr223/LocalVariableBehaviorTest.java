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
public class LocalVariableBehaviorTest {

    public LocalVariableBehaviorTest() {
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
     * Test of simple output.
     */
    @Test
    public void testSimple() throws Exception {
        System.out.println("[Simple]");
        String expResult;
        String result;
        JRubyScriptEngineManager manager = new JRubyScriptEngineManager();
        JRubyEngine engine = (JRubyEngine) manager.getEngineByName("jruby");
        String script = "puts \"Hello World!!!\"";
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.eval(script);
        expResult = "Hello World!!!\n";
        result = writer.toString();
        assertEquals(expResult, result);
        System.out.println(result);
    }

    /**
     * Vanishable local variable test.
     */
    @Test
    public void testVanishiableLocalVariable() throws Exception {
        System.out.println("[Vanishiable Local Variable]");
        String expResult;
        String result;
        System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
        JRubyScriptEngineManager manager = new JRubyScriptEngineManager();
        JRubyEngine engine = (JRubyEngine) manager.getEngineByName("jruby");
        engine.put("message", "Old Faithful");
        String script = "puts message";
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.eval(script);
        expResult = "Old Faithful\n";
        result = writer.toString();
        assertEquals(expResult, result);
        System.out.println("result: " + result);
        expResult = null;
        result = (String) engine.get("message");
        assertEquals(expResult, result);
    }

    /**
     * Vanishable local variable test.
     */
    @Test
    public void testPersistentLocalVariable() throws Exception {
        System.out.println("[Persistet Local Variable]");
        String expResult;
        String result;
        System.setProperty("org.jruby.embed.localvariable.behavior", "persistent");
        JRubyScriptEngineManager manager = new JRubyScriptEngineManager();
        JRubyEngine engine = (JRubyEngine) manager.getEngineByName("jruby");
        engine.put("message", "a herd of bisons");
        String script = "puts message";
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.eval(script);
        expResult = "a herd of bisons\n";
        result = writer.toString();
        assertEquals(expResult, result);
        System.out.println("result: " + result);
        expResult = "a herd of bisons";
        result = (String) engine.get("message");
        assertEquals(expResult, result);
    }

    /**
     * Global variable backwards compatibility test.
     */
    @Test
    public void testOldGlobalVariable() throws Exception {
        System.out.println("[Old Global Variable]");
        String expResult;
        String result;
        System.setProperty("org.jruby.embed.localvariable.behavior", "global");
        JRubyScriptEngineManager manager = new JRubyScriptEngineManager();
        JRubyEngine engine = (JRubyEngine) manager.getEngineByName("jruby");
        engine.put("message", "anltlers of elks");
        String script = "puts $message";
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.eval(script);
        expResult = "anltlers of elks";
        result = writer.toString().trim();
        assertEquals(expResult, result);
        System.out.println("result: " + result);

        writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.put("message", "a howling coyote");
        engine.eval(script);
        expResult = "a howling coyote";
        result = writer.toString().trim();
        assertEquals(expResult, result);
        result = (String) engine.get("message");
        assertEquals(expResult, result);
        System.out.println("result: " + result);
        
        engine.eval("$abc = \"tranquil lakes\"");
        result = (String) engine.get("abc");
        expResult = "tranquil lakes";
        assertEquals(expResult, result);
        System.out.println("result: " + result);
    }
}