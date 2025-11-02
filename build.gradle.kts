import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.compiler
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.kotlin.gradle.utils.extendsFrom

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlinVersion}")
    }
}

plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version libs.versions.kotlinVersion
    id("maven-publish")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
    id("eclipse")
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.9"
}

val forgelinContinuousVersion: String by project
val mixinBooterVersion: String by project

val modName: String by project
val modId: String by project
val modGroup: String by project
val modVersion: String by project
val modArchivesBaseName: String by project
val minecraftVersion: String by project

// CoreMod
val coreMod: String by project
val includeMod: String by project
val coreModPluginClassName: String by project

val accessTransformersFile: String by project

val gradleTokenClassName: String by project

val developmentEnvironmentUserName: String by project
val separateRunDirectories: String by project

project.version = "$minecraftVersion-$modVersion"
project.group = modGroup

base {
    archivesName = modArchivesBaseName
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        // Azul covers the most platforms for Java 8 toolchains, crucially including MacOS arm64
        vendor.set(JvmVendorSpec.AZUL)
    }
    // Generate sources and javadocs jars when building and publishing
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

configurations {
    val embed = create("embed")
    implementation.configure {
        extendsFrom(embed)
    }

    val runtimeOnlyNonPublishable = register("runtimeOnlyNonPublishable") {
        description = "Runtime only dependencies that are not published alongside the jar"
        isCanBeConsumed = false
        isCanBeResolved = false
    }

    val devOnlyNonPublishable = register("devOnlyNonPublishable") {
        description = "Runtime and compiletime dependencies that are not published alongside the jar (compileOnly + runtimeOnlyNonPublishable)"
        isCanBeConsumed = false
        isCanBeResolved = false
    }

    compileOnly.extendsFrom(devOnlyNonPublishable)
    runtimeOnlyNonPublishable.extendsFrom(devOnlyNonPublishable)
    runtimeClasspath.extendsFrom(runtimeOnlyNonPublishable)
    testRuntimeClasspath.extendsFrom(runtimeOnlyNonPublishable)
}

minecraft {
    mcVersion.set("1.12.2")

    // MCP Mappings
    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")

    // Set username here, the UUID will be looked up automatically
    username.set(developmentEnvironmentUserName)

    // Add any additional tweaker classes here
    // extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")

    // Add various JVM arguments here for runtime
    val args = mutableListOf("-ea:${group}")
    if (coreMod.toBoolean()) {
        args += "-Dfml.coreMods.load=$coreModPluginClassName"
    }
    if (coreMod.toBoolean()) {
        args += "-Dmixin.hotSwap=true"
        args += "-Dmixin.checks.interfaces=true"
        args += "-Dmixin.debug.export=true"
    }
    extraRunJvmArguments.addAll(args)

    // Include and use dependencies' Access Transformer files
    useDependencyAccessTransformers.set(true)

    // Add any properties you want to swap out for a dynamic value at build time here
    // Any properties here will be added to a class at build time, the name can be configured below
    injectedTags.put("VERSION", modVersion)
}

// Generate a group.archives_base_name.Tags class
tasks.injectTags.configure {
    // Change Tags class' name here:
    outputClassName.set(gradleTokenClassName)
}

apply(from = "gradle/scripts/repositories.gradle.kts")

repositories {
    mavenLocal() // Must be last for caching to work
}

dependencies {
    runtimeOnly("io.github.chaosunity.forgelin:Forgelin-Continuous:${forgelinContinuousVersion}") {
        exclude("net.minecraftforge")
    }

    /* mixin */
    val mixin = modUtils.enableMixins("zone.rong:mixinbooter:$mixinBooterVersion", "mixins.$modId.refmap.json") as String
    api(mixin) {
        isTransitive = false
    }
    annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
    annotationProcessor("com.google.guava:guava:32.1.3-jre")
    annotationProcessor("com.google.code.gson:gson:2.13.2")
    annotationProcessor(mixin) {
        isTransitive = false
    }
}
apply(from = "gradle/scripts/dependencies.gradle")
apply(from = "gradle/scripts/mod_dependencies.gradle")

