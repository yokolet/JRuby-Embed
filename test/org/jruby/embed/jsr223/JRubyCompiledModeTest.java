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

import java.io.FileReader;
import java.io.StringWriter;
import java.util.Calendar;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
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
public class JRubyCompiledModeTest {
    private final static String jrubyhome = "/Users/yoko/Works/091709-jruby/jruby~main";
    private final static int iteration = 100;
    private String filename = jrubyhome + "/test/testString.rb";
    private String localContextScope = "threadsafe";

    public JRubyCompiledModeTest() {
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

    @After
    public void tearDown() {
    }

    @Test
    public void testCompileMode_Off() throws Exception {
        System.out.println("CompileMode off");
        System.setProperty("org.jruby.embed.localcontext.scope", localContextScope);
        JRubyEngineFactory factory = new JRubyEngineFactory();
        ScriptEngine engine = factory.getScriptEngine();
        FileReader reader = new FileReader(filename);
        long start = Calendar.getInstance().getTimeInMillis();
        engine.eval(reader);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
    }

    @Test
    public void testCompileMode_Jit() throws Exception {
        System.out.println("CompileMode jit");
        System.setProperty("org.jruby.embed.localcontext.scope", localContextScope);
        System.setProperty("org.jruby.embed.compilemode", "jit");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        ScriptEngine engine = factory.getScriptEngine();
        FileReader reader = new FileReader(filename);
        long start = Calendar.getInstance().getTimeInMillis();
        engine.eval(reader);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
    }

    @Test
    public void testCompileMode_Force() throws Exception {
        System.out.println("CompileMode force");
        System.setProperty("org.jruby.embed.localcontext.scope", localContextScope);
        System.setProperty("org.jruby.embed.compilemode", "force");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        ScriptEngine engine = factory.getScriptEngine();
        FileReader reader = new FileReader(filename);
        long start = Calendar.getInstance().getTimeInMillis();
        engine.eval(reader);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
    }

    @Test
    public void testCompileMode_Off_multieval() throws Exception {
        System.out.println("CompileMode off, multiple evals");
        System.setProperty("org.jruby.embed.localcontext.scope", localContextScope);
        System.setProperty("org.jruby.embed.compilemode", "off");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        ScriptEngine engine = factory.getScriptEngine();
        FileReader reader = new FileReader(filename);
        CompiledScript compiled = ((Compilable)engine).compile(reader);
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.getContext().setErrorWriter(writer);
        long start = Calendar.getInstance().getTimeInMillis();
        for (int i=0; i< iteration; i++) {
            compiled.eval();
        }
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
    }

    @Test
    public void testCompileMode_Jit_multieval() throws Exception {
        System.out.println("CompileMode jit, multiple evals");
        System.setProperty("org.jruby.embed.localcontext.scope", localContextScope);
        System.setProperty("org.jruby.embed.compilemode", "jit");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        ScriptEngine engine = factory.getScriptEngine();
        FileReader reader = new FileReader(filename);
        CompiledScript compiled = ((Compilable)engine).compile(reader);
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.getContext().setErrorWriter(writer);
        long start = Calendar.getInstance().getTimeInMillis();
        for (int i=0; i< iteration; i++) {
            compiled.eval();
        }
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
    }

    @Test
    public void testCompileMode_Force_multieval() throws Exception {
        System.out.println("CompileMode force, multiple evals");
        System.setProperty("org.jruby.embed.localcontext.scope", localContextScope);
        System.setProperty("org.jruby.embed.compilemode", "force");
        JRubyEngineFactory factory = new JRubyEngineFactory();
        ScriptEngine engine = factory.getScriptEngine();
        FileReader reader = new FileReader(filename);
        CompiledScript compiled = ((Compilable)engine).compile(reader);
        StringWriter writer = new StringWriter();
        engine.getContext().setWriter(writer);
        engine.getContext().setErrorWriter(writer);
        long start = Calendar.getInstance().getTimeInMillis();
        for (int i=0; i< iteration; i++) {
            compiled.eval();
        }
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println(end - start);
    }
}