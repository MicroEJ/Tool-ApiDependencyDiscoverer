# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import os
import argparse
import sys
import re
import importlib.metadata
import hashlib
import datetime
from microej.webassemblydependencydiscoverer import (
    microej_utils,
    unsupported_features,
    webassembly_utils,
)
from microej.webassemblydependencydiscoverer.wasm_objdump_parser import WasmObjdumpParser
from microej.webassemblydependencydiscoverer.wasm_stats_parser import WasmStatsParser


def main_parse_args(args_line):
    parser = argparse.ArgumentParser(
        prog="microejwdd", description="Get MicroEJ WebAssembly dependencies information"
    )
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument(
        "-f",
        "--wasm-file",
        nargs="?",
        type=argparse.FileType("r"),
        help="set WebAssembly .wasm file to analyze",
    )
    parser.add_argument(
        "-o",
        "--output",
        nargs="?",
        type=argparse.FileType("w"),
        help="set output reporting file",
    )
    parser.add_argument("-v", "--verbose", action="store_true", help="enable verbose mode")
    group.add_argument(
        "-s",
        "--wasm-stats-file",
        nargs="?",
        type=argparse.FileType("r"),
        help="ADVANCED: set wasm-stats output file to analyze if no WebAssembly "
        "binary file (*.wasm) provided. This file can be generated with the "
        'command line: "wasm-stats my_file.wasm -o my_file.wasm-stats.txt"',
    )
    parser.add_argument(
        "-m",
        "--microej-instr-file",
        nargs="?",
        type=argparse.FileType("r"),
        help="ADVANCED: override default MicroEJ WebAssembly implemented "
        "instructions file. Please contact support@microej.com for more "
        "details",
    )
    parser.add_argument(
        "-e",
        "--wasm-stats-extra-args",
        action="append",
        type=str,
        default=None,
        help="ADVANCED: add extra option to the wasm-stats subprocess command line. "
        'Please use "-e=<EXTRA_OPTION>" instead of "-e <EXTRA_OPTION>"',
    )
    parser.add_argument(
        "--no-feature-check",
        action="store_true",
        help="do not list the WebAssembly features that MicroEJ does not implement",
    )
    return parser.parse_args(args_line)


