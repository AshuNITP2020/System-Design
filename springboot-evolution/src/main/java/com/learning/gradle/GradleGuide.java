package com.learning.gradle;

/**
 * ============================================================================
 *                         GRADLE - BUILD AUTOMATION TOOL
 * ============================================================================
 * 
 * Gradle is a BUILD TOOL that automates:
 * - Compiling your code
 * - Managing dependencies (downloading JARs)
 * - Running tests
 * - Packaging (JAR, WAR)
 * - Deployment
 * 
 * ============================================================================
 * EVOLUTION OF JAVA BUILD TOOLS
 * ============================================================================
 * 
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  2000          2004              2012              Today               │
 * │   │             │                  │                 │                  │
 * │   ▼             ▼                  ▼                 ▼                  │
 * │  ANT    ───►  MAVEN    ───►    GRADLE    ───►    GRADLE               │
 * │                                                                         │
 * │  XML            XML            Groovy/Kotlin      Kotlin DSL           │
 * │  Procedural     Declarative    Flexible           Preferred            │
 * │  Manual deps    Centralized    Best of both       Type-safe            │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * ============================================================================
 */

// ============================================================================
// ERA 1: ANT (2000) - The Stone Age
// ============================================================================

/*
 * ANT = "Another Neat Tool"
 * 
 * PROBLEMS:
 * - Had to write EVERY step manually (compile, copy, jar, etc.)
 * - No dependency management (manually download JARs!)
 * - Verbose XML
 * 
 * build.xml (ANT):
 * 
 * <?xml version="1.0"?>
 * <project name="MyProject" default="jar" basedir=".">
 *     
 *     <!-- Define directories -->
 *     <property name="src.dir" value="src"/>
 *     <property name="build.dir" value="build"/>
 *     <property name="lib.dir" value="lib"/>  <!-- Manually put JARs here! -->
 *     
 *     <!-- Clean -->
 *     <target name="clean">
 *         <delete dir="${build.dir}"/>
 *     </target>
 *     
 *     <!-- Create directories -->
 *     <target name="init">
 *         <mkdir dir="${build.dir}/classes"/>
 *     </target>
 *     
 *     <!-- Compile -->
 *     <target name="compile" depends="init">
 *         <javac srcdir="${src.dir}" 
 *                destdir="${build.dir}/classes"
 *                classpath="${lib.dir}/*.jar">  <!-- Manual JARs! -->
 *         </javac>
 *     </target>
 *     
 *     <!-- Create JAR -->
 *     <target name="jar" depends="compile">
 *         <jar destfile="${build.dir}/myproject.jar"
 *              basedir="${build.dir}/classes"/>
 *     </target>
 *     
 * </project>
 * 
 * To add a dependency (e.g., Spring):
 * 1. Go to Spring website
 * 2. Download spring.jar
 * 3. Copy to lib/ folder
 * 4. Add to classpath in build.xml
 * 5. Download all 15 transitive dependencies manually!
 * 
 * NIGHTMARE!
 */

// ============================================================================
// ERA 2: MAVEN (2004) - Convention over Configuration
// ============================================================================

/*
 * Maven introduced:
 * ✅ Centralized dependency repository (Maven Central)
 * ✅ Automatic transitive dependency resolution
 * ✅ Standard project structure
 * ✅ Lifecycle phases (compile, test, package, install, deploy)
 * 
 * PROBLEMS:
 * ❌ Very verbose XML
 * ❌ Rigid structure (hard to customize)
 * ❌ Slow (no incremental builds)
 * ❌ Complex for multi-module projects
 * 
 * pom.xml (Maven):
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <project xmlns="http://maven.apache.org/POM/4.0.0"
 *          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *          xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
 *                              http://maven.apache.org/xsd/maven-4.0.0.xsd">
 *     
 *     <modelVersion>4.0.0</modelVersion>
 *     
 *     <groupId>com.example</groupId>
 *     <artifactId>my-app</artifactId>
 *     <version>1.0.0</version>
 *     <packaging>jar</packaging>
 *     
 *     <properties>
 *         <maven.compiler.source>17</maven.compiler.source>
 *         <maven.compiler.target>17</maven.compiler.target>
 *         <spring.version>5.3.20</spring.version>
 *     </properties>
 *     
 *     <dependencies>
 *         <dependency>
 *             <groupId>org.springframework</groupId>
 *             <artifactId>spring-core</artifactId>
 *             <version>${spring.version}</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.springframework</groupId>
 *             <artifactId>spring-context</artifactId>
 *             <version>${spring.version}</version>
 *         </dependency>
 *         <!-- Every dependency needs 5 lines of XML! -->
 *     </dependencies>
 *     
 *     <build>
 *         <plugins>
 *             <plugin>
 *                 <groupId>org.apache.maven.plugins</groupId>
 *                 <artifactId>maven-compiler-plugin</artifactId>
 *                 <version>3.8.1</version>
 *                 <configuration>
 *                     <source>17</source>
 *                     <target>17</target>
 *                 </configuration>
 *             </plugin>
 *         </plugins>
 *     </build>
 *     
 * </project>
 * 
 * Just to add ONE dependency = 5 lines of XML!
 */

