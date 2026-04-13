package ic2_120_advanced_solar_addon.content.block

import ic2_120_advanced_solar_addon.IC2AdvancedSolarAddon
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModBlocks {
    // 方块实例
    val ADVANCED_SOLAR_PANEL: Block = registerBlock("advanced_solar_panel", AdvancedSolarPanelBlock())
    val HYBRID_SOLAR_PANEL: Block = registerBlock("hybrid_solar_panel", HybridSolarPanelBlock())
    val ULTIMATE_SOLAR_PANEL: Block = registerBlock("ultimate_solar_panel", UltimateSolarPanelBlock())
    val QUANTUM_SOLAR_PANEL: Block = registerBlock("quantum_solar_panel", QuantumSolarPanelBlock())
    val MOLECULAR_TRANSFORMER: Block = registerBlock("molecular_transformer", MolecularTransformerBlock())
    val QUANTUM_GENERATOR: Block = registerBlock("quantum_generator", QuantumGeneratorBlock())
    
    // BlockEntity类型
    lateinit var ADVANCED_SOLAR_PANEL_ENTITY: BlockEntityType<AdvancedSolarPanelBlockEntity>
    lateinit var HYBRID_SOLAR_PANEL_ENTITY: BlockEntityType<HybridSolarPanelBlockEntity>
    lateinit var ULTIMATE_SOLAR_PANEL_ENTITY: BlockEntityType<UltimateSolarPanelBlockEntity>
    lateinit var QUANTUM_SOLAR_PANEL_ENTITY: BlockEntityType<QuantumSolarPanelBlockEntity>
    lateinit var MOLECULAR_TRANSFORMER_ENTITY: BlockEntityType<MolecularTransformerBlockEntity>
    lateinit var QUANTUM_GENERATOR_ENTITY: BlockEntityType<QuantumGeneratorBlockEntity>
    
    private fun registerBlock(name: String, block: Block): Block {
        val id = Identifier(IC2AdvancedSolarAddon.MOD_ID, name)
        Registry.register(Registries.BLOCK, id, block)
        Registry.register(Registries.ITEM, id, BlockItem(block, Item.Settings()))
        return block
    }
    
    fun init() {
        // 注册BlockEntity
        ADVANCED_SOLAR_PANEL_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(IC2AdvancedSolarAddon.MOD_ID, "advanced_solar_panel"),
            FabricBlockEntityTypeBuilder.create({ pos, state -> AdvancedSolarPanelBlockEntity(pos, state) }, ADVANCED_SOLAR_PANEL).build()
        )
        
        HYBRID_SOLAR_PANEL_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(IC2AdvancedSolarAddon.MOD_ID, "hybrid_solar_panel"),
            FabricBlockEntityTypeBuilder.create({ pos, state -> HybridSolarPanelBlockEntity(pos, state) }, HYBRID_SOLAR_PANEL).build()
        )
        
        ULTIMATE_SOLAR_PANEL_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(IC2AdvancedSolarAddon.MOD_ID, "ultimate_solar_panel"),
            FabricBlockEntityTypeBuilder.create({ pos, state -> UltimateSolarPanelBlockEntity(pos, state) }, ULTIMATE_SOLAR_PANEL).build()
        )
        
        QUANTUM_SOLAR_PANEL_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(IC2AdvancedSolarAddon.MOD_ID, "quantum_solar_panel"),
            FabricBlockEntityTypeBuilder.create({ pos, state -> QuantumSolarPanelBlockEntity(pos, state) }, QUANTUM_SOLAR_PANEL).build()
        )
        
        MOLECULAR_TRANSFORMER_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(IC2AdvancedSolarAddon.MOD_ID, "molecular_transformer"),
            FabricBlockEntityTypeBuilder.create({ pos, state -> MolecularTransformerBlockEntity(pos, state) }, MOLECULAR_TRANSFORMER).build()
        )
        
        QUANTUM_GENERATOR_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(IC2AdvancedSolarAddon.MOD_ID, "quantum_generator"),
            FabricBlockEntityTypeBuilder.create({ pos, state -> QuantumGeneratorBlockEntity(pos, state) }, QUANTUM_GENERATOR).build()
        )
        
        // 注册到物品栏
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register { entries ->
            entries.add(ADVANCED_SOLAR_PANEL)
            entries.add(HYBRID_SOLAR_PANEL)
            entries.add(ULTIMATE_SOLAR_PANEL)
            entries.add(QUANTUM_SOLAR_PANEL)
            entries.add(MOLECULAR_TRANSFORMER)
            entries.add(QUANTUM_GENERATOR)
        }
    }
}
