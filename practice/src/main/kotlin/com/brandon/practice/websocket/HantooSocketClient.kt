package com.brandon.practice.websocket

import com.brandon.practice.domain.CcldDeserializer
import com.brandon.practice.domain.CcldRealTime
import com.brandon.practice.repository.UserAccessInfoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import com.brandon.practice.websocket.WebsocketApiTemplate.CcldSocketRequest
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

@Service
class HantooSocketClient(
    private val userAccessInfoRepository: UserAccessInfoRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val client = ReactorNettyWebSocketClient()

    private val mapper = jacksonObjectMapper().registerKotlinModule()

    private val url: URI = URI.create("ws://ops.koreainvestment.com:21000")

    enum class TradeKind(val tradeType: Int) {
        REGISTER(1), CLEAR(2)
    }

    private fun makeRegisterTemplate(stockCd: String, approvalKey: String, trType: TradeKind): String {
        val registerClass = CcldSocketRequest(
            body = CcldSocketRequest.Request(
                input = CcldSocketRequest.RequestInput(trKey = stockCd)
            ),
            header = CcldSocketRequest.Header(
                approvalKey = approvalKey, trType=trType.tradeType.toString()
            )
        )
        return mapper.writeValueAsString(registerClass)
    }

    fun sendMultipleRegister(approvalKey: String, stockList: List<String>, duration: Duration) {
        client.execute(url) { session ->
            Flux.fromIterable(stockList).flatMap { stock ->
                val toSendMessage = makeRegisterTemplate(stock, approvalKey, HantooSocketClient.TradeKind.REGISTER)
                session.send(Mono.just(session.textMessage(toSendMessage)))
            }.thenMany(
                session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext { msg: String ->
                        when {
                            msg.contains("PINGPONG") -> logger.info("ping pong : ${msg}")
                            msg.contains("0|H0STCNT0") -> {
                                logger.info(" msg :: ${msg}")
                                val ccldRealTime = CcldDeserializer.deserialize(msg.split("|").last())
//                                logger.info("${ccldRealTime.MKSC_SHRN_ISCD}:" +
//                                        "체결강도 : ${ccldRealTime.CTTR}" +
//                                        "체결거래량 ${ccldRealTime.CNTG_VOL}" +
//                                        "매도체결 건수: ${ccldRealTime.SELN_CNTG_CSNU} 매수체결건수: ${ccldRealTime.SHNU_CNTG_CSNU}")
                            }
                            else -> logger.info("else msg :: ${msg}")
                        }

                    }
            ).then()
        }.subscribe()
        Thread.sleep(duration.toMillis())
    }

    fun receiveMessage(duration: Duration) {
        logger.info("started handling:: ${url.path}")
        client.execute(url) { session ->
            session.receive().map(WebSocketMessage::getPayloadAsText)
                .doOnNext{msg -> logger.info("plain receive msg: ${msg}")}
                .then()
        }.subscribe()
        Thread.sleep(duration.toMillis())
    }

}

