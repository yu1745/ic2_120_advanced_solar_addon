package ic2_120_advanced_solar_addon.client.render

import ic2_120_advanced_solar_addon.content.block.MolecularTransformerBlock
import ic2_120_advanced_solar_addon.content.block.MolecularTransformerBlockEntity
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.joml.Matrix3f
import org.joml.Matrix4f

class MolecularTransformerBlockEntityRenderer(
    private val context: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<MolecularTransformerBlockEntity> {

    companion object {
        private val TEXTURE = Identifier.of("ic2_120_advanced_solar_addon", "textures/models/texturemoleculartransformer")
        private val PLAZMA_TEXTURE = Identifier.of("ic2_120_advanced_solar_addon", "textures/models/plazma")
        private val PARTICLES_TEXTURE = Identifier.of("ic2_120_advanced_solar_addon", "textures/models/particles")
    }

    override fun render(
        entity: MolecularTransformerBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val state = entity.world?.getBlockState(entity.pos) ?: return
        val isActive = state.get(MolecularTransformerBlock.ACTIVE) ?: false

        matrices.push()

        // Rotate based on facing
        val facing = state.get(net.minecraft.state.property.Properties.HORIZONTAL_FACING)
        val rotation = when (facing) {
            net.minecraft.util.math.Direction.EAST -> 90f
            net.minecraft.util.math.Direction.SOUTH -> 180f
            net.minecraft.util.math.Direction.WEST -> 270f
            else -> 0f
        }
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(rotation))

        // Render base model
        renderModel(matrices, vertexConsumers, light, overlay)

        // Render active core effect
        if (isActive) {
            renderActiveCore(matrices, vertexConsumers, tickDelta)
        }

        matrices.pop()
    }

    private fun renderModel(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE))
        val entry = matrices.peek()
        val pos = entry.positionMatrix
        val normal = entry.normalMatrix

        // Core Bottom (10x3x10) at y=0, offset from center
        renderBox(vc, pos, normal, light, overlay,
            x = -5f, y = 0f, z = -5f,
            width = 10f, height = 3f, depth = 10f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )

        // Core Work Zone (6x9x6) at y=3, transparent
        val vcTranslucent = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE))
        renderBox(vcTranslucent, pos, normal, light, overlay,
            x = -3f, y = 3f, z = -3f,
            width = 6f, height = 9f, depth = 6f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )

        // Core Top Electr (3x2x3) at y=12
        renderBox(vc, pos, normal, light, overlay,
            x = -1.5f, y = 12f, z = -1.5f,
            width = 3f, height = 2f, depth = 3f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )

        // Core Top Plate (9x3x9) at y=14
        renderBox(vc, pos, normal, light, overlay,
            x = -4.5f, y = 14f, z = -4.5f,
            width = 9f, height = 3f, depth = 9f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )

        // Electrodes (3 pairs)
        // First electrode
        renderBox(vc, pos, normal, light, overlay,
            x = 3f, y = 3f, z = -3f,
            width = 4f, height = 5f, depth = 6f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )

        // Second electrode (rotated -120 degrees)
        matrices.push()
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-120f))
        renderBox(vc, pos, normal, light, overlay,
            x = 3f, y = 3f, z = -3f,
            width = 4f, height = 5f, depth = 6f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )
        matrices.pop()

        // Third electrode (rotated 120 degrees)
        matrices.push()
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(120f))
        renderBox(vc, pos, normal, light, overlay,
            x = 3f, y = 3f, z = -3f,
            width = 4f, height = 5f, depth = 6f,
            u = 0f, v = 0f, textureWidth = 128f, textureHeight = 64f
        )
        matrices.pop()
    }

    private fun renderActiveCore(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        tickDelta: Float
    ) {
        val vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(PLAZMA_TEXTURE))
        val entry = matrices.peek()
        val pos = entry.positionMatrix
        val normal = entry.normalMatrix
        val fullLight = LightmapTextureManager.MAX_LIGHT_COORDINATE

        // Pulsing effect
        val pulse = (kotlin.math.sin((System.currentTimeMillis() % 1000000) / 1000.0) * 0.5 + 0.5).toFloat()
        val alpha = (100 + 100 * pulse).toInt().coerceIn(50, 200)

        // Render glowing core at center
        renderBox(vc, pos, normal, fullLight, overlay = 0,
            x = -1.5f, y = 5f, z = -1.5f,
            width = 3f, height = 4f, depth = 3f,
            u = 0f, v = 0f, textureWidth = 64f, textureHeight = 64f,
            red = 255, green = 255, blue = 255, alpha = alpha
        )
    }

    private fun renderBox(
        vc: VertexConsumer,
        pos: Matrix4f,
        normal: Matrix3f,
        light: Int,
        overlay: Int,
        x: Float, y: Float, z: Float,
        width: Float, height: Float, depth: Float,
        u: Float, v: Float,
        textureWidth: Float, textureHeight: Float,
        red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255
    ) {
        val x2 = x + width
        val y2 = y + height
        val z2 = z + depth

        // Bottom
        quad(vc, pos, normal, light, overlay,
            x, y, z, x2, y, z, x2, y, z2, x, y, z2,
            0f, -1f, 0f, red, green, blue, alpha
        )
        // Top
        quad(vc, pos, normal, light, overlay,
            x, y2, z2, x2, y2, z2, x2, y2, z, x, y2, z,
            0f, 1f, 0f, red, green, blue, alpha
        )
        // North
        quad(vc, pos, normal, light, overlay,
            x, y, z2, x2, y, z2, x2, y2, z2, x, y2, z2,
            0f, 0f, 1f, red, green, blue, alpha
        )
        // South
        quad(vc, pos, normal, light, overlay,
            x2, y, z, x, y, z, x, y2, z, x2, y2, z,
            0f, 0f, -1f, red, green, blue, alpha
        )
        // West
        quad(vc, pos, normal, light, overlay,
            x, y, z, x, y, z2, x, y2, z2, x, y2, z,
            -1f, 0f, 0f, red, green, blue, alpha
        )
        // East
        quad(vc, pos, normal, light, overlay,
            x2, y, z2, x2, y, z, x2, y2, z, x2, y2, z2,
            1f, 0f, 0f, red, green, blue, alpha
        )
    }

    private fun quad(
        vc: VertexConsumer,
        pos: Matrix4f,
        normal: Matrix3f,
        light: Int,
        overlay: Int,
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float,
        nx: Float, ny: Float, nz: Float,
        red: Int, green: Int, blue: Int, alpha: Int
    ) {
        vertex(vc, pos, normal, x1, y1, z1, nx, ny, nz, light, overlay, red, green, blue, alpha)
        vertex(vc, pos, normal, x2, y2, z2, nx, ny, nz, light, overlay, red, green, blue, alpha)
        vertex(vc, pos, normal, x3, y3, z3, nx, ny, nz, light, overlay, red, green, blue, alpha)
        vertex(vc, pos, normal, x4, y4, z4, nx, ny, nz, light, overlay, red, green, blue, alpha)
    }

    private fun vertex(
        vc: VertexConsumer,
        pos: Matrix4f,
        normal: Matrix3f,
        x: Float, y: Float, z: Float,
        nx: Float, ny: Float, nz: Float,
        light: Int,
        overlay: Int,
        red: Int, green: Int, blue: Int, alpha: Int
    ) {
        vc.vertex(pos, x, y, z)
            .color(red, green, blue, alpha)
            .texture(0f, 0f)
            .overlay(overlay)
            .light(light)
            .normal(normal, nx, ny, nz)
            .next()
    }

    override fun rendersOutsideBoundingBox(entity: MolecularTransformerBlockEntity): Boolean = true
}
