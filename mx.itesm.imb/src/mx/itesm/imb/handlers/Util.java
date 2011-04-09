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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import mx.itesm.mdd.EcoreDataTypeConverter;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2m.atl.common.ATLLogFormatter;
import org.eclipse.m2m.atl.common.ATLLogger;
import org.eclipse.m2m.atl.common.ConsoleStreamHandler;
import org.eclipse.m2m.atl.drivers.emf4atl.ASMEMFModel;
import org.eclipse.m2m.atl.drivers.emf4atl.AtlEMFModelHandler;
import org.eclipse.m2m.atl.engine.vm.AtlLauncher;
import org.eclipse.m2m.atl.engine.vm.AtlModelHandler;
import org.eclipse.m2m.atl.engine.vm.ModelLoader;
import org.eclipse.m2m.atl.engine.vm.nativelib.ASMModel;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jdom.JDOMException;

/**
 * 
 * @author jccastrejon
 * 
 */
public class Util {

    /**
     * 
     * @param currentObject
     * @param ecoreFile
     * @param consoleStream
     * @throws IOException
     * @throws JDOMException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, File> executeAtlQueries(final Object currentObject, final File ecoreFile,
            final MessageConsoleStream consoleStream) throws IOException, JDOMException {
        File imbEcoreFile;
        Map<String, URL> libraries;
        Map<String, File> returnValue;

        // Load query libraries
        libraries = new HashMap<String, URL>();
        libraries.put("Enum", Util.getResourceURL(currentObject, "Enum.asm"));
        libraries.put("Entity", Util.getResourceURL(currentObject, "Entity.asm"));

        // Ecore2ImbEcore query to generate a valid Ecore editor
        imbEcoreFile = Util.executeAtl("Ecore2ImbEcore", currentObject, ecoreFile, Collections.EMPTY_MAP, true,
                consoleStream);
        EcoreDataTypeConverter.convertTypes(imbEcoreFile);

        // Execute the ATL roo and bus queries
        returnValue = new HashMap<String, File>(2);
        returnValue.put("roo",
                Util.executeAtl("Ecore2Roo", currentObject, imbEcoreFile, libraries, false, consoleStream));
        returnValue.put("bus",
                Util.executeAtl("Ecore2Bus", currentObject, imbEcoreFile, libraries, false, consoleStream));

        return returnValue;
    }

    /**
     * Get the current selected elements when a plugin operation is executed.
     * 
     * @param event
     * @return
     */
    public static List<?> getSelectedItems(final ExecutionEvent event) {
        List<?> returnValue;
        IStructuredSelection currentSelection;

        currentSelection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        returnValue = currentSelection.toList();

        return returnValue;
    }

    /**
     * Get the Ecore file associated to the given Ecore package.
     * 
     * @param ecorePackage
     * @return
     */
    public static File getEcoreFile(final EPackage ecorePackage) {
        File returnValue;
        String ecoreFilePath;
        String workspacePath;

        workspacePath = Util.getWorkspacePath();
        ecoreFilePath = ecorePackage.eResource().getURI().toPlatformString(false);
        returnValue = new File(workspacePath + "/" + ecoreFilePath);

        return returnValue;
    }

    /**
     * Get the Ecore file associated to the given Eclipse resource.
     * 
     * @param resource
     * @return
     */
    public static File getEcoreFile(final Resource resource) {
        File returnValue;
        String ecoreFilePath;
        String workspacePath;

        workspacePath = Util.getWorkspacePath();
        ecoreFilePath = resource.getURI().toPlatformString(false);
        returnValue = new File(workspacePath + "/" + ecoreFilePath);

        return returnValue;
    }

    /**
     * 
     * @param name
     * @throws CoreException
     */
    public static IProject createProject(final String name) throws CoreException {
        IProject returnValue;
        IProgressMonitor progressMonitor;

        progressMonitor = new NullProgressMonitor();
        returnValue = ResourcesPlugin.getWorkspace().getRoot().getProject(name);

        // Delete previous results and create a blank project
        if (returnValue.exists()) {
            returnValue.delete(true, progressMonitor);
        }

        returnValue.create(progressMonitor);
        returnValue.open(progressMonitor);

        return returnValue;
    }

