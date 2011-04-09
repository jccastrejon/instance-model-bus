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

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CommandMarker;

/**
 * Instance Model Bus commands
 */
@Component
@Service
public class ImbCommands implements CommandMarker {

    @Reference
    private ImbOperations operations;

    @CliAvailabilityIndicator("imb update controllers")
    public boolean isUpdateInterfaceAvailable() {
        return operations.isUpdateControllersAvailable();
    }

    @CliCommand(value = "imb update controllers", help = "Updates the web mvc controllers to communicate with the Instance Model Bus")
    public void updateController() {
        operations.updateControllers();
    }

    @CliCommand(value = "imb generate schemas", help = "Generates XML Schemas for the entities of an application")
    public void generateSchemas() {
        operations.generateEntitiesSchemas();
    }

    @CliCommand(value = "imb generate notificationScheduling", help = "Generate the artifacts needed for the notification scheduling")
    public void generatedNotificationScheduling() {
        operations.generatedNotificationScheduling();
    }
}