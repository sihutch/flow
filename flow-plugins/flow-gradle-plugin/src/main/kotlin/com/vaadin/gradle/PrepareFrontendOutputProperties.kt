/**
 *    Copyright 2000-2023 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.gradle

import com.vaadin.flow.server.Constants
import com.vaadin.flow.server.frontend.FrontendUtils
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import java.io.File

/**
 * Declaratively defines the outputs of the [VaadinPrepareFrontendTask]:
 * package.json, vite.config.ts, and other files generated by Vaadin, as well
 * as the files in the frontend directory. Being used for caching the results
 * of vaadinPrepareFrontend task to not run it again if inputs are the same.
 */
internal class PrepareFrontendOutputProperties(
    private val project: Project,
    private val config: PluginEffectiveConfiguration,
) {

    @OutputFile
    @Optional
    public fun getPackageJson(): File {
        return File(project.projectDir, Constants.PACKAGE_JSON)
    }

    @OutputFile
    @Optional
    public fun getPackageLockJson(): File {
        return File(project.projectDir, Constants.PACKAGE_LOCK_JSON)
    }

    @OutputFile
    @Optional
    public fun getPackageLockYaml(): File {
        return File(project.projectDir, Constants.PACKAGE_LOCK_YAML)
    }

    @OutputFile
    @Optional
    public fun getViteConfig(): File {
        return File(project.projectDir, FrontendUtils.VITE_CONFIG)
    }

    @OutputFile
    @Optional
    public fun getViteGeneratedConfig(): File {
        return File(project.projectDir, FrontendUtils.VITE_GENERATED_CONFIG)
    }

    @OutputFile
    @Optional
    public fun getTsConfig(): File {
        return File(project.projectDir, "tsconfig.json")
    }

    @OutputFile
    @Optional
    public fun getTsDefinition(): File {
        return File(project.projectDir, "types.d.ts")
    }

    @OutputDirectory
    public fun getGeneratedTsFolder(): Property<File> =
        config.generatedTsFolder

    @OutputDirectory
    public fun getResourceOutputDirectory(): Property<File> = config.resourceOutputDirectory
}
