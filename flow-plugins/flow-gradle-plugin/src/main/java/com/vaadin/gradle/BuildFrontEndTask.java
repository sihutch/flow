package com.vaadin.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.War;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.jvm.toolchain.JavaToolchainSpec;
import org.gradle.workers.ProcessWorkerSpec;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public abstract class BuildFrontEndTask extends DefaultTask {

    @Inject
    public abstract WorkerExecutor getWorkerExecutor();

    @Inject
    public abstract JavaToolchainService getToolchainService();

    @Optional
    @InputDirectory
    public abstract DirectoryProperty getFrontendResourcesDirectory();

    @OutputDirectory
    public abstract DirectoryProperty getServletResourceOutputDirectory();

    @InputFiles
    public abstract SetProperty<File> getJarFiles();

    @InputFiles
    public abstract SetProperty<File> getClasspathFiles();

    @Optional
    @OutputDirectory
    public abstract DirectoryProperty getWebpackOutputDirectory();

    @InputDirectory
    public abstract DirectoryProperty getNpmFolder();

    @OutputDirectory
    public abstract DirectoryProperty getGeneratedFolder();

    @InputDirectory
    public abstract DirectoryProperty getFrontendDirectory();

    @Optional
    @InputFile
    public abstract RegularFileProperty getApplicationProperties();

    @Input
    public abstract Property<String> getBuildFolder();

    @Input
    public abstract Property<String> getNodeDownloadRoot();

    @Input
    public abstract Property<String> getUseDeprecatedV14Bootstrapping();

    @OutputDirectory
    public abstract DirectoryProperty getGeneratedTsFolder();

    @InputDirectory
    public abstract DirectoryProperty getJavaSourceFolder();

    @Optional
    @InputDirectory
    public abstract DirectoryProperty getJavaResourceFolder();

    @Optional
    @InputFile
    public abstract RegularFileProperty getOpenApiJsonFile();

    @InputDirectory
    public abstract RegularFileProperty getProjectDir();

    @Input
    public abstract Property<String> getIsJarProject();

    @Input
    public abstract Property<String> getNodeVersion();


    public BuildFrontEndTask() {
        setDependsOn(List.of("vaadinPrepareFrontend","classes"));

        getProject().getTasks().withType(War.class)
            .forEach(task -> task.mustRunAfter("vaadinBuildFrontend") );
    }

    @TaskAction
    public void buildFrontend() {
        WorkQueue workQueue = getWorkerExecutor().processIsolation(this::execute);

        workQueue.submit(BuildFrontEndAction.class, parameters -> {
            parameters.getFrontendResourcesDirectory().set(getFrontendResourcesDirectory());
            parameters.getServletResourceOutputDirectory().set(getServletResourceOutputDirectory());
            parameters.getJarFiles().set(getJarFiles());
            parameters.getClasspathFiles().set(getClasspathFiles());
            parameters.getWebpackOutputDirectory().set(getWebpackOutputDirectory());
            parameters.getNpmFolder().set(getNpmFolder());
            parameters.getGeneratedFolder().set(getGeneratedFolder());
            parameters.getFrontendDirectory().set(getFrontendDirectory());
            parameters.getBuildFolder().set(getBuildFolder());
            parameters.getNodeDownloadRoot().set(getNodeDownloadRoot());
            parameters.getUseDeprecatedV14Bootstrapping().set(getUseDeprecatedV14Bootstrapping());
            parameters.getApplicationProperties().set(getApplicationProperties());
            parameters.getGeneratedTsFolder().set(getGeneratedTsFolder());
            parameters.getIsJarProject().set(getIsJarProject());
            parameters.getJavaSourceFolder().set(getJavaSourceFolder());
            parameters.getJavaResourceFolder().set(getJavaResourceFolder());
            parameters.getNodeVersion().set(getNodeVersion());
            parameters.getOpenApiJsonFile().set(getOpenApiJsonFile());
            parameters.getProjectDir().set(getProjectDir());
        });
    }

    private void execute(ProcessWorkerSpec spec) {
        spec.getForkOptions().setExecutable(
            getJavaLauncher().getExecutablePath().getAsFile().getAbsolutePath()
        );
    }

    private JavaLauncher getJavaLauncher() {
        //TODO pass the details to create the toolchain spec to avoid Project access
        final JavaToolchainSpec toolchainSpec =
            getProject().getExtensions().findByType(JavaPluginExtension.class).getToolchain();

        return getToolchainService().launcherFor(toolchainSpec).get();
    }
}