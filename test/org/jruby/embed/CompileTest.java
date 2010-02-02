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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import org.jruby.RubyInstanceConfig;
import org.jruby.RubyInstanceConfig.CompileMode;
import org.jruby.javasupport.JavaEmbedUtils.EvalUnit;
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
public class CompileTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.4.0RC3";
    private int max = 10;

    public CompileTest() {
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
        
    }

    @After
    public void tearDown() {
    }
    
    /**
     * Test of getInstance method, of class ScriptContainer.
     */
    @Test
    public void testCompileModeOff() throws FileNotFoundException {
        System.out.println("[testCompileModeOff]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        LocalContextProvider provider = instance.getProvider();
        RubyInstanceConfig config = provider.getRubyInstanceConfig();
        config.setCompileMode(CompileMode.OFF);
        String filename = jrubyhome + "/test/testString.rb";
        instance.runScriptlet(PathType.ABSOLUTE, filename);
        long start = Calendar.getInstance().getTimeInMillis();
        instance.runScriptlet(PathType.ABSOLUTE, filename);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);

        InputStream istream = new FileInputStream(filename);
        EvalUnit unit = instance.parse(istream, filename);
        start = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < max; i++) {
            unit.run();
        }
        end = Calendar.getInstance().getTimeInMillis();
        System.out.println("After " + max + " times' evaluations: " + (end - start));
        provider = null;
        instance = null;
    }

    @Test
    public void testCompileModeJit() throws FileNotFoundException {
        System.out.println("[testCompileModeJit]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        LocalContextProvider provider = instance.getProvider();
        RubyInstanceConfig config = provider.getRubyInstanceConfig();
        config.setCompileMode(CompileMode.JIT);
        String filename = jrubyhome + "/test/testString.rb";
        instance.runScriptlet(PathType.ABSOLUTE, filename);
        long start = Calendar.getInstance().getTimeInMillis();
        instance.runScriptlet(PathType.ABSOLUTE, filename);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
        
        InputStream istream = new FileInputStream(filename);
        EvalUnit unit = instance.parse(istream, filename);
        start = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < max; i++) {
            unit.run();
        }
        end = Calendar.getInstance().getTimeInMillis();
        System.out.println("After " + max + " times' evaluations: " + (end - start));
        instance = null;
    }

    @Test
    public void testCompileModeForce() throws FileNotFoundException {
        System.out.println("[testCompileModeForce]");
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        LocalContextProvider provider = instance.getProvider();
        RubyInstanceConfig config = provider.getRubyInstanceConfig();
        config.setCompileMode(CompileMode.FORCE);
        String filename = jrubyhome + "/test/testString.rb";
        instance.runScriptlet(PathType.ABSOLUTE, filename);
        long start = Calendar.getInstance().getTimeInMillis();
        instance.runScriptlet(PathType.ABSOLUTE, filename);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);

        InputStream istream = new FileInputStream(filename);
        EvalUnit unit = instance.parse(istream, filename);
        start = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < max; i++) {
            unit.run();
        }
        end = Calendar.getInstance().getTimeInMillis();
        System.out.println("After " + max + " times' evaluations: " + (end - start));
        instance = null;
    }
}