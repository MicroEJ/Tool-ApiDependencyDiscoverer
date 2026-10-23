# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import re
import subprocess
from typing import List


class WasmObjdumpParser:
    """Class representing a wasm-objdump output parser."""

    def __init__(self, file_path: str):
        self.file_path = file_path
        self.sections = {}
        wasm_objdump_report = self.__generate()
        self.parse(wasm_objdump_report)

    def __generate(self, option: str = "-x") -> List[str]:
        """
        Generate wasm-objdump report of a WebAssembly .wasm binary file path given as parameter

        Parameters
        ----------
            option : str
                wasm-objdump option that selects the report: "-x" for the section details,
                "-d" for the disassembly of the code

        Returns
        -------
        List
            a list of strings representing the wasm-objdump output

        Raises
        ------
        Exception
            If wasm-objdump process call error occurred
        """
        try:
            output = subprocess.run(
                ["wasm-objdump", option, self.file_path],
                check=True,
                capture_output=True,
                text=True,
            )
            results = [line.strip() for line in output.stdout.split("\n")]
            return results
        except subprocess.CalledProcessError as e:
            if e.stderr is not None:
                raise Exception("wasm-objdump: failed with the following message:\n" + e.stderr)
            else:
                raise Exception()

    def get_disassembly(self) -> List[str]:
        """
        Get the wasm-objdump disassembly of the code, one string per line.

        The disassembly is not part of the section details, so this method runs wasm-objdump again.
        Only the callers that need instruction details pay for this second run.
        """
        return self.__generate("-d")

    def parse(self, lines=None):
        """
        Parse wasm-objdump output.

        Parameters
        ----------
            lines : List[str]
                string List representing a wasm-objdump output. Each List entry represents a string
                line (ending by `\n`) of the wasm-objdump output.

        """
        if lines is None:
            with open(self.file_path, "r") as file:
                lines = file.readlines()

        current_section = None
        for line in lines:
            line = line.strip()

            # Match for section headers (e.g., "Type[1]", "Import[2]", "Function[3]", etc.).
            # The count is optional because "Custom:", "Start:" and "DataCount:" have none:
            # without a match, their lines would go into the section before them.
            section_match = re.match(r"^([A-Za-z]+)(?:\[\d+\])?:$", line)
            if section_match:
                current_section = section_match.group(1)
                self.sections[current_section] = []
                continue

            # Add lines to the current section
            if current_section:
                self.sections[current_section].append(line)

    def get_section(self, section_name):
        """
        Get a wasm-objdump output section.

        Parameters
        ----------
            section_name : str
                Section name of the wasm-objdump output
        """
        return self.sections.get(section_name, [])

    def print_section(self, section_name):
        """
        Print a wasm-objdump output section on the standard output.

        Parameters
        ----------
            section_name : str
                Section name of the wasm-objdump output
        """
        section = self.get_section(section_name)
        if section:
            print(f"{section_name} section content:")
            for line in section:
                print(line)
        else:
            print(f"No content found for section: {section_name}")
