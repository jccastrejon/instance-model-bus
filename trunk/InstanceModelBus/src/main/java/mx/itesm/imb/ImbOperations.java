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
}