package com.vaadin.gradle

import org.gradle.testkit.runner.BuildResult
import org.junit.Test
import java.io.File
import kotlin.test.expect

class JavaToolChainTest : AbstractGradleTest() {

    @Test
    fun testBuildsFrontendWithJavaToolchains() {
        testProject.buildFile.writeText("""
            plugins {
                id 'war'
                id 'com.vaadin'
            }

            repositories {
                mavenLocal()
                mavenCentral()
                maven { url = 'https://maven.vaadin.com/vaadin-prereleases' }
            }
            dependencies {
                implementation("com.vaadin:flow:$flowVersion")
                providedCompile("jakarta.servlet:jakarta.servlet-api:6.0.0")
                implementation("org.slf4j:slf4j-simple:$slf4jVersion")
            }
            vaadin {
                nodeAutoUpdate = true // test the vaadin{} block by changing some innocent property with limited side-effect
            }
            
            java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(21)
                }
            }

        """.trimIndent()

        )
        testProject.newFile("src/main/java/org/vaadin/example/MainView.java", """
            package org.vaadin.example;

            import com.vaadin.flow.component.html.Div;
            import com.vaadin.flow.component.html.Span;
            import com.vaadin.flow.router.Route;

            @Route("")
            public class MainView extends Div {
                
                record Point(int x, int y) {}
                public static int beforeRecordPattern(Object obj) {
                    int sum = 0;
                    if(obj instanceof Point p) {
                        int x = p.x();
                        int y = p.y();
                        sum = x+y;
                    }
                    return sum;
                }
            
                public static int afterRecordPattern(Object obj) {
                    if(obj instanceof Point(int x, int y)) {
                        return x+y;
                    }
                    return 0;
                }
    
                public MainView() {
                    add(new Span("It works!"));
                }
            }
        """.trimIndent())

        val result: BuildResult = testProject.build("-Pvaadin.productionMode", "vaadinBuildFrontend")
        result.expectTaskSucceded("vaadinPrepareFrontend")
        result.expectTaskSucceded("vaadinBuildFrontend")
    }
}