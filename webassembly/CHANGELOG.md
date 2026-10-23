# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.0] - 2026-10-23

### Added

  - Analyze WebAssembly modules that use exception handling, and report their exception instructions as missing.
  - Report the WebAssembly features that MicroEJ does not implement, such as multi-value or multiple memories.
  - Add the `--no-feature-check` option to skip the report of unsupported WebAssembly features.

### Changed

  - Require `wabt` 1.0.39 or higher.

### Fixed

  - Fix the tool version printed at the top of the report.
  - Fix the analysis of a WebAssembly module that has no code.
  - Fix the number of functions printed for the code section.
  - Fix the message printed when wasm-objdump fails.
  - Fix the analysis of a WebAssembly module that has a custom section, such as debug names.
  - Fix the exit status of the tool: 0 when the report is written, 1 when the analysis fails.

## 1.5.0 - 2025-05-26

### Added

  - Update the list with newly supported atomic instructions. 

## 1.4.0 - 2025-05-02

### Added

  - New List with the extended WebAssembly instructions.

### Changed

  - Update the report to show stats about the extended instructions.

## 1.3.0 - 2024-10-14

### Changed

  - Full WebAssembly MVP instructions implemented.

### Fixed

  - WebAssembly MVP instructions list.

## 1.2.2 - 2024-10-07

### Fixed

  - Data section size information extraction of `wasm-objdump` report on some WebAssembly files.

## 1.2.1 - 2024-10-03

### Fixed

  - Data section parsing of `wasm-objdump` report.
  - Error when`wasm-stats` launched on WebAssembly files using extra features (POSIX threads or others).

## 1.2.0 - 2024-10-01

### Added

  - Total item numbers (instructions, functions, data sections, etc.).
  - WebAssembly code section functions total size.
  - WebAssembly data section information.
  - Report metadata (WebAssembly file name with hash, creation date).
  - WebAssembly imported functions module name.

### Changed

  - Application usage documentation.
  - Application source code directories.

## 1.1.0 - 2024-08-30

### Added

  - `-e | --wasm-stats-extra-args` command line option to be able to pass `wasm-stats` command line extra options.
  - WebAssembly total instructions count to the output report.
  - WebAssembly instruction counts to the output report.
  - WebAssembly code functions list (name, index and size) to the output report.

### Fixed

  - Errors on WebAssembly binary files using WASI features.

## 1.0.0 - 2024-07-17

### Added

  - Initial revision.

---  
_Markdown_  
_Copyright 2024-2026 MicroEJ Corp. All rights reserved._  
_Use of this source code is governed by a BSD-style license that can be found with this software._  
_Build: 7E4D1F7C_
