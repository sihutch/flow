/*
 * Copyright 2000-2018 Vaadin Ltd.
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
 */
package com.vaadin.flow.server.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps to locate the tools in the system by their names.
 */
public class FrontendToolsLocator implements Serializable {
    private static class CommandResult implements Serializable {
        private final String command;
        private final int exitCode;
        private final List<String> stdout;
        private final List<String> stderr;

        private CommandResult(String command, int exitCode, List<String> stdout,
                List<String> stderr) {
            this.command = command;
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        private List<String> getStdout() {
            return stdout;
        }

        private boolean isSuccessful() {
            return exitCode == 0;
        }
    }

    /**
     * Makes an attempt to locate the tool by its name. If there are multiple
     * tools to pick from, the first one that will be selected.
     *
     * @param toolName
     *            the name of a tool to locate, not {@code null}
     * @return absolute path to a tool if it was located and
     *         {@link FrontendToolsLocator#verifyTool(File)} returned
     *         {@code true} for it or {@link Optional#empty()} if there are no
     *         such tools
     */
    public Optional<File> tryLocateTool(String toolName) {
        return executeCommand(isWindows() ? "where" : "which", toolName)
                .map(this::omitErrorResult).map(CommandResult::getStdout)
                .orElse(Collections.emptyList()).stream().map(File::new)
                .filter(this::verifyTool).findFirst();
    }

    /**
     * Verifies that the tool specified works by performing its test launch.
     *
     * @param toolPath
     *            the path to a tool to check
     * @return {@code true} if the test launch had ended with successful error
     *         code, {@code false} otherwise
     */
    public boolean verifyTool(File toolPath) {
        return Optional.ofNullable(toolPath).filter(File::isFile)
                .map(File::getAbsolutePath)
                .flatMap(path -> executeCommand(path, "-v"))
                .map(this::omitErrorResult).isPresent();
    }

    boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().startsWith("windows");
    }

    private Optional<CommandResult> executeCommand(String... commandParts) {
        String commandString = Arrays.toString(commandParts);
        Process process;
        try {
            process = new ProcessBuilder(commandParts).start();
        } catch (IOException e) {
            log().error("Failed to execute the command '{}'", commandString, e);
            return Optional.empty();
        }

        boolean commandExited = false;
        try {
            commandExited = process.waitFor(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log().error(
                    "Unexpected interruption happened during '{}' command execution",
                    commandString, e);
            return Optional.empty();
        } finally {
            if (!commandExited) {
                process.destroyForcibly();
            }
        }

        if (!commandExited) {
            log().error(
                    "Could not get a response from '{}' command in 3 seconds",
                    commandString);
            return Optional.empty();
        }

        List<String> stdout;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8))) {
            stdout = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            log().error("Failed to read the command '{}' stdout", commandString,
                    e);
            return Optional.empty();
        }

        List<String> stderr;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                process.getErrorStream(), StandardCharsets.UTF_8))) {
            stderr = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            log().error("Failed to read the command '{}' stderr", commandString,
                    e);
            return Optional.empty();
        }

        return Optional.of(new CommandResult(commandString, process.exitValue(),
                stdout, stderr));
    }

    private CommandResult omitErrorResult(CommandResult commandResult) {
        if (!commandResult.isSuccessful()) {
            if (log().isDebugEnabled()) {
                log().debug(
                        "Command '{}' exited with non-zero exit code: {}. stdout:\n'{}'\nstderr:\n'{}'",
                        commandResult.command, commandResult.exitCode,
                        commandResult.exitCode,
                        String.join("\n", commandResult.stderr));
            }
            return null;
        }
        if (commandResult.stdout.isEmpty()) {
            if (log().isDebugEnabled()) {
                log().debug("Command '{}' has no output, stderr:\n'{}'",
                        commandResult.command,
                        String.join("\n", commandResult.stderr));
            }
            return null;
        }
        if (!commandResult.stderr.isEmpty()) {
            if (log().isDebugEnabled()) {
                log().debug("Command '{}' has non-empty stderr:\n'{}'",
                        commandResult.command,
                        String.join("\n", commandResult.stderr));
            }
            return null;
        }
        return commandResult;
    }

    private Logger log() {
        return LoggerFactory.getLogger(FrontendToolsLocator.class);
    }
}
