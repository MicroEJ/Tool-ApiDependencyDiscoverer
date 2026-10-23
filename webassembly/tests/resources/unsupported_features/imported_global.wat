;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses an imported global whatever its mutability (testsuite case globals TestErrorInvalidMutability1).
;; Compile it with: wat2wasm  tests/resources/unsupported_features/imported_global.wat -o tests/resources/unsupported_features/imported_global.wasm

(module (import "env" "global" (global i32)))