def main(args_line=None):
    if args_line is None:
        args_line = sys.argv[1:]

    # Init output instruction sections Storage (all non mvp, extended and missing instructions)
    instructions_lists = {}
    all_extended_instructions = "all_extended_instructions"
    available_extended_instructions = "available_extended_instructions"
    missing_extended_instructions = "missing_extended_instructions"
    unsupported_features_key = "unsupported_features"

    # Parse arguments
    args = main_parse_args(args_line)

    # Set the default MicroEJ WebAssembly implemented instructions file location
    default_wasm_implemented_instructions_list_file = (
        os.path.dirname(__file__) + os.path.sep + "microej-wasm-implemented-instr-list.txt"
    )

    # Set the extended MicroEJ WebAssembly implemented instructions file location
    extended_wasm_instructions_list_file = (
        os.path.dirname(__file__) + os.path.sep + "microej-wasm-extended-instr-list.txt"
    )

    # Set WebAssembly core spec v1.0 instructions list file location
    webassembly_core_1_0_instructions_list_file = (
        os.path.dirname(__file__) + os.path.sep + "webassembly-core-1.0-instr-list.txt"
    )

    # 1. Retrieve WebAssembly implemented instructions list from existing file
    if args.microej_instr_file is not None:
        default_wasm_implemented_instructions_list_file = args.microej_instr_file.name
    implemented_instructions_list = microej_utils.load_instructions_from_file(
        default_wasm_implemented_instructions_list_file
    )

    # 2. Extend WebAssembly implemented instructions list from an existing file
    extended_instructions_list = microej_utils.load_extended_instructions_list_from_file(
        extended_wasm_instructions_list_file, implemented_instructions_list
    )

    # 3. Get WebAssembly core 1.0 spec instructions
    webassembly_core_1_0_instructions_list = microej_utils.load_instructions_from_file(
        webassembly_core_1_0_instructions_list_file
    )

    # 2. Generate wasm-stats results from .wasm file and extract WebAssembly
    # instructions list
    wasm_stats_file_path = None
    wasm_file_path = None
    if args.wasm_stats_file is not None:
        wasm_stats_file_path = args.wasm_stats_file.name
    if args.wasm_file is not None:
        wasm_file_path = args.wasm_file.name

    wasm_stats_parser = WasmStatsParser(
        wasm_file_path, wasm_stats_file_path, args.wasm_stats_extra_args
    )

    # 3. Get WebAssembly wasm-stats instructions list
    wasm_stats_instructions_list = wasm_stats_parser.get_wasm_stats_instructions_list()

    # 4. Compute analysis report
    report = (
        "MicroEJ WebAssembly Dependency Discoverer v"
        + importlib.metadata.version("microej_webassembly_dependencies_discoverer")
        + "\n\n"
    )

    report += "Metadata:\n"

    if args.wasm_file is not None:
        wasm_file_name = os.path.basename(args.wasm_file.name)
        # Compute WebAssembly file SHA-256 hash
        wasm_file_hash = ""
        with open(args.wasm_file.name, "rb") as f:
            bytes = f.read()
            wasm_file_hash = hashlib.sha256(bytes).hexdigest()
        report += (
            "\t- WebAssembly file: " + wasm_file_name + " (SHA-256: " + wasm_file_hash + ")" + "\n"
        )

    # Compute current time based on local timezone
    now = datetime.datetime.now()
    local_now = now.astimezone()
    local_tz = local_now.tzinfo
    local_tzname = local_tz.tzname(local_now)
    report += "\t- Created: " + str(datetime.datetime.now()) + " (" + local_tzname + ")" + "\n\n"
    report += (
        "WebAssembly total instructions:" + wasm_stats_parser.wasm_stats_opcode_counts + "\n\n"
    )

    # List Extended WebAssembly instructions ( not in WebAssembly core v1.0 specification)
    not_in_webassembly_core_1_0_instructions_list = [
        instruction
        for instruction in wasm_stats_instructions_list
        if instruction not in webassembly_core_1_0_instructions_list
    ]
    instructions_lists[all_extended_instructions] = not_in_webassembly_core_1_0_instructions_list
    report += (
        "Extended WebAssembly instruction(s) (i.e. not in core spec v1.0 MVP) (nb: "
        + str(len(not_in_webassembly_core_1_0_instructions_list))
        + "):\n"
    )
    for instruction in not_in_webassembly_core_1_0_instructions_list:
        report += (
            instruction + ":" + wasm_stats_parser.get_wasm_stats_opcode_count(instruction) + "\n"
        )

    # List Available extended WebAssembly instruction(s)
    available_none_mvp_list = [
        instruction
        for instruction in not_in_webassembly_core_1_0_instructions_list
        if instruction in extended_instructions_list
    ]
    instructions_lists[available_extended_instructions] = available_none_mvp_list
    report += (
        "\nAvailable extended WebAssembly instruction(s) (nb: "
        + str(len(available_none_mvp_list))
        + "):\n"
    )
    for instruction in available_none_mvp_list:
        report += (
            instruction + ":" + wasm_stats_parser.get_wasm_stats_opcode_count(instruction) + "\n"
        )

    # List Missing extended WebAssembly instruction(s)
    missing_instructions_list = [
        instruction
        for instruction in wasm_stats_instructions_list
        if instruction not in extended_instructions_list
    ]
    instructions_lists[missing_extended_instructions] = missing_instructions_list
    report += (
        "\nMissing extended WebAssembly instruction(s)  (nb: "
        + str(len(missing_instructions_list))
        + "):\n"
    )
    for instruction in missing_instructions_list:
        report += (
            instruction + ":" + wasm_stats_parser.get_wasm_stats_opcode_count(instruction) + "\n"
        )

    if args.wasm_file is not None:
        parser = WasmObjdumpParser(args.wasm_file.name)

        # List the WebAssembly features that the MicroEJ runtime does not implement.
        # A module can use no missing instruction and still be refused for one of them.
        # The user can skip this check. The report then says so, and the returned lists
        # have no entry for it.
        if args.no_feature_check:
            report += "\nUnsupported WebAssembly feature(s): not checked (--no-feature-check)\n"
        else:
            features_list = unsupported_features.list_unsupported_features(parser)
            instructions_lists[unsupported_features_key] = features_list
            report += (
                "\nUnsupported WebAssembly feature(s) (nb: " + str(len(features_list)) + "):\n"
            )
            for feature in features_list:
                report += feature + "\n"

        # List WebAssembly imported functions(s)
        webassembly_imported_functions_list = webassembly_utils.list_imported_functions(
            args.wasm_file.name
        )
        report += (
            "\nWebAssembly imported function(s) list (nb: "
            + str(len(webassembly_imported_functions_list))
            + "):\n"
        )
        report += "".join(webassembly_imported_functions_list)

        # List WebAssembly code functions(s)
        wasm_objdump_code_section = parser.get_section("Code")
        # Keep the function entries only, without their "- " prefix
        wasm_objdump_code_section = [
            i[2:] for i in wasm_objdump_code_section if i.startswith("- func[")
        ]
        # Compute code section total size
        wasm_objdump_code_section_total_size = 0
        for code_section_item in wasm_objdump_code_section:
            if code_section_item.startswith("func["):
                code_section_item_split = re.split("size=| ", code_section_item)
                size = code_section_item_split[2]
                wasm_objdump_code_section_total_size += int(size)
        report += (
            "\nWebAssembly code section function(s) list (nb: "
            + str(len(wasm_objdump_code_section))
            + ", total size: "
            + str(wasm_objdump_code_section_total_size)
            + " bytes):\n"
        )
        report += "\n".join(wasm_objdump_code_section)

        # List WebAssembly data section(s)
        wasm_objdump_data_section = parser.get_section("Data")
        # Remove "- " prefix of each lines
        wasm_objdump_data_section = [i[2:] for i in wasm_objdump_data_section]
        # Compute code section total size
        wasm_objdump_data_section_total_size = 0
        wasm_objdump_data_section_list = []
        for data_section_item in wasm_objdump_data_section:
            if data_section_item.startswith("segment["):
                wasm_objdump_data_section_list.append(data_section_item)
                data_section_item_split = re.split(" ", data_section_item)
                for item in data_section_item_split:
                    item = item.strip()
                    if item.startswith("size"):
                        size = re.split("=", item)[1]
                        wasm_objdump_data_section_total_size += int(size)
        report += (
            "\n\nWebAssembly data section(s) list (nb: "
            + str(len(wasm_objdump_data_section_list))
            + ", total size: "
            + str(wasm_objdump_data_section_total_size)
            + " bytes):\n"
        )
        report += "\n".join(wasm_objdump_data_section_list)

    # 6. Print analysis report
    if args.output is not None:
        # In the specified output file
        f = open(args.output.name, "w")
        f.write(report)
        f.close()
    else:
        # On the standard output
        print(report)
    return instructions_lists
