;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses it with SOAR-L,81 multiple memories (testsuite cases imports TestErrorMultipleMemories1 to 3).
;; Compile it with: wat2wasm --enable-multi-memory tests/resources/unsupported_features/multiple_memories_imported.wat -o tests/resources/unsupported_features/multiple_memories_imported.wasm

(module (import "env" "memory" (memory 1)) (memory 1))
