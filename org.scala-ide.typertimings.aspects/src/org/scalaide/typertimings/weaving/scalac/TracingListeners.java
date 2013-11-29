package org.scalaide.typertimings.weaving.scalac;

import org.scalaide.typertimings.weaving.util.AbstractProviderRegistry;

public class TracingListeners extends AbstractProviderRegistry<ITraceListener> {
  private static final TracingListeners INSTANCE = new TracingListeners();

  public static String METHOD_VERIFIER_PROVIDERS_EXTENSION_POINT = "org.scalaide.typertimings.tracing"; //$NON-NLS-1$

  public static TracingListeners getInstance() {
    return INSTANCE;
  }

  @Override
  protected String getExtensionPointId() {
    return METHOD_VERIFIER_PROVIDERS_EXTENSION_POINT;
  }
}
