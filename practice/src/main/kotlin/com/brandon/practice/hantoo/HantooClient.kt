package com.brandon.practice.hantoo


import com.brandon.practice.module.HantooPriceInfoDeserializer
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono

class HantooClient(
    val clientProperties: HantooClientProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("Initializing HantooClient with {}", clientProperties)
    }
    data class RequestInfo(val responseType: Class<*>, val reqType: Class<*>, val headerType: Class<*>)

    private fun makeHeaders(headerInfo: Map<String, String>): HttpHeaders {
        val headers = HttpHeaders()
        val headerForm = LinkedMultiValueMap<String, String>()
        headerInfo.forEach { (k, v) -> headerForm.add(k, v) }
        headers.addAll(headerForm)
        return headers
    }

    private val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(
            SimpleModule().addDeserializer(
                HantooPriceTemplate.PriceResponse::class.java, HantooPriceInfoDeserializer()
            )
        )
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    fun <Res: HantooPriceTemplate.ApiResponse, Req: HantooPriceTemplate.ApiRequest, H: HantooPriceTemplate.ApiHeader>
            getPrice(request: HantooPriceTemplate.ApiTemplate<Res, Req, H>) : Mono<HantooPriceTemplate.PriceResponse> {
        val headerType = object: TypeReference<Map<String, String>>(){}
        val header = mapper.convertValue(request.header(), headerType)

        val reqBodyType = object: TypeReference<Map<String, String>>() {}
        val reqBody = mapper.convertValue(request.request(), reqBodyType)
        val formData = LinkedMultiValueMap<String, String>()
        reqBody.forEach { (k, v) -> formData.add(k, v) }

        return WebClient.builder()
            .defaultHeaders{ it.addAll(makeHeaders(header)) }
            .baseUrl(clientProperties.server)
            .codecs { it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper)) }
            .build()
            .method(request.method())
            .uri{ builder: UriBuilder ->
                builder.path(request.path())
                    .queryParams(formData).build() }
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError){
                it.bodyToMono(HantooPriceTemplate.PostException::class.java)
            }
            .bodyToMono(HantooPriceTemplate.PriceResponse::class.java)
            .timeout(clientProperties.timeout)

    }
}
