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

import java.io.FileNotFoundException;
import java.io.FileReader;
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
public class ScriptsRunnerTest {

    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.4.0RC3";
    private ScriptingContainer instance;

    public ScriptsRunnerTest() {
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
        instance = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
        String[] paths =
            System.getProperty("org.jruby.embed.class.path").split(System.getProperty("path.separator"));
        List<String> loadPaths = Arrays.asList(paths);
        instance.getProvider().setLoadPaths(loadPaths);
    }

    @After
    public void tearDown() {
        //instance.getVarMap().clear();
        instance = null;
    }

    private void printVariables(String filename) {
        Map map = instance.getVarMap();
        Set<String> keys = map.keySet();
        if (keys == null) {
            return;
        }
        for (String key : keys) {
            System.out.println("[" + filename + "] " + key + ": " + instance.get(key));
        }
    }

    @Test
    public void testFirstScript() throws FileNotFoundException {
        System.out.println("[single script executor 1]");
        String filename = jrubyhome + "/test/test_big_decimal.rb";
        instance.runScriptlet(new FileReader(filename), filename);
        printVariables(filename);
    }

    @Test
    public void testSecondScript() throws FileNotFoundException {
        System.out.println("[single script executor 2]");
        String filename = jrubyhome + "/test/testInspect.rb";
        instance.runScriptlet(new FileReader(filename), filename);
        printVariables(filename);
    }

    @Test
    public void testThirdScript() throws FileNotFoundException {
        System.out.println("[single script executor 3]");
        String filename = jrubyhome + "/test/testException.rb";
        instance.runScriptlet(new FileReader(filename), filename);
        printVariables(filename);
    }

    @Test
    public void testFourthScript() throws FileNotFoundException {
        System.out.println("[single script executor 4]");
        String filename = jrubyhome + "/test/testInstantiatingInterfaces.rb";
        instance.runScriptlet(new FileReader(filename), filename);
        printVariables(filename);
    }
}