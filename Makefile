# Makefile for Spring Boot project using Gradle Wrapper

# Default target
.PHONY: help
help:
	@echo "Usage:"
	@echo "  make build         - Build the project"
	@echo "  make run           - Run the application"
	@echo "  make clean         - Clean build artifacts"
	@echo "  make test          - Run tests"
	@echo "  make skip-tests    - Build without running tests"
	@echo "  make jar           - Build and show jar path"

# Build the project
.PHONY: build
build:
	./gradlew build

# Build without running tests
.PHONY: skip-tests
skip-tests:
	./gradlew build -x test

# Run the Spring Boot application
.PHONY: run
run:
	./gradlew bootRun

# Clean build outputs
.PHONY: clean
clean:
	./gradlew clean

# Run tests
.PHONY: test
test:
	./gradlew test

# Print the JAR file path after build
.PHONY: jar
jar: build
	@echo "Built JAR:"
	@ls -lh build/libs/*.jar
