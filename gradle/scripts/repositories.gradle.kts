repositories {
    maven {
        name = "BlameJared Maven"
        url = uri("https://maven.blamejared.com")
    }
    maven {
        name = "CleanroomMC Maven"
        url = uri("https://maven.cleanroommc.com")
    }
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
    maven {
        name = "GTCEu Maven"
        url = uri("https://maven.gtceu.com")
    }
    maven {
        name = "SpongePowered Maven"
        url = uri("https://repo.spongepowered.org/maven")
    }
    maven {
        name = "ProgWML6 Maven"
        url = uri("https://dvs1.progwml6.com/files/maven/")
    }
    maven {
        name = "thiakil"
        url = uri("http://maven.thiakil.com")
        isAllowInsecureProtocol = true
    }

    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}