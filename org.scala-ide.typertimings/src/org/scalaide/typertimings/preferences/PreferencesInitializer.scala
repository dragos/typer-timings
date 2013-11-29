package org.scalaide.typertimings.preferences

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.scalaide.typertimings.TyperTimingsPlugin

class PreferencesInitializer extends AbstractPreferenceInitializer {
  override def initializeDefaultPreferences() {
    import TyperTimingsPreferences._
    val store = TyperTimingsPlugin.plugin.getPreferenceStore
    store.setDefault(MINIMUM_DURATION, 500)
    store.setDefault(MERGE_TIMINGS, true)
  }
}
