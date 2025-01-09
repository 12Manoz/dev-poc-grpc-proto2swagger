#!/bin/bash

# Find all .proto files under src/main/proto
PROTO_FILES=$(find src/main/proto -name "*.proto")

# Output directory for Swagger files
SWAGGER_OUTPUT_DIR="build/swagger"

# Create the output directory if it doesn't exist
mkdir -p "$SWAGGER_OUTPUT_DIR"

# Iterate over each .proto file
for PROTO_FILE in $PROTO_FILES; do
  # Generate Swagger file for the current .proto file
  protoc \
      -I. \
      --java_out=src/main/generated/java \
      --swagger_out=logtostderr=true:"$SWAGGER_OUTPUT_DIR/$(basename "$PROTO_FILE" .proto).json" \
      "$PROTO_FILE"

  # Check if the Swagger file was generated successfully
  if [ -f "$SWAGGER_OUTPUT_DIR/$(basename "$PROTO_FILE" .proto).json" ]; then
    echo "Swagger file generated successfully for $PROTO_FILE"
  else
    echo "Failed to generate Swagger file for $PROTO_FILE"
  fi
done