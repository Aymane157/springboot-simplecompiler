package com.example.demo.service;

import com.example.demo.entity.submission;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class CompilerService {

    public String compileSubmission(submission request) throws Exception {

        Path tempDir = Files.createTempDirectory("submission-");

        for (Map.Entry<String, String> entry : request.getFiles().entrySet()) {
            Path filePath = tempDir.resolve(entry.getKey());
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, entry.getValue());
        }

        String pom = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                   http://maven.apache.org/xsd/maven-4.0.0.xsd">
                
                              <modelVersion>4.0.0</modelVersion>
                
                              <groupId>com.example</groupId>
                              <artifactId>submission</artifactId>
                              <version>0.0.1-SNAPSHOT</version>
                
                              <parent>
                                  <groupId>org.springframework.boot</groupId>
                                  <artifactId>spring-boot-starter-parent</artifactId>
                                  <version>3.2.5</version>
                              </parent>
                
                              <dependencies>
                                  <dependency>
                                      <groupId>org.springframework.boot</groupId>
                                      <artifactId>spring-boot-starter-web</artifactId>
                                  </dependency>
                              </dependencies>
                
                              <build>
                                  <plugins>
                                      <plugin>
                                          <groupId>org.springframework.boot</groupId>
                                          <artifactId>spring-boot-maven-plugin</artifactId>
                                      </plugin>
                                  </plugins>
                              </build>
                
                          </project>
                """;
        Files.writeString(tempDir.resolve("pom.xml"), pom);

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run",
                "--rm",
                "-v", tempDir.toAbsolutePath() + ":/app",
                "-v", "/tmp/maven-cache:/root/.m2",
                "-w", "/app",
                "maven:3.9.6-eclipse-temurin-17",
                "mvn", "clean", "package"     //to test apis later dir hadi but test
        );

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        while ((line = errorReader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();

        return output.toString();
    }

    public String compileAndTest(submission request) throws Exception {
        Path tempDir = Files.createTempDirectory("submission-");

        for (Map.Entry<String, String> entry : request.getFiles().entrySet()) {
            Path filePath = tempDir.resolve(entry.getKey());
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, entry.getValue());
        }

        String pom = """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                   http://maven.apache.org/xsd/maven-4.0.0.xsd">
                
                              <modelVersion>4.0.0</modelVersion>
                
                              <groupId>com.example</groupId>
                              <artifactId>submission</artifactId>
                              <version>0.0.1-SNAPSHOT</version>
                
                              <parent>
                                  <groupId>org.springframework.boot</groupId>
                                  <artifactId>spring-boot-starter-parent</artifactId>
                                  <version>3.2.5</version>
                              </parent>
                
                              <dependencies>
                                  <dependency>
                                      <groupId>org.springframework.boot</groupId>
                                      <artifactId>spring-boot-starter-web</artifactId>
                                  </dependency>
                                  <dependency>
                                          <groupId>org.springframework.boot</groupId>
                                          <artifactId>spring-boot-starter-test</artifactId>
                                          <scope>test</scope>
                                      </dependency>
                                       <dependency>
                                                  <groupId>org.springframework.boot</groupId>
                                                  <artifactId>spring-boot-starter-data-jpa</artifactId>
                                              </dependency>
                                              <dependency>
                                                          <groupId>org.projectlombok</groupId>
                                                          <artifactId>lombok</artifactId>
                                                          <optional>true</optional>
                                                      </dependency>
                                              <dependency>
                                                  <groupId>org.springframework.boot</groupId>
                                                  <artifactId>spring-boot-starter-validation</artifactId>
                                              </dependency>
                              </dependencies>
                
                              <build>
                                  <plugins>
                                      <plugin>
                                          <groupId>org.springframework.boot</groupId>
                                          <artifactId>spring-boot-maven-plugin</artifactId>
                                      </plugin>
                                  </plugins>
                              </build>
                
                          </project>
                """;
        Files.writeString(tempDir.resolve("pom.xml"), pom);

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run",
                "--rm",
                "-v", tempDir.toAbsolutePath() + ":/app",
                "-v", "/tmp/maven-cache:/root/.m2",
                "-w", "/app",
                "maven:3.9.6-eclipse-temurin-17",
                "mvn", "clean", "test"     //to test apis later dir hadi but test
        );

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        while ((line = errorReader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();
        Path reportDir = tempDir.resolve("target/surefire-reports");//where maven writes test results

        if (Files.exists(reportDir)) {
            Files.list(reportDir)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(p -> {
                        try {
                            output.append("\n=== TEST REPORT: ")
                                    .append(p.getFileName())
                                    .append(" ===\n");
                            output.append(Files.readString(p));
                        } catch (Exception ignored) {}
                    });
        }

        return output.toString();
    }
}