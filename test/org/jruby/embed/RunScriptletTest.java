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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
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
public class RunScriptletTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.4.0RC3";
    private List<String> loadPaths;

    public RunScriptletTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        String[] paths = {
            jrubyhome + "/lib/ruby/1.8",
            jrubyhome + "/lib/ruby/site_ruby/1.8",
            jrubyhome
        };
        String separator = System.getProperty("path.separator");
        String classPath = "";
        for (int i=0; i < paths.length; i++) {
            classPath = classPath + paths[i] + separator;
        }
        classPath = classPath.substring(0, classPath.length()-1);
        System.setProperty("org.jruby.embed.class.path", classPath);
        System.setProperty("jruby.home", jrubyhome);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        String[] paths =
            System.getProperty("org.jruby.embed.class.path").split(System.getProperty("path.separator"));
        loadPaths = Arrays.asList(paths);
    }

    @After
    public void tearDown() {
    }
    
    /**
     * Test of getInstance method, of class ScriptContainer.
     */
    @Test
    public void testRunScriptlet() {
        System.out.println("[testRunScriptlet]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        //Writer writer = new StringWriter();
        //instance.setWriter(writer);

        instance.put("message", "Hello World!!!");
        instance.runScriptlet("puts message");

        instance.put("percentage", 80);
        instance.runScriptlet("STDOUT.print(\"This is \", percentage, \" percent.\n\")");
        
        instance.put("to", "Ashley");
        instance.runScriptlet("print \"<p>What's up? \", to, \"</p>\n\"");

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet2() {
        System.out.println("[testRunScriptlet 2]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        Writer writer = new StringWriter();
        instance.setWriter(writer);

        instance.put("percentage", 70);
        instance.runScriptlet("STDOUT.print(\"This is \", percentage, \" percent.\n\")");

        instance.put("to", "Ashley");
        instance.runScriptlet("print \"<p>What's up? \", to, \"</p>\n\"");

        String expResult = "This is 70 percent.\n<p>What's up? Ashley</p>\n";
        assertEquals(expResult, writer.toString());
        System.out.println(writer.toString());
        instance.resetWriter();

        instance.runScriptlet("puts 1+2");
        writer = new StringWriter();
        instance.setWriter(writer);
        instance.runScriptlet("puts 1+2");
        assertEquals("3\n", writer.toString());
        System.out.println(writer.toString());

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet3() {
        System.out.println("[testRunScriptlet 3]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        Writer writer = new StringWriter();
        instance.setWriter(writer);
        String str = "\u3053\u3093\u306b\u3061\u306f\u4e16\u754c";
        instance.runScriptlet("puts \"" + str + "\"");
        String expResult = "こんにちは世界\n";
        assertEquals(expResult, writer.toString());
        System.out.println(writer.toString());
        instance.resetWriter();

        instance.getVarMap().clear();
        instance = null;
    }
    
    @Test
    public void testRunScriptlet4() {
        System.out.println("[testRunScriptlet 4]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename =  "/Users/yoko/NetBeansProjects/jruby-embed/test/ruby/iteration.rb";
        Object receiver = instance.runScriptlet(PathType.ABSOLUTE, filename);
        EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
        String ret = adapter.callMethod(receiver, "repeat", new Integer(3), String.class);
        String expRet = "Trick or Treat!\nTrick or Treat!\nTrick or Treat!\n";
        assertEquals(expRet, ret);
        System.out.println(ret);

        instance.getVarMap().clear();
        instance = null;
    }


    @Test
    public void testRunScriptlet5() {
        System.out.println("[testRunScriptlet 5]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        instance.setAttribute(AttributeName.BASE_DIR, System.getProperty("user.dir"));
        String filename =  "test/ruby/iteration.rb";
        Object receiver = instance.runScriptlet(PathType.RELATIVE, filename);
        EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
        String ret = adapter.callMethod(receiver, "repeat", new Integer(2), String.class);
        String expRet = "Trick or Treat!\nTrick or Treat!\n";
        assertEquals(expRet, ret);
        System.out.println(ret);

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet6() {
        System.out.println("[testRunScriptlet 6]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename =  "ruby/iteration.rb";
        Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
        EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
        String ret = adapter.callMethod(receiver, "repeat", new Integer(1), String.class);
        String expRet = "Trick or Treat!\n";
        assertEquals(expRet, ret);
        System.out.println(ret);

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet7() {
        System.out.println("[testRunScriptlet 7]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename =  "ruby/calendar.rb";
        Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
        EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
        Integer result1 =
            (Integer) adapter.callMethod(receiver, "next_year", Integer.class);
        assertEquals(new Integer(2010), result1);
        System.out.println("next year: " + result1);
        System.out.println(instance.get("@today"));

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet8() {
        System.out.println("[runScriptlet 8]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula.rb";
        Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
        QuadraticFormula qf = instance.getInstance(receiver, QuadraticFormula.class);
        try {
            List<Double> solutions = qf.solve(1, -2, -13);
            printSolutions(solutions);
            solutions = qf.solve(1, -2, 13);
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet9() {
        System.out.println("[runScriptlet 9]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula_class.rb";
        Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
        QuadraticFormula qf = instance.getInstance(receiver, QuadraticFormula.class);
        try {
            List<Double> solutions = qf.solve(1, -2, -13);
            printSolutions(solutions);
            solutions = qf.solve(1, -2, 13);
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instance.getVarMap().clear();
        instance = null;
    }

    private void printSolutions(List<Double> solutions) {
        for (double s : solutions) {
            System.out.print(s + ", ");
        }
        System.out.println();
    }

    @Test
    public void testRunScriptlet10() {
        System.out.println("[runScriptlet 10]");
        //ScriptingContainer instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String script =
            "def get_area\n" +
              "@x * @y + Math::PI / 8.0 * @x ** 2.0\n" +
            "end\n" +
            "get_area";
        instance.put("@x", 1.0);
        instance.put("@y", 3.0);
        Double d = (Double) instance.runScriptlet(script);
        System.out.println(d);

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testRunScriptlet11() {
        System.out.println("[runScriptlet 11]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        Object year = instance.runScriptlet(PathType.CLASSPATH, "ruby/calendar2.rb");
        System.out.println("next year: " + year);
        System.out.println("today: " + instance.get("@today"));//can't get @today

        instance.getVarMap().clear();
        instance = null;
    }
}