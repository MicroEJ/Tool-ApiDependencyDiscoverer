# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

from microej.webassemblydependencydiscoverer import microej_utils
import os

# Define here the number of WebAssembly instructions implemented by the MEJ32 runtime
# Note: update this number each time the file 
# "webassembly/microej/webassemblydependencydiscoverer/microej-wasm-implemented-instr-list.txt"
# is updated
microej_nb_implemented_webassembly_instructions = 172

def test_load_instructions_from_file_1():
    microej_instructions_list_file = os.path.join(os.path.dirname(__file__), 
                   '..', 'microej', 'webassemblydependencydiscoverer', 'microej-wasm-implemented-instr-list.txt')
    try:
        wasm_instructions_list = microej_utils.load_instructions_from_file(microej_instructions_list_file)
        # File "webassembly/microej/webassemblydependencydiscoverer/microej-wasm-implemented-instr-list.txt" has been 
        # updated if the following assert failed. Please update this test accordingly
        assert(len(wasm_instructions_list) == microej_nb_implemented_webassembly_instructions)
    except Exception as e:
        assert False

def test_load_instructions_from_file_2():
    # Check that a wrong file path given as parameter raise an IOError exception
    microej_instructions_list_file = "wrong-file.txt"
    try:
        wasm_instructions_list = microej_utils.load_instructions_from_file(microej_instructions_list_file)
        assert False
    except IOError as e:
        assert True