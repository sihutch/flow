package com.vaadin.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.workers.WorkParameters;
import java.io.File;

public interface BuildFrontEndParameters extends WorkParameters {
    @InputDirectory
    DirectoryProperty getFrontendResourcesDirectory();
    DirectoryProperty getServletResourceOutputDirectory();
    DirectoryProperty getWebpackOutputDirectory();
    DirectoryProperty getNpmFolder();
    DirectoryProperty getGeneratedFolder();
    DirectoryProperty getFrontendDirectory();
    Property<String> getBuildFolder();
    Property<String> getNodeDownloadRoot();
    SetProperty<File> getJarFiles();
    SetProperty<File> getClasspathFiles();
    Property<String> getUseDeprecatedV14Bootstrapping();
    RegularFileProperty getApplicationProperties();
    DirectoryProperty getGeneratedTsFolder();
    Property<String> getIsJarProject();
    DirectoryProperty getJavaSourceFolder();
    DirectoryProperty getJavaResourceFolder();
    Property<String> getNodeVersion();
    RegularFileProperty getOpenApiJsonFile();
    RegularFileProperty getProjectDir();
}
