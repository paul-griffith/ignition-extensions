package org.imdc.extensions.designer

import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory
import com.inductiveautomation.ignition.client.util.action.BaseAction
import com.inductiveautomation.ignition.client.util.gui.Listen
import com.inductiveautomation.ignition.common.BundleUtil
import com.inductiveautomation.ignition.common.licensing.LicenseState
import com.inductiveautomation.ignition.common.script.ScriptManager
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook
import com.inductiveautomation.ignition.designer.model.DesignerContext
import com.inductiveautomation.ignition.designer.model.menu.JMenuMerge
import com.inductiveautomation.ignition.designer.model.menu.MenuBarMerge
import com.inductiveautomation.ignition.designer.model.menu.WellKnownMenuConstants.TOOLS_MENU_LOCATION
import com.inductiveautomation.ignition.designer.model.menu.WellKnownMenuConstants.TOOLS_MENU_NAME
import net.miginfocom.swing.MigLayout
import org.imdc.extensions.common.DatasetExtensions
import org.imdc.extensions.common.ExtensionDocProvider
import org.imdc.extensions.common.IconRPC
import org.imdc.extensions.common.MODULE_ID
import org.imdc.extensions.common.UtilitiesExtensions
import org.imdc.extensions.common.addPropertyBundle
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

@Suppress("unused")
class DesignerHook : AbstractDesignerModuleHook() {
    private lateinit var context: DesignerContext

    override fun startup(context: DesignerContext, activationState: LicenseState) {
        this.context = context
        iconUtilities = IconUtilities()

        BundleUtil.get().apply {
            addPropertyBundle<DatasetExtensions>()
            addPropertyBundle<UtilitiesExtensions>()
            addPropertyBundle<DesignerProjectExtensions>()
        }
    }

    override fun initializeScriptManager(manager: ScriptManager) {
        manager.apply {
            addScriptModule("system.dataset", DatasetExtensions, ExtensionDocProvider)
            addScriptModule("system.util", UtilitiesExtensions(context), ExtensionDocProvider)
            addScriptModule("system.project", DesignerProjectExtensions(context), ExtensionDocProvider)
        }
    }

    private lateinit var iconUtilities: IconUtilities
    private val iconManager by lazy { IconManager(iconUtilities) }
    private val menu = MenuBarMerge(MODULE_ID).apply {
        add(
            TOOLS_MENU_LOCATION,
            JMenuMerge(TOOLS_MENU_NAME).apply {
                add(
                    BaseAction.create("Icon Management", null) {
                        iconManager.apply {
                            isVisible = true
                            toFront()
                        }
                    },
                )
            },
        )
    }

    override fun getModuleMenu(): MenuBarMerge = menu
}

class IconUtilities : IconRPC by ModuleRPCFactory.create(MODULE_ID, IconRPC::class.java)

class IconManager(private val iconRPC: IconRPC) : JFrame("Icon Management") {
    private val librariesCombobox = JComboBox(iconRPC.getIconLibraries().toTypedArray())

    private val contentPanel = JTextArea()

    init {
        contentPane = JPanel(MigLayout("ins 16, fill")).apply {
            add(librariesCombobox, "north")
            add(JScrollPane(contentPanel), "push, grow")
        }
        preferredSize = Dimension(800, 600)
        defaultCloseOperation = HIDE_ON_CLOSE

        contentPanel.text = iconRPC.getIconPack("${librariesCombobox.selectedItem}.svg")
        contentPanel.scrollRectToVisible(Rectangle(0, 0))

        Listen.toCombobox(librariesCombobox) { newValue ->
            contentPanel.text = iconRPC.getIconPack("$newValue.svg")
            contentPanel.scrollRectToVisible(Rectangle(0, 0))
        }

        pack()
    }
}
