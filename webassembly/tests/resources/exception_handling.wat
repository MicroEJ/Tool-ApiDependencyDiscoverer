;; Wat
;;
;; Copyright 2026 MicroEJ Corp. All rights reserved.
;; Use of this source code is governed by a BSD-style license that can be found with this software.
;; Build: 7E4D1F7C

;; This code is not intended to be executed; it is only compiled to wasm for testing purposes.
;; It uses the exception handling proposal, which the MicroEJ runtime does not implement.
;; The tool must load it and report its exception instructions as missing.
;; Compile it with: wat2wasm --enable-exceptions tests/resources/exception_handling.wat -o tests/resources/exception_handling.wasm

(module
  (tag $error (param i32))
  (func (export "throw_and_catch") (param i32) (result i32)
    (block $handler (result i32)
      (try_table (result i32) (catch $error $handler)
        (throw $error (local.get 0))))))
