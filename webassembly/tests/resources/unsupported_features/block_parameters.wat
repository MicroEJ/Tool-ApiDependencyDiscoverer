;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; A block that takes parameters (multi-value). No testsuite case covers it. Architecture 9.0.0 refuses it
;; without naming the feature: SOAR stops with "[M0] - Internal limits reached (-1)".
;; Compile it with: wat2wasm tests/resources/unsupported_features/block_parameters.wat -o tests/resources/unsupported_features/block_parameters.wasm

(module
  (func (param i32) (result i32)
    (local.get 0)
    (block (param i32) (result i32)
      (i32.const 1)
      (i32.add))))
