#!/bin/bash
# 
# Bash
#
# Copyright 2024-2026 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.
# Build: 7E4D1F7C

# Force exit on error
set -e

# Save application current directory and jump one level above scripts
CURRENT_DIRECTORY=$(pwd)

cd "$(dirname "$0")"

# Check and install required tools
if [ ! -d '/tmp/wasm_env/wabt-1.0.39' ]; then
    # Install wabt in the current environment
    wget --directory-prefix=/tmp/wasm_env https://github.com/WebAssembly/wabt/releases/download/1.0.39/wabt-1.0.39-linux-x64.tar.gz
    tar xvzf /tmp/wasm_env/wabt-1.0.39-linux-x64.tar.gz -C /tmp/wasm_env/ 
fi

# Setup the environment
export PATH=/tmp/wasm_env/wabt-1.0.39/bin:$PATH

# Launch build
if [ $# -eq 0 ] 
then
    make
else
    make "$1"
fi

# Restore current directory
cd "${CURRENT_DIRECTORY}"