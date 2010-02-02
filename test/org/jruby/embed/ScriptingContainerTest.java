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
package org.jruby.embed;

import org.jruby.embed.internal.BiVariableMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.javasupport.JavaEmbedUtils.EvalUnit;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Yoko Harada <yokolet@gmial.com>
 */
public class ScriptingContainerTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.4.0RC3";
    List<String> loadPaths;
    private Ruby runtime;

    public ScriptingContainerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        String[] paths = {
            jrubyhome + "/lib/ruby/1.8",
            jrubyhome + "/lib/ruby/1.8/rdoc",
            jrubyhome + "/lib/ruby/site_ruby/1.8",
            jrubyhome + "/lib/ruby/site_ruby/shared",
            jrubyhome + "/test",
            jrubyhome + "/build/classes/test",
            jrubyhome
        };
        loadPaths = Arrays.asList(paths);
        runtime = JavaEmbedUtils.initialize(loadPaths);
    }

    @After
    public void tearDown() {
        runtime = null;
    }

    /**
     * Test of getProperty method, of class ScriptContainer.
     */
    @Test
    public void testGetProperty() {
        System.out.println("getProperty");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String key = "language.extension";
        String[] extensions = {"rb"};
        String[] result = instance.getProperty(key);
        assertArrayEquals(key, extensions, result);
        key = "language.name";
        String[] names = {"ruby"};
        result = instance.getProperty(key);
        assertArrayEquals(key, names, result);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getSupportedRubyVersion method, of class ScriptContainer.
     */
    @Test
    public void testGetSupportedRubyVersion() {
        System.out.println("getSupportedRubyVersion");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String expResult = "jruby 1.4.0";
        String result = instance.getSupportedRubyVersion();
        assertTrue(result.startsWith(expResult));

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getRuntime method, of class ScriptContainer.
     */
    @Test
    public void testGetRuntime() {
        System.out.println("getRuntime");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Class expClazz = runtime.getClass();
        Ruby result = instance.getRuntime();
        Class resultClazz = result.getClass();
        assertEquals(expClazz, resultClazz);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getVarMap method, of class ScriptContainer.
     */
    @Test
    public void testGetVarMap() {
        System.out.println("getVarMap");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        BiVariableMap expResult = new BiVariableMap(runtime);
        BiVariableMap result = instance.getVarMap();
        assertEquals(expResult.getClass(), result.getClass());
        result.put("@name", "camellia");
        assertEquals("camellia", instance.getVarMap().get("@name"));
        result.put("COLOR", "red");
        assertEquals("red", instance.getVarMap().get("COLOR"));
        // class variable injection does not work
        //result.put("@@season", "spring");
        //assertEquals("spring", instance.getVarMap().get("@@season"));
        result.put("$category", "flower");
        assertEquals("flower", instance.getVarMap().get("$category"));

        assertEquals("camellia", instance.getVarMap().get("@name"));
        assertEquals("red", instance.getVarMap().get("COLOR"));
        //assertEquals("spring", instance.getVarMap().get("@@season"));
        assertEquals("flower", instance.getVarMap().get("$category"));

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getIoMap method, of class ScriptContainer.
     */
    @Test
    public void testGetIoMap() {
        System.out.println("getIoMap");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Map result = instance.getAttributeMap();
        Object obj = result.get(AttributeName.READER);
        assertEquals(obj.getClass(), java.io.InputStreamReader.class);
        obj = result.get(AttributeName.WRITER);
        assertEquals(obj.getClass(), java.io.PrintWriter.class);
        obj = result.get(AttributeName.ERROR_WRITER);
        assertEquals(obj.getClass(), java.io.PrintWriter.class);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of get method, of class ScriptContainer.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String key = null;
        try {
            instance.get(key);
        } catch (NullPointerException e) {
            assertEquals("key is null", e.getMessage());
        }
        key = "";
        try {
            instance.get(key);
        } catch (IllegalArgumentException e) {
            assertEquals("key is empty", e.getMessage());
        }
        key = "a";
        Object expResult = null;
        Object result = instance.get(key);
        assertEquals(expResult, result);
        BiVariableMap varMap = instance.getVarMap();
        varMap.put("@name", "camellia");
        assertEquals("camellia", instance.get("@name"));
        varMap.put("COLOR", "red");
        assertEquals("red", instance.get("COLOR"));
        //varMap.put("@@season", "spring");
        //assertEquals("spring", instance.get("@@season"));
        varMap.put("$category", "flower");
        assertEquals("flower", instance.get("$category"));

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of put method, of class ScriptContainer.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String key = null;
        Object value = null;
        try {
            instance.put(key, value);
        } catch (NullPointerException e) {
            assertEquals("key is null", e.getMessage());
        }
        key = "";
        try {
            instance.put(key, value);
        } catch (IllegalArgumentException e) {
            assertEquals("key is empty", e.getMessage());
        }
        key = "a";
        instance.put(key, value);
        assertEquals(null, instance.get(key));
        instance.put("@name", "camellia");
        assertEquals("camellia", instance.get("@name"));
        instance.put("COLOR", "red");
        assertEquals("red", instance.get("COLOR"));
        // class variable injection does not work
        //instance.put("@@season", "spring");
        //assertEquals("spring", instance.get("@@season"));
        instance.put("$category", "flower");
        assertEquals("flower", instance.get("$category"));

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of parse method, of class ScriptContainer.
     */
    @Test
    public void testParse_String() {
        System.out.println("parse");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String script = "def hello()" +
                   "\"Hello, Hello, はろ!\"\n" +
                 "end\n" +
                 "hello";
        EvalUnit expUnit =
            JavaEmbedUtils.newRuntimeAdapter().parse(runtime, script, "<script>", 0);
        IRubyObject expRet = expUnit.run();
        String expString = (String) JavaEmbedUtils.rubyToJava(expRet);
        //JRubyRootNode result = instance.parse(script);
        EvalUnit result = instance.parse(script);
        IRubyObject ret = result.run();
        String retString = (String) JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expString, retString);
        System.out.println(retString);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of parse method, of class ScriptContainer.
     */
    @Test
    public void testParse_Reader_String() throws FileNotFoundException {
        System.out.println("parse");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Reader reader = null;
        String filename = "";
        EvalUnit expResult = null;
        //JRubyRootNode result = instance.parse(reader, filename);
        EvalUnit result = instance.parse(reader, filename);
        assertEquals(expResult, result);
        filename = "ruby/simple_output.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        EvalUnit expUnit =
            JavaEmbedUtils.newRuntimeAdapter().parse(runtime, istream, filename, 0);
        IRubyObject expRet = expUnit.run();
        String expString = (String) JavaEmbedUtils.rubyToJava(expRet);
        reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename));
        result = instance.parse(reader, filename);
        IRubyObject ret = result.run();
        String retString = (String) JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expString, retString);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of parse method, of class ScriptContainer.
     */
    @Test
    public void testParse_InputStream_String() throws FileNotFoundException {
        System.out.println("parse");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        InputStream istream = null;
        String filename = "";
        EvalUnit expResult = null;
        EvalUnit result = instance.parse(istream, filename);
        assertEquals(expResult, result);
        filename = "ruby/simple_output.rb";
        istream = getClass().getClassLoader().getResourceAsStream(filename);
        EvalUnit expUnit =
            JavaEmbedUtils.newRuntimeAdapter().parse(runtime, istream, filename, 0);
        IRubyObject expRet = expUnit.run();
        String expString = (String) JavaEmbedUtils.rubyToJava(expRet);
        filename = "test/ruby/simple_output.rb";
        InputStream stream = new FileInputStream(filename);
        result = instance.parse(stream, filename);
        IRubyObject ret = result.run();
        String retString = (String) JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expString, retString);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of eval method, of class ScriptContainer.
     */
    @Test
    public void testEvalUnit() {
        System.out.println("testEvalUnit");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.PERSISTENT);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        EvalUnit unit = null;
        Object expResult = null;
        Object result = null;
        String script =
            "def hello()" +
                "\"Hello, Hello, はろ!\"\n" +
            "end\n" +
            "hello";
        unit = instance.parse(script);
        IRubyObject expRet =
            JavaEmbedUtils.newRuntimeAdapter().eval(runtime, script);
        expResult = (String)JavaEmbedUtils.rubyToJava(expRet);
        unit = instance.parse(script);
        IRubyObject ret = unit.run();
        result = (String)JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expResult, result);
        script =
            "def message\n" +
                "\"message: #{@message}\"\n" +
            "end\n" +
            "message";
        instance.getVarMap().put("@message", "What's up?");
        unit = instance.parse(script);
        ret = unit.run();
        expResult = "message: What's up?";
        result = JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expResult, result);
        System.out.println(result);
        instance.put("@message", "Fabulous!");
        ret = unit.run();
        expResult = "message: Fabulous!";
        result = JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expResult, result);
        System.out.println(result);
        instance.put("@message", "That's the way you are.");
        ret = unit.run();
        expResult = "message: That's the way you are.";
        result = JavaEmbedUtils.rubyToJava(ret);
        assertEquals(expResult, result);
        System.out.println(result);
        
        script =
            "def print_list\n" +
                "$list.each {|name| print name, \" >> \"}\n" +
            "end\n" +
            "print_list";
        String[] names = {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn"};
        List<String> list = Arrays.asList(names);
        instance.getVarMap().put("$list", list);
        unit = instance.parse(script);
        result = unit.run();

        //should raise an exception
        script = "puts 'Hello World!";
        try {
            unit = instance.parse(script);
        } catch (RuntimeException e) {
            e.getMessage();
        }

        //shoud raise exception
        script = "puts 1/0";
        unit = instance.parse(script);
        try {
            result = unit.run();
        } catch (RuntimeException e) {
            e.getMessage();
        }
        
        instance.getVarMap().put("x", 12345);
        unit = instance.parse("puts x.to_s(2)");
        unit.run();

        instance.runScriptlet("y=9.0");
        instance.runScriptlet("puts y");

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of evalScriptlet method, of class ScriptContainer.
     */
    @Test
    public void testRunScriptlet() {
        System.out.println("runScriptlet");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.PERSISTENT);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String script = "";
        Object expResult = null;
        Object result = instance.runScriptlet(script);
        assertEquals(expResult, result);
        instance.runScriptlet("p=9.0");
        instance.runScriptlet("q = Math.sqrt p");
        instance.runScriptlet("puts \"square root of #{p} is #{q}\"");
        Map m = instance.getVarMap();
        Set<String> keys = instance.getVarMap().keySet();
        for (String key : keys) {
            System.out.println(key + ", " + m.get(key));
        }
        System.out.println("Ruby used values: p = " + instance.get("p") +
                ", q = " + instance.get("q"));

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of callMethod method, of class ScriptContainer.
     */
    @Test
    public void testCallMethod_4args() throws FileNotFoundException {
        System.out.println("callMethod (4 args)");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Object receiver = null;
        String method = "";
        Object[] args = null;
        Class returnType = null;
        Object expResult = null;
        EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
        Object result = adapter.callMethod(receiver, method, args, returnType);
        assertEquals(expResult, result);
        
        String filename =  "ruby/calendar.rb";
        method = "next_year";
        args = null;
        returnType = Integer.class;
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        IRubyObject expReceiver =
            JavaEmbedUtils.newRuntimeAdapter().parse(runtime, istream, filename, 0).run();
        IRubyObject expRet =
            JavaEmbedUtils.newObjectAdapter().callMethod(expReceiver, method);
        Integer expResult1 = (Integer) JavaEmbedUtils.rubyToJava(runtime, expRet, returnType);
        Reader reader =
            new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename));
        EvalUnit unit = instance.parse(reader, filename);
        receiver = unit.run();
        Integer result1 =
            (Integer) adapter.callMethod(receiver, method, returnType);
        assertEquals(expResult1, result1);
        System.out.println("next year: " + result1);
        System.out.println(instance.get("@today"));

        filename = "ruby/three_times.rb";
        method = "three_times";
        args = null;
        returnType = List.class;
        istream = getClass().getClassLoader().getResourceAsStream(filename);
        //no receiver
        JavaEmbedUtils.newRuntimeAdapter().parse(runtime, istream, filename, 0).run();
        expRet =
            JavaEmbedUtils.newObjectAdapter().callMethod(runtime.getTopSelf(), method);
        List expResult2 = (List) JavaEmbedUtils.rubyToJava(runtime, expRet, returnType);
        filename = "test/ruby/three_times.rb";
        reader = new FileReader(filename);
        unit = instance.parse(reader, filename);
        //no receiver
        //node.get().run();
        unit.run();
        List result2 = (List) adapter.callMethod(null, method, returnType);
        assertEquals(expResult2, result2);
        System.out.println(result2.get(0) + ", " + result2.get(1) + ", " + result2.get(2));

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of callMethod method, of class ScriptContainer.
     */
    @Test
    public void testCallMethod_5args() {
        System.out.println("callMethod (5 args)");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Object receiver = null;
        String method = "";
        Object[] args = null;
        Class returnType = null;
        EmbedEvalUnit unit = null;
        Object expResult = null;
        EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
        Object result = adapter.callMethod(receiver, method, args, returnType, unit);
        assertEquals(expResult, result);

        /*
         * global vars injection
         */
        String filename = "ruby/greetings_globalvars.rb";
        instance.put("$who", new String("a"));
        List<String> people = new ArrayList<String>();
        people.add("b");
        instance.put("$people", people);
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        unit = instance.parse(istream, filename);
        receiver = unit.run();
        method = "greet";
        returnType = String.class;
        instance.put("$who", new String("Sharpey"));
        people.clear();
        people.add("Troy");
        people.add("Gabriella");
        people.add("Chad");
        result = (String)adapter.callMethod(receiver, method, returnType, unit);
        expResult = "How are you? Sharpey.";
        assertEquals(expResult, result);
        System.out.println(result);

        method = "sayhi";
        returnType = null;
        result = adapter.callMethod(receiver, method, returnType, unit);

        method = "count";
        returnType = Integer.class;
        result = (Integer)adapter.callMethod(receiver, method, returnType, unit);
        assertEquals(4, result);
        System.out.println(result + " people in total.");

        /*
         * instance vars injection
         */
        filename = "ruby/greetings_instancevars.rb";
        instance.put("@who", new String("a"));
        people.clear();
        people.add("b");
        instance.put("@people", people);
        istream = getClass().getClassLoader().getResourceAsStream(filename);
        unit = instance.parse(istream, filename);
        receiver = unit.run();
        method = "greet";
        returnType = String.class;
        instance.put("@who", new String("R2-D2"));
        people.clear();
        people.add("Anakin");
        people.add("Obi-Wan");
        people.add("Padme");
        people.add("C-3PO");
        result = (String)adapter.callMethod(receiver, method, returnType, unit);
        expResult = "How are you? R2-D2.";
        assertEquals(expResult, result);
        System.out.println(result);

        method = "sayhi";
        returnType = null;
        result = adapter.callMethod(receiver, method, returnType, unit);

        method = "count";
        returnType = Integer.class;
        result = (Integer)adapter.callMethod(receiver, method, returnType, unit);
        assertEquals(5, result);
        System.out.println(result + " people(?) in total.");

        /*
         * local vars injection fails for callMethod()
         * 
        filename = "ruby/greetings_localvars.rb";
        instance.put("who", new String("Harry"));
        people = new ArrayList<String>();
        people.add("Ron");
        people.add("Fred");
        people.add("Geroge");
        instance.put("people", people);
        istream = getClass().getClassLoader().getResourceAsStream(filename);
        node = instance.parse(istream, filename);
        receiver = instance.eval(node);
        method = "greet";
        returnType = String.class;
        result = adapter.callMethod(receiver, method, args, returnType, node);
        expResult = "How're you doing? Harry.";
        assertEquals(expResult, result);

        method = "sayhi";
        returnType = null;
        result = adapter.callMethod(receiver, method, args, returnType, node);

        method = "count";
        returnType = null;
        result = adapter.callMethod(receiver, method, args, returnType, node);
        */

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getInstance method, of class ScriptContainer.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Object receiver = null;

        String script =
                "def open()" +
                  "@msgfile = File.new(@name, \"w\");" +
                  "@msgfile.chmod(0600);" +
                "end\n" +
                "def write(message)" +
                  "message.each { |m| @msgfile.puts(m) }" +
                "end\n" +
                "def close()" +
                  "@msgfile.close;" +
                  "puts \"The file, #{@name}, has #{File.size(@name)} bytes.\"" +
                "end";
        instance.put("@name", "trig.txt");
        receiver = instance.runScriptlet(script);
        Writable msgFile = instance.getInstance(receiver, Writable.class);
        msgFile.open();
        List<String> list = new ArrayList<String>();
        list.add("Sine square plus cosine square equals one.");
        list.add("One plus tangent square equals secant square.");
        list.add("Cosine theta is hypotenuse over adjacent.");
        msgFile.write(list);
        msgFile.close();

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testGetInstance_2() {
        System.out.println("getInstance 2");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        String script =
                "class WritableImple\n" +
                  "include Java::org.jruby.embed.Writable\n" +
                  "def initialize(name)" +
                    "@name = name;" +
                  "end\n" +
                  "def open()" +
                    "@msgfile = File.new(@name, \"w\");" +
                    "@msgfile.chmod(0600);" +
                    "puts \"#{@name} is available.\"" +
                  "end\n" +
                  "def write(message)" +
                    "message.each { |m| @msgfile.puts(m) }" +
                  "end\n" +
                  "def close()" +
                    "@msgfile.close;" +
                    "puts \"The file, #{@name}, has #{File.size(@name)} bytes.\"" +
                  "end\n" +
                "end\n" +
                "WritableImple.new(name)";

        instance.put("name", "mathematican.txt");
        Object object = instance.runScriptlet(script);
        Writable msgFile = instance.getInstance(object, Writable.class);
        List<String> list = new ArrayList<String>();
        list.add("Leonhard Paul Euler was a Swiss mathematician and physicist.");
        list.add("Pierre de Fermat was a French lawer and mathematician.");
        list.add("Sir Isaac Newton was an English physicist, mathematician, and astronomer.");
        msgFile.open();
        msgFile.write(list);
        msgFile.close();

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of setReader method, of class ScriptContainer.
     */
    @Test
    public void testSetReader() {
        System.out.println("setReader");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Reader reader = new InputStreamReader(System.in);
        instance.setReader(reader);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getReader method, of class ScriptContainer.
     */
    @Test
    public void testGetReader() {
        System.out.println("getReader");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Reader reader = new InputStreamReader(System.in);
        instance.setReader(reader);
        Reader result = instance.getReader();
        assertTrue(reader == result);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getIn method, of class ScriptContainer.
     */
    @Test
    public void testGetIn() {
        System.out.println("getIn");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Object result = instance.getIn();
        assertTrue(result instanceof InputStream);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of setWriter method, of class ScriptContainer.
     */
    @Test
    public void testSetWriter() throws IOException {
        System.out.println("setWriter");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Writer writer = null;
        instance.setWriter(writer);
        writer = new StringWriter();
        instance.setWriter(writer);
        String filename = "ruby/greetings_imple.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        Object receiver = instance.runScriptlet(istream, filename);
        Greetings result = (Greetings) instance.getInstance(receiver, Greetings.class);
        result.hello("たろ");
        result.bye("じろ");
        String expResult = "<p>What's up? たろ</p>\nSee you, じろ\n";
        assertEquals(expResult, writer.toString());
        System.out.println(writer.toString());
        instance.setWriter(new PrintWriter(System.out));

        writer = new FileWriter("short_greetings.txt");
        instance.setWriter(writer);
        result.hello("Olivia");
        result.bye("Jack");
        

        filename = "ruby/hello_bye.rb";
        istream = getClass().getClassLoader().getResourceAsStream(filename);
        instance.runScriptlet(istream, filename);

        writer.close();

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getWriter method, of class ScriptContainer.
     */
    @Test
    public void testGetWriter() throws IOException {
        System.out.println("getWriter");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        PrintWriter writer = new PrintWriter(System.out, true);
        instance.setWriter(writer);
        Writer result = instance.getWriter();
        assertTrue(writer == result);
        result.write("result.getWriter()");

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getOut method, of class ScriptContainer.
     */
    @Test
    public void testGetOut() {
        System.out.println("getOut");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Object result = instance.getOut();
        assertTrue(result instanceof PrintStream);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of setErrorWriter method, of class ScriptContainer.
     */
    @Test
    public void testSetErrorWriter() {
        System.out.println("setErrorWriter");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Writer errorWriter = new StringWriter();
        instance.setErrorWriter(errorWriter);

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getErrorWriter method, of class ScriptContainer.
     */
    @Test
    public void testGetErrorWriter() throws IOException {
        System.out.println("getWriter");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        Writer errorWriter = new PrintWriter(System.out, true);
        instance.setWriter(errorWriter);
        Writer result = instance.getWriter();
        assertTrue(errorWriter == result);
        result.write("result.getWriter()");

        instance.getVarMap().clear();
        instance = null;
    }

    /**
     * Test of getErr method, of class ScriptContainer.
     */
    @Test
    public void testGetErr() throws IOException {
        System.out.println("getErr");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
        instance.setErrorWriter(new PrintWriter(System.err, true));
        Object result = instance.getErr();
        assertTrue(result instanceof PrintStream);

        instance.getVarMap().clear();
        instance = null;
    }

}