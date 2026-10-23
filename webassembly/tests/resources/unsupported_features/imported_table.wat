;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses it with SOAR-L,401 import table (testsuite cases imports TestErrorMultipleTables1 and 2).
;; Compile it with: wat2wasm  tests/resources/unsupported_features/imported_table.wat -o tests/resources/unsupported_features/imported_table.wasm

(module (import "env" "table" (table 1 funcref)))
