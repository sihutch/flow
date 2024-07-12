package com.vaadin.gradle;

import com.vaadin.flow.plugin.base.BuildFrontendUtil;
import org.gradle.api.JavaVersion;
import org.gradle.workers.WorkAction;
import java.io.File;
import java.net.URI;

public abstract class BuildFrontEndAction implements WorkAction<BuildFrontEndParameters> {
    @Override
    public void execute() {
        try {
            final BuildFrontEndParameters parameters = getParameters();
            PluginAdapter adapter = PluginAdapter.builder()
                .frontendResourcesDirectory(parameters.getFrontendResourcesDirectory().getAsFile().getOrNull())
                .servletResourceOutputDirectory(parameters.getServletResourceOutputDirectory().get().getAsFile())
                .webpackOutputDirectory(parameters.getWebpackOutputDirectory().get().getAsFile())
                .npmFolder(parameters.getNpmFolder().get().getAsFile())
                .jarFiles(parameters.getJarFiles().get())
                .classpathFiles(parameters.getClasspathFiles().get())
                .generatedFolder(parameters.getGeneratedFolder().get().getAsFile())
                .frontendDirectory(parameters.getFrontendDirectory().get().getAsFile())
                .nodeDownloadRoot(URI.create(parameters.getNodeDownloadRoot().get()))
                .useDeprecatedV14Bootstrapping(parameters.getUseDeprecatedV14Bootstrapping().get())
                .applicationProperties(parameters.getApplicationProperties().getAsFile().getOrNull())
                .generatedTsFolder(parameters.getGeneratedTsFolder().get().getAsFile())
                .isJarProject(Boolean.valueOf(parameters.getIsJarProject().get()))
                .buildFolder(parameters.getBuildFolder().get())
                .javaSourceFolder(parameters.getJavaSourceFolder().get().getAsFile())
                .javaResourceFolder(parameters.getJavaResourceFolder().getAsFile().getOrNull())
                .nodeVersion(parameters.getNodeVersion().get())
                .openApiJsonFile(parameters.getOpenApiJsonFile().getAsFile().getOrNull())
                .projectBaseDirectory(parameters.getProjectDir().get().getAsFile().toPath())
                .build();

            final File tokenFile = BuildFrontendUtil.getTokenFile(adapter);
            if(tokenFile.exists()) {
                BuildFrontendUtil.runNodeUpdater(adapter);

                if (adapter.generateBundle()) {
                    BuildFrontendUtil.runFrontendBuild(adapter);
                }

                BuildFrontendUtil.updateBuildFile(adapter);
            } else {
                throw new RuntimeException("token file" + tokenFile + "does not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
