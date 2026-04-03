plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
}

dependencies {
    implementation("com.madgag.spongycastle:core:1.58.0.0")
    implementation("com.madgag.spongycastle:prov:1.58.0.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["java"])
                groupId = "com.mcal"
                artifactId = "apksigner"
                version = "1.2.0"
            }
        }
    }
}
