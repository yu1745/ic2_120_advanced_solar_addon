package ic2_120_advanced_solar_addon.config

import ic2_120_advanced_solar_addon.IC2AdvancedSolarAddon
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.lang.reflect.Modifier
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigComment(val value: String, val defaultValue: String = "")

@Serializable
data class MolecularTransformerRecipeConfig(
    @field:ConfigComment("输入物品 ID，例如 minecraft:iron_ingot", "")
    val input: String = "",
    @field:ConfigComment("输出物品 ID，例如 ic2_120_advanced_solar_addon:iridium_ingot", "")
    val output: String = "",
    @field:ConfigComment("所需能量（EU）", "0")
    val energy: Long = 0
)

@Serializable
data class Ic2AdvancedSolarAddonMainConfig(
    @field:ConfigComment("分子重组仪配方配置。")
    val molecularTransformer: MolecularTransformerConfig = MolecularTransformerConfig()
)

@Serializable
data class MolecularTransformerConfig(
    @field:ConfigComment("分子重组仪配方列表。")
    val recipes: List<MolecularTransformerRecipeConfig> = defaultRecipes
)

private val defaultRecipes = listOf(
    // 萤石 -> 阳光化合物，900W EU
    MolecularTransformerRecipeConfig(
        input = "ic2_120_advanced_solar_addon:fluorite",
        output = "ic2_120_advanced_solar_addon:sunnarium",
        energy = 900000000
    ),
    // 萤石粉 -> 小块阳光化合物，100W EU
    MolecularTransformerRecipeConfig(
        input = "ic2_120_advanced_solar_addon:fluorite_dust",
        output = "ic2_120_advanced_solar_addon:sunnarium_part",
        energy = 100000000
    ),
    // 铁锭 -> 铱锭，900W EU
    MolecularTransformerRecipeConfig(
        input = "minecraft:iron_ingot",
        output = "ic2_120_advanced_solar_addon:iridium_ingot",
        energy = 900000000
    )
)

private val DEFAULT_CONFIG_TEMPLATE = Ic2AdvancedSolarAddonMainConfig()

object Ic2AdvancedSolarAddonConfig {
    private val logger = LoggerFactory.getLogger("${IC2AdvancedSolarAddon.MOD_ID}/config")
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    private val configPath: Path by lazy {
        FabricLoader.getInstance().configDir.resolve("${IC2AdvancedSolarAddon.MOD_ID}.json")
    }

    @Volatile
    var current: Ic2AdvancedSolarAddonMainConfig = DEFAULT_CONFIG_TEMPLATE
        private set

    fun loadOrThrow() {
        current = readOrCreateDefault()
        logLoaded("loaded")
    }

    fun reloadOrThrow() {
        current = readOrCreateDefault()
        logLoaded("reloaded")
    }

    fun prettyCurrentConfig(): String {
        return json.encodeToString(current)
    }

    fun getMolecularTransformerRecipes(): List<MolecularTransformerRecipeConfig> {
        return current.molecularTransformer.recipes
    }

    fun getRecipeByInput(inputId: String): MolecularTransformerRecipeConfig? {
        val normalized = inputId.trim()
        if (normalized.isEmpty()) return null
        return current.molecularTransformer.recipes.find { it.input == normalized }
    }

    fun addOrUpdateRecipeEnergy(inputId: String, energy: Long): Boolean {
        val normalizedId = inputId.trim()
        if (normalizedId.isEmpty() || energy <= 0) return false

        val currentRecipes = current.molecularTransformer.recipes.toMutableList()
        val existingIndex = currentRecipes.indexOfFirst { it.input == normalizedId }

        val newRecipe = MolecularTransformerRecipeConfig(
            input = normalizedId,
            output = existingIndex.takeIf { it >= 0 }?.let { current.molecularTransformer.recipes[it].output } ?: "",
            energy = energy
        )

        if (existingIndex >= 0) {
            currentRecipes[existingIndex] = newRecipe
        } else {
            currentRecipes.add(newRecipe)
        }

        current = current.copy(
            molecularTransformer = current.molecularTransformer.copy(
                recipes = currentRecipes
            )
        )

        saveCurrentConfig()
        return true
    }

    fun removeRecipe(inputId: String): Boolean {
        val normalizedId = inputId.trim()
        if (normalizedId.isEmpty()) return false

        val currentRecipes = current.molecularTransformer.recipes.toMutableList()
        val existingIndex = currentRecipes.indexOfFirst { it.input == normalizedId }

        if (existingIndex < 0) return false

        currentRecipes.removeAt(existingIndex)
        current = current.copy(
            molecularTransformer = current.molecularTransformer.copy(
                recipes = currentRecipes
            )
        )

        saveCurrentConfig()
        return true
    }

