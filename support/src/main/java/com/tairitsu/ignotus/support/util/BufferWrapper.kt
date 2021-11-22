//package com.tairitsu.ignotus.support.util
//
//import java.nio.ByteBuffer
//import java.nio.charset.Charset
//import javax.servlet.http.HttpServletRequest
//
//object BufferWrapper {
//    @JvmStatic
//    fun HttpServletRequest.readBodyToString(): String {
//        val bytes = this.inputStream.readAllBytes()
//        val encoding = this.characterEncoding ?: "UTF-8"
//        val charset = try {
//            Charset.forName(encoding)
//        } catch (e: Exception) {
//            Charset.forName("UTF-8")
//        }
//        return charset.decode(ByteBuffer.wrap(bytes)).toString()
//    }
//}
