# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

import subprocess
from typing import List


class WasmStatsParser:
    """Class representing a wasm-stats output parser."""

    def __init__(self, wasm_file_path: str, wasm_stats_file_path: str, extra_args=None):
        self.wasm_file_path = wasm_file_path
        self.wasm_stats_file_path = wasm_stats_file_path
        self.extra_args = extra_args
        self.wasm_stats_opcode_dict = {}
        self.wasm_stats_opcode_counts = ""
        if self.wasm_file_path is not None:
            # Generate wasm-stats report and parse it
            wasm_stats_report = self.__generate()
            self.__parse(wasm_stats_report)
        else:
            # wasm-stats report file path given as parameter.
            # Parse the wasm-stats report file.
            self.__parse()

    def __generate(self) -> List[str]:
        """
        Generate wasm-stats report of a WebAssembly .wasm binary file path given as parameter

        Parameters
        ----------
            file_path : str
                WebAssembly .wasm binary file

        Returns
        -------
        List
            a list of strings representing the wasm-stats output

        Raises
        ------
        Exception
            If wasm-stats process call error occurred
        """
        try:
            output = None
            if self.extra_args is None:
                output = subprocess.run(["wasm-stats", self.wasm_file_path,
                                        "--enable-all"],
                                        check=True,
                                        capture_output=True,
                                        text=True
                                        )
            else:
                output = subprocess.run(
                    ["wasm-stats", self.wasm_file_path] + self.extra_args,
                    check=True,
                    capture_output=True,
                    text=True,
                )
            results = [line.strip() for line in output.stdout.split("\n")]
            return results
        except subprocess.CalledProcessError as e:
            if e.stderr is not None:
                raise Exception('wasm-stats: failed with the following message:\n' + e.stderr)
            else:
                raise Exception()

    def __parse(self, lines=None):
        """
        Extract metadata from wasm-stats file report given as parameter

        Parameters
        ----------
            wasm_file : str
                WebAssembly .wasm binary file

        Returns
        -------
        Dic

        Raises
        ------
        Exception
            If wasm-stats process call error occurred
        """
        try:
            if lines is None:
                with open(self.wasm_stats_file_path, "r") as file:
                    lines = file.readlines()

            analysis_started = False
            for line in lines:
                if line.startswith("Total opcodes:"):
                    self.wasm_stats_opcode_counts = line.split(":")[1].strip()
                if line.startswith("Opcode counts:"):
                    analysis_started = True
                    # read next line
                    continue

                if analysis_started:
                    # check for end of file analysis
                    if line.strip() == "":
                        break

                    opcode = line.split(":")[0]
                    self.wasm_stats_opcode_dict[opcode] = line.split(":")[1].strip()

        except IOError as e:
            print(e)

        # A module without code has no opcode to count, so only a missing total is an error.
        if self.wasm_stats_opcode_counts == "":
            raise Exception("wasm-stats output file parsing error.")

    def get_wasm_stats_instructions_list(self) -> List[str]:
        """
        Return string list of the WebAssembly instructions listed in a wasm-stats file
        result given as parameter

        Returns
        -------
        List
            a list of strings representing the WebAssembly instructions

        Raises
        ------
        Exception
            If wasm-stats file result could not be parsed
        """
        if self.wasm_stats_opcode_counts == "":
            # Extract wasm-stats report metadata if not already done
            self.__parse()

        return self.wasm_stats_opcode_dict.keys()

    def get_wasm_stats_opcode_count(self, opcode: str) -> str:
        """
        Return string representing the number of occurrence of a given opcode listed in a
        wasm-stats file report

        Parameters
        ----------
            opcode: str
                WebAssembly opcode name

        Returns
        -------
        List
            Opcode count if exists in wasm-stat file report, empty string otherwise.

        Raises
        ------
        Exception
            If wasm-stats file result could not be parsed
        """
        if self.wasm_stats_opcode_counts == "":
            # Extract wasm-stats report metadata if not already done
            self.__parse()

        return self.wasm_stats_opcode_dict[opcode]
