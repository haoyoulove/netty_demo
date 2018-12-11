// 创建ProtoBuf
var ProtoBuf = dcodeIO.ProtoBuf;
// 加载ProtoBuf文件
var CommonProtocol = ProtoBuf.loadProtoFile("ProtocolModule.proto").build("CommonProtocol");
var commonProtocol = new CommonProtocol();
// 组装请求包
var RequestHeader = CommonProtocol.RequestHeader;
var requestHeader = new RequestHeader();
requestHeader.setId(100).setCode(0).setMsg(120);


commonProtocol.ReqHeader = requestHeader;
commonProtocol.data = "2123";

// 请求websocket
var websocket = null;
websocket = new WebSocket('ws://localhost:19999/chat');
websocket.binaryType = "arraybuffer";

// 连接成功建立的回调方法
websocket.onopen = function () {
    console.log("连接成功");
    websocket.send(commonProtocol.toArrayBuffer());
}
// 接收到消息的回调方法
websocket.onmessage = function (res) {
    console.log("接收到消息");
    console.log(res.data);
    console.log(CommonProtocol.decode(res.data));
}
// 连接关闭的回调方法
websocket.onclose = function () {
    console.log("连接关闭");
}
