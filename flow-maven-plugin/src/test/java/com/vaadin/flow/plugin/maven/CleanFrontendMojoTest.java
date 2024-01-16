/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.vaadin.flow.plugin.maven;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vaadin.flow.server.frontend.FrontendTools;
import com.vaadin.flow.server.frontend.installer.NodeInstaller;

import elemental.json.Json;
import elemental.json.JsonObject;
import static com.vaadin.flow.plugin.maven.BuildFrontendMojoTest.assertContainsPackage;
import static com.vaadin.flow.plugin.maven.BuildFrontendMojoTest.getPackageJson;
import static com.vaadin.flow.plugin.maven.BuildFrontendMojoTest.setProject;
import static com.vaadin.flow.server.Constants.PACKAGE_JSON;
import static com.vaadin.flow.server.frontend.FrontendUtils.DEFAULT_FRONTEND_DIR;
import static com.vaadin.flow.server.frontend.FrontendUtils.DEFAULT_GENERATED_DIR;
import static com.vaadin.flow.server.frontend.FrontendUtils.NODE_MODULES;

public class CleanFrontendMojoTest {
    public static final String TEST_PROJECT_RESOURCE_JS = "test_project_resource.js";
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final CleanFrontendMojo mojo = new CleanFrontendMojo();
    private String packageJson;
    private File projectBase;
    private File generatedFolder;
    private File projectFrontendResourcesDirectory;
    private File frontendGenerated;

    private MavenProject project;

    @Before
    public void setup() throws Exception {

        projectBase = temporaryFolder.getRoot();

        project = Mockito.mock(MavenProject.class);
        Mockito.when(project.getBasedir()).thenReturn(projectBase);

        packageJson = new File(projectBase, PACKAGE_JSON).getAbsolutePath();
        generatedFolder = new File(projectBase, DEFAULT_GENERATED_DIR);
        frontendGenerated = new File(projectBase, "frontend/generated");

        File frontendDirectory = new File(projectBase, DEFAULT_FRONTEND_DIR);

        projectFrontendResourcesDirectory = new File(projectBase,
                "flow_resources");

        Assert.assertTrue("Failed to create a test project resources",
                projectFrontendResourcesDirectory.mkdirs());
        Assert.assertTrue("Failed to create a test project file",
                new File(projectFrontendResourcesDirectory,
                        TEST_PROJECT_RESOURCE_JS).createNewFile());

        ReflectionUtils.setVariableValueInObject(mojo, "npmFolder",
                projectBase);
        ReflectionUtils.setVariableValueInObject(mojo, "projectBuildDir",
                projectBase.toPath().resolve("target").toString());
        ReflectionUtils.setVariableValueInObject(mojo, "projectBasedir",
                projectBase);
        ReflectionUtils.setVariableValueInObject(mojo, "frontendDirectory",
                frontendDirectory);
        ReflectionUtils.setVariableValueInObject(mojo, "compatibilityMode",
                "false");
        ReflectionUtils.setVariableValueInObject(mojo, "nodeVersion",
                FrontendTools.DEFAULT_NODE_VERSION);
        ReflectionUtils.setVariableValueInObject(mojo, "nodeDownloadRoot",
                NodeInstaller.DEFAULT_NODEJS_DOWNLOAD_ROOT);

        setProject(mojo, projectBase);
    }

    @Test
    public void mavenGoal_when_packageJsonMissing() throws Exception {
        Assert.assertFalse(FileUtils.fileExists(packageJson));
        mojo.execute();
    }

    @Test
    public void should_removeNodeModulesFolder() throws MojoFailureException {
        final File nodeModules = new File(projectBase, NODE_MODULES);
        Assert.assertTrue("Failed to create 'node_modules'",
                nodeModules.mkdirs());
        mojo.execute();
        Assert.assertFalse("'node_modules' was not removed.",
                nodeModules.exists());
    }

