# Makefile
#
# Copyright 2024 MicroEJ Corp. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be found with this software.

# Variables
PACKAGE_NAME = microej
PYTHON = python3
PIP = pip
VENV_DIR = .venv

# Default target
.PHONY: all
all: clean install lint test build ## Run all steps (clean, install, lint, test, build).

# Show help
.PHONY: help
help:  ## Show this help.
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# Setting up environment
.PHONY: venv
venv:  ## Create a virtual environment.
	@echo "Creating virtual environment in $(VENV_DIR)"
	@$(PYTHON) -m venv $(VENV_DIR)

.PHONY: install
install: venv ## Install dependencies in the virtual environment.
	@echo "Installing dependencies"
	@$(VENV_DIR)/bin/$(PIP) install -e .[dev]

.PHONY: clean
clean:  ## Clean up build artifacts.
	@echo "Cleaning build artifacts"
	@rm -rf build dist *.egg-info
	@find . -type f -name '*.pyc' -delete
	@find . -type d -name '__pycache__' -exec rm -r {} +
	@rm -rf $(VENV_DIR)
	@rm -rf .coverage
	@rm -rf .pytest_cache

# Build
.PHONY: build
build: ## Build the package.
	@echo "Building the package"
	@$(PYTHON) -m venv $(VENV_DIR)
	@$(VENV_DIR)/bin/$(PYTHON) -m pip install --upgrade build
	@$(VENV_DIR)/bin/$(PYTHON) -m build

# Run tests
.PHONY: test
test: ## Run tests using pytest.
	@echo "Running tests"
	@$(VENV_DIR)/bin/pytest --junitxml=dist/reports/microej_webassembly_dependencies_discoverer-junit-testsuite-report.xml -v --cov-config .coveragerc --cov=microej.webassemblydependencydiscoverer -l --tb=short --maxfail=1 tests/
	@$(VENV_DIR)/bin/coverage xml -o dist/reports/coverage/cobertura.xml
	@$(VENV_DIR)/bin/coverage html -d dist/reports/coverage/html

.PHONY: lint
lint:  ## Run linters (flake8).
	@echo "Running linters"
	@$(VENV_DIR)/bin/flake8 $(PACKAGE_NAME)

.PHONY: format
format:  ## Format code using black.
	@echo "Formatting code"
	@$(VENV_DIR)/bin/black $(PACKAGE_NAME)

# Run the application
.PHONY: run
run: ## Run the application.
	@echo "Running the application"
	@$(VENV_DIR)/bin/$(PYTHON) -m $(PACKAGE_NAME)

