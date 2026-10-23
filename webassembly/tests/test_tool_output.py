# Python
#
# Copyright 2025-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import os
import requests
import pytest
import microej.webassemblydependencydiscoverer.cli as cli
from microej.webassemblydependencydiscoverer import microej_utils
from microej.webassemblydependencydiscoverer import webassembly_utils


# Define the Wasm files to run tests on.
@pytest.fixture(params=[
    ("main_posix.wasm"),
    ("factorial.wasm"),
    ("application.wasm"),
    ("extended_and_missing_instructions.wasm"),
])
def wasm_resource_file_name(request):
    return request.param

def assert_instructions_absent(instruction_list, reference_list):
    """Assert that none of the given instructions are present in the reference instruction list.

    Args:
        instruction_list (list): the instructions to use.
        reference_list (list): the reference instructions to compare with.
    """
    for instruction in instruction_list:
        if instruction in reference_list:
            assert False

def assert_instructions_present(instruction_list, reference_list):
    """Assert that all of the given instructions are present in the reference instruction list.

    Args:
        instruction_list (list): the instructions to use.
        reference_list (list): the reference instructions to compare with.
    """    
    for instruction in instruction_list:
        if instruction not in reference_list:
            assert False

def main_output_test(wasm_resource_file_name, section_name, reference_instruction_list_file_name):
    """Run the tool on the given input Wasm file and compare its output list of instructions with the reference list.

    Args:
        instruction_list (list): the instructions to use.
        reference_list (list): the reference instructions to compare with.
    """
    script_dir = os.path.dirname(__file__)
    wasm_resource_file = script_dir + os.path.sep + os.path.join("resources", wasm_resource_file_name)
    reference_instruction_list_file = script_dir + os.path.sep + os.path.join("..","microej", "webassemblydependencydiscoverer",  reference_instruction_list_file_name)

    # Load the reference instructions list
    reference_instruction_list = microej_utils.load_instructions_from_file(
        reference_instruction_list_file
    )

    # Execute the tool and get the output list of instructions corresponding to the section `section_name`
    instruction_lists = cli.main(['-f', wasm_resource_file])
    output_instruction_list = instruction_lists[section_name]

    # Run the test
    if section_name == "available_extended_instructions":
        assert_instructions_present(output_instruction_list, reference_instruction_list)
    else:
        assert_instructions_absent(output_instruction_list, reference_instruction_list)

def test_all_extended_wasm_instructions(wasm_resource_file_name):
    # Get the extended Wasm instruction list present in the Wasm source file 
    # then compare it with `webassembly-core-1.0-instr-list.txt` reference list.
    # The output list must be part of the reference list.
    section_name = "all_extended_instructions"
    reference_instruction_list_file_name = "webassembly-core-1.0-instr-list.txt"
    main_output_test(wasm_resource_file_name, section_name, reference_instruction_list_file_name)

def test_available_extended_wasm_instructions(wasm_resource_file_name):
    # Get MicroEJ extended Wasm instruction list present in the Wasm source file 
    # then compare it with ``microej-wasm-extended-instr-list.txt` reference list.
    # The output list must be part of the reference list.
    section_name = "available_extended_instructions"
    reference_instruction_list_file_name = "microej-wasm-extended-instr-list.txt"
    main_output_test(wasm_resource_file_name, section_name, reference_instruction_list_file_name)

def test_missing_extended_wasm_instructions(wasm_resource_file_name):
    # Get MicroEJ extended Wasm instruction list present in the Wasm source file 
    # then compare it with ``microej-wasm-extended-instr-list.txt` reference list.
    # There should be no overlap between the two lists.
    section_name = "missing_extended_instructions"
    reference_instruction_list_file_name = "microej-wasm-extended-instr-list.txt"
    main_output_test(wasm_resource_file_name, section_name, reference_instruction_list_file_name)

def test_exception_handling_instructions_missing():
    # The MicroEJ runtime does not implement the exception handling proposal.
    # The tool must still load such a module, and report its instructions as missing.
    # wasm-stats does not count `try_table`, so only `throw` is checked.
    # TODO: check `try_table` too when wasm-stats counts it.
    script_dir = os.path.dirname(__file__)
    wasm_resource_file = os.path.join(script_dir, "resources", "exception_handling.wasm")
    instruction_lists = cli.main(['-f', wasm_resource_file])
    assert "throw" in instruction_lists["missing_extended_instructions"]
    assert "throw" not in instruction_lists["available_extended_instructions"]

def test_wasm_imported_function_list_export():
    # Get .wasm file URL from README.md
    url = "https://raw.githubusercontent.com/wasmerio/wasmer/v4.3.5/lib/wasix/tests/stdin-hello.wasm"
    file_path = "stdin-hello.wasm"
    response = requests.get(url)
    if response.status_code == 200:
        with open(file_path, "wb") as file:
            file.write(response.content)
            print("File downloaded successfully!")
    else:
        print("Failed to download the file at " + url)
        assert False
    wasm_imported_functions_list = webassembly_utils.list_imported_functions(file_path)
    # Cleanup downloaded file
    os.remove(file_path)
    assert len(wasm_imported_functions_list) == 5
    assert any("module: wasi_snapshot_preview1, name: proc_exit, FunctionType(params: [I32], results: [])\n" in s for s in wasm_imported_functions_list)
    assert any("module: wasi_snapshot_preview1, name: fd_seek, FunctionType(params: [I32], [I64], [I32], [I32], results: [I32])\n" in s for s in wasm_imported_functions_list)
    assert any("module: wasi_snapshot_preview1, name: fd_write, FunctionType(params: [I32], [I32], [I32], [I32], results: [I32])\n" in s for s in wasm_imported_functions_list)
    assert any("module: wasi_snapshot_preview1, name: fd_read, FunctionType(params: [I32], [I32], [I32], [I32], results: [I32])\n" in s for s in wasm_imported_functions_list)
    assert any("module: wasi_snapshot_preview1, name: fd_close, FunctionType(params: [I32], results: [I32])\n" in s for s in wasm_imported_functions_list)