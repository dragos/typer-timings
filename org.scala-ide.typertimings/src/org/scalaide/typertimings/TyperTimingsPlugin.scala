package org.scalaide.typertimings

import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext
import scala.reflect.runtime.universe._

object TyperTimingsPlugin {
  @volatile
  var plugin: TyperTimingsPlugin = _

  final val pluginId = "org.scala-ide.typertimings"
}

class TyperTimingsPlugin extends AbstractUIPlugin {

  override def start(context: BundleContext) {
    super.start(context)
    TyperTimingsPlugin.plugin = this
  }

  override def stop(context: BundleContext) {
    try super.stop(context)
    finally TyperTimingsPlugin.plugin = null
  }
}