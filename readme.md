This example can be used to generate documentations for the grpc 

1. Install protoc and documentation plugins like protoc-gen-doc or protoc-gen-openapiv2. 

''' command 

# Install protoc (if not installed)
brew install protobuf  # macOS
sudo apt-get install protobuf-compiler  # Ubuntu

# Install protoc-gen-doc for generating documentation
go install github.com/pseudomuto/protoc-gen-doc/cmd/protoc-gen-doc@latest


export PATH="$PATH:$(go env GOPATH)/bin"
'''

2. Add Custom Metadata with Protobuf Options

You can define custom options in your .proto files to embed metadata, i have not done here but we can use options.prot and import into our main greetings.proto. 
'''
#options.proto 

syntax = "proto3";

package documentation;

import "google/protobuf/descriptor.proto";

extend google.protobuf.MessageOptions {
  string doc_description = 50001;
}

'''

3. Use protoc-gen-doc to generate documentation:
'''
protoc -I=. \
  --doc_out=docs \
  --doc_opt=markdown,api.md \
  example.proto

options for other format;
html: --doc_opt=html,index.html
json: --doc_opt=json,api.json

'''