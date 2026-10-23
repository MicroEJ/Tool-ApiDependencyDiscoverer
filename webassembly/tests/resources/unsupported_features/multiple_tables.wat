;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses it with SOAR-L,81 multiple tables (testsuite case imports TestErrorMultipleTables3).
;; Compile it with: wat2wasm  tests/resources/unsupported_features/multiple_tables.wat -o tests/resources/unsupported_features/multiple_tables.wasm

(module (table 1 funcref) (table 1 funcref))