// ============================================================================
// ERA 3: GRADLE (2012) - Best of Both Worlds
// ============================================================================

/*
 * Gradle combines:
 * ✅ Maven's dependency management (uses same repositories!)
 * ✅ Ant's flexibility (can write custom tasks)
 * ✅ Concise DSL (Groovy or Kotlin)
 * ✅ Incremental builds (FAST!)
 * ✅ Build cache
 * ✅ Parallel execution
 * 
 * build.gradle (Groovy DSL):
 * 
 * plugins {
 *     id 'java'
 *     id 'org.springframework.boot' version '3.2.4'
 * }
 * 
 * group = 'com.example'
 * version = '1.0.0'
 * 
 * java {
 *     sourceCompatibility = '17'
 * }
 * 
 * repositories {
 *     mavenCentral()
 * }
 * 
 * dependencies {
 *     implementation 'org.springframework.boot:spring-boot-starter-web'
 *     testImplementation 'org.springframework.boot:spring-boot-starter-test'
 * }
 * 
 * THAT'S IT! Compare to Maven's 50+ lines!
 * 
 * One dependency = ONE line!
 */

// ============================================================================
// GRADLE FILE STRUCTURE
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  PROJECT STRUCTURE                                                      │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  my-project/                                                            │
 * │  ├── build.gradle          ← Build configuration (dependencies, etc.)  │
 * │  ├── settings.gradle       ← Project settings (name, subprojects)      │
 * │  ├── gradle.properties     ← Gradle properties (optional)              │
 * │  ├── gradlew               ← Gradle wrapper script (Linux/Mac)         │
 * │  ├── gradlew.bat           ← Gradle wrapper script (Windows)           │
 * │  ├── gradle/                                                            │
 * │  │   └── wrapper/                                                       │
 * │  │       ├── gradle-wrapper.jar      ← Wrapper JAR                     │
 * │  │       └── gradle-wrapper.properties ← Gradle version                │
 * │  └── src/                                                               │
 * │      ├── main/                                                          │
 * │      │   ├── java/         ← Source code                               │
 * │      │   └── resources/    ← Config files                              │
 * │      └── test/                                                          │
 * │          ├── java/         ← Test code                                 │
 * │          └── resources/    ← Test config                               │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// build.gradle - THE HEART OF GRADLE
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  build.gradle ANATOMY                                                   │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * // ═══════════════════════════════════════════════════════════════════════
 * // SECTION 1: PLUGINS
 * // ═══════════════════════════════════════════════════════════════════════
 * // Plugins add capabilities to your build
 * 
 * plugins {
 *     id 'java'                                    // Java compilation
 *     id 'org.springframework.boot' version '3.2.4' // Spring Boot plugin
 *     id 'io.spring.dependency-management' version '1.1.4' // BOM management
 * }
 * 
 * // ═══════════════════════════════════════════════════════════════════════
 * // SECTION 2: PROJECT METADATA
 * // ═══════════════════════════════════════════════════════════════════════
 * 
 * group = 'com.example'      // Like Maven's groupId
 * version = '1.0.0'          // Your project version
 * 
 * // ═══════════════════════════════════════════════════════════════════════
 * // SECTION 3: JAVA CONFIGURATION
 * // ═══════════════════════════════════════════════════════════════════════
 * 
 * java {
 *     sourceCompatibility = '17'   // Java version for source code
 *     targetCompatibility = '17'   // Java version for compiled code
 * }
 * 
 * // ═══════════════════════════════════════════════════════════════════════
 * // SECTION 4: REPOSITORIES
 * // ═══════════════════════════════════════════════════════════════════════
 * // Where to download dependencies from
 * 
 * repositories {
 *     mavenCentral()              // Most common - Maven Central
 *     mavenLocal()                // Your local ~/.m2/repository
 *     google()                    // Google's Maven repo (Android)
 *     maven {                     // Custom repository
 *         url 'https://repo.mycompany.com/maven'
 *     }
 * }
 * 
 * // ═══════════════════════════════════════════════════════════════════════
 * // SECTION 5: DEPENDENCIES
 * // ═══════════════════════════════════════════════════════════════════════
 * // Libraries your project needs
 * 
 * dependencies {
 *     // Format: configuration 'group:artifact:version'
 *     
 *     implementation 'org.springframework.boot:spring-boot-starter-web'
 *     implementation rg.projectlombok:lombok'
 *     annotationProcessor 'org.projectlombok:lombok'
 *     
 *     runtimeOnly 'com.h2database:h2'
 *     
 *     testImplementation 'org.springframework.boot:spring-boot-starter-test'
 * }
 * 
 * // ═══════════════════════════════════════════════════════════════════════
 * // SECTION 6: TASKS
 * // ═══════════════════════════════════════════════════════════════════════
 * // Custom build tasks
 * 
 * tasks.named('t'org.springframework.boot:spring-boot-starter-data-jpa'
 *     
 *     compileOnly 'oest') {
 *     useJUnitPlatform()
 * }
 */

