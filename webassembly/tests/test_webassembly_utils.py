# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import sys
import os
import requests
import pytest
import microej.webassemblydependencydiscoverer.cli
from microej.webassemblydependencydiscoverer import webassembly_utils
from microej.webassemblydependencydiscoverer.wasm_stats_parser import WasmStatsParser

def test_wrong_wasm_stats_1():
    # Test a wrong wasm-stats output file passed as arguments
    # Exception has to be raised during data extraction
    wasm_stats_file = "resources/iso15118_wasm_instructions"
    script_dir = os.path.dirname(__file__)
    with pytest.raises(Exception, match="wasm-stats output file parsing error"):
        WasmStatsParser(None, os.path.join(script_dir, wasm_stats_file))

def test_generate_wasm_stats():
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

    # Check that WebAssembly instructions list extractions from 
    # wasm-stats report is working  
    wasm_stats_parser = WasmStatsParser(file_path, None)
    wasm_stats_instructions_list = wasm_stats_parser.get_wasm_stats_instructions_list()
    assert len(wasm_stats_instructions_list) != 0 
    
    # Cleanup downloaded file
    os.remove(file_path)

def test_cli_ok1():
    script_dir = os.path.dirname(__file__)
    print(script_dir)
    try:
        microej.webassemblydependencydiscoverer.cli.main(['-f', script_dir + "/resources/factorial.wasm"])
    except Exception:
       assert False 

def test_cli_ok2():
    # Check that passing extra option to wasm-stats subprocess is working
    script_dir = os.path.dirname(__file__)
    print(script_dir)
    try:
        microej.webassemblydependencydiscoverer.cli.main(['-f', script_dir + "/resources/factorial.wasm", '-e=-v'])
    except Exception:
       assert False

def test_cli_ok3():
    # Check that parsing wasm file using wasi-threads is working when adding extra option to wasm-stats subprocess
    script_dir = os.path.dirname(__file__)
    print(script_dir)
    try:
        microej.webassemblydependencydiscoverer.cli.main(['-f', script_dir + "/resources/main_posix.wasm", '-e=--enable-threads'])
    except Exception:
       assert False

def test_cli_ok4():
    # Check that parsing wasm file using wasi-threads is working when no extra option is passed to wasm-stats subprocess
    script_dir = os.path.dirname(__file__)
    print(script_dir)
    try:
        microej.webassemblydependencydiscoverer.cli.main(['-f', script_dir + "/resources/application.wasm"])
    except Exception:
       assert False

def test_cli_nok1():
    # Check that "-e" option can only by used using '-e=' and not '-e "[WASM-STATS-OPTION]"'
    script_dir = os.path.dirname(__file__)
    print(script_dir)
    try:
        microej.webassemblydependencydiscoverer.cli.main(['-f', script_dir + "/resources/factorial.wasm", '-e', '-v'])
        assert False
    except SystemExit:
        assert True # System exit call is true in that case