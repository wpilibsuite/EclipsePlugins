package edu.wpi.first.wpilib.plugins.cpp.launching;

import java.util.Arrays;

import org.eclipse.remote.core.IRemoteServicesManager;
import org.eclipse.remote.core.IRemoteConnectionType;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import edu.wpi.first.wpilib.plugins.cpp.WPILibCPPPlugin;

public class RemoteUtils {
  
  /**
   * Return the OSGi service with the given service interface.
   *
   * @param service service interface
   * @return the specified service or null if it's not registered
   */
  public static <T> T getService(Class<T> service) {
          BundleContext context = WPILibCPPPlugin.getDefault().getBundle().getBundleContext();
          ServiceReference<T> ref = context.getServiceReference(service);
          return ref != null ? context.getService(ref) : null;
  }

	public static IRemoteConnection getTarget(int teamNumber) {
		// The ip address based on the team number
        //String hostName = "10."+(teamNumber/100)+"."+(teamNumber%100)+".2";
        //String connectionName = hostName; //"Team "+teamNumber;
		String hostName = "roboRIO-" + teamNumber + "-FRC.local";
		String connectionName = hostName;
    
    IRemoteServicesManager mgr = getService(IRemoteServicesManager.class);
    IRemoteConnectionType connType = mgr.getConnectionType("org.eclipse.remote.JSch");
    IRemoteConnection connection = connType.getConnection(connectionName);
    if(connection == null)
    {
      try {
        IRemoteConnectionWorkingCopy connWC = connType.newConnection(connectionName);
        connWC.setAttribute("JSCH_ADDRESS_ATTR", hostName);
        connWC.setAttribute("JSCH_USERNAME_ATTR", "lvuser");
        connWC.setAttribute("JSCH_PASSWORD_ATTR", "");
        connWC.setAttribute("JSCH_IS_PASSWORD_ATTR", "true");
        connWC.setAttribute("JSCH_USE_LOGIN_SHELL_ATTR", "false");
        return connWC.save();
      } catch (RemoteConnectionException e) {
        WPILibCPPPlugin.logError("Error creating remote connection", e);
        return null;
      }
    } else
    {
      return connection;
    }
  }
}
