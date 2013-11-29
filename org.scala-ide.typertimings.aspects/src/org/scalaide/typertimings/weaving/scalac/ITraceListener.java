package org.scalaide.typertimings.weaving.scalac;

public interface ITraceListener {

  public void beginTracing();

  /**
   * A new Trace object has been recorded in the Scala compiler
   * 
   * @param t
   */
  public void trace(Trace t);

  public void endTracing();
}
