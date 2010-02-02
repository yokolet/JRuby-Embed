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
import org.jruby.exceptions.RaiseException;
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
public class IoTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.3.1";
    private ScriptingContainer instance;

    public IoTest() {
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
        instance = new ScriptingContainer();
        String[] paths =
            System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        List<String> loadPaths = Arrays.asList(paths);
    }

    @After
    public void tearDown() {
        instance.getVarMap().clear();
        instance = null;
    }
    
    /**
     * Test of getInstance method, of class ScriptContainer.
     */
    @Test
    public void testRunScriptlet() {
        System.out.println("[testRunScriptlet]");
        instance.runScriptlet("STDOUT.print(\"This should be  print to \", $stdout)");
        
        Writer writer = new StringWriter();
        instance.setWriter(writer);
        instance.runScriptlet("STDOUT.print(\"This should be  print to \", $stdout)");
        String expResult = "This should be  print to";
        String result = writer.toString();
        assertTrue(result.startsWith(expResult));
        System.out.println("\nStringWriter: " + writer.toString() + "\n");
    }

    @Test
    public void testRunScriptlet2() {
        System.out.println("[testRunScriptlet 2]");
        instance.resetWriter();
        instance.runScriptlet("STDOUT.print \"Alive?\"");
        String filename =  "ruby/quiet.rb";
        Writer writer = new StringWriter();
        instance.setWriter(writer);
        Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
        String expRet = "foo";
        //assertEquals(expRet, writer.toString()); //never successes
        System.out.println("StringBuffer: " + writer.toString()); //empty
        instance.runScriptlet("STDOUT.print \"Still alive?\""); //doesn't show up
        instance.resetWriter();
        instance.runScriptlet("STDOUT.print \"Should revive.\"");
        instance.runScriptlet(PathType.CLASSPATH, filename);
    }

    @Test
    public void testRunScriptlet3() {
        System.out.println("[testRunScriptlet 3]");
        try {
            instance.runScriptlet("puts \"Hello...");
        } catch (RaiseException re) {
            re.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        StringWriter writer = new StringWriter();
        instance.setErrorWriter(writer);
        try {
            instance.runScriptlet("puts \"Hello...");
        } catch (RaiseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        System.out.println("ERROR: " + writer.toString());

        try {
            instance.runScriptlet("puts 1/0");
        } catch (RaiseException re) {
            re.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        System.out.println("2nd ERROR: " + writer.toString());

        instance.resetErrorWriter();
        try {
            instance.runScriptlet("puts 1.0 / a");
        } catch (RaiseException re) {
            re.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        System.out.println("3rd ERROR: " + writer.toString());
    }
}