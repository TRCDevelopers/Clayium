package io.github.trcdevelopers.clayium.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3d
import javax.vecmath.Vector3f

class CSimpleBakedModel(
    val quads: List<BakedQuad>,
    val base: IBakedModel,
    val scaleOffset : Float,
) : IBakedModel by base {

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad?> {
        return this.quads
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return base.itemCameraTransforms
    }

    override fun isAmbientOcclusion(state: IBlockState): Boolean {
        return base.isAmbientOcclusion(state)
    }

    override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel?, Matrix4f?> {
        val superVal = base.handlePerspective(cameraTransformType)
        val model = superVal.left
        val transform = superVal.right
        val trsr = TRSRTransformation(transform)
        val vec = Vector3f(scaleOffset, scaleOffset, scaleOffset)
        val newScale = Vector3f(
            trsr.scale.x * scaleOffset,
            trsr.scale.y * scaleOffset,
            trsr.scale.z * scaleOffset
        )
        val newTrsr = TRSRTransformation(
            trsr.translation.apply { add(Vector3f(scaleOffset, scaleOffset, scaleOffset)) },
            trsr.leftRot,
            newScale,
            trsr.rightRot
        )
        return Pair.of(model, newTrsr.matrix)
    }
}