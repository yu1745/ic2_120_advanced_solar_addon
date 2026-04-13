package ic2_120_advanced_solar_addon.content.item

import ic2_120_advanced_solar_addon.IC2AdvancedSolarAddon
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModItems {
    // 物品实例
    val SUNNARIUM = register("sunnarium", Item(FabricItemSettings()))
    val SUNNARIUM_PART = register("sunnarium_part", Item(FabricItemSettings()))
    val SUNNARIUM_ALLOY = register("sunnarium_alloy", Item(FabricItemSettings()))
    val IRRADIANT_URANIUM = register("irradiant_uranium", Item(FabricItemSettings()))
    val ENRICHED_SUNNARIUM = register("enriched_sunnarium", Item(FabricItemSettings()))
    val ENRICHED_SUNNARIUM_ALLOY = register("enriched_sunnarium_alloy", Item(FabricItemSettings()))
    val IRRADIANT_GLASS_PANE = register("irradiant_glass_pane", Item(FabricItemSettings()))
    val IRIDIUM_IRON_PLATE = register("iridium_iron_plate", Item(FabricItemSettings()))
    val REINFORCED_IRIDIUM_IRON_PLATE = register("reinforced_iridium_iron_plate", Item(FabricItemSettings()))
    val IRRADIANT_REINFORCED_PLATE = register("irradiant_reinforced_plate", Item(FabricItemSettings()))
    val IRIDIUM_INGOT = register("iridium_ingot", Item(FabricItemSettings()))
    val URANIUM_INGOT = register("uranium_ingot", Item(FabricItemSettings()))
    val MT_CORE = register("mt_core", Item(FabricItemSettings()))
    val QUANTUM_CORE = register("quantum_core", Item(FabricItemSettings()))

    private fun register(name: String, item: Item): Item {
        val id = Identifier(IC2AdvancedSolarAddon.MOD_ID, name)
        return Registry.register(Registries.ITEM, id, item)
    }

    fun init() {
        // 注册到物品栏
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register { entries ->
            entries.add(SUNNARIUM)
            entries.add(SUNNARIUM_PART)
            entries.add(SUNNARIUM_ALLOY)
            entries.add(IRRADIANT_URANIUM)
            entries.add(ENRICHED_SUNNARIUM)
            entries.add(ENRICHED_SUNNARIUM_ALLOY)
            entries.add(IRRADIANT_GLASS_PANE)
            entries.add(IRIDIUM_IRON_PLATE)
            entries.add(REINFORCED_IRIDIUM_IRON_PLATE)
            entries.add(IRRADIANT_REINFORCED_PLATE)
            entries.add(IRIDIUM_INGOT)
            entries.add(URANIUM_INGOT)
            entries.add(MT_CORE)
            entries.add(QUANTUM_CORE)
        }
    }
}
