/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package todolist.provider;

import org.eclipse.emf.common.EMFPlugin;

import org.eclipse.emf.common.util.ResourceLocator;

/**
 * This is the central singleton for the TodoList edit plugin.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public final class TodoListEditPlugin extends EMFPlugin {
    /**
     * Keep track of the singleton.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final TodoListEditPlugin INSTANCE = new TodoListEditPlugin();

    /**
     * Keep track of the singleton.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static Implementation plugin;

    /**
     * Create the instance.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TodoListEditPlugin() {
        super
          (new ResourceLocator [] {
           });
    }

    /**
     * Returns the singleton instance of the Eclipse plugin.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the singleton instance.
     * @generated
     */
    @Override
    public ResourceLocator getPluginResourceLocator() {
        return plugin;
    }

    /**
     * Returns the singleton instance of the Eclipse plugin.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the singleton instance.
     * @generated
     */
    public static Implementation getPlugin() {
        return plugin;
    }

    /**
     * The actual implementation of the Eclipse <b>Plugin</b>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
        public static class Implementation extends EclipsePlugin {
        private static Process tomcatProcess;

        /**
         * Creates an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        public Implementation() {
            super();

            // Remember the static instance.
            plugin = this;
        }

        @Override
        public void start(org.osgi.framework.BundleContext context) throws Exception {
            java.io.File localImbDir;

            try {
                localImbDir = new java.io.File("/Users/jccastrejon/java/workspace_AgoDic2010/TodoList.ecore.edit/imb");
                Implementation.tomcatProcess = Runtime.getRuntime().exec("mvn tomcat:run", null, localImbDir);
                System.out.println("Successfully launched tomcat associated with the eclipse editor");
            } catch (Exception e) {
                System.out.println("Error launching tomcat associated to the eclipse editor: " + e.getMessage());
            }
            
            // Default behavior
            super.start(context);
        }

        @Override
        public void stop(org.osgi.framework.BundleContext context) throws Exception {
            try {
                if (Implementation.tomcatProcess != null) {
                    Implementation.tomcatProcess.destroy();
                    System.out.println("Successfully terminated tomcat associated with the eclipse editor");
                } else {
                    System.out.println("No tomcat instance associated with the eclipse editor");
                }
            } catch (Exception e) {
                System.out.println("Error stopping tomcat associated with the eclipse editor" + e.getMessage());
            }
            
            // Default behavior
            super.stop(context);
        }
    }
}