;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses it with SOAR-L,81 multiple memories (testsuite cases memory TestErrorMultipleMemories1 and 2).
;; Compile it with: wat2wasm --enable-multi-memory tests/resources/unsupported_features/multiple_memories.wat -o tests/resources/unsupported_features/multiple_memories.wasm

(module (memory 1) (memory 1))