// ============================================================================
// DEPENDENCY CONFIGURATIONS EXPLAINED
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  DEPENDENCY CONFIGURATION    │  MEANING                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  implementation              │  Compile + Runtime (most common)        │
 * │                              │  NOT exposed to consumers                │
 * │                                                                         │
 * │  api                         │  Compile + Runtime                       │
 * │                              │  EXPOSED to consumers (library projects) │
 * │                                                                         │
 * │  compileOnly                 │  Compile time ONLY                       │
 * │                              │  Not in JAR (e.g., Lombok)               │
 * │                                                                         │
 * │  runtimeOnly                 │  Runtime ONLY                            │
 * │                              │  Not needed for compilation              │
 * │                              │  (e.g., database drivers)                │
 * │                                                                         │
 * │  annotationProcessor         │  Compile-time annotation processing      │
 * │                              │  (e.g., Lombok, MapStruct)               │
 * │                                                                         │
 * │  testImplementation          │  Compile + Runtime for TESTS only        │
 * │                                                                         │
 * │  testRuntimeOnly             │  Runtime only for tests                  │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * EXAMPLE:
 * 
 * dependencies {
 *     // Spring Web - needed at compile AND runtime
 *     implementation 'org.springframework.boot:spring-boot-starter-web'
 *     
 *     // Lombok - only needed at compile time (generates code)
 *     compileOnly 'org.projectlombok:lombok'
 *     annotationProcessor 'org.projectlombok:lombok'
 *     
 *     // H2 Database - only needed at runtime
 *     runtimeOnly 'com.h2database:h2'
 *     
 *     // JUnit - only for testing
 *     testImplementation 'org.junit.jupiter:junit-jupiter:5.9.0'
 * }
 */

// ============================================================================
// GRADLE WRAPPER (gradlew)
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  WHY USE GRADLE WRAPPER?                                                │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  WITHOUT Wrapper:                                                       │
 * │  - Developer A has Gradle 7.0                                          │
 * │  - Developer B has Gradle 8.0                                          │
 * │  - CI server has Gradle 6.5                                            │
 * │  - "It works on my machine!" 😱                                        │
 * │                                                                         │
 * │  WITH Wrapper:                                                          │
 * │  - Everyone uses SAME Gradle version                                   │
 * │  - No need to install Gradle                                           │
 * │  - Version controlled with project                                     │
 * │  - Consistent builds everywhere! ✅                                    │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * COMMANDS:
 * 
 * ./gradlew build       # Linux/Mac
 * gradlew.bat build     # Windows
 * 
 * The wrapper downloads the correct Gradle version automatically!
 */

