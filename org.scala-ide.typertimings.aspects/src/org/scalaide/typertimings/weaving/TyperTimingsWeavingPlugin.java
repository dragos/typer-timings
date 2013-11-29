package org.scalaide.typertimings.weaving;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class TyperTimingsWeavingPlugin extends Plugin {
  private static TyperTimingsWeavingPlugin INSTANCE;

  public static String ID = "org.scala-ide.typertimings.aspects"; //$NON-NLS-1$

  public TyperTimingsWeavingPlugin() {
    super();
    INSTANCE = this;
  }

  public static void logException(Throwable t) {
    INSTANCE.getLog().log(new Status(IStatus.ERROR, ID, t.getMessage(), t));
  }

  public static void logErrorMessage(String msg) {
    INSTANCE.getLog().log(new Status(IStatus.ERROR, ID, msg));
  }

  public static TyperTimingsWeavingPlugin getInstance() {
    return INSTANCE;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
  }
}
