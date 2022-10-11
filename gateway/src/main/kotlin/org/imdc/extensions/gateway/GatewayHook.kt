package org.imdc.extensions.gateway

import com.inductiveautomation.ignition.common.BundleUtil
import com.inductiveautomation.ignition.common.licensing.LicenseState
import com.inductiveautomation.ignition.common.script.ScriptManager
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook
import com.inductiveautomation.ignition.gateway.model.GatewayContext
import org.imdc.extensions.common.DatasetExtensions
import org.imdc.extensions.common.ExtensionDocProvider
import org.imdc.extensions.common.UtilitiesExtensions
import org.imdc.extensions.common.addPropertyBundle

@Suppress("unused")
class GatewayHook : AbstractGatewayModuleHook() {
    private lateinit var context: GatewayContext

    private lateinit var rpcImpl: ExtensionsRPCImpl

    override fun setup(context: GatewayContext) {
        this.context = context
        rpcImpl = ExtensionsRPCImpl(context)

        BundleUtil.get().apply {
            addPropertyBundle<DatasetExtensions>()
            addPropertyBundle<UtilitiesExtensions>()
            addPropertyBundle<GatewayProjectExtensions>()
        }
    }

    override fun startup(activationState: LicenseState) {}
    override fun shutdown() {}

    override fun initializeScriptManager(manager: ScriptManager) {
        manager.apply {
            addScriptModule("system.dataset", DatasetExtensions, ExtensionDocProvider)
            addScriptModule("system.util", UtilitiesExtensions(context), ExtensionDocProvider)
            addScriptModule("system.project", GatewayProjectExtensions(context), ExtensionDocProvider)
        }
    }

    override fun isFreeModule(): Boolean = true
    override fun isMakerEditionCompatible(): Boolean = true

    override fun getRPCHandler(session: ClientReqSession, projectName: String): Any {
        require(session.isDesigner) { "Must be a designer" }
        require(session.isValid) { "Must be a valid session" }
        return rpcImpl
    }
}
