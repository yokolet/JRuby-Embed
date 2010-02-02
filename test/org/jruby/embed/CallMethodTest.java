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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class CallMethodTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.4.0RC3";
    private ScriptingContainer container;

    public CallMethodTest() {
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
        container = new ScriptingContainer(LocalContextScope.THREADSAFE);
        String[] paths =
            System.getProperty("org.jruby.embed.class.path").split(System.getProperty("path.separator"));
        List<String> loadPaths = Arrays.asList(paths);
        container.getProvider().setLoadPaths(loadPaths);
        container.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyhome);
    }

    @After
    public void tearDown() {
        container.getVarMap().clear();
        container = null;
    }
    
    /**
     * Test of getInstance method, of class ScriptContainer.
     */
    @Test
    public void testCallMathod() {
        System.out.println("[testCallMethod]");
        container.put("@who", new String("a"));
        List<String> people = new ArrayList<String>();
        people.add("b");
        container.put("@people", people);
        String filename = "ruby/greetings_instancevars.rb";
        InputStream istream = getClass().getClassLoader().getResourceAsStream(filename);
        EmbedEvalUnit unit = container.parse(istream, filename);
        IRubyObject receiver = unit.run();

        container.put("@who", new String("R2-D2"));
        people.clear();
        people.add("Anakin");
        people.add("Obi-Wan");
        people.add("Padme");
        people.add("C-3PO");
        EmbedRubyObjectAdapter adapter = container.newObjectAdapter();
        Object result = (String) adapter.callMethod(receiver, "greet", String.class, unit);
        System.out.println(result);

        result = adapter.callMethod(receiver, "sayhi", null, unit);

        result = (Integer) adapter.callMethod(receiver, "count", Integer.class, unit);
        System.out.println(result + " people(?) in total.");
    }

    /**
     * Test of getInstance method, of class ScriptContainer.
     */
    @Test
    public void testCallMathod2() {
        System.out.println("[testCallMethod 2]");
        container.put("@who", new String("a"));
        List<String> people = new ArrayList<String>();
        people.add("b");
        container.put("@people", people);
        String filename = "ruby/greetings_instancevars.rb";
        EmbedEvalUnit unit = container.parse(PathType.CLASSPATH, filename);
        IRubyObject receiver = unit.run();

        container.put("@who", new String("Dobby"));
        people.clear();
        people.add("Nearly Headless Nick");
        people.add("Moaning Myrtle");
        people.add("Harry Potter");
        people.add("Albus Dumbledore");
        Object result = (String) container.callMethod(receiver, "greet", String.class);
        System.out.println(result);

        result = container.callMethod(receiver, "sayhi", null, unit);

        result = (Integer) container.callMethod(receiver, "count", Integer.class);
        System.out.println(result + " people(?) in total.");
    }
}