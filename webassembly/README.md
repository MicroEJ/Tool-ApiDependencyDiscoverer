# Overview

This tool analyzes WebAssembly (`.wasm`) binary files or "wasm-stats" report results and compute the MicroEJ WebAssembly missing instruction(s) list and WebAssembly ".wasm" binary file imported function(s) list.

# Requirements

- Python v3.10 or higher
- `wabt` v1.0.39 or higher (see https://github.com/WebAssembly/wabt/releases) installed in the system PATH. 
  Check it is successfully installed and its version by running the following command:
  
       $ wasm-stats --version
       1.0.39
- `python3-venv` Linux packet installed if used on a Linux distribution (e.g: with `sudo apt install python3-venv`)

# Usage

By default, the tool is launched with steps described below.

1. Create a virtual Python environment (`venv`) with the following command line:

    $ make install

    _Note:_ Python app dependencies will be installed in this virtual Python environment

2. Activate the virtual Python environment with the following command line:

    $ source .venv/bin/activate

3. Launch the Python app with the following command line:

    $ python -m microej.webassemblydependencydiscoverer --wasm-file myModule.wasm -o myReport.txt

## Get Started

In this section, we will analyze the dependencies of a `wasmer` WebAssembly binary file (`.wasm`) test against the MicroEJ WebAssembly implemented instructions list provided in this repository:

- Open a terminal in the directory where this repository has been cloned. 
- Go to `webassembly` directory.
- Download `wasmer` WebAssembly binary test file https://github.com/wasmerio/wasmer/blob/v4.3.5/lib/wasix/tests/stdin-hello.wasm 
- Move it in the current directory.
- Launch the tool with the following command:
       
       $ python -m microej.webassemblydependencydiscoverer --wasm-file stdin-hello.wasm -o doc/results.txt

The tool performs the MicroEJ WebAssembly dependencies analysis.

The file named [results.txt](doc/results.txt) is overwritten and contains the report of dependencies (missing MicroEJ WebAssembly instructions, WebAssembly imported functions list, etc.) required by this WebAssembly binary file.


## Interpreting the Results

Open the file named [results.txt](doc/results.txt) with a text editor.
You can see the following sections:

- `WebAssembly total instructions:` WebAssembly total instructions found in the WebAssembly binary file (`.wasm`).
- `Extended WebAssembly instruction(s) (i.e. not in core spec v1.0 MVP):` lists all extended WebAssembly instructions which are not part of [WebAssembly core specification v1.0](https://www.w3.org/TR/wasm-core-1/) and are present in the WebAssembly input file.
- `Available extended WebAssembly instruction(s)`: lists the extended WebAssembly instructions that are present in the WebAssembly input file, and **implemented by MicroEJ**.
- `Missing extended WebAssembly instruction(s)`: lists the extended WebAssembly instructions that are present in the Wasm input file, and **not implemented by MicroEJ**.
- `Unsupported WebAssembly feature(s)`: lists the WebAssembly features that the module uses and that **MicroEJ does not implement**: multi-value, multiple memories, 64-bit memory, multiple tables, imported tables and imported globals.
  A module that uses one of them is refused at build time, even when no instruction is missing.
  The `--no-feature-check` option skips this check, and the report then shows the section as not checked.
- `WebAssembly imported function(s) list:` lists all imported WebAssembly functions including their name and signature.
- `WebAssembly code section function(s) list:` lists all WebAssembly functions with index, name and size in byte(s).
- `WebAssembly data section(s) list`: lists all WebAssembly data segments with index, name and size in byte(s). 

_Note:_ number printed after a colon separator (`:`) next to a WebAssembly instruction specify the WebAssembly instruction occurrence found in the WebAssembly binary file (`.wasm`).

_Note:_ the report lists what the tool can detect. The MicroEJ build of the application remains the final check.

## Options

Here is the list of all available options (output of `python -m microej.webassemblydependencydiscoverer -h`):

```
usage: microejwdd [-h] [-f [WASM_FILE]] [-o [OUTPUT]] [-v] [-s [WASM_STATS_FILE]] [-m [MICROEJ_INSTR_FILE]]
                  [-e WASM_STATS_EXTRA_ARGS] [--no-feature-check]

Get MicroEJ WebAssembly dependencies information

options:
  -h, --help            show this help message and exit
  -f [WASM_FILE], --wasm-file [WASM_FILE]
                        set WebAssembly .wasm file to analyze
  -o [OUTPUT], --output [OUTPUT]
                        set output reporting file
  -v, --verbose         enable verbose mode
  -s [WASM_STATS_FILE], --wasm-stats-file [WASM_STATS_FILE]
                        ADVANCED: set wasm-stats output file to analyze if no WebAssembly binary file (*.wasm)
                        provided. This file can be generated with the command line: "wasm-stats my_file.wasm -o
                        my_file.wasm-stats.txt"
  -m [MICROEJ_INSTR_FILE], --microej-instr-file [MICROEJ_INSTR_FILE]
                        ADVANCED: override default MicroEJ WebAssembly implemented instructions file. Please
                        contact support@microej.com for more details
  -e WASM_STATS_EXTRA_ARGS, --wasm-stats-extra-args WASM_STATS_EXTRA_ARGS
                        ADVANCED: add extra option to the wasm-stats subprocess command line. Please use
                        "-e=<EXTRA_OPTION>" instead of "-e <EXTRA_OPTION>"
  --no-feature-check    do not list the WebAssembly features that MicroEJ does not implement
```

## Advanced Usage

Here is a command line example to get report from a `wasm-stats` file report:

    $ python -m microej.webassemblydependencydiscoverer -s my_wasm_stats_file.txt -o my_report.txt

_Note:_ only MicroEJ WebAssembly missing instructions will be listed in the report in that case.

Here is a command line example to get report from a WebAssembly `.wasm` binary file and a specific MicroEJ WebAssembly implemented instructions list:

    $ python -m microej.webassemblydependencydiscoverer --wasm-file my_file.wasm -o my_report.txt --microej-instr-file microej_webassembly_implemented_instructions_list.txt

By default, the tool runs `wasm-stats` with `--enable-all`, so it reads a module whatever the WebAssembly features it uses. No option is needed, for example, for a module built with WASI threads.

The `-e` option replaces `--enable-all` with the `wasm-stats` options you give. Use it to turn on only some features: `wasm-stats` then stops on a module that uses a feature you left out. Here is a command line example that accepts threads only:

    $ python -m microej.webassemblydependencydiscoverer --wasm-file my_file.wasm -o my_report.txt -e=--enable-threads

_Note:_ `wasm-stats` options can be listed with `wasm-stats --help` command line.

# Build and Install the Tool as Python Package

1. Install the latest version of `setuptools` using `pip`:
    
    $ pip install --upgrade setuptools

2. Install `build` using `pip`:

    $ pip install --upgrade build

3. Launch package build using `pip`:

    $ python -m build .

_Note:_ for Linux users, requires to have `python3-venv` packet installed on your Linux distribution.

4. Install the tool project in editable mode with:

    $ pip install --editable .

You are now able to launch the installed tool. Here is a command line example:

    $ microejwdd --wasm-file my_file.wasm

# Troubleshooting

## Python build backend is missing

    $ pip install --editable .
    ERROR: Project file:///home/[user]/Tool-ApiDependencyDiscoverer/webassembly has a 'pyproject.toml' and its build backend is missing the 'build_editable' hook. Since it does not have a 'setup.py' nor a 'setup.cfg', it cannot be installed in editable mode. Consider using a build backend that supports PEP 660.

This issue can occurred if you try to build the tool with an old version of `pip`. Upgrade your `pip` installed version to fix it with:

    $ python3 -m pip install --upgrade pip


---  
_Markdown_  
_Copyright 2024-2026 MicroEJ Corp. All rights reserved._  
_Use of this source code is governed by a BSD-style license that can be found with this software._  
_Build: 7E4D1F7C_
