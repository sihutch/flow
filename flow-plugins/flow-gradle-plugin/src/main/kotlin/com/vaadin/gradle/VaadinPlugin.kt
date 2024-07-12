/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import java.io.File

//Added by Si
private val servletApiJarRegex = Regex(".*(/|\\\\)(portlet-api|javax\\.servlet-api)-.+jar$")
/**
 * The main class of the Vaadin Gradle Plugin.
 * @author mavi@vaadin.com
 */
public class VaadinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // we need Java Plugin conventions so that we can ensure the order of tasks
        project.pluginManager.apply(JavaPlugin::class.java)
        var extensionName = "vaadin"
        if (project.extensions.findByName(extensionName) != null) {
            // fixes https://github.com/vaadin/vaadin-gradle-plugin/issues/26
            extensionName = "vaadinPlatform"
        }
        project.extensions.create(extensionName, VaadinFlowPluginExtension::class.java, project)

        val adapter = GradlePluginAdapter(project, false)
        val extension: VaadinFlowPluginExtension =
            VaadinFlowPluginExtension.get(project)

        project.tasks.apply {
            register("vaadinClean", VaadinCleanTask::class.java)
            register("vaadinPrepareFrontend", VaadinPrepareFrontendTask::class.java)
            //register("vaadinBuildFrontend", VaadinBuildFrontendTask::class.java)
            register("vaadinBuildFrontend", BuildFrontEndTask::class.java) { task ->
                addFrontendResourcesDirectory(adapter,task)
                task.servletResourceOutputDirectory.set(adapter.servletResourceOutputDirectory())
                task.jarFiles.set(adapter.jarFiles)
                task.classpathFiles.set(getClassPathFiles(project, extension))
                task.webpackOutputDirectory.set(adapter.webpackOutputDirectory())
                task.npmFolder.set(adapter.npmFolder())
                task.generatedFolder.set(adapter.generatedFolder());
                task.frontendDirectory.set(adapter.frontendDirectory());
                task.buildFolder.set(adapter.buildFolder())
                task.nodeDownloadRoot.set(extension.nodeDownloadRoot)
                task.useDeprecatedV14Bootstrapping.set(adapter.useDeprecatedV14Bootstrapping)
                addApplicationProperties(adapter,task)
                task.generatedTsFolder.set(adapter.generatedTsFolder())
                task.isJarProject.set(adapter.isJarProject.toString());
                task.javaSourceFolder.set(adapter.javaSourceFolder());
                addJavaResourceFolder(adapter,task)
                task.nodeVersion.set(adapter.nodeVersion());
                openApiJsonFile(adapter,task)
                task.projectDir.set(project.projectDir)            }
        }

        project.afterEvaluate {
            val extension: VaadinFlowPluginExtension = VaadinFlowPluginExtension.get(it)
            extension.autoconfigure(project)

            // add a new source-set folder for generated stuff, by default `vaadin-generated`
            val sourceSets: SourceSetContainer = it.properties["sourceSets"] as SourceSetContainer
            sourceSets.getByName(extension.sourceSetName).resources.srcDirs(extension.resourceOutputDirectory)

            // auto-activate tasks: https://github.com/vaadin/vaadin-gradle-plugin/issues/48
            project.tasks.getByPath(extension.processResourcesTaskName!!).dependsOn("vaadinPrepareFrontend")
            if (extension.productionMode) {
                // this will also catch the War task since it extends from Jar
                project.tasks.withType(Jar::class.java) { task: Jar ->
                    task.dependsOn("vaadinBuildFrontend")
                }
            }
        }
    }

    private fun openApiJsonFile(adapter: GradlePluginAdapter, task: BuildFrontEndTask) {
        val openApiJsonFile = adapter.openApiJsonFile();
        if(openApiJsonFile.exists()) {
            task.openApiJsonFile.set(openApiJsonFile)
        }
    }

    private fun addApplicationProperties(adapter: GradlePluginAdapter, task: BuildFrontEndTask) {
        val applicationProperties = adapter.applicationProperties();
        if(applicationProperties.exists()) {
            task.applicationProperties.set(applicationProperties)
        }
    }

    private fun addFrontendResourcesDirectory(adapter: GradlePluginAdapter, task: BuildFrontEndTask) {
        val frontendResourcesDirectory = adapter.frontendResourcesDirectory();
        if(frontendResourcesDirectory.exists()) {
            task.frontendResourcesDirectory.set(frontendResourcesDirectory)
        }
    }

    private fun addJavaResourceFolder(adapter: GradlePluginAdapter, task: BuildFrontEndTask) {
        val javaResourceFolder = adapter.javaResourceFolder();
        if(javaResourceFolder.exists()) {
            task.javaResourceFolder.set(javaResourceFolder)
        }
    }

    //Added by Si
    private fun getClassPathFiles(project: Project, extension: VaadinFlowPluginExtension): Set<File> {
        val dependencyConfiguration: Configuration? = project.configurations.findByName(extension.dependencyScope!!)
        val dependencyConfigurationJars: List<File> = if (dependencyConfiguration != null) {
            var artifacts: List<ResolvedArtifact> =
                dependencyConfiguration.resolvedConfiguration.resolvedArtifacts.toList()
            val extension = VaadinFlowPluginExtension.get(project)
            val artifactFilter = extension.classpathFilter.toPredicate()
            artifacts = artifacts.filter { artifactFilter.test(it.moduleVersion.id.module) }
            artifacts.map { it.file }
        } else listOf()

        // we need to also analyze the project's classes
        val sourceSet: SourceSetContainer = project.properties["sourceSets"] as SourceSetContainer
        val classesDirs: List<File> = sourceSet.getByName(extension.sourceSetName).output.classesDirs
            .toList()
            .filter { it.exists() }

        val resourcesDir: List<File> = listOfNotNull(sourceSet.getByName(extension.sourceSetName).output.resourcesDir)
            .filter { it.exists() }

        // for Spring Boot project there is no "providedCompile" scope: the WAR plugin brings that in.
        val providedDeps: Configuration? = project.configurations.findByName("providedCompile")
        val servletJar: List<File> = providedDeps
            ?.filter { it.absolutePath.matches(servletApiJarRegex) }
            ?.toList()
            ?: listOf()

        val apis: Set<File> = (dependencyConfigurationJars + classesDirs + resourcesDir + servletJar).toSet()

        // eagerly check that all the files/folders exist, to avoid spamming the console later on
        // see https://github.com/vaadin/vaadin-gradle-plugin/issues/38 for more details
        apis.forEach {
            check(it.exists()) { "$it doesn't exist" }
        }

        return apis
    }
}