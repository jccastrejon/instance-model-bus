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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 
 * @author jccastrejon
 * 
 */
public class RooHandler extends ImbHandler {

    @SuppressWarnings("unchecked")
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        File rooFile;
        File ecoreFile;
        IProject project;
        File installRooFile;
        Map<String, File> rooFiles;
        List<File> installRooFiles;
        List<EPackage> ecorePackages;
        MessageConsoleStream consoleStream;

        consoleStream = this.getConsoleStream();
        ecorePackages = (List<EPackage>) Util.getSelectedItems(event);

        // Generate the IMB artifacts for the selected Ecore packages
        this.clearConsole();
        for (EPackage ecorePackage : ecorePackages) {
            try {
                ecoreFile = Util.getEcoreFile(ecorePackage);

                // Roo and Bus scripts
                consoleStream.println("Generating roo scripts for package: " + ecorePackage.getName() + "...");
                rooFiles = Util.executeAtlQueries(this, ecoreFile, consoleStream);

                // Generate a new project for each roo script
                installRooFiles = new ArrayList<File>(rooFiles.size());
                for (String script : rooFiles.keySet()) {
                    rooFile = rooFiles.get(script);
                    project = Util.createProject(ecoreFile.getName().replace(".ecore", "." + script));
                    installRooFile = new File(project.getLocation().toFile(), rooFile.getName());

                    installRooFiles.add(installRooFile);
                    FileUtils.copyFile(rooFile, installRooFile);
                    rooFile.delete();
                }
            } catch (Exception e) {
                consoleStream.println("The Ecore package could not be successfully transformed to a Spring Roo script");
                e.printStackTrace(new PrintStream(consoleStream));
            }
        }

        return null;
    }
}
