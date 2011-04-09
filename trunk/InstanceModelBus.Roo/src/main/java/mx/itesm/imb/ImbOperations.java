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

/**
 * Interface of IMB commands that are available via the Roo shell.
 */
public interface ImbOperations {

    /**
     * Determine if the update operation is available.
     * 
     * @return
     */
    boolean isUpdateControllersAvailable();

    /**
     * Update MVC controllers to allow communication with the Instance Model
     * Bus.
     */
    void updateControllers();

    /**
     * Generate XML schemas for the entities associated to MVC controllers
     */
    void generateEntitiesSchemas();
    
    /**
     * Generate the artifacts needed for the notification scheduling
     */
    void generatedNotificationScheduling();
}