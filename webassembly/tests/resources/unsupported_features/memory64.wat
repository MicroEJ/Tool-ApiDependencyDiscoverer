;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses it with SOAR-L,81 memory64 (testsuite cases binary_leb128 TestErrorIntegerRepresentationTooLong1, 2, 16 and 19).
;; Compile it with: wat2wasm --enable-memory64 tests/resources/unsupported_features/memory64.wat -o tests/resources/unsupported_features/memory64.wasm

(module (memory i64 1))
