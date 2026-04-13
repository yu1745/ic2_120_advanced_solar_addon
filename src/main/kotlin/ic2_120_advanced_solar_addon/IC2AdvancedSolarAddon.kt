package ic2_120_advanced_solar_addon

import ic2_120_advanced_solar_addon.content.block.ModBlocks
import ic2_120_advanced_solar_addon.content.item.ModItems
import ic2_120_advanced_solar_addon.content.recipe.MTRecipes
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object IC2AdvancedSolarAddon : ModInitializer {
    const val MOD_ID = "ic2_120_advanced_solar_addon"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        LOGGER.info("Initializing IC2 Advanced Solar Addon...")

        // 初始化物品
        ModItems.init()

        // 初始化方块
        ModBlocks.init()

        // 初始化分子重组仪配方
        MTRecipes.init()

        LOGGER.info("IC2 Advanced Solar Addon initialized!")
    }
}
