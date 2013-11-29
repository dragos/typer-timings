package org.scalaide.typertimings.weaving.scalac;

import scala.reflect.api.Position;

public class Trace {

  public final String symFullName;
  public final Position pos;
  public final long startTime;
  public final long duration;
  public final String traceName;

  public Trace(Position pos, String symFullName, long startTime, long duration, String traceName) {
    this.pos = pos;
    this.symFullName = symFullName;
    this.startTime = startTime;
    this.duration = duration;
    this.traceName = traceName;
  }
  
  public boolean includes(Trace other) {
    if ((this.startTime <= other.startTime)
        && (this.startTime + duration >= other.startTime + other.duration))
      return true;
    else return false;
  }
}
