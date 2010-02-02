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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
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
public class JRubyEngineTest {

    public JRubyEngineTest() {
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
     * Test of compile method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testCompile_String() throws Exception {
        System.out.println("[compile string]");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        String script =
            "def norman_window(x, y)\n" +
               "puts \"Dimensions of a #{x} x #{y} Norman window are\"\n" +
               "puts \"area: #{get_area(x, y)}\"\n" +
               "puts \"perimeter: #{get_perimeter(x, y)}\"\n" +
            "end\n" +
            "def get_area(x, y)\n" +
              "x * y + Math::PI / 8.0 * x ** 2.0\n" +
            "end\n" +
            "def get_perimeter(x, y)\n" +
              "x + 2.0 * y + Math::PI / 2.0 * x\n" +
            "end\n" +
            "norman_window(2, 1)\n" +
            "norman_window(1, 2)";
        String expResult = "Dimensions of a 2 x 1 Norman window are\n";
        CompiledScript cs = instance.compile(script);
        StringWriter writer = new StringWriter();
        SimpleScriptContext context = new SimpleScriptContext();
        context.setWriter(writer);
        instance.setContext(context);
        cs.eval();
        String result = writer.toString();
        assertTrue(result.startsWith(expResult));
        System.out.println(writer.toString());
        writer.close();
    }

    /**
     * Test of compile method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testCompile_Reader() throws Exception {
        System.out.println("[compile reader]");
        Reader reader = new FileReader("test/ruby/norman_window_dimensions.rb");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        String expResult = "Dimensions of a 2 x 1 Norman window are\n";
        CompiledScript cs = instance.compile(reader);

        StringWriter writer = new StringWriter();
        SimpleScriptContext context = new SimpleScriptContext();
        context.setWriter(writer);
        instance.setContext(context);
        cs.eval();
        String result = writer.toString();
        assertTrue(result.startsWith(expResult));
        System.out.println(writer.toString());
        writer.close();
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_String_ScriptContext() throws Exception {
        System.out.println("[eval String with ScriptContext]");
        String script =
            "def norman_window(x, y)\n" +
               "return get_area(x, y), get_perimeter(x, y)\n" +
            "end\n" +
            "def get_area(x, y)\n" +
              "x * y + Math::PI / 8.0 * x ** 2.0\n" +
            "end\n" +
            "def get_perimeter(x, y)\n" +
              "x + 2.0 * y + Math::PI / 2.0 * x\n" +
            "end\n" +
            "norman_window(1, 3)";
        ScriptContext context = new SimpleScriptContext();
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        List<Double> expResult = new ArrayList();
        expResult.add(3.392);
        expResult.add(8.571);
        List<Double> result = (List<Double>) instance.eval(script, context);
        for (int i=0; i<result.size(); i++) {
            System.out.println(result.get(i));
            assertEquals(expResult.get(i), result.get(i), 0.01);
        }

        script =
            "def get_area\n" +
              "$x * $y + Math::PI / 8.0 * $x ** 2.0\n" +
            "end\n" +
            "get_area";
        context.setAttribute("x", 1.0, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("y", 3.0, ScriptContext.ENGINE_SCOPE);
        Double result2 = (Double) instance.eval(script, context);
        System.out.println("area(1 x 3): " + result2);
        assertEquals(expResult.get(0), result2, 0.01);
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_String_ScriptContext2() throws Exception {
        System.out.println("[eval String with ScriptContext 2]");
        ScriptContext context = new SimpleScriptContext();
        System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();

        String script =
            "def get_area\n" +
              "@x * @y + Math::PI / 8.0 * @x ** 2.0\n" +
            "end\n" +
            "get_area";
        context.setAttribute("@x", 1.0, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("@y", 3.0, ScriptContext.ENGINE_SCOPE);
        Double expResult = 3.392;
        Double result = (Double) instance.eval(script, context);
        assertEquals(expResult, result, 0.01);
        System.out.println("area(1 x 3): " + result);
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_Reader_ScriptContext() throws Exception {
        System.out.println("[eval Reader with ScriptContext]");
        Reader reader = new FileReader("test/ruby/norman_window_dimensions2.rb");
        ScriptContext context = new SimpleScriptContext();
        System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();

        context.setAttribute("@x", 4.0, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("@y", 1.0, ScriptContext.ENGINE_SCOPE);
        List<Double> expResult = new ArrayList();
        expResult.add(10.283);
        expResult.add(12.283);
        List<Double> result = (List<Double>) instance.eval(reader, context);
        for (int i=0; i<result.size(); i++) {
            System.out.println(result.get(i));
            assertEquals(expResult.get(i), result.get(i), 0.01);
        }
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_String() throws Exception {
        System.out.println("eval String");
        //System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
        System.setProperty("org.jruby.embed.localvariable.behavior", "persistent");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        instance.eval("p=9.0");
        instance.eval("q = Math.sqrt p");
        instance.eval("puts \"square root of #{p} is #{q}\"");
        Double expResult = 3.0;
        Double result = (Double) instance.get("q");
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_Reader() throws Exception {
        System.out.println("eval Reader");
        Reader reader = new FileReader("test/ruby/calendar2.rb");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();

        Long expResult = 2010L;
        Object result = instance.eval(reader);
        assertEquals(expResult, result);
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_String_Bindings() throws Exception {
        System.out.println("eval String with Bindings");
        String script =
            "def get_perimeter(x, y)\n" +
              "x + 2.0 * y + PI / 2.0 * x\n" +
            "end\n" +
            "get_perimeter(1.5, 1.5)";
        Bindings bindings = new SimpleBindings();
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        Double expResult = 6.856;
        bindings.put("PI", 3.1415);
        Double result = (Double) instance.eval(script, bindings);
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of eval method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testEval_Reader_Bindings() throws Exception {
        System.out.println("eval Reader with Bindings");
        Reader reader = new FileReader("test/ruby/count_down.rb");
        Bindings bindings = new SimpleBindings();
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        bindings.put("@month", 6);
        bindings.put("@day", 2);
        instance.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        String result = (String) instance.eval(reader, bindings);
        assertTrue(result.startsWith("Happy") || result.startsWith("You have"));
        System.out.println(result.toString());
    }

    /**
     * Test of get method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();

        instance.put("abc", "aabc");
        instance.put("@abc", "abbc");
        instance.put("$abc", "abcc");
        String key = "abc";
        Object expResult = "aabc";
        Object result = instance.get(key);
        assertEquals(expResult, result);
        List list = new ArrayList(); list.add("aabc");
        instance.put("abc", list);
        Map map = new HashMap(); map.put("Ruby", "Rocks");
        instance.put("@abc", map);
        result = instance.get(key);
        assertEquals(expResult, ((List)result).get(0));
        key = "@abc";
        expResult = "Rocks";
        result = instance.get(key);
        assertEquals(expResult, ((Map)result).get("Ruby"));
    }

    /**
     * Test of put method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        String key = "";
        Object value = null;
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        try {
            instance.put(key, value);
        } catch (IllegalArgumentException e) {
            String expResult = "key is empty";
            assertEquals(expResult, e.getMessage());
        }
    }

    /**
     * Test of getBindings method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testGetBindings() throws ScriptException {
        System.out.println("getBindings");
        //JRubyEngineFactory factory = new JRubyEngineFactory();
        //JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        System.setProperty("org.jruby.embed.localvariable.behavior", "persistent");
        JRubyScriptEngineManager manager = new JRubyScriptEngineManager();
        JRubyEngine instance = (JRubyEngine) manager.getEngineByName("jruby");
        instance.eval("p = 9.0");
        instance.eval("q = Math.sqrt p");
        Double expResult = 9.0;
        int scope = ScriptContext.ENGINE_SCOPE;
        Bindings result = instance.getBindings(scope);
        assertEquals(expResult, (Double)result.get("p"), 0.01);
        expResult = 3.0;
        assertEquals(expResult, (Double)result.get("q"), 0.01);

        scope = ScriptContext.GLOBAL_SCOPE;
        result = instance.getBindings(scope);
        assertTrue(result instanceof SimpleBindings);
        assertEquals(0, result.size());
    }

    /**
     * Test of setBindings method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testSetBindings() throws ScriptException {
        System.out.println("setBindings");
        String script =
            "def message\n" +
                "\"message: #{@message}\"\n" +
            "end\n" +
            "message";
        Bindings bindings = new SimpleBindings();
        bindings.put("@message", "What's up?");
        int scope = ScriptContext.ENGINE_SCOPE;
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        Object expResult = "message: What's up?";
        instance.setBindings(bindings, scope);
        Object result = instance.eval(script);
        assertEquals(expResult, result);
        System.out.println(result.toString());
    }

    /**
     * Test of createBindings method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testCreateBindings() {
        System.out.println("createBindings");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        Bindings bindings = instance.getBindings(ScriptContext.ENGINE_SCOPE);
        Bindings result = instance.createBindings();
        assertNotSame(bindings, result);
    }

    /**
     * Test of getContext method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testGetContext() {
        System.out.println("getContext");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        ScriptContext result = instance.getContext();
        assertTrue(result instanceof JRubyContext);
    }

    /**
     * Test of setContext method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testSetContext() {
        System.out.println("setContext");
        //System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        ScriptContext ctx = new SimpleScriptContext();
        StringWriter writer = new StringWriter();
        writer.write("Have a great summer!");
        ctx.setWriter(writer);
        instance.setContext(ctx);
        ScriptContext result = instance.getContext();
        Writer w = result.getWriter();
        Object expResult = "Have a great summer!";
        assertTrue(writer == result.getWriter());
        assertEquals(expResult, (result.getWriter()).toString());
        System.out.println((result.getWriter()).toString());
        instance = null;
    }

    /**
     * Test of getFactory method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testGetFactory() {
        System.out.println("getFactory");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        ScriptEngineFactory result = instance.getFactory();
        assertTrue(factory == result);
        String expResult = "JSR 223 JRuby Engine";
        String ret = result.getEngineName();
        assertEquals(expResult, ret);
    }

    /**
     * Test of invokeMethod method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testInvokeMethod() throws Exception {
        System.out.println("invokeMethod");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        Reader reader = new FileReader("test/ruby/tree.rb");
        Object receiver = instance.eval(reader);
        String method = "to_s";
        Object[] args = null;
        String expResult = "Cherry blossom is a round shaped,";
        String result = (String) instance.invokeMethod(receiver, method, args);
        assertTrue(result.startsWith(expResult));
        System.out.println(result.toString());

        Bindings bindings = new SimpleBindings();
        bindings.put("name", "cedar");
        bindings.put("shape", "pyramidal");
        bindings.put("foliage", "evergreen");
        bindings.put("color", "nondescript");
        bindings.put("bloomtime", "April - May");
        instance.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        reader = new FileReader("test/ruby/tree_given_localvars.rb");
        receiver = instance.eval(reader);
        expResult = "Cedar is a pyramidal shaped,";
        result = (String) instance.invokeMethod(receiver, method, args);
        assertTrue(result.startsWith(expResult));
        System.out.println(result.toString());
    }

    /**
     * Test of invokeFunction method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testInvokeFunction() throws Exception {
        System.out.println("invokeFunction");
        Reader reader = new FileReader("test/ruby/count_down.rb");
        Bindings bindings = new SimpleBindings();
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        bindings.put("@month", 6);
        bindings.put("@day", 3);
        instance.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        Object result = instance.eval(reader, bindings);
        System.out.println(result.toString());

        String method = "count_down_birthday";
        bindings.put("@month", 12);
        bindings.put("@day", 3);
        instance.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        Object[] args = null;
        result = instance.invokeFunction(method, args);
        assertTrue(((String)result).startsWith("Happy") || ((String)result).startsWith("You have"));
        System.out.println(result.toString());
    }

    /**
     * Test of getInterface method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testGetInterface_Class() throws FileNotFoundException, ScriptException {
        System.out.println("getInterface (no receiver)");
        Class returnType = Sphere.class;
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        Reader reader = new FileReader("test/ruby/sphere.rb");
        Object receiver = instance.eval(reader);
        double expResult = 113.097;
        Sphere result = (Sphere) instance.getInterface(returnType);
        assertEquals(expResult, result.volume(3), 0.01);
        assertEquals(expResult, result.surface_area(3), 0.01);
    }

    /**
     * Test of getInterface method, of class Jsr223JRubyEngine.
     */
    @Test
    public void testGetInterface_Object_Class() throws FileNotFoundException, ScriptException {
        System.out.println("getInterface (with receiver)");
        //System.setProperty("org.jruby.embed.localcontext.scope", "singlethread");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        JRubyEngine instance = (JRubyEngine) factory.getScriptEngine();
        Reader reader = new FileReader("test/ruby/position_function.rb");
        Bindings bindings = instance.createBindings();
        bindings.put("initial_velocity", 30.0);
        bindings.put("initial_height", 30.0);
        bindings.put("system", "metric");
        Object receiver = instance.eval(reader, bindings);
        Class returnType = PositionFunction.class;
        PositionFunction result = (PositionFunction) instance.getInterface(receiver, returnType);
        String expResult = "75.9 m";
        double t = 3.0;
        assertEquals(expResult, result.position(t));
        System.out.println("Height after " + t + " sec is " + result.position(t));
        expResult = "20.2 m/sec";
        t = 1.0;
        assertEquals(expResult, result.velocity(t));
        System.out.println("Velocity after " + t + " sec is " + result.velocity(t));

        reader = new FileReader("test/ruby/position_function.rb");
        bindings = instance.createBindings();
        bindings.put("initial_velocity", 30.0);
        bindings.put("initial_height", 30.0);
        bindings.put("system", "english");
        receiver = instance.eval(reader, bindings);
        result = (PositionFunction) instance.getInterface(receiver, returnType);
        expResult = "26.0 ft.";
        t = 2.0;
        assertEquals(expResult, result.position(t));
        System.out.println("Height after " + t + " sec is " + result.position(2.0));
        expResult = "-34.0 ft./sec";
        assertEquals(expResult, result.velocity(t));
        System.out.println("Velocity after " + t + " sec is " + result.velocity(2.0));
    }
}