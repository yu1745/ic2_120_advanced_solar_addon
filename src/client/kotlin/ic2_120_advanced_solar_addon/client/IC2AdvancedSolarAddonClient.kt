package ic2_120_advanced_solar_addon.client

import ic2_120_advanced_solar_addon.client.render.MolecularTransformerBlockEntityRenderer
import ic2_120_advanced_solar_addon.content.block.MolecularTransformerBlockEntity
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import stardust.fabric.registry.ClientScreenRegistrar
import stardust.fabric.registry.type

object IC2AdvancedSolarAddonClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientScreenRegistrar.registerScreens(
            "ic2_120_advanced_solar_addon",
            listOf("ic2_120_advanced_solar_addon.client.screen")
        )

        // Register Block Entity Renderers
        BlockEntityRendererFactories.register(
            MolecularTransformerBlockEntity::class.type(),
            ::MolecularTransformerBlockEntityRenderer
        )
    }
}
