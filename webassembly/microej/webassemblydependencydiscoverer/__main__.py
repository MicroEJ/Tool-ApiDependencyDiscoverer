# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

"""Entry point of MicroEJ WebAssembly Dependency Discoverer tool."""

import sys

from microej.webassemblydependencydiscoverer.cli import main


def run() -> int:
    """
    Run the tool from the command line, and return its exit status.

    Both `python -m microej.webassemblydependencydiscoverer` and the `microejwdd` command call
    this function. `main()` returns the instruction lists for the tests, so it cannot be the exit
    status: `sys.exit()` would print the lists and exit with 1.

    Returns
    -------
    int
        0 when the report is written, 1 when the analysis fails
    """
    try:
        main()
    except Exception as e:
        if str(e) != "":
            print(str(e))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(run())
