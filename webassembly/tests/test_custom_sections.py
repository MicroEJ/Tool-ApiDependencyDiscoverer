# Python
#
# Copyright 2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import os
import microej.webassemblydependencydiscoverer.cli as cli


# wasm-objdump prints a custom section header without a count ("Custom:").
# These modules are built with --debug-names, so a "name" custom section follows
# the section under test. Its lines must not be read as part of that section.


def test_custom_section_after_memory_not_read_as_memory():
    features = run_tool("memory64_debug_names.wasm")["unsupported_features"]
    assert len(features) == 1
    assert features[0].startswith("64-bit memory:")


def test_custom_section_after_code_not_read_as_function(tmp_path):
    report_file = tmp_path / "report.txt"
    run_tool("function_debug_names.wasm", "-o", str(report_file))
    assert "code section function(s) list (nb: 1, total size: 4 bytes)" in report_file.read_text()


def run_tool(wasm_resource_file_name, *extra_args):
    """Run the tool on a module of `resources/custom_sections` and return its instruction lists."""
    script_dir = os.path.dirname(__file__)
    wasm_resource_file = os.path.join(script_dir, "resources", "custom_sections", wasm_resource_file_name)
    return cli.main(["-f", wasm_resource_file, *extra_args])
