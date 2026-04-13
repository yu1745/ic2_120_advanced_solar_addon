package ic2_120_advanced_solar_addon.content.recipe

import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object MTRecipes {
    private val recipes = mutableListOf<MTRecipe>()
    
    data class MTRecipe(
        val input: ItemStack,
        val output: ItemStack,
        val energy: Long
    )
    
    fun init() {
        // 注册配方：铁锭 -> 铱矿石，耗电9M EU
        // 注意：这里使用字符串ID，实际实现时需要获取IC2的铱矿石物品
        recipes.add(MTRecipe(
            input = ItemStack(Items.IRON_INGOT),
            output = ItemStack(Items.DIAMOND), // 临时用钻石代替，实际需要IC2铱矿石
            energy = 9_000_000
        ))
    }
    
    fun findRecipe(input: ItemStack): MTRecipe? {
        return recipes.find { recipe ->
            ItemStack.canCombine(recipe.input, input) && input.count >= recipe.input.count
        }
    }
    
    fun getRecipes(): List<MTRecipe> = recipes.toList()
}
