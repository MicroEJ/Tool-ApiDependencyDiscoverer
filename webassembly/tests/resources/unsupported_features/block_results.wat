;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; A block that returns more than one value (multi-value), in a function with one result.
;; Architecture 9.0.0 refuses it with SOAR-L,81 multi-value.
;; Compile it with: wat2wasm tests/resources/unsupported_features/block_results.wat -o tests/resources/unsupported_features/block_results.wasm

(module
  (func (param i32) (result i32)
    (block (result i32 i32)
      (local.get 0)
      (i32.const 1))
    (i32.add)))
