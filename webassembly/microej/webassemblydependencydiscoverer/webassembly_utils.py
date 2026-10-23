# Python
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.

from wasmtime import Store, Module, FuncType, Config, Engine
from typing import List


def list_imported_functions(wasm_file_path: str) -> List[str]:
    """
    Return WebAssembly imported functions list

    Parameters
    ----------
        wasm_file_path : str
            WebAssembly .wasm binary file

    Returns
    -------
    List
        a list of strings representing the WebAssembly imported functions in the
        WebAssembly .wasm binary file
    """

    # Create a store.
    config = Config()
    config.wasm_exceptions = True
    store = Store(Engine(config))

    # Create a Module instance. The binary data is used to create a Module instance
    module = Module.from_file(store.engine, wasm_file_path)

    # Compute imported functions signature
    imported_functions = []
    for imported in module.imports:
        imported_function = ""
        if isinstance(imported.type, FuncType):
            # Add function parameter(s) type(s)
            imported_function = "".join(
                [
                    "module: ",
                    imported.module,
                    ", name: ",
                    imported.name,
                    ", FunctionType(params: ",
                ]
            )
            if imported.type.params:
                length = len(imported.type.params)
                for i in range(length - 1):
                    imported_function = "".join(
                        [imported_function, "[", imported.type.params[i].__str__().upper(), "], "]
                    )
                imported_function = "".join(
                    [
                        imported_function,
                        "[",
                        imported.type.params[length - 1].__str__().upper(),
                        "]",
                    ]
                )
            else:
                imported_function = "".join([imported_function, "[]"])

            # Add function result type
            imported_function = "".join([imported_function, ", results: "])
            if imported.type.results:
                length = len(imported.type.results)
                for i in range(length - 1):
                    imported_function = "".join(
                        [imported_function, "[", imported.type.results[i].__str__().upper(), "], "]
                    )
                imported_function = "".join(
                    [
                        imported_function,
                        "[",
                        imported.type.results[length - 1].__str__().upper(),
                        "])\n",
                    ]
                )
            else:
                imported_function = "".join([imported_function, "[])\n"])
            imported_functions.append(imported_function)
    return imported_functions
