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
	public void writeHello() {
		operations.updateControllers();
	}
}