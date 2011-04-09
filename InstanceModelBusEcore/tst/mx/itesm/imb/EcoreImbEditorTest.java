/*
 * Copyright 2011 jccastrejon
 *  
 * This file is part of InstanceModelBus.
 *
 * InstanceModelBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * InstanceModelBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with InstanceModelBus.  If not, see <http://www.gnu.org/licenses/>.
*/
package mx.itesm.imb;

import java.io.File;

import junit.framework.TestCase;

/**
 * 
 * @author jccastrejon
 * 
 */
public class EcoreImbEditorTest extends TestCase {
    public void testEditor() {
        EcoreImbEditor.configureRestTemplate(new File("/Users/jccastrejon/java/workspace_IMB/TodoList.ecore"),
                new File("/Users/jccastrejon/java/workspace_IMB/TodoList.roo"), new File(
                        "/Users/jccastrejon/java/workspace_IMB/InstanceModelBusEcore"));
        EcoreImbEditor.createRooApplication(new File("/Users/jccastrejon/java/workspace_IMB/TodoList.ecore"),
                new File("/Users/jccastrejon/java/workspace_IMB/TodoList.roo"), new File(
                        "/Users/jccastrejon/java/workspace_IMB/InstanceModelBusEcore"));
        EcoreImbEditor.generateProvidersArtifacts(new File(
                "/Users/jccastrejon/java/workspace_IMB/TodoList.ecore"), new File(
                "/Users/jccastrejon/java/workspace_IMB/InstanceModelBusEcore"));
    }
}