if (accessTransformersFile.isNotBlank()) {
    val fileLocation = file("$projectDir/src/main/resources/$accessTransformersFile")
    if (fileLocation.exists()) {
        tasks.deobfuscateMergedJarToSrg.configure {
            accessTransformerFiles.from(fileLocation)
        }
        tasks.srgifyBinpatchedJar.configure {
            accessTransformerFiles.from(fileLocation)
        }
    } else {
        throw GradleException("Access transformers file '$accessTransformersFile' does not exist at expected location: $fileLocation")
    }
}

tasks.withType<ProcessResources> {
    // This will ensure that this task is redone when the versions change
    inputs.property("version", modVersion)
    inputs.property("mcversion", minecraft.mcVersion)

    // Replace various properties in mcmod.info and pack.mcmeta if applicable
    filesMatching(arrayListOf("mcmod.info", "pack.mcmeta")) {
        expand("version" to modVersion, "mcversion" to minecraft.mcVersion)
    }

    if (accessTransformersFile.isNotBlank()) {
        rename("(.+_at.cfg)", "META-INF/$1") // Make sure Access Transformer files are in META-INF folder
    }
}

tasks.withType<Jar> {
    manifest {
        val attributeMap = mutableMapOf<String, String>()
        if (coreMod.toBoolean()) {
            attributeMap["FMLCorePlugin"] = coreModPluginClassName
            if (includeMod.toBoolean()) {
                attributeMap["FMLCorePluginContainsFMLMod"] = true.toString()
                attributeMap["ForceLoadAsMod"] = (project.gradle.startParameter.taskNames[0] == "build").toString()
            }
        }
        if (accessTransformersFile.isNotBlank()) {
            attributeMap["FMLAT"] = accessTransformersFile
        }
        attributes(attributeMap)
    }
    // Add all embedded dependencies into the jar
    from(provider {
        configurations.getByName("embed").map {
            if (it.isDirectory()) it else zipTree(it)
        }
    })
}

idea {
    module {
        inheritOutputDirs = true
    }
    project {
        settings {
            runConfigurations {
                add(Gradle("1. Run Client").apply {
                    setProperty("taskNames", listOf("runClient"))
                })
                add(Gradle("2. Run Server").apply {
                    setProperty("taskNames", listOf("runServer"))
                })
                add(Gradle("3. Run Obfuscated Client").apply {
                    setProperty("taskNames", listOf("runObfClient"))
                })
                add(Gradle("4. Run Obfuscated Server").apply {
                    setProperty("taskNames", listOf("runObfServer"))
                })
            }
            compiler.javac {
                afterEvaluate {
                    javacAdditionalOptions = "-encoding utf8"
                    moduleJavacAdditionalOptions = mutableMapOf(
                        (project.name + ".main") to tasks.compileJava.get().options.compilerArgs.joinToString(
                            " ") { "\"$it\"" })
                }
            }
        }
    }
}

if (separateRunDirectories.toBoolean()) {
    tasks.named<JavaExec>("runClient") {
        workingDir = project.file("run/client")
    }
    tasks.named<JavaExec>("runServer") {
        workingDir = project.file("run/server")
    }
}

tasks.named("processIdeaSettings").configure {
    dependsOn("injectTags")
}

sourceSets {
    named("test") {
        java {
            compileClasspath += patchedMc.get().output + mcLauncher.get().output
            runtimeClasspath += patchedMc.get().output + mcLauncher.get().output
        }
    }
}

tasks.named<Test>("test") {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    })

    testLogging {
        events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED)

        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showStackTraces = true
        showCauses = true
        showStandardStreams = true
    }

    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addStringOption("tag", "reason:a:\"Reason for Overwrite:\"")

    options.encoding = "UTF-8"
    options.locale = "en_US"
}