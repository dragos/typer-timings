package org.scalaide.typertimings

import scala.reflect.api.Position
import scala.tools.eclipse.ScalaPlugin
import scala.tools.eclipse.util.EclipseResource
import scala.util.Try

import org.eclipse.core.resources.IMarker
import org.eclipse.core.resources.IResource
import org.scalaide.typertimings.preferences.TyperTimingsPreferences
import org.scalaide.typertimings.weaving.scalac.ITraceListener
import org.scalaide.typertimings.weaving.scalac.Trace

class TyperTimings extends ITraceListener {
  private var traces = Vector[Trace]()
  private val displayedTraces = Set("lub", "typedDefDef")

  def trace(trace: Trace) {
    if (displayedTraces(trace.traceName)) {
      traces = traces :+ trace
    }
  }

  private def renderTraces() = {
    for ((pos, ts) <- traces.groupBy(_.pos.line)) yield {
      val tracesAtPos = if (TyperTimingsPreferences.getMergeTimings()) List(ts.maxBy(_.duration)) else ts
      for (t <- tracesAtPos if (t.duration > TyperTimingsPreferences.getMinimumDuration() * 1000))
        createMarker(t.pos, t.duration, t.symFullName)
    }
  }

  // min/max working on Long
  def min(x: Long, y: Long) = if (x < y) x else y
  def max(x: Long, y: Long) = if (x < y) y else x

  private def createMarker(pos: Position, duration: Long, msg: String) {
    pos.source.file match {
      case EclipseResource(resource: IResource) =>
        val marker = resource.createMarker(ScalaPlugin.plugin.problemMarkerId)
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO)
        marker.setAttribute(IMarker.MESSAGE, "Type-checking: %,10d us (%s)".format(duration / 1000, msg))
        marker.setAttribute(IMarker.LINE_NUMBER, Try(pos.line).getOrElse(0))
      case _ =>
      // don't know what to do with this trace
    }
  }

  // FIXME: This event is not yet sent by the aspecj component
  def beginTracing(): Unit = ()

  def endTracing(): Unit = {
    renderTraces()
    traces = Vector.empty
  }
}
