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
public class PropertyNameTest {

    public PropertyNameTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
     * Test of values method, of class PropertyName.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        PropertyName[] result = PropertyName.values();
        assertEquals(6, result.length);
        String[] names = {
            "org.jruby.embed.class.path",
            "org.jruby.embed.localcontext.scope",
            "org.jruby.embed.localvariable.behavior",
            "org.jruby.embed.compilemode",
            "org.jruby.embed.linenumber",
            "org.jruby.embed.compat.version"
        };
        for (int i=0; i<result.length; i++) {
            assertEquals(result[i].toString(), names[i]);
        }
    }

    /**
     * Test of getType method, of class PropertyName.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        String[] names = {
            "org.jruby.embed.class.path",
            "org.jruby.embed.localcontext.scope",
            "org.jruby.embed.localvariable.behavior",
            "org.jruby.embed.compilemode",
            "org.jruby.embed.linenumber"
        };
        for (int i=0; i<names.length; i++) {
            PropertyName result = PropertyName.getType(names[i]);
            assertEquals(i, result.ordinal());
        }
    }

}