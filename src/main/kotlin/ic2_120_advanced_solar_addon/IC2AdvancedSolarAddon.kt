package ic2_120_advanced_solar_addon

import ic2_120_advanced_solar_addon.content.recipe.MTRecipes
import ic2_120_advanced_solar_addon.content.RegistryConfigurerImpl
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import stardust.fabric.registry.ClassScanner
import stardust.fabric.registry.ClassScannerHolder

object IC2AdvancedSolarAddon : ModInitializer {
    const val MOD_ID = "ic2_120_advanced_solar_addon"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        LOGGER.info("Initializing IC2 Advanced Solar Addon...")

        val scanner = ClassScanner(RegistryConfigurerImpl())
        ClassScannerHolder.instance = scanner

        scanner.scanAndRegister(
            MOD_ID,
            listOf(
                "ic2_120_advanced_solar_addon.content.tab",
                "ic2_120_advanced_solar_addon.content.block",
                "ic2_120_advanced_solar_addon.content.screen",
                "ic2_120_advanced_solar_addon.content.item"
            )
        )

        // 初始化分子重组仪配方
        MTRecipes.init()

        LOGGER.info("IC2 Advanced Solar Addon initialized!")
    }
}
