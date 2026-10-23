;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; SOAR refuses it with SOAR-L,81 multi-value (testsuite cases type and func TestErrorInvalidResultArity1 and 2).
;; Compile it with: wat2wasm  tests/resources/unsupported_features/multi_value.wat -o tests/resources/unsupported_features/multi_value.wasm

(module (func (param i32) (result i32 i32) (local.get 0) (local.get 0)))
