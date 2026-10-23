;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; The "name" custom section follows the Memory section, so its lines must not count as a second memory.
;; Compile it with: wat2wasm --enable-memory64 --debug-names tests/resources/custom_sections/memory64_debug_names.wat -o tests/resources/custom_sections/memory64_debug_names.wasm

(module (memory i64 1))
