#!/bin/bash

# Create a temporary directory for the required proto files
TEMP_DIR=$(mktemp -d)
SWAGGER_OUT_DIR="./swagger"

# Download required proto files
curl -sSL "https://raw.githubusercontent.com/googleapis/googleapis/master/google/api/annotations.proto" > "${TEMP_DIR}/google/api/annotations.proto"
curl -sSL "https://raw.githubusercontent.com/googleapis/googleapis/master/google/api/http.proto" > "${TEMP_DIR}/google/api/http.proto"
curl -sSL "https://raw.githubusercontent.com/grpc-ecosystem/grpc-gateway/master/protoc-gen-openapiv2/options/annotations.proto" > "${TEMP_DIR}/protoc-gen-openapiv2/options/annotations.proto"
curl -sSL "https://raw.githubusercontent.com/grpc-ecosystem/grpc-gateway/master/protoc-gen-openapiv2/options/openapiv2.proto" > "${TEMP_DIR}/protoc-gen-openapiv2/options/openapiv2.proto"

# Create output directory if it doesn't exist
mkdir -p "${SWAGGER_OUT_DIR}"

# Generate Swagger/OpenAPI specification
protoc -I . \
  -I "${TEMP_DIR}" \
  --openapiv2_out "${SWAGGER_OUT_DIR}" \
  --openapiv2_opt logtostderr=true \
  --openapiv2_opt json_names_for_fields=false \
  src/main/proto/greeting.proto

# Clean up temporary directory
rm -rf "${TEMP_DIR}"

echo "Swagger file generated in ${SWAGGER_OUT_DIR}/greeting.swagger.json"