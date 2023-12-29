package com.brandon.practice.websocket

import com.brandon.practice.domain.CcldDeserializer
import com.brandon.practice.domain.CcldRealTime
import com.brandon.practice.domain.SocketResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import com.brandon.practice.websocket.WebsocketApiTemplate.CcldSocketRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.util.retry.Retry

@Service
class HantooSocketClient() {
    private val dataLogger = LoggerFactory.getLogger("SocketLogger")

    private val logger = LoggerFactory.getLogger("Debugger")

    private val client = ReactorNettyWebSocketClient()

    companion object {
        val RETRY_COUNT: Long = 5L
        val RETRY_DELAY_DURATION: Duration = Duration.ofSeconds(10)
    }

    val mapper = jacksonObjectMapper().registerKotlinModule()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

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

    // TODO: 일괄 flatMap하는게 맞나 싶기도.. flatMap을 해야 pingpong시에 다시 publish할 수 있다.
    //       그런데 나머지 case들은 doOnNext에 해당...
    fun collectCcld(acctId: String, approvalKey: String, stockList: List<String>) {
        client.execute(url) { session: WebSocketSession ->
            Flux.fromIterable(stockList).flatMap { stock ->
                val toSendMessage = makeRegisterTemplate(stock, approvalKey, HantooSocketClient.TradeKind.REGISTER)
                session.send(Mono.just(session.textMessage(toSendMessage)))
            }.thenMany(
                session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .flatMap { msg: String ->
                        when {
                            msg.contains("PINGPONG") -> {
                                val recvData: SocketResponse = mapper.readValue(msg, SocketResponse::class.java)
                                val strRecvData = mapper.writeValueAsString(recvData)
                                logger.warn(strRecvData)
                                logger.warn("received data: ${recvData}")
                                session.send(Mono.just(session.textMessage(msg)))
                            }
                            msg.contains("0|H0STCNT0") -> {
                                val ccldRealTime: CcldRealTime = CcldDeserializer.deserialize(msg.split("|").last())
                                val ccldStrValue = mapper.writeValueAsString(ccldRealTime)
                                dataLogger.warn("${acctId}:: ${ccldStrValue}")
                                Mono.empty()
                            }
                            else -> {
                                logger.warn("${acctId} else msg :: ${msg}")
                                Mono.empty()
                            }

                        }
                    }
            ).retryWhen(Retry.fixedDelay(RETRY_COUNT,RETRY_DELAY_DURATION)).then()
        }.subscribe()
        //Thread.sleep(duration.toMillis())
    }


    fun sendMultipleRegister(acctId: String, approvalKey: String, stockList: List<String>) {
        client.execute(url) { session: WebSocketSession ->
            Flux.fromIterable(stockList).flatMap { stock ->
                val toSendMessage = makeRegisterTemplate(stock, approvalKey, HantooSocketClient.TradeKind.REGISTER)
                session.send(Mono.just(session.textMessage(toSendMessage)))
            }.thenMany(
                session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext { msg: String ->
                        when {
                            msg.contains("PINGPONG") -> {
                                val recvData: SocketResponse = mapper.readValue(msg, SocketResponse::class.java)
                                session.send(Mono.just(session.textMessage("PONG: ${msg}"))).doOnNext {
                                    val strRecvData = mapper.writeValueAsString(recvData)
                                    logger.warn(strRecvData)
                                    logger.warn("received data: ${recvData}")
                                }

                            }
                            msg.contains("0|H0STCNT0") -> {
                                val ccldRealTime: CcldRealTime = CcldDeserializer.deserialize(msg.split("|").last())
                                val ccldStrValue = mapper.writeValueAsString(ccldRealTime)
                                dataLogger.warn(ccldStrValue)

//                                logger.info("${ccldRealTime.MKSC_SHRN_ISCD}:" +
//                                        "체결강도 : ${ccldRealTime.CTTR}" +
//                                        "체결거래량 ${ccldRealTime.CNTG_VOL}" +
//                                        "매도체결 건수: ${ccldRealTime.SELN_CNTG_CSNU} 매수체결건수: ${ccldRealTime.SHNU_CNTG_CSNU}")
                            }
                            else -> logger.warn("${acctId} else msg :: ${msg}")
                        }

                    }
            ).then()
        }.subscribe()
        //Thread.sleep(duration.toMillis())
    }

    fun receiveMessage(duration: Duration) {
        dataLogger.info("started handling:: ${url.path}")
        client.execute(url) { session ->
            session.receive().map(WebSocketMessage::getPayloadAsText)
                .doOnNext{msg -> dataLogger.info("plain receive msg: ${msg}")}
                .then()
        }.subscribe()
    }

}