    private fun saveCurrentConfig() {
        Files.writeString(configPath, encodeConfigWithComments(current), StandardCharsets.UTF_8)
    }

    private fun readOrCreateDefault(): Ic2AdvancedSolarAddonMainConfig {
        if (!Files.exists(configPath)) {
            writeDefaultConfig(configPath)
            return DEFAULT_CONFIG_TEMPLATE
        }

        return try {
            val raw = Files.readString(configPath, StandardCharsets.UTF_8)
            val config = json.decodeFromString<Ic2AdvancedSolarAddonMainConfig>(raw)
            val parsedRoot = json.parseToJsonElement(raw).jsonObject
            if (shouldRewriteConfig(parsedRoot, config)) {
                Files.writeString(configPath, encodeConfigWithComments(config), StandardCharsets.UTF_8)
            }
            config
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse config: $configPath", e)
        }
    }

    private fun writeDefaultConfig(path: Path) {
        Files.createDirectories(path.parent)
        Files.writeString(path, defaultConfigText(), StandardCharsets.UTF_8)
    }

    private fun defaultConfigText(): String {
        return encodeConfigWithComments(DEFAULT_CONFIG_TEMPLATE)
    }

    private fun shouldRewriteConfig(root: JsonObject, config: Ic2AdvancedSolarAddonMainConfig): Boolean {
        return !containsAllExpectedKeys(root, buildCommentedConfigJson(config))
    }

    private fun encodeConfigWithComments(config: Ic2AdvancedSolarAddonMainConfig): String =
        json.encodeToString(JsonObject.serializer(), buildCommentedConfigJson(config))

    private fun buildCommentedConfigJson(config: Ic2AdvancedSolarAddonMainConfig): JsonObject =
        buildCommentedObject(
            instance = config,
            jsonObject = json.encodeToJsonElement(Ic2AdvancedSolarAddonMainConfig.serializer(), config).jsonObject,
            rootComment = "配置文件允许保留这些 _comment_* 说明字段；程序读取时会自动忽略它们。"
        )

    private fun buildCommentedObject(
        instance: Any,
        jsonObject: JsonObject,
        rootComment: String? = null
    ): JsonObject = buildJsonObject {
        if (rootComment != null) {
            put("_comment", JsonPrimitive(rootComment))
        }

        declaredConfigFields(instance.javaClass).forEach { field ->
            field.isAccessible = true
            val fieldName = field.name
            val valueElement = jsonObject[fieldName] ?: return@forEach
            field.getAnnotation(ConfigComment::class.java)?.let { annotation ->
                put("_comment_$fieldName", JsonPrimitive(formatComment(annotation)))
            }

            val fieldValue = field.get(instance)
            val isNestedConfigObject =
                fieldValue != null &&
                    valueElement is JsonObject &&
                    !Map::class.java.isAssignableFrom(field.type)

            if (isNestedConfigObject) {
                put(fieldName, buildCommentedObject(fieldValue!!, valueElement.jsonObject))
            } else {
                put(fieldName, valueElement)
            }
        }
    }

    private fun containsAllExpectedKeys(actual: JsonObject, expected: JsonObject): Boolean =
        expected.all { (key, expectedValue) ->
            val actualValue = actual[key] ?: return@all false
            if (expectedValue is JsonObject && actualValue is JsonObject) {
                containsAllExpectedKeys(actualValue, expectedValue)
            } else {
                true
            }
        }

    private fun declaredConfigFields(type: Class<*>): List<java.lang.reflect.Field> =
        type.declaredFields.filterNot { field ->
            field.isSynthetic || Modifier.isStatic(field.modifiers)
        }

    private inline fun <reified T : Any> commentOf(fieldName: String): String =
        T::class.java.getDeclaredField(fieldName).getAnnotation(ConfigComment::class.java)?.let { annotation ->
            if (annotation.defaultValue.isBlank()) {
                annotation.value
            } else {
                "${annotation.value} 默认值: ${annotation.defaultValue}"
            }
        } ?: error("Missing @ConfigComment on ${T::class.java.simpleName}.$fieldName")

    private fun formatComment(annotation: ConfigComment): String =
        if (annotation.defaultValue.isBlank()) {
            annotation.value
        } else {
            "${annotation.value} 默认值: ${annotation.defaultValue}"
        }

    private fun logLoaded(action: String) {
        logger.info(
            "Config {}:\n{}",
            action,
            prettyCurrentConfig()
        )
    }
}
