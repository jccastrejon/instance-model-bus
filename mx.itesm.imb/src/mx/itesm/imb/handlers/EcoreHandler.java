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
package mx.itesm.imb.handlers;

import java.io.File;
import java.util.List;

import mx.itesm.imb.EcoreImbEditor;
import mx.itesm.imb.ImbBusController;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 
 * @author jccastrejon
 * 
 */
public class EcoreHandler extends ImbHandler {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        List<IProject> projects;
        File ecoreProject;
        File busProject;
        File rooProject;
        File templateProject;
        MessageConsoleStream consoleStream;

        consoleStream = this.getConsoleStream();
        projects = (List<IProject>) Util.getSelectedItems(event);

        this.clearConsole();
        for (IProject project : projects) {
            ecoreProject = project.getLocation().toFile();
            rooProject = new File(ecoreProject.getParentFile(), project.getName() + ".roo");
            busProject = new File(ecoreProject.getParentFile(), project.getName() + ".bus");
            // TODO: Change for properties file
            templateProject = new File("/Users/jccastrejon/java/workspace_IMB/InstanceModelBusEcore");

            consoleStream.println("Modifying the IMB Bus associated to the " + project.getName() + " model...");
            ImbBusController.generateImbBusController(rooProject, busProject);
            
            consoleStream.println("Modifying the Ecore editor associated to the " + project.getName() + " model...");
            EcoreImbEditor.configureRestTemplate(ecoreProject, busProject, templateProject);
            EcoreImbEditor.createRooApplication(ecoreProject, busProject, templateProject);
            EcoreImbEditor.generateProvidersArtifacts(ecoreProject, templateProject);
        }

        return null;
    }
}
