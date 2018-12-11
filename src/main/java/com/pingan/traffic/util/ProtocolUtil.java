//package com.pingan.traffic.util;
//
//import com.google.protobuf.ByteString;
//import com.pingan.traffic.protocol.Protocol.CcResponse;
//import com.pingan.traffic.protocol.Protocol.Header;
//
//public class ProtocolUtil {
//
//
//
//	public static CcResponse buildFailResponse(Header.Builder header, int errorCode, String msg){
//		header.setCode(errorCode);
//		header.setMsg(msg);
//		CcResponse.Builder builder = CcResponse.newBuilder();
//		builder.setHeader(header);
//		return builder.build();
//	}
//
//	public static CcResponse buildSuccessResponse(Header.Builder header, ByteString byteString){
//		header.setCode(0);
//		header.setMsg("success");
//		CcResponse.Builder builder = CcResponse.newBuilder();
//		builder.setHeader(header);
//		builder.setBody(byteString);
//		return builder.build();
//	}
//}
