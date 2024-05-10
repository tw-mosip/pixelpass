plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}
android {
    namespace = "io.mosip.pixelpass"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlinOptions {
        jvmTarget = "20"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.qrcodegen)
    implementation(libs.base45)
    implementation(libs.cbor)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation("org.json:json:20140107")
}

publishing {
    repositories {
        maven {
            url = uri(System.getenv("OSSRH_URL"))
            name = "pixelpass"
            credentials {
                username = System.getenv("OSSRH_USER")
                password = System.getenv("OSSRH_SECRET")
            }
        }
    }
    publications {
        create<MavenPublication>("aar") {
            pom {
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    configurations["implementation"].allDependencies.forEach {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }

                    asNode().appendNode("licenses").appendNode("license").apply {
                        appendNode("name", "MIT")
                        appendNode("url", "https://www.mit.edu/~amini/LICENSE.md")
                    }

                    asNode().appendNode("developers").appendNode("developer").apply {
                        appendNode("id(", ")Your developer ID")
                        appendNode("name", "Mosip")
                        appendNode("email", "mosip.emailnotifier@gmail.com")
                        appendNode("organization", "io.mosip")
                        appendNode("organizationUrl", "https://github.com/mosip/pixelpass")
                    }

                    val pluginsNode = asNode().appendNode("build").appendNode("plugins")

                    val pluginNode = pluginsNode.appendNode("plugin")
                    pluginNode.appendNode("groupId", "pl.project13.maven")
                    pluginNode.appendNode("artifactId", "git-commit-id-plugin")
                    pluginNode.appendNode("version", "3.0.1")

                    val executionsNode = pluginNode.appendNode("executions")
                    val executionNode = executionsNode.appendNode("execution")
                    executionNode.appendNode("id(", ")get-the-git-infos")
                    val goalsNode = executionNode.appendNode("goals")
                    goalsNode.appendNode("goal", "revision")
                    executionNode.appendNode("phase", "validate")

                    val configurationNode = pluginNode.appendNode("configuration")
                    configurationNode.appendNode("generateGitPropertiesFile", "true")
                    configurationNode.appendNode(
                        "generateGitPropertiesFilename",
                        project.buildDir.resolve("git.properties")
                    )

                    val includeOnlyPropertiesNode =
                        configurationNode.appendNode("includeOnlyProperties")
                    includeOnlyPropertiesNode.appendNode(
                        "includeOnlyProperty",
                        "^git.build.(time|version)$"
                    )
                    includeOnlyPropertiesNode.appendNode(
                        "includeOnlyProperty",
                        "^git.commit.id.(abbrev|full)$"
                    )

                    configurationNode.appendNode("commitIdGenerationMode", "full")
                    configurationNode.appendNode("dotGitDirectory", "${project.rootDir}/.git")

                    val gpgPluginNode = pluginsNode.appendNode("plugin")
                    gpgPluginNode.appendNode("groupId", "org.apache.maven.plugins")
                    gpgPluginNode.appendNode("artifactId", "maven-gpg-plugin")
                    gpgPluginNode.appendNode("version", "1.5")

                    val gpgExecutionsNode = gpgPluginNode.appendNode("executions")
                    val gpgExecutionNode = gpgExecutionsNode.appendNode("execution")
                    gpgExecutionNode.appendNode("id(", ")sign-artifacts")
                    gpgExecutionNode.appendNode("phase", "verify")
                    val gpgGoalsNode = gpgExecutionNode.appendNode("goals")
                    gpgGoalsNode.appendNode("goal", "sign")

                    val gpgConfigurationNode = gpgExecutionNode.appendNode("configuration")
                    val gpgArgumentsNode = gpgConfigurationNode.appendNode("gpgArguments")
                    gpgArgumentsNode.appendNode("arg", "--pinentry-mode")
                    gpgArgumentsNode.appendNode("arg", "loopback")
                }
            }
            groupId = "io.mosip"
            artifactId = "pixelpass"
            version = "1.0-SNAPSHOT"
            if (project.gradle.startParameter.taskNames.any { it.contains("assembleRelease") }) {
                artifacts {
                    base.archivesName.set("${artifactId}-${version}")
                }
            }
            artifact("/build/outputs/aar/${artifactId}-${version}-release.aar")
        }
    }
}