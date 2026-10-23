# Python
#
# Copyright 2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import re
from typing import List

from microej.webassemblydependencydiscoverer.wasm_objdump_parser import WasmObjdumpParser

# Unsupported WebAssembly Features
#
# The instruction lists cannot show every reason why the MicroEJ build (SOAR) refuses a module:
# some refusals are about the structure of the module, not about an instruction. A module with no
# missing instruction can still be refused, so the report lists these features too.
#
# The list comes from the SOAR error tests of the MicroEJ WebAssembly testsuite, the cases where
# SOAR refuses a feature it does not implement (errors SOAR-L,81 and SOAR-L,401).
# Block parameters are not in that testsuite: a run of SOAR on a module that uses them gave
# the answer.
# Each check reads the `wasm-objdump -x` output, which shows these features in its sections.
# Only the block parameter check also reads the `wasm-objdump -d` disassembly.
#
# TODO: detect shared globals (shared-everything-threads). SOAR refuses them, but wasm-objdump
# does not decode them, and compilers do not emit them today.

# A function type with more than one result, such as "type[0] (i32) -> (i32, i32)".
_MULTI_VALUE_TYPE = re.compile(r"->\s*\(.*,.*\)")

# A function type with at least one parameter, such as "type[0] (i32) -> i32".
_TYPE_WITH_PARAMETERS = re.compile(r"^type\[(\d+)\] \([^)]")

# A block, a loop, an if or a try_table whose type is a function type, as `wasm-objdump -d`
# prints it: "| loop type[0]". A block needs a type index only when it takes parameters or
# returns more than one value. wasm-objdump prints any other block type inline ("| block i32").
_BLOCK_TYPE_INDEX = re.compile(r"\|\s*(?:block|loop|if|try_table)\s+type\[(\d+)\]")


def list_unsupported_features(parser: WasmObjdumpParser) -> List[str]:
    """
    Return the WebAssembly features of a module that the MicroEJ runtime does not implement.

    Parameters
    ----------
        parser : WasmObjdumpParser
            wasm-objdump output of the WebAssembly .wasm binary file

    Returns
    -------
    List
        one string per feature found, with the wasm-objdump lines that show it
    """
    # Remove the "- " prefix of each wasm-objdump entry.
    types = [line[2:] for line in parser.get_section("Type") if line.startswith("- ")]
    imports = [line[2:] for line in parser.get_section("Import") if line.startswith("- ")]
    memories = [line[2:] for line in parser.get_section("Memory") if line.startswith("- ")]
    tables = [line[2:] for line in parser.get_section("Table") if line.startswith("- ")]

    imported_memories = [entry for entry in imports if entry.startswith("memory[")]
    imported_tables = [entry for entry in imports if entry.startswith("table[")]
    imported_globals = [entry for entry in imports if entry.startswith("global[")]

    features = []

    # A function, or a block, that returns more than one value.
    multi_value_types = [entry for entry in types if _MULTI_VALUE_TYPE.search(entry)]
    for entry in multi_value_types:
        features.append("multi-value: " + entry)

    # A block that takes parameters, which multi-value also allows. Parameters on a function are
    # fine, so the check keeps only the types that a block uses. Architecture 9.0.0 does not name
    # this feature: SOAR stops with "[M0] - Internal limits reached (-1)".
    # A type that also has several results is already reported above.
    block_type_indices = set()
    for line in parser.get_disassembly():
        block_type_match = _BLOCK_TYPE_INDEX.search(line)
        if block_type_match:
            block_type_indices.add(block_type_match.group(1))
    for entry in types:
        parameters_match = _TYPE_WITH_PARAMETERS.match(entry)
        if (
            parameters_match
            and parameters_match.group(1) in block_type_indices
            and entry not in multi_value_types
        ):
            features.append("multi-value: block parameters " + entry)

    # The module may hold one linear memory only, imported or defined.
    all_memories = imported_memories + memories
    if len(all_memories) > 1:
        features.append("multiple memories: " + "; ".join(all_memories))

    # The linear memory must use 32-bit addresses.
    for entry in all_memories:
        if "i64" in entry.split("<-")[0].split():
            features.append("64-bit memory: " + entry)

    # The module may hold one table only.
    all_tables = imported_tables + tables
    if len(all_tables) > 1:
        features.append("multiple tables: " + "; ".join(all_tables))

    # A table or a global cannot be imported, whatever its type or mutability.
    for entry in imported_tables:
        features.append("imported table: " + entry)
    for entry in imported_globals:
        features.append("imported global: " + entry)

    return features
