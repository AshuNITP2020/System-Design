package com.learning.java_interview;

/**
 * BUILD TOOLS DEEP DIVE: ANT → MAVEN → GRADLE
 *
 * WHY BUILD TOOLS MATTER
 * • Automate compile, test, package, publish; keep builds repeatable and portable.
 * • Manage external libraries with versioning, conflict resolution, and caching.
 *
 * FROM MAKE/ANT TO MAVEN
 * • Make (C world) relied on Makefiles and system package managers; not Java-centric.
 * • Apache Ant (2000) gave Java tasks but fully imperative XML; no standard layout or dependency resolver.
 * • Pain points: every project invented its own directory layout; jar hell; hard to share build logic.
 *
 * MAVEN ARRIVES (Apache, ~2003–2004)
 * • Philosophy: convention over configuration + declarative build metadata.
 * • Standard layout: src/main/java, src/main/resources, src/test/java, target/.
 * • Single descriptor: pom.xml with GAV coordinates (groupId, artifactId, version).
 * • Dependency management: local repo ~/.m2, remote repos (Maven Central), transitive resolution, scopes
 *   (compile, provided, runtime, test, system, import), SNAPSHOT vs release versions.
 * • Lifecycle model: clean, default (build), site lifecycles with ordered phases (validate, compile, test,
 *   package, verify, install, deploy). Plugins bind goals to phases.
 * • Plugins & goals: surefire:test, compiler:compile, jar:jar, shade:shade, spring-boot:run, etc.
 * • Reuse: inheritance (parent POM), aggregation (multi-module reactor), profiles (env-specific configs),
 *   archetypes (project templates).
 * • Outcomes: reproducible builds, standard IDE import, predictable commands (`mvn test`, `mvn package`).
 *
 * GRADLE SHOWS UP (started 2008, 1.0 in 2012)
 * • Motivation: Maven XML verbosity and limited flexibility; need faster incremental builds at scale.
 * • Design mix: declarative where possible, imperative when needed. Uses Groovy DSL (default) or Kotlin DSL
 *   (first-class since Gradle 5).
 * • Task graph model: tasks with inputs/outputs; up-to-date checks; incremental tasks (only changed pieces).
 * • Performance: build daemon (keeps JVM warm), parallel execution, configuration on demand, build cache
 *   (local/remote) for reuse across machines/CI.
 * • Dependency management: still uses Maven/Ivy repos; rich version conflict resolution strategies,
 *   constraints, platforms/BOMs.
 * • Extensibility: write tasks/plugins in Groovy/Kotlin/Java; configuration avoidance APIs; composite builds;
 *   version catalogs for centralized dependency coordinates.
 * • Ecosystem: Android builds, large polyglot monorepos, remote build execution support.
 *
 * MAVEN CORE CONCEPTS (CHEAT SHEET)
 * • Coordinates: groupId:artifactId:version[:packaging][:classifier].
 * • POM sections: project info, properties, dependencies, dependencyManagement, build/plugins,
 *   profiles, modules (for multi-module).
 * • Scopes: compile (default), provided (container supplies), runtime (needed at runtime only),
 *   test (tests only), import (pull BOM).
 * • Typical flow: mvn clean package → runs validate→compile→test→package; mvn install adds to ~/.m2;
 *   mvn deploy pushes to remote repo.
 *
 * GRADLE CORE CONCEPTS (CHEAT SHEET)
 * • Tasks: basic unit of work; inputs/outputs drive incrementalism. Examples: compileJava, test, jar.
 * • Configurations: bucket of dependencies (implementation, api, testImplementation, runtimeOnly, annotationProcessor).
 * • Source sets: main, test by default; easily add more.
 * • Plugins: java, application, kotlin, spring-boot, android, jacoco, etc.
 * • Multi-project builds: root settings.gradle(.kts) includes modules; shared convention plugins reduce duplication.
 * • Execution: gradle build uses a task graph, runs only needed tasks; --scan for build scans; --build-cache to
 *   enable cache; --parallel for parallel tasks.
 *
 * MINIMAL EXAMPLES
 *
 * Maven pom.xml (snippet):
 *   <project>
 *     <modelVersion>4.0.0</modelVersion>
 *     <groupId>com.example</groupId>
 *     <artifactId>demo-app</artifactId>
 *     <version>1.0.0</version>
 *     <properties>
 *       <maven.compiler.source>17</maven.compiler.source>
 *       <maven.compiler.target>17</maven.compiler.target>
 *     </properties>
 *     <dependencies>
 *       <dependency>
 *         <groupId>org.springframework.boot</groupId>
 *         <artifactId>spring-boot-starter-web</artifactId>
 *         <version>3.3.2</version>
 *       </dependency>
 *       <dependency>
 *         <groupId>org.projectlombok</groupId>
 *         <artifactId>lombok</artifactId>
 *         <version>1.18.32</version>
 *         <scope>provided</scope>
 *       </dependency>
 *     </dependencies>
 *     <build>
 *       <plugins>
 *         <plugin>
 *           <groupId>org.springframework.boot</groupId>
 *           <artifactId>spring-boot-maven-plugin</artifactId>
 *         </plugin>
 *       </plugins>
 *     </build>
 *   </project>
 *
 * Gradle Kotlin DSL build.gradle.kts (snippet):
 *   plugins {
 *     java
 *     id("org.springframework.boot") version "3.3.2"
 *     id("io.spring.dependency-management") version "1.1.5"
 *   }
 *
 *   java {
 *     toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
 *   }
 *
 *   repositories {
 *     mavenCentral()
 *   }
 *
 *   dependencies {
 *     implementation("org.springframework.boot:spring-boot-starter-web")
 *     compileOnly("org.projectlombok:lombok:1.18.32")
 *     annotationProcessor("org.projectlombok:lombok:1.18.32")
 *     testImplementation("org.springframework.boot:spring-boot-starter-test")
 *   }
 *
 *   tasks.test {
 *     useJUnitPlatform()
 *   }
 *
 * QUICK COMPARISON
 * • DSL: Maven XML vs Gradle Groovy/Kotlin.
 * • Conventions: Maven stricter; Gradle similar but easier to override via code.
 * • Speed: Maven favors clean builds; Gradle focuses on incremental builds, daemon, caching, parallelism.
 * • Flexibility: Maven via plugins bound to lifecycle; Gradle via arbitrary tasks and plugins (full programming).
 * • Debugging: Maven effective POM (mvn help:effective-pom); Gradle configuration cache/build scans for insight.
 *
 * WHEN TO CHOOSE
 * • Prefer Maven when you want high standardization, minimal custom logic, and a large team with mixed experience.
 * • Prefer Gradle for large/multi-module builds, Android, heavy customization, or when build speed is critical.
 *
 * NOTE: This class is intentionally documentation-only to keep the project compilable without extra dependencies.
 */
public class _09_BuildToolsMavenGradle {
    // Intentionally left without executable code; serves as a study note within the package.
}