    @Test
    public void should_removeFrontendGeneratedFolder()
            throws MojoFailureException, IOException {
        Assert.assertTrue("Failed to create 'frontend/generated'",
                frontendGenerated.mkdirs());
        FileUtils.fileWrite(new File(frontendGenerated, "my_theme.js"),
                "fakeThemeFile");

        mojo.execute();
        Assert.assertFalse(
                "Generated frontend folder 'frontend/generated' was not removed.",
                frontendGenerated.exists());
    }

    @Test
    public void should_removeNpmPackageLockFile()
            throws MojoFailureException, IOException {
        final File packageLock = new File(projectBase, "package-lock.json");
        FileUtils.fileWrite(packageLock, "{ \"fake\": \"lock\"}");

        mojo.execute();
        Assert.assertFalse("package-lock.json was not removed",
                packageLock.exists());
    }

    @Test
    public void should_removePnpmPackageLockFile()
            throws MojoFailureException, IOException {
        final File pnpmLock = new File(projectBase, "pnpm-lock.yaml");
        FileUtils.fileWrite(pnpmLock, "lockVersion: -1");
        mojo.execute();
        Assert.assertFalse("pnpm-lock.yaml was not removed", pnpmLock.exists());
    }

    @Test
    public void should_cleanPackageJson_removeVaadinAndHashObjects()
            throws MojoFailureException, IOException {
        JsonObject json = createInitialPackageJson();
        FileUtils.fileWrite(packageJson, json.toJson());
        mojo.execute();
        assertPackageJsonContent();

        JsonObject packageJsonObject = getPackageJson(packageJson);

        Assert.assertFalse("'vaadin' object was left in package.json",
                packageJsonObject.hasKey("vaadin"));
        Assert.assertFalse("'hash' object was left in package.json",
                packageJsonObject.hasKey("hash"));
    }

    @Test
    public void should_keepUserDependencies_whenPackageJsonEdited()
            throws MojoFailureException, IOException {
        JsonObject json = createInitialPackageJson();
        json.put("dependencies", Json.createObject());
        json.getObject("dependencies").put("foo", "bar");
        FileUtils.fileWrite(packageJson, json.toJson());
        mojo.execute();
        assertPackageJsonContent();

        JsonObject packageJsonObject = getPackageJson(packageJson);
        assertContainsPackage(packageJsonObject.getObject("dependencies"),
                "foo");
    }

    private void assertPackageJsonContent() throws IOException {
        JsonObject packageJsonObject = getPackageJson(packageJson);

        assertNotContainsPackage(packageJsonObject.getObject("dependencies"),
                "@polymer/polymer", "@webcomponents/webcomponentsjs");

        assertNotContainsPackage(packageJsonObject.getObject("devDependencies"),
                "webpack", "webpack-cli", "webpack-dev-server",
                "html-webpack-plugin");
    }

    static void assertNotContainsPackage(JsonObject dependencies,
            String... packages) {
        Arrays.asList(packages).forEach(dep -> Assert.assertFalse("Has " + dep,
                dependencies.hasKey(dep)));
    }

    static JsonObject createInitialPackageJson() {
        JsonObject packageJson = Json.createObject();
        JsonObject vaadinPackages = Json.createObject();

        vaadinPackages.put("dependencies", Json.createObject());
        JsonObject defaults = vaadinPackages.getObject("dependencies");
        defaults.put("@polymer/polymer", "3.2.0");
        defaults.put("@webcomponents/webcomponentsjs", "^2.2.10");

        packageJson.put("dependencies", defaults);

        vaadinPackages.put("devDependencies", Json.createObject());
        defaults = vaadinPackages.getObject("devDependencies");
        defaults.put("webpack", "4.30.0");
        defaults.put("webpack-cli", "3.3.0");
        defaults.put("webpack-dev-server", "3.3.0");
        defaults.put("webpack-babel-multi-target-plugin", "2.3.1");
        defaults.put("compression-webpack-plugin", "3.0.0");
        defaults.put("webpack-merge", "4.2.1");
        defaults.put("raw-loader", "3.0.0");
        packageJson.put("devDependencies", defaults);

        vaadinPackages.put("hash", "");
        packageJson.put("vaadin", vaadinPackages);

        return packageJson;
    }
}