// ============================================================================
// COMMON GRADLE COMMANDS
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  COMMAND                     │  WHAT IT DOES                           │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  ./gradlew build             │  Compile + Test + Package                │
 * │                                                                         │
 * │  ./gradlew clean             │  Delete build/ directory                 │
 * │                                                                         │
 * │  ./gradlew clean build       │  Clean then build (fresh build)          │
 * │                                                                         │
 * │  ./gradlew test              │  Run tests only                          │
 * │                                                                         │
 * │  ./gradlew bootRun           │  Run Spring Boot app                     │
 * │                                                                         │
 * │  ./gradlew bootJar           │  Create executable JAR                   │
 * │                                                                         │
 * │  ./gradlew dependencies      │  Show dependency tree                    │
 * │                                                                         │
 * │  ./gradlew tasks             │  List all available tasks                │
 * │                                                                         │
 * │  ./gradlew tasks --all       │  List ALL tasks (including hidden)       │
 * │                                                                         │
 * │  ./gradlew help --task build │  Get help for specific task              │
 * │                                                                         │
 * │  ./gradlew build -x test     │  Build but SKIP tests                    │
 * │                                                                         │
 * │  ./gradlew build --info      │  Build with detailed output              │
 * │                                                                         │
 * │  ./gradlew build --debug     │  Build with debug output                 │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 */

// ============================================================================
// settings.gradle - PROJECT SETTINGS
// ============================================================================

/*
 * settings.gradle is for PROJECT-LEVEL configuration:
 * 
 * // Single project
 * rootProject.name = 'my-app'
 * 
 * // Multi-module project
 * rootProject.name = 'my-parent'
 * include 'module-api'
 * include 'module-core'
 * include 'module-web'
 * 
 * // Your project structure becomes:
 * my-parent/
 * ├── settings.gradle          ← Defines subprojects
 * ├── build.gradle             ← Parent build config
 * ├── module-api/
 * │   └── build.gradle         ← API module config
 * ├── module-core/
 * │   └── build.gradle         ← Core module config
 * └── module-web/
 *     └── build.gradle         ← Web module config
 */

// ============================================================================
// GRADLE VS MAVEN COMPARISON
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  ASPECT              │  MAVEN              │  GRADLE                   │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │  Config Language     │  XML                │  Groovy/Kotlin            │
 * │  Config File         │  pom.xml            │  build.gradle             │
 * │  Build Speed         │  Slower             │  2-10x Faster             │
 * │  Incremental Build   │  No                 │  Yes                      │
 * │  Build Cache         │  No                 │  Yes                      │
 * │  Flexibility         │  Rigid              │  Very Flexible            │
 * │  Learning Curve      │  Moderate           │  Steeper initially        │
 * │  IDE Support         │  Excellent          │  Excellent                │
 * │  Plugins             │  Many               │  Many + Custom easy       │
 * │  Dependency Mgmt     │  Good               │  Same (uses Maven repos)  │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * 
 * SAME DEPENDENCY - Different syntax:
 * 
 * MAVEN (pom.xml):
 * <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter-web</artifactId>
 *     <version>3.2.4</version>
 * </dependency>
 * 
 * GRADLE (build.gradle):
 * implementation 'org.springframework.boot:spring-boot-starter-web:3.2.4'
 * 
 * 5 lines vs 1 line!
 */

// ============================================================================
// PRACTICAL EXAMPLES
// ============================================================================

/*
 * EXAMPLE 1: Basic Java Project
 * 
 * plugins {
 *     id 'java'
 * }
 * 
 * group = 'com.example'
 * version = '1.0.0'
 * 
 * java {
 *     sourceCompatibility = '17'
 * }
 * 
 * repositories {
 *     mavenCentral()
 * }
 * 
 * dependencies {
 *     testImplementation 'org.junit.jupiter:junit-jupiter:5.9.0'
 * }
 * 
 * test {
 *     useJUnitPlatform()
 * }
 */

/*
 * EXAMPLE 2: Spring Boot Project
 * 
 * plugins {
 *     id 'java'
 *     id 'org.springframework.boot' version '3.2.4'
 *     id 'io.spring.dependency-management' version '1.1.4'
 * }
 * 
 * group = 'com.example'
 * version = '1.0.0'
 * 
 * java {
 *     sourceCompatibility = '17'
 * }
 * 
 * repositories {
 *     mavenCentral()
 * }
 * 
 * dependencies {
 *     implementation 'org.springframework.boot:spring-boot-starter-web'
 *     implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
 *     
 *     compileOnly 'org.projectlombok:lombok'
 *     annotationProcessor 'org.projectlombok:lombok'
 *     
 *     runtimeOnly 'com.h2database:h2'
 *     
 *     testImplementation 'org.springframework.boot:spring-boot-starter-test'
 * }
 * 
 * test {
 *     useJUnitPlatform()
 * }
 */

