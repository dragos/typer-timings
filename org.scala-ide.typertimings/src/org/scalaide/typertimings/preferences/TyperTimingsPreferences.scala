package org.scalaide.typertimings.preferences

import org.eclipse.ui.IWorkbenchPreferencePage
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.jface.preference.BooleanFieldEditor
import org.eclipse.ui.IWorkbench
import TyperTimingsPreferences._
import org.scalaide.typertimings.TyperTimingsPlugin
import org.eclipse.jface.preference.IntegerFieldEditor

class TyperTimingsPreferences extends FieldEditorPreferencePage with IWorkbenchPreferencePage {

  setPreferenceStore(TyperTimingsPlugin.plugin.getPreferenceStore)
  setDescription("""
Typer Timings:
  """)

  override def createFieldEditors() {
    val parent = getFieldEditorParent
    addField(new IntegerFieldEditor(MINIMUM_DURATION, "Minimum duration (microseconds)", getFieldEditorParent))
    addField(new BooleanFieldEditor(MERGE_TIMINGS, "Merge timing at the same line", getFieldEditorParent))
  }

  def init(workbench: IWorkbench) {}
}

object TyperTimingsPreferences {
  final val MINIMUM_DURATION = TyperTimingsPlugin.pluginId + "minimuDuration"
  final val MERGE_TIMINGS = TyperTimingsPlugin.pluginId + "merteTimings"

  def getMinimumDuration(): Long = {
    TyperTimingsPlugin.plugin.getPreferenceStore().getLong(MINIMUM_DURATION)
  }

  def getMergeTimings() =
    TyperTimingsPlugin.plugin.getPreferenceStore().getBoolean(MERGE_TIMINGS)
}
