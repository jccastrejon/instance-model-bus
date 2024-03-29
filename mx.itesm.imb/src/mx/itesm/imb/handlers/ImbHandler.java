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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 
 * @author jccastrejon
 * 
 */
public abstract class ImbHandler extends AbstractHandler {

    /**
     * Eclipse console.
     */
    private MessageConsole messageConsole;

    /**
     * Output Stream to the Eclipse console.
     */
    private MessageConsoleStream messageConsoleStream;

    public ImbHandler() {
        this.messageConsole = this.findConsole("Instance Model Bus");
        this.messageConsoleStream = this.messageConsole.newMessageStream();
    }

    /**
     * Get the Output Stream to the Eclipse console.
     * 
     * @return
     */
    protected MessageConsoleStream getConsoleStream() {
        return this.messageConsoleStream;
    }

    /**
     * Delete any messages previously written to the Eclipse console.
     */
    protected void clearConsole() {
        IConsoleView view;
        IWorkbenchPage page;

        try {
            this.messageConsole.clearConsole();
            page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
            view.display(messageConsole);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the Eclipse console identified with the given name.
     * 
     * @param name
     * @return
     */
    private MessageConsole findConsole(final String name) {
        ConsolePlugin plugin;
        IConsoleManager conMan;
        IConsole[] existing;

        plugin = ConsolePlugin.getDefault();
        conMan = plugin.getConsoleManager();
        existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++) {
            if (name.equals(existing[i].getName())) {
                return (MessageConsole) existing[i];
            }
        }

        // no console found, so create a new one
        MessageConsole myConsole = new MessageConsole(name, null);
        conMan.addConsoles(new IConsole[] { myConsole });
        return myConsole;
    }
}