/*
 * EXAMPLE 3: Custom Task
 * 
 * // Define a custom task
 * tasks.register('hello') {
 *     group = 'Custom'
 *     description = 'Prints hello'
 *     doLast {
 *         println 'Hello, Gradle!'
 *     }
 * }
 * 
 * // Run with: ./gradlew hello
 * 
 * 
 * // Task with dependencies
 * tasks.register('greet') {
 *     dependsOn 'hello'  // Runs 'hello' first
 *     doLast {
 *         println 'Greetings!'
 *     }
 * }
 */

/*
 * EXAMPLE 4: Copy Task
 * 
 * tasks.register('copyDocs', Copy) {
 *     from 'src/docs'
 *     into 'build/docs'
 *     include '**\/*.md'
 * }
 */

// ============================================================================
// GRADLE BUILD LIFECYCLE
// ============================================================================

/*
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  GRADLE BUILD PHASES                                                    │
 * ├─────────────────────────────────────────────────────────────────────────┤
 * │                                                                         │
 * │  1. INITIALIZATION                                                      │
 * │     └── Read settings.gradle                                           │
 * │     └── Determine which projects to build                              │
 * │                                                                         │
 * │  2. CONFIGURATION                                                       │
 * │     └── Execute build.gradle for all projects                          │
 * │     └── Build task dependency graph                                    │
 * │     └── Configure all tasks (but don't run them yet)                   │
 * │                                                                         │
 * │  3. EXECUTION                                                           │
 * │     └── Run tasks in correct order                                     │
 * │     └── Only run tasks needed for requested task                       │
 * │                                                                         │
 * └─────────────────────────────────────────────────────────────────────────┘
 * 
 * 
 * TASK GRAPH for 'build':
 * 
 *                    build
 *                      │
 *          ┌───────────┼───────────┐
 *          ▼           ▼           ▼
 *        check      assemble     ...
 *          │           │
 *          ▼           ▼
 *        test        jar
 *          │           │
 *          ▼           ▼
 *    testClasses    classes
 *          │           │
 *          ▼           ▼
 *    compileTestJava  compileJava
 */

// ============================================================================
// TIPS & BEST PRACTICES
// ============================================================================

/*
 * 1. Always use Gradle Wrapper
 *    ./gradlew instead of gradle
 * 
 * 2. Use implementation, not compile (deprecated)
 *    implementation 'group:artifact:version'
 * 
 * 3. Avoid using + for versions
 *    BAD:  implementation 'group:artifact:+'
 *    GOOD: implementation 'group:artifact:1.2.3'
 * 
 * 4. Use dependency management for version consistency
 *    With Spring Boot, versions are managed automatically!
 * 
 * 5. Check dependency tree for conflicts
 *    ./gradlew dependencies
 * 
 * 6. Use buildSrc for complex custom logic
 *    buildSrc/src/main/groovy/... for shared build code
 * 
 * 7. Consider Kotlin DSL for type safety
 *    build.gradle.kts instead of build.gradle
 */

// ============================================================================
// GROOVY vs KOTLIN DSL
// ============================================================================

/*
 * Groovy DSL (build.gradle):
 * 
 * plugins {
 *     id 'java'
 *     id 'org.springframework.boot' version '3.2.4'
 * }
 * 
 * dependencies {
 *     implementation 'org.springframework.boot:spring-boot-starter-web'
 * }
 * 
 * 
 * Kotlin DSL (build.gradle.kts):
 * 
 * plugins {
 *     java
 *     id("org.springframework.boot") version "3.2.4"
 * }
 * 
 * dependencies {
 *     implementation("org.springframework.boot:spring-boot-starter-web")
 * }
 * 
 * Kotlin DSL benefits:
 * ✅ Type safety - IDE catches errors
 * ✅ Better autocompletion
 * ✅ Refactoring support
 * ✅ Same language as Android/Kotlin projects
 */

