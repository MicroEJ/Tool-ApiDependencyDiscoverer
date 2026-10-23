;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; The "name" custom section follows the Code section, and it lists the function again without its size.
;; Compile it with: wat2wasm --debug-names tests/resources/custom_sections/function_debug_names.wat -o tests/resources/custom_sections/function_debug_names.wasm

(module (func $foo (result i32) (i32.const 1)))
