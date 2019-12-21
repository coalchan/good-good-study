# 参考 https://grpc.io/docs/quickstart/python/
# pip install grpcio
# pip install protobuf
# pip install grpcio-tools

# 通过以下命令生成的 hello_pb2.py 和 maven 的 protobuf:compile-python 插件基本一致
# 但是 protobuf:compile-python 插件不能生成 hello_pb2_grpc.py


cd `dirname $0`
python -m grpc_tools.protoc -I ../proto \
--python_out=../../../target/ \
--grpc_python_out=../../../target/ \
hello.proto
