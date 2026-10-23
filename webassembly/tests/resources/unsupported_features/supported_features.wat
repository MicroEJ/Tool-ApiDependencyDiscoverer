;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR accepts it: one imported shared memory, one table, a function with one result, and a function with a parameter
;; and a block with an inline type. The tool must report no unsupported feature.
;; Compile it with: wat2wasm --enable-threads tests/resources/unsupported_features/supported_features.wat -o tests/resources/unsupported_features/supported_features.wasm

(module (import "env" "memory" (memory 1 1 shared)) (table 1 funcref) (func (result i32) (i32.const 0))
  (func (param i32) (result i32) (block (result i32) (local.get 0))))
