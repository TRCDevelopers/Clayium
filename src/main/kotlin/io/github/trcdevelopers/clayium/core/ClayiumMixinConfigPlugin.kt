package io.github.trcdevelopers.clayium.core

import io.github.trcdevelopers.clayium.api.util.CUtils.isDeobfEnvironment
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class ClayiumMixinConfigPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String?) {
    }

    override fun getRefMapperConfig(): String {
        return ""
    }

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String): Boolean {
        // Mixin Narrator is enabled only in deobf environment
        return isDeobfEnvironment || !mixinClassName.endsWith("MixinNarrator")
    }

    override fun acceptTargets(myTargets: MutableSet<String?>?, otherTargets: MutableSet<String?>?) {
    }

    override fun getMixins(): MutableList<String?> {
        return mutableListOf()
    }

    override fun preApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }

    override fun postApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }
}
