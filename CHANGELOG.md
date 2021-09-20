# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 3.0.0 - 2021-09-20

### Changed

-   Introduce Dependency Discoverer v3, based on [ASM](https://asm.ow2.io/) library with new
    command line options.
-   Rename `againstClasspath` folder to `providedClasspath`.

### Added

-   Add support for `.class` file format up to Java 17.
-   Implement caches for module repositories (ZIP and URL).
-   Add JSON output format. The default output is still the textual
    format.
-   Add load of raw `.class` files from `classpath` directory.

## 2.2.0 - 2020-11-18

### Added

-   XML output format (using `-outputType` option). The default output is still the textual format.
-   Listing of native methods (available in both textual and XML output formats).

### Fixed

-   Missing types only referenced by a `catch` clause.
-   Missing array dependencies used in `.class` notation.

## 2.1.0 - 2020-10-09

### Changed

-   Migrate to MicroEJ central repository available at
    <https://repository.microej.com/>.
-   Update license.
-   Use MicroEJ SDK 5.0 or later instead of MicroEJ SDK 4.0.

## 2.0.1 - 2017-10-16

### Fixed

-   Update Repository version.

## 2.0.0 - 2016-12-29

### Changed

-   Change organization.

## 1.0.2 - 2016-11-10

### Fixed

-   Some small wording changes.
-   Move options to a separated interface.

## 1.0.1 - 2016-10-28

### Fixed

-   Completed the README with an explanation of how to
    interpret the results.

## 1.0.0 - 2016-10-27

### Added

-   Initial revision.

----
Copyright 2016-2021 MicroEJ Corp. All rights reserved.  
This library is provided in source code for use, modification and test, subject to license terms.  
Any modification of the source code will break MicroEJ Corp. warranties on the whole library.  


