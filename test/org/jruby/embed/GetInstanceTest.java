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

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class GetInstanceTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.4.0RC3";
    private List<String> loadPaths;
    
    //private Ruby runtime;

    public GetInstanceTest() {
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
    public void testGetInstance() {
        System.out.println("[getInstance]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        Object receiver = instance.runScriptlet(istream, filename);
        QuadraticFormula qf = instance.getInstance(receiver, QuadraticFormula.class);
        try {
            List<Double> solutions = qf.solve(1, -2, -15);
            printSolutions(solutions);
            solutions = qf.solve(1, -2, 15);
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instance.getVarMap().clear();
            instance = null;
        }
    }

    private void printSolutions(List<Double> solutions) {
        for (double s : solutions) {
            System.out.print(s + ", ");
        }
        System.out.println();
    }

    @Test
    public void testGetInstance_2() {
        System.out.println("[getInstance 2]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula_class.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        Object receiver = instance.runScriptlet(istream, filename);
        QuadraticFormula qf = instance.getInstance(receiver, QuadraticFormula.class);
        try {
            List<Double> solutions = qf.solve(1, -2, -13);
            printSolutions(solutions);
            solutions = qf.solve(1, -2, 13);
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instance.getVarMap().clear();
            instance = null;
        }
    }

    @Test //local vars injection does not work with method invocation
    public void testGetInstance_3() {
        System.out.println("[getInstance 3]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula_localvars.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        instance.put("a", 2);
        instance.put("b", -1);
        instance.put("c", -3);
        Object receiver = instance.runScriptlet(istream, filename);
        QuadraticFormulaNoArg qf = instance.getInstance(receiver, QuadraticFormulaNoArg.class);
        try {
            List<Double> solutions = qf.solve();
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map m = instance.getVarMap();
        Set<String> keys = instance.getVarMap().keySet();
        for (String key : keys) {
            System.out.println(key + ", " + m.get(key));
        }
        
        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testGetInstance_4() {
        System.out.println("[getInstance 4]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula_globalvars.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        instance.put("$a", 2);
        instance.put("$b", -1);
        instance.put("$c", -3);
        Object receiver = instance.runScriptlet(istream, filename);
        QuadraticFormulaNoArg qf = instance.getInstance(receiver, QuadraticFormulaNoArg.class);
        try {
            List<Double> solutions = qf.solve();
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map m = instance.getVarMap();
        Set<String> keys = instance.getVarMap().keySet();
        for (String key : keys) {
            System.out.println(key + ", " + m.get(key));
        }
        
        instance.getVarMap().clear();
        instance = null;
    }

    @Test //instance vars injection does not work
    public void testGetInstance_5() {
        System.out.println("[getInstance 5]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);

        String filename = "ruby/quadratic_formula_instancevars.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        instance.put("@a", 2);
        instance.put("@b", -1);
        instance.put("@c", -3);
        Object receiver = instance.runScriptlet(istream, filename);
        QuadraticFormulaNoArg qf = instance.getInstance(receiver, QuadraticFormulaNoArg.class);
        try {
            List<Double> solutions = qf.solve();
            printSolutions(solutions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map m = instance.getVarMap();
        Set<String> keys = instance.getVarMap().keySet();
        for (String key : keys) {
            System.out.println(key + ", " + m.get(key));
        }

        instance.getVarMap().clear();
        instance = null;
    }

    @Test
    public void testGetInstance_6() {
        System.out.println("[getInstance 6]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.THREADSAFE);
        instance.getProvider().setLoadPaths(loadPaths);
        
        String filename = "ruby/greetings_imple.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        Object receiver = instance.runScriptlet(istream, filename);
        Greetings g = instance.getInstance(receiver, Greetings.class);
        g.hello("Corbin");
        g.bye("Zac");
        g.hello("たろ");
        g.bye("じろ");

        instance.setWriter(null);
        Writer writer = new StringWriter();
        instance.setWriter(writer);
        g.hello("London");
        g.bye("Beijing");
        g.hello("ロンドン");
        g.bye("北京");
        System.out.println(writer.toString());

        instance.getVarMap().clear();
        instance = null;
    }
}