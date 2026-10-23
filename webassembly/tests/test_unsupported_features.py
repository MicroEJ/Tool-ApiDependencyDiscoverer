# Python
#
# Copyright 2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import os
import pytest
import microej.webassemblydependencydiscoverer.cli as cli


# Each module uses one feature that the MicroEJ build (SOAR) refuses.
# The expected features follow the SOAR error tests of the MicroEJ WebAssembly testsuite:
# each .wat file names the testsuite cases it stands for.
@pytest.mark.parametrize(
    "wasm_resource_file_name, expected_feature",
    [
        ("multi_value.wasm", "multi-value:"),
        ("block_parameters.wasm", "multi-value: block parameters"),
        ("block_results.wasm", "multi-value:"),
        ("multiple_memories.wasm", "multiple memories:"),
        ("multiple_memories_imported.wasm", "multiple memories:"),
        ("memory64.wasm", "64-bit memory:"),
        ("multiple_tables.wasm", "multiple tables:"),
        ("imported_table.wasm", "imported table:"),
        ("imported_global.wasm", "imported global:"),
    ],
)
def test_unsupported_feature_reported(wasm_resource_file_name, expected_feature):
    features = run_tool(wasm_resource_file_name)
    assert len(features) == 1
    assert features[0].startswith(expected_feature)


def test_supported_features_not_reported():
    # An imported shared memory, one table, single results, function parameters and
    # inline block types are supported.
    assert run_tool("supported_features.wasm") == []


def run_tool(wasm_resource_file_name):
    """Run the tool on a module of `resources/unsupported_features` and return its unsupported features."""
    script_dir = os.path.dirname(__file__)
    wasm_resource_file = os.path.join(
        script_dir, "resources", "unsupported_features", wasm_resource_file_name
    )
    return cli.main(["-f", wasm_resource_file])["unsupported_features"]


def test_feature_check_skipped(tmp_path):
    # With --no-feature-check, the tool does not look for unsupported features,
    # and the report says that the check did not run.
    script_dir = os.path.dirname(__file__)
    wasm_resource_file = os.path.join(script_dir, "resources", "unsupported_features", "multi_value.wasm")
    report_file = tmp_path / "report.txt"
    instruction_lists = cli.main(["-f", wasm_resource_file, "-o", str(report_file), "--no-feature-check"])
    assert "unsupported_features" not in instruction_lists
    assert "Unsupported WebAssembly feature(s): not checked" in report_file.read_text()
