package ic2_120_advanced_solar_addon.content.item

import ic2_120_advanced_solar_addon.content.tab.SolarMachinesTab
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import stardust.fabric.registry.annotation.ModItem

@ModItem(name = "sunnarium", tab = SolarMachinesTab::class, group = "material")
class Sunnarium : Item(FabricItemSettings())

@ModItem(name = "sunnarium_part", tab = SolarMachinesTab::class, group = "material")
class SunnariumPart : Item(FabricItemSettings())

@ModItem(name = "sunnarium_alloy", tab = SolarMachinesTab::class, group = "material")
class SunnariumAlloy : Item(FabricItemSettings())

@ModItem(name = "irradiant_uranium", tab = SolarMachinesTab::class, group = "material")
class IrradiantUranium : Item(FabricItemSettings())

@ModItem(name = "enriched_sunnarium", tab = SolarMachinesTab::class, group = "material")
class EnrichedSunnarium : Item(FabricItemSettings())

@ModItem(name = "enriched_sunnarium_alloy", tab = SolarMachinesTab::class, group = "material")
class EnrichedSunnariumAlloy : Item(FabricItemSettings())

@ModItem(name = "irradiant_glass_pane", tab = SolarMachinesTab::class, group = "material")
class IrradiantGlassPane : Item(FabricItemSettings())

@ModItem(name = "iridium_iron_plate", tab = SolarMachinesTab::class, group = "material")
class IridiumIronPlate : Item(FabricItemSettings())

@ModItem(name = "reinforced_iridium_iron_plate", tab = SolarMachinesTab::class, group = "material")
class ReinforcedIridiumIronPlate : Item(FabricItemSettings())

@ModItem(name = "irradiant_reinforced_plate", tab = SolarMachinesTab::class, group = "material")
class IrradiantReinforcedPlate : Item(FabricItemSettings())

@ModItem(name = "iridium_ingot", tab = SolarMachinesTab::class, group = "material")
class IridiumIngot : Item(FabricItemSettings())

@ModItem(name = "uranium_ingot", tab = SolarMachinesTab::class, group = "material")
class UraniumIngot : Item(FabricItemSettings())

@ModItem(name = "mt_core", tab = SolarMachinesTab::class, group = "component")
class MtCore : Item(FabricItemSettings())

@ModItem(name = "quantum_core", tab = SolarMachinesTab::class, group = "component")
class QuantumCore : Item(FabricItemSettings())
