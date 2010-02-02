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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jruby.CompatVersion;
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
public class MultipleScriptsRunner {

    private final static String jrubyhome = "/Users/yoko/Works/090209-jruby/jruby~main";

    public MultipleScriptsRunner() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {        
        System.setProperty("jruby.home", jrubyhome);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testScripts() throws FileNotFoundException {
        System.out.println("[test scripts runner]");
        String[] paths = {
            jrubyhome + "/lib/ruby/1.8",
            jrubyhome + "/lib/ruby/site_ruby/1.8",
            jrubyhome + "/test",
            jrubyhome + "/build/classes/test",
            jrubyhome
        };
        List<String> loadPaths = Arrays.asList(paths);
        String testDir = jrubyhome + "/test";
        List<String> filenames = new ArrayList<String>();
        getRubyFileNames(testDir, filenames);
        Iterator itr = filenames.iterator();
        ScriptingContainer instance;
        while (itr.hasNext()) {
            try {
                String testname = (String) itr.next();
                System.out.println("\n[" + testname + "]");
                if (!isTestable(testname)) {
                    System.out.println("+++++ skip " + testname);
                    continue;
                }
                instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
                //ScriptingContainer instance = new ScriptingContainer();
                instance.getProvider().setLoadPaths(loadPaths);
                instance.getProvider().getRubyInstanceConfig().setObjectSpaceEnabled(true);
                instance.runScriptlet(new FileReader(testname), testname);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                instance = null;
            }
        }
    }

    private static void getRubyFileNames(String testDir, List<String> filenames) {
        File[] files = new File(testDir).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String filename = file.getName();
                if (filename.endsWith("rb")) {
                    filenames.add(testDir + "/" + filename);
                }
            } else if (file.isDirectory()) {
                //String nextDir = testDir + "/" + file.getName();
                //getRubyFileNames(nextDir, filenames);
            }
        }
    }

    private boolean isTestable(String filename) {
        String[] skipList = {
            "test_file.rb",
            "test_jar_on_load_path.rb",
            "test_jarred_gems_with_spaces_in_directory.rb",
            "test_kernel.rb",
            "test_launching_by_shell_script.rb",
            "test_load.rb",
            "test_load_compiled_ruby_class_from_classpath.rb",
            "test_local_jump_error.rb",
            "test_numeric.rb",
            "test_object_class_default_methods.rb",
            "test_primitive_to_java.rb",
            "test_thread_backtrace.rb",
            "test_encoding_1_9.rb",
            "test_enumerator_1_9.rb",
            "test_io_1_9.rb",
            "test_kernel_1_9_features.rb",
            "test_loading_builtin_libraries_1_9.rb",
            "test_object_1_9.rb",
            "test_symbol_1_9.rb",
            "test_time_1_9.rb"
        };
        for (int i = 0; i < skipList.length; i++) {
            String name = jrubyhome + "/test/" + skipList[i];
            if (filename.equals(name)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testRuby19Script() throws FileNotFoundException {
        System.out.println("[ruby 1.9 script]");
        String[] paths = {
            jrubyhome + "/lib/ruby/1.9",
            jrubyhome + "/lib/ruby/site_ruby",
            jrubyhome + "/test",
            jrubyhome
        };
        List<String> loadPaths = Arrays.asList(paths);
        String[] testFiles = {
            "test_encoding_1_9.rb",
            "test_enumerator_1_9.rb",
            "test_io_1_9.rb",
            "test_kernel_1_9_features.rb",
            "test_loading_builtin_libraries_1_9.rb",
            "test_object_1_9.rb",
            "test_symbol_1_9.rb",
            "test_time_1_9.rb"
        };
        String testname;
        ScriptingContainer instance;
        for (int i = 0; i < testFiles.length; i++) {
            testname = jrubyhome + "/test/" + testFiles[i];
            System.out.println("\n[" + testname + "]");
            try {
                instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
                instance.getProvider().setLoadPaths(loadPaths);
                instance.getProvider().getRubyInstanceConfig().setCompatVersion(CompatVersion.RUBY1_9);
                instance.runScriptlet(new FileReader(testname), testname);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                instance = null;
            }
        }
    }

    //@Test
    public void testTestFile() throws FileNotFoundException {
        System.out.println("[test_file.rb]");
        String testname = jrubyhome + "/test/test_file.rb";
        String[] paths = {
            jrubyhome + "/lib/ruby/1.8",
            jrubyhome + "/lib/ruby/site_ruby",
            jrubyhome + "/test",
            jrubyhome
        };
        List<String> loadPaths = Arrays.asList(paths);
        ScriptingContainer instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        instance.getProvider().setLoadPaths(loadPaths);
        instance.runScriptlet(new FileReader(testname), testname);
    }
}