    /**
     * 
     * @param atlName
     * @param currentObject
     * @param ecoreFile
     * @param libraries
     * @param consoleStream
     * @throws IOException
     */
    @SuppressWarnings({ "deprecation" })
    private static File executeAtl(final String atlName, final Object currentObject, final File ecoreFile,
            Map<String, URL> libraries, final boolean isOut, final MessageConsoleStream consoleStream)
            throws IOException {
        URL atlQuery;
        File returnValue;
        Handler rooHandler;
        ModelLoader modelLoader;
        ASMModel ecoreMetaModel;
        AtlEMFModelHandler modelHandler;
        Map<String, Object> transformationModels;

        // The roo script will be regenerated
        returnValue = new File(ecoreFile.getAbsolutePath().substring(0,
                ecoreFile.getAbsolutePath().lastIndexOf(".ecore"))
                + atlName.substring(atlName.indexOf('2')) + ".roo");
        returnValue.delete();
        returnValue.createNewFile();

        // Ours will be the only ATL logger
        for (Handler logHandler : ATLLogger.getLogger().getHandlers()) {
            ATLLogger.getLogger().removeHandler(logHandler);
        }
        rooHandler = new ConsoleStreamHandler(new BufferedOutputStream(new FileOutputStream(returnValue)));
        rooHandler.setFormatter(ATLLogFormatter.INSTANCE);
        rooHandler.setLevel(Level.ALL);
        ATLLogger.getLogger().addHandler(rooHandler);

        // ATL execution
        atlQuery = Util.getResourceURL(currentObject, atlName + ".asm");
        modelHandler = (AtlEMFModelHandler) AtlModelHandler.getDefault(AtlModelHandler.AMH_EMF);
        modelLoader = modelHandler.createModelLoader();
        ecoreMetaModel = modelLoader.loadModel("Ecore", null, "uri:http://www.eclipse.org/emf/2002/Ecore");
        transformationModels = new HashMap<String, Object>();
        transformationModels.put("Ecore", ecoreMetaModel);
        transformationModels.put("IN",
                modelLoader.loadModel("IN", ecoreMetaModel, new BufferedInputStream(new FileInputStream(ecoreFile))));

        // Create output model
        if (isOut) {
            transformationModels.put("OUT", modelLoader.newModel("OUT", ecoreMetaModel));
        }

        consoleStream.println("Generating Roo script: " + returnValue.getAbsolutePath() + "...");
        AtlLauncher.getDefault().launch(atlQuery, libraries, transformationModels, Collections.EMPTY_MAP,
                Collections.EMPTY_LIST, Collections.EMPTY_MAP);

        // Save output model in the original file
        if (isOut) {
            returnValue.delete();
            returnValue = new File(ecoreFile.getParentFile(), ecoreFile.getName().replace(".ecore", ".imb.ecore"));
            returnValue.delete();
            returnValue.createNewFile();
            ((ASMEMFModel) transformationModels.get("OUT")).getExtent().save(new FileOutputStream(returnValue),
                    Collections.EMPTY_MAP);
        }

        consoleStream.println("Roo script successfully generated!");
        return returnValue;
    }

    /**
     * Get the URL of the given resourceName, according to the class path of the
     * current object.
     * 
     * @param currentObject
     * @param resourceName
     * @return
     */
    public static URL getResourceURL(final Object currentObject, final String resourceName) {
        URL returnValue;
        ClassLoader currentClassLoader;

        currentClassLoader = Util.getCurrentClassLoader(currentObject);
        returnValue = currentClassLoader.getResource(resourceName);

        return returnValue;
    }

    /**
     * Get the class loader associated to the specified object.
     * 
     * @param currentObject
     * @return
     */
    private static ClassLoader getCurrentClassLoader(final Object currentObject) {
        ClassLoader returnValue;

        returnValue = Thread.currentThread().getContextClassLoader();
        if (returnValue == null) {
            returnValue = currentObject.getClass().getClassLoader();
        }

        return returnValue;
    }

    /**
     * Get the current workspace path.
     * 
     * @return
     */
    private static String getWorkspacePath() {
        return ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
    }
}
