package io.github.paulgriffith.extensions.designer

import com.inductiveautomation.ignition.common.BundleUtil
import com.inductiveautomation.ignition.common.licensing.LicenseState
import com.inductiveautomation.ignition.common.script.ScriptManager
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook
import com.inductiveautomation.ignition.designer.model.DesignerContext
import io.github.paulgriffith.extensions.common.DatasetExtensions
import io.github.paulgriffith.extensions.common.UtilitiesExtensions
import io.github.paulgriffith.extensions.common.addPropertyBundle

@Suppress("unused")
class DesignerHook : AbstractDesignerModuleHook() {
    private lateinit var context: DesignerContext

    override fun startup(context: DesignerContext, activationState: LicenseState) {
        this.context = context

        BundleUtil.get().apply {
            addPropertyBundle<DatasetExtensions>()
            addPropertyBundle<UtilitiesExtensions>()
        }
    }

    override fun initializeScriptManager(manager: ScriptManager) {
        manager.addScriptModule("system.dataset", DatasetExtensions, PropertiesFileDocProvider())
        manager.addScriptModule("system.utils", UtilitiesExtensions(context), PropertiesFileDocProvider())
    }
}
