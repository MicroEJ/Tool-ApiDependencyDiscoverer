# Python
#
# Copyright 2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import os
import sys
from microej.webassemblydependencydiscoverer.__main__ import run

# `run()` is the entry point of `python -m` and of the `microejwdd` command.
# A script or a CI job reads its exit status, so a success must give 0 and a failure 1.

resources_dir = os.path.join(os.path.dirname(__file__), "resources")


def test_run_returns_0_on_success(monkeypatch, tmp_path):
    wasm_file = os.path.join(resources_dir, "factorial.wasm")
    monkeypatch.setattr(sys, "argv", ["microejwdd", "-f", wasm_file, "-o", str(tmp_path / "report.txt")])
    assert run() == 0


def test_run_returns_1_on_failure(monkeypatch, tmp_path):
    # A .wat text file is not a .wasm binary, so wasm-stats fails on it.
    wat_file = os.path.join(resources_dir, "exception_handling.wat")
    monkeypatch.setattr(sys, "argv", ["microejwdd", "-f", wat_file, "-o", str(tmp_path / "report.txt")])
    assert run() == 1
