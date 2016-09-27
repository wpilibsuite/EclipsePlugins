# Eclipse Plugins

[![Build Status](https://travis-ci.org/wpilibsuite/EclipsePlugins.svg?branch=master)](https://travis-ci.org/wpilibsuite/EclipsePlugins)

Welcome to the WPILib project. This repository contains the Eclipse Plugins project.

- [WPILib Mission](#wpilib-mission)
- [Building Eclipse Plugins](#building-eclipse-plugins)
    - [Requirements](#requirements)
- [Contributing to WPILib](#contributing-to-wpilib)

## WPILib Mission

The WPILib Mission is to enable FIRST teams to focus on writing game-specific software rather than on hardware details - "raise the floor, don't lower the ceiling". We try to enable teams with limited programming knowledge and/or mentor experience to do as much as possible, while not hampering the abilities of teams with more advanced programming capabilities. We support Kit of Parts control system components directly in the library. We also strive to keep parity between major features of each language (Java, C++, and NI's LabVIEW), so that teams aren't at a disadvantage for choosing a specific programming language. WPILib is an open-source project, licensed under the BSD 3-clause license. You can find a copy of the license [here](license.txt).

# Building Eclipse Plugins

Building the Eclipse Plugins is very straightforward. The plugins use Gradle to compile, which calls into Maven under the hood.

## Requirements
- [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- Maven

## Building

To actually build the plugins, simply call:

```bash
./gradlew build
```

To clean, call:

```bash
./gradlew clean
```

Note that there will be a lot of output. This is because Gradle is actually calling into Maven under the hood.

## Modifying the NI Image Version

The Gradle harness takes care of deduplicating the NI allowable image versions by copying [`ni_image.properties`](ni_image.properties) to `edu.wpi.first.wpilib.plugins.java/src/main/resources/java-zip/ant/` and to `edu.wpi.first.wpilib.plugins.cpp/src/main/resources/cpp-zip/ant/`. To update the version, simply change the `ni_image.properties` in the repository root and the changes will be copied during the next build.

# Contributing to WPILib

See [CONTRIBUTING.md](CONTRIBUTING.md).
