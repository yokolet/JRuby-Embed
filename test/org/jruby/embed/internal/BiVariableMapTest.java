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
package org.jruby.embed.internal;

import org.jruby.embed.variable.Constant;
import org.jruby.embed.variable.GlobalVariable;
import org.jruby.embed.variable.InstanceVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jruby.embed.BiVariable;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.parser.EvalStaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.scope.ManyVarsDynamicScope;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Yoko Harada <yokolet@gmail.com>
 */
public class BiVariableMapTest {
    private final static String jrubyhome = "/Users/yoko/Tools/jruby-1.2.0";
    private ScriptingContainer container;
    private BiVariableMap instance;

    public BiVariableMapTest() {
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
        container = new ScriptingContainer();
        instance = container.getVarMap();
    }

    @After
    public void tearDown() {
        instance.clear();
        instance = null;
        container = null;
    }

    /**
     * Test of put method, of class JRubyVariableMap.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        String name = "@name";
        Object value = "poinsettia";
        Object expResult = null;
        Object result = instance.put(name, value);
        assertEquals(expResult, result);
        expResult = "poinsettia";
        result = instance.put(name, "camellia");
        assertEquals(expResult, result);
        name = "@@name";
        value = "camellia";
        expResult = null;
        //result = instance.put(name, value);
        //assertEquals(expResult, result);
    }

    /**
     * Test of getVariables method, of class JRubyVariableMap.
     */
    @Test
    public void testGetVariables() {
        System.out.println("getVariables");
        List<BiVariable> expResult = new ArrayList<BiVariable>();
        expResult.add(InstanceVariable.getInstance(container.getRuntime(), "@name", "camellia"));
        expResult.add(Constant.getInstance(container.getRuntime(), "COLOR", "red"));
        //expResult.add(JRubyClassVariable.getInstance(container.getRuntime(), "@@season", "spring"));
        expResult.add(GlobalVariable.getInstance(container.getRuntime(), "$category", "flower"));
        instance.put("@name", "camellia");
        instance.put("COLOR", "red");
        //instance.put("@@season", "spring");
        instance.put("$category", "flower");
        List<BiVariable> result = instance.getVariables();
        assertEquals(expResult.size(), result.size());
        //assertEquals(expResult.get(2).getJavaObject(), instance.get("@@season"));
    }

    /**
     * Test of getMap method, of class JRubyVariableMap.
     */
    @Test
    public void testGetMap() {
        System.out.println("getMap");
        Map expResult = new HashMap();
        expResult.put("@name", "camellia");
        expResult.put("COLOR", "red");
        //expResult.put("@@season", "spring");
        expResult.put("$category", "flower");
        instance.put("@name", "camellia");
        instance.put("COLOR", "red");
        //instance.put("@@season", "spring");
        instance.put("$category", "flower");
        Map result = instance.getMap();
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult.get("$category"), result.get("$category"));
    }

    /**
     * Test of size method, of class JRubyVariableMap.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        instance.put("@name", "camellia");
        instance.put("COLOR", "red");
        //instance.put("@@season", "spring");
        instance.put("$category", "flower");
        int expResult = 3;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class JRubyVariableMap.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
        instance.put("PI", new Double(3.14));
        expResult = false;
        result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of containsKey method, of class JRubyVariableMap.
     */
    @Test
    public void testContainsKey() {
        System.out.println("containsKey");
        String key = "";
        try {
            instance.containsKey(key);
        } catch (IllegalArgumentException e) {
            assertEquals("key is empty", e.getMessage());
        }
        //instance.put("@@song", "Bubbly");
        instance.put("@artist", "Colbie Caillat");
        boolean expResult = true;
        //boolean result = instance.containsKey("@@song");
        //assertEquals(expResult, result);
        expResult = true;
        boolean result = instance.containsKey("@artist");
        assertEquals(expResult, result);
        expResult = false;
        //result = instance.containsKey("@@artist");
        //assertEquals(expResult, result);
    }

    /**
     * Test of containsValue method, of class JRubyVariableMap.
     */
    @Test
    public void testContainsValue() {
        System.out.println("containsValue");
        //instance.put("@@song", "Bubbly");
        instance.put("@artist", "Colbie Caillat");
        Object value = "";
        boolean expResult = false;
        boolean result = instance.containsValue(value);
        assertEquals(expResult, result);
        value = "Colbie Caillat";
        expResult = true;
        result = instance.containsValue(value);
        assertEquals(expResult, result);
        value = "The Killers - Human";
        expResult = false;
        result = instance.containsValue(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of get method, of class JRubyVariableMap.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        String key = "";
        try {
            instance.get(key);
        } catch (IllegalArgumentException e) {
            assertEquals("key is empty", e.getMessage());
        }
        instance.put("@name", "camellia");
        instance.put("PI", new Double(3.14));
        //instance.put("@@list", new ArrayList());
        instance.put("$init", 0);
        Object expResult = "camellia";
        Object result = instance.get("@name");
        assertEquals(expResult, result);
        expResult = new Double(3.14);
        result = instance.get("PI");
        assertEquals(expResult, result);
        expResult = new ArrayList();
        //result = instance.get("@@list");
        //assertEquals(expResult, result);
        expResult = new Integer(0);
        result = instance.get("$init");
        assertEquals(expResult, result);
    }

    /**
     * Test of getVariable method, of class JRubyVariableMap.
     */
    @Test
    public void testGetVariable() {
        System.out.println("getVariable");
        String name = "";
        try {
            instance.getVariable(name);
        } catch (IllegalArgumentException e) {
            assertEquals("key is empty", e.getMessage());
        }
        instance.put("@name", "camellia");
        instance.put("PI", new Double(3.14));
        //instance.put("@@list", new ArrayList());
        instance.put("$init", 0);
        BiVariable expResult =
            InstanceVariable.getInstance(container.getRuntime(), "@name", "camellia");
        BiVariable result = instance.getVariable("@name");
        assertEquals(expResult.getType(), result.getType());
        assertEquals(expResult.getRubyObject().getClass(), result.getRubyObject().getClass());
        /*
        expResult =
            JRubyClassVariable.getInstance(container.getRuntime(), "@@list", new ArrayList());
        result = instance.getVariable("@@list");
        assertEquals(expResult.getType(), result.getType());
        */
    }


    /**
     * Test of remove method, of class JRubyVariableMap.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        String key = "";
        try {
            instance.remove(key);
        } catch (IllegalArgumentException e) {
            assertEquals("key is empty", e.getMessage());
        }
        key = "@name";
        Object expResult = null;
        Object result = instance.remove(key);
        assertEquals(expResult, result);
        String value = "camellia";
        instance.put(key, value);
        expResult = "camellia";
        result = instance.remove(key);
        assertEquals(expResult, result);
        assertFalse(instance.containsKey(key));
        assertFalse(instance.containsValue(result));
    }

    /**
     * Test of putAll method, of class JRubyVariableMap.
     */
    @Test
    public void testPutAll() {
        System.out.println("putAll");
        Map t = new HashMap();
        String key = "@name";
        String value = "camellia";
        t.put(key, value);
        instance.putAll(t);
        assertTrue(instance.containsKey(key));
        assertTrue(instance.containsValue(value));
        t.put(key, "poinsettia");
        t.put("COLOR", "red");
        //t.put("@@season", "spring");
        t.put("$category", "flower");
        instance.putAll(t);
        String expResult = "poinsettia";
        String result = (String) instance.get(key);
        assertEquals(expResult, result);
        assertEquals(3, instance.size());
        assertEquals("red", instance.get("COLOR"));
    }

    /**
     * Test of clear method, of class JRubyVariableMap.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        instance.clear();
        assertEquals(0, instance.size());
        instance.put("@name", "camellia");
        instance.put("PI", new Double(3.14));
        //instance.put("@@list", new ArrayList());
        instance.put("$init", 0);
        instance.inject(null, 0,null);
        assertEquals(3, instance.size());
        instance.clear();
        assertEquals(0, instance.size());
    }

    /**
     * Test of keySet method, of class JRubyVariableMap.
     */
    @Test
    public void testKeySet() {
        System.out.println("keySet");
        Set expResult = null;
        Set result = instance.keySet();
        assertEquals(expResult, result);
    }

    /**
     * Test of values method, of class JRubyVariableMap.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        Collection expResult = null;
        Collection result = instance.values();
        assertEquals(expResult, result);
        expResult = new ArrayList();
        expResult.add("camellia");
        expResult.add(new Double(3.14));
        //expResult.add(new ArrayList());
        expResult.add(0);
        instance.put("@name", "camellia");
        instance.put("PI", new Double(3.14));
        //instance.put("@@list", new ArrayList());
        instance.put("$init", 0);
        result = instance.values();
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult.contains("camellia"), result.contains("camellia"));
    }

    /**
     * Test of entrySet method, of class JRubyVariableMap.
     */
    @Test
    public void testEntrySet() {
        System.out.println("entrySet");
        Set expResult = null;
        Set result = instance.entrySet();
        assertEquals(expResult, result);
        instance.put("@name", "camellia");
        instance.put("PI", new Double(3.14));
        //instance.put("@@list", new ArrayList());
        instance.put("$init", 0);
        Set set = instance.entrySet();
        assertEquals(3, set.size());
        Iterator itr = set.iterator();
        //String[] keys = {"@name", "PI", "@@list", "$init"};
        //Object[] values = {"camellia", new Double(3.14), new ArrayList(), 0};
        String[] keys = {"@name", "PI", "$init"};
        Object[] values = {"camellia", new Double(3.14), 0};
        List keyList = Arrays.asList(keys);
        List valueList = Arrays.asList(values);
        while (itr.hasNext()) {
             Map.Entry e = (Map.Entry)itr.next();
             assertTrue(keyList.contains(e.getKey()));
             assertTrue(valueList.contains(e.getValue()));
        }
    }

    /**
     * Test of update method, of class JRubyVariableMap.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        String name = "";
        BiVariable value = null;
        instance.update(name, value);
    }


    /**
     * Test of getLocalVarNames and getLocalVarValues method, of class JRubyVariableMap.
     */
    @Test
    public void testGetLocalVarNamesAndValues() {
        System.out.println("getLocalVarNamesAndValues");
        String[] expResult = null;
        String[] result = instance.getLocalVarNames();
        assertEquals(expResult, result);
        expResult = new String[]{"field", "realm", "region"};
        instance.put("field", "geology");
        instance.put("realm", "global");
        instance.put("region", "northern hemisphere");
        result = instance.getLocalVarNames();
        assertEquals(expResult.length, result.length);
        for (int i=0; i < result.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
        IRubyObject[] expValues = new IRubyObject[]{
            JavaEmbedUtils.javaToRuby(container.getRuntime(), "geology"),
            JavaEmbedUtils.javaToRuby(container.getRuntime(), "global"),
            JavaEmbedUtils.javaToRuby(container.getRuntime(), "northern hemisphere")};
        IRubyObject[] retValues = instance.getLocalVarValues();
        assertEquals(expValues.length, retValues.length);
        for (int i=0; i < retValues.length; i++) {
            assertEquals(expValues[i], retValues[i]);
        }
    }

    /**
     * Test of inject method, of class JRubyVariableMap.
     */
    @Test
    public void testInject() {
        System.out.println("inject");
        instance.inject(null, 0,null);
        instance.put("field", "geology");
        instance.put("realm", "global");
        instance.put("@name", "camellia");
        instance.put("PI", new Double(3.14));
        //instance.put("@@list", new ArrayList());
        instance.put("$init", 0);
        int depth = 0;
        ThreadContext context = container.getRuntime().getCurrentContext();
        DynamicScope currentScope = context.getCurrentScope();
        ManyVarsDynamicScope scope =
            new ManyVarsDynamicScope(new EvalStaticScope(currentScope.getStaticScope(), instance.getLocalVarNames()), currentScope);
        instance.inject(scope, depth,null);
    }

    /**
     * Test of retrieve method, of class JRubyVariableMap.
     */
    @Test
    public void testRetrieve() {
        System.out.println("retrieve");
        IRubyObject receiver = null;
        instance.retrieve(receiver);
    }
}