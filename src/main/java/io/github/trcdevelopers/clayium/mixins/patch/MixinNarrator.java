package io.github.trcdevelopers.clayium.mixins.patch;

import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorDummy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Narrator.class, remap = false)
public interface MixinNarrator {

    /**
     * @author bqc0n
     * @reason NarratorOSX {@link com.mojang.text2speech.NarratorOSX} causes UnsatisfiedLinkError on Apple Silicon Macs
     * (Maybe native libs are not available in arm64 Java 8 JDKs?).
     * This mixin should be enabled only on deobf environment.
     */
    @Overwrite(remap = false)
    static Narrator getNarrator() {
        return new NarratorDummy();
    }
}
