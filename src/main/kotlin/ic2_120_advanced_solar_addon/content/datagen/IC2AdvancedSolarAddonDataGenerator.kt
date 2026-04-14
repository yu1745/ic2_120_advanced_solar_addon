package ic2_120_advanced_solar_addon.content.datagen

import ic2_120_advanced_solar_addon.IC2AdvancedSolarAddon
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput

object IC2AdvancedSolarAddonDataGenerator : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()

        pack.addProvider(::ModBlockLootTableProvider)
    }
}
