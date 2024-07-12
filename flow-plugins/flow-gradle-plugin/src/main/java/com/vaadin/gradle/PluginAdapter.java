package com.vaadin.gradle;

import com.vaadin.flow.plugin.base.PluginAdapterBuild;
import com.vaadin.flow.server.frontend.scanner.ClassFinder;
import com.vaadin.flow.server.scanner.ReflectionsClassFinder;
import com.vaadin.flow.utils.FlowFileUtils;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class PluginAdapter implements PluginAdapterBuild {

    private final File frontendResourcesDirectory;
    private final File servletResourceOutputDirectory;
    private final File webpackOutputDirectory;
    private final File npmFolder;
    private final File generatedFolder;
    private final File frontendDirectory;
    private final String buildFolder;
    private final URI nodeDownloadRoot;
    private final String useDeprecatedV14Bootstrapping;
    private final Set<File> jarFiles;
    private final Set<File> classpathFiles;
    private final File applicationProperties;
    private final File generatedTsFolder;
    private final boolean isJarProject;
    private final File javaSourceFolder;
    private final File javaResourceFolder;
    private final String nodeVersion;
    private final File openApiJsonFile;
    private final Path projectBaseDirectory;

    private PluginAdapter(Builder builder) {
        this.frontendResourcesDirectory = builder.frontendResourcesDirectory;
        this.servletResourceOutputDirectory = builder.servletResourceOutputDirectory;
        this.jarFiles = builder.jarFiles;
        this.classpathFiles = builder.classpathFiles;
        this.webpackOutputDirectory = builder.webpackOutputDirectory;
        this.npmFolder = builder.npmFolder;
        this.generatedFolder = builder.generatedFolder;
        this.frontendDirectory = builder.frontendDirectory;
        this.buildFolder = builder.buildFolder;
        this.nodeDownloadRoot = builder.nodeDownloadRoot;
        this.useDeprecatedV14Bootstrapping = builder.useDeprecatedV14Bootstrapping;
        this.applicationProperties = builder.applicationProperties;
        this.generatedTsFolder = builder.generatedTsFolder;
        this.isJarProject = builder.isJarProject;
        this.javaSourceFolder = builder.javaSourceFolder;
        this.javaResourceFolder = builder.javaResourceFolder;
        this.nodeVersion = builder.nodeVersion;
        this.openApiJsonFile = builder.openApiJsonFile;
        this.projectBaseDirectory = builder.projectBaseDirectory;
    }
    
    @Override
    public File frontendResourcesDirectory() {
        return frontendResourcesDirectory;
    }

    @Override
    public boolean generateBundle() {
        return true;
    }

    @Override
    public boolean generateEmbeddableWebComponents() {
        return true;
    }

    @Override
    public boolean optimizeBundle() {
        return true;
    }

    @Override
    public boolean runNpmInstall() {
        return true;
    }

    @Override
    public boolean ciBuild() {
        return false;
    }

    @Override
    public File applicationProperties() {
        return applicationProperties;
    }

    @Override
    public boolean eagerServerLoad() {
        return false;
    }

    @Override
    public File frontendDirectory() {
        return frontendDirectory;
    }

    @Override
    public File generatedFolder() {
        return generatedFolder;
    }

    @Override
    public File generatedTsFolder() {
        return generatedTsFolder;
    }

    @Override
    public ClassFinder getClassFinder() {
        URL[] urls = classpathFiles.stream().distinct()
            .map(FlowFileUtils::convertToUrl).toArray(URL[]::new);

        return new ReflectionsClassFinder(urls);

    }

    @Override
    public Set<File> getJarFiles() {
        return jarFiles;
    }

    @Override
    public boolean isJarProject() {
        return isJarProject;
    }

    @Override
    public String getUseDeprecatedV14Bootstrapping() {
        return useDeprecatedV14Bootstrapping;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public File javaSourceFolder() {
        return javaSourceFolder;
    }

    @Override
    public File javaResourceFolder() {
        return javaResourceFolder;
    }

    @Override
    public void logDebug(final CharSequence debugMessage) {

    }

    @Override
    public void logInfo(final CharSequence infoMessage) {

    }

    @Override
    public void logWarn(final CharSequence warningMessage) {

    }

    @Override
    public void logWarn(final CharSequence warningMessage, final Throwable throwable) {

    }

    @Override
    public void logError(final CharSequence warning, final Throwable e) {

    }

    @Override
    public URI nodeDownloadRoot() throws URISyntaxException {
        return nodeDownloadRoot;
    }

    @Override
    public boolean nodeAutoUpdate() {
        return false;
    }

    @Override
    public String nodeVersion() {
        return nodeVersion;
    }

    @Override
    public File npmFolder() {
        return npmFolder;
    }

    @Override
    public File openApiJsonFile() {
        return openApiJsonFile;
    }

    @Override
    public boolean pnpmEnable() {
        return false;
    }

    @Override
    public boolean useGlobalPnpm() {
        return false;
    }

    @Override
    public boolean productionMode() {
        return false;
    }

    @Override
    public Path projectBaseDirectory() {
        return projectBaseDirectory;
    }

    @Override
    public boolean requireHomeNodeExec() {
        return false;
    }

    @Override
    public File servletResourceOutputDirectory() {
        return servletResourceOutputDirectory;
    }

    @Override
    public File webpackOutputDirectory() {
        return webpackOutputDirectory;
    }

    @Override
    public String buildFolder() {
        return buildFolder;
    }

    @Override
    public List<String> postinstallPackages() {
        return List.of();
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private File servletResourceOutputDirectory;
        private File frontendResourcesDirectory;
        private File webpackOutputDirectory;
        private File npmFolder;
        private File generatedFolder;
        private File frontendDirectory;
        private String buildFolder;
        private Set<File> jarFiles;
        private Set<File> classpathFiles;
        private URI nodeDownloadRoot;
        private String useDeprecatedV14Bootstrapping;
        private File applicationProperties;
        private File generatedTsFolder;
        private boolean isJarProject;
        private File javaSourceFolder;
        private File javaResourceFolder;
        private String nodeVersion;
        private File openApiJsonFile;
        private Path projectBaseDirectory;

        Builder projectBaseDirectory(final Path projectBaseDirectory) {
            this.projectBaseDirectory = projectBaseDirectory;
            return this;
        }

        Builder openApiJsonFile(final File openApiJsonFile) {
            this.openApiJsonFile = openApiJsonFile;
            return this;
        }

        Builder nodeVersion(final String nodeVersion) {
            this.nodeVersion = nodeVersion;
            return this;
        }

        Builder javaResourceFolder(final File javaResourceFolder) {
            this.javaResourceFolder = javaResourceFolder;
            return this;
        }

        Builder javaSourceFolder(final File javaSourceFolder) {
            this.javaSourceFolder = javaSourceFolder;
            return this;
        }

        Builder isJarProject(final boolean isJarProject) {
            this.isJarProject = isJarProject;
            return this;
        }

        Builder useDeprecatedV14Bootstrapping(final String useDeprecatedV14Bootstrapping) {
            this.useDeprecatedV14Bootstrapping = useDeprecatedV14Bootstrapping;
            return this;
        }

        Builder nodeDownloadRoot(final URI nodeDownloadRoot) {
            this.nodeDownloadRoot = nodeDownloadRoot;
            return this;
        }

        Builder applicationProperties(final File applicationProperties) {
            this.applicationProperties = applicationProperties;
            return this;
        }

        Builder generatedTsFolder(final File generatedTsFolder) {
            this.generatedTsFolder = generatedTsFolder;
            return this;
        }

        Builder frontendDirectory(final File frontendDirectory) {
            this.frontendDirectory = frontendDirectory;
            return this;
        }

        Builder webpackOutputDirectory(final File webpackOutputDirectory) {
            this.webpackOutputDirectory = webpackOutputDirectory;
            return this;
        }

        Builder servletResourceOutputDirectory(final File servletResourceOutputDirectory) {
            this.servletResourceOutputDirectory = servletResourceOutputDirectory;
            return this;
        }

        Builder frontendResourcesDirectory(final File frontendResourcesDirectory) {
            this.frontendResourcesDirectory = frontendResourcesDirectory;
            return this;
        }

        Builder npmFolder(final File npmFolder) {
            this.npmFolder = npmFolder;
            return this;
        }

        Builder generatedFolder(final File generatedFolder) {
            this.generatedFolder = generatedFolder;
            return this;
        }

        Builder jarFiles(final Set<File> jarFiles) {
            this.jarFiles = jarFiles;
            return this;
        }

        Builder classpathFiles(final Set<File> classpathFiles) {
            this.classpathFiles = classpathFiles;
            return this;
        }

        Builder buildFolder(final String buildFolder) {
            this.buildFolder = buildFolder;
            return this;
        }


        PluginAdapter build() {
            return new PluginAdapter(this);
        }
    }
}
