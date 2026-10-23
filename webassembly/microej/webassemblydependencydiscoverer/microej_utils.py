# Python
#
# Copyright 2024-2025 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.

from typing import List


def load_instructions_from_file(file_absolute_path: str) -> List[str]:
    """
    Return file content as string list

    Parameters
    ----------
        file_absolute_path : str
            Absolute file path
    Returns
    -------
    List
        a list of strings representing the given file content

    Raises
    ------
    IOError
        If file the given file does not exists
    """
    result = []
    with open(file_absolute_path, "r") as reader:
        line = reader.readline()
        while line:
            result.append(line.strip())
            # read next line
            line = reader.readline()

    return result


def load_extended_instructions_list_from_file(file_absolute_path: str,
                                              list: List[str]) -> List[str]:
    """
    Return file content as string list

    Parameters
    ----------
        file_absolute_path : str
            Absolute file path
        list: List[str]
            List of instructions
    Returns
    -------
    List
        an extended list with the input file content

    Raises
    ------
    IOError
        If `load_instructions_from_file()` fails.
    """
    extended_instructions_list = load_instructions_from_file(file_absolute_path)
    list.extend(extended_instructions_list)
    return list
