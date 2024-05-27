package com.example.demo;

import static java.lang.System.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Log
@SpringBootApplication
public class DemoApplication {

  public static final String COMMA = ",";
  public static final String DOT = ".";
  public static final String SEMICOLON = ";";
  public static final String PACKAGE_NAME = "package";
  public static final String PUBLIC = "public";
  public static final String CLASS = "class";
  public static final String CURLY_OPEN = "{";
  public static final String SPACE = " ";
  public static final String JAVA = ".java";
  public static final String ANO_KATW = ":";
  @Value("${basePath}")
  private String basePath;

  @Value("${contains}")
  private String contains;

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }


  @Component
  public class PostC {

    @PostConstruct
    public void run() throws IOException {
      log.info("START");
      Path startPath = Paths.get(basePath);
      List<Path> el = Files.find(startPath,
              100,
              this::findJavaFiles,
              new FileVisitOption[]{})
          .toList();
      el.parallelStream().forEach(this::printFile);
      log.info("FINISHED");
    }

    private void printFile(Path x) throws RuntimeException {
      log.info("======Starting to read filename: " + x + " =======");
      try {
        List<String> strings = Files.readAllLines(x);
        StringBuilder sb = new StringBuilder();
        sb.append(getClassNameName(strings) + COMMA);
        if (strings.size() <= 149) {
          sb.append("small");
        } else if (strings.size() <= 459) {
          sb.append("medium");
        } else {//size>450
          sb.append("large");
        }
        if (strings.isEmpty()) {
          log.info("========= No files found =========");
        }
        Map<String, List<String>> stringStringMap = groupByPackage(sb);
        printMap(stringStringMap);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private void printMap(Map<String, List<String>> packages) {
      packages.keySet().forEach(packageName -> {
        out.println("Package = " + packageName);
        packages.get(packageName)
            .stream()
            .map(className -> "\tClass = " + className)
            .forEach(out::println);
      });
    }

    private boolean findJavaFiles(Path xPath, BasicFileAttributes a) {
      boolean isAFile = !xPath.toFile().isDirectory();
      boolean isJavaClass = xPath.getFileName().toString().endsWith(JAVA);
      return isJavaClass
          && isAFile;
    }
  }

  private Map<String, List<String>> groupByPackage(StringBuilder sb) {
    Map<String, List<String>> packages = new HashMap<>();
    for (String s : sb.toString().split("\n")) {
      String[] split = s.split(":", 1);
      String pacakgeName = split[0];
      String className = split[1];
      if (packages.get(pacakgeName) != null) {
        packages.get(pacakgeName).add(className);
      } else {
        List list = new ArrayList();
        list.add(className);
        packages.put(pacakgeName, list);
      }
    }
    return packages;
  }

  private String getClassNameName(List<String> strings) {
    StringBuilder sb = new StringBuilder();
    String pkg = strings.get(0)
        .replace(PACKAGE_NAME, "")
        .replaceAll(SPACE, "")
        .replace(SEMICOLON, "");

    String className = getClassName(strings);
    return sb.append(pkg).append(ANO_KATW).append(className).toString();
  }

  private static String getClassName(List<String> strings) {
    return strings.stream()
        .filter(line -> line.contains(CLASS))
        .findFirst().get()
        .replace(PUBLIC, "")
        .replace(CLASS, "")
        .replace(CURLY_OPEN, "")
        .replaceAll(SPACE, "");
  }

}