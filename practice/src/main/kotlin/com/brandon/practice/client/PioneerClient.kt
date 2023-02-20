package com.brandon.practice.client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import com.brandon.practice.client.PriceApiTemplate.*
import com.brandon.practice.domain.PriceInfoDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.core.ResolvableType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MimeTypeUtils
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import org.springframework.web.reactive.function.client.WebClient

class PioneerClient(
    val clientProperties: ClientProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("Initializing HantooClient with {}", clientProperties)
    }

    data class RequestInfo(val responseType: Class<*>, val reqType: Class<*>, val headerType: Class<*>)

    val requestInfos = ApiTemplate::class.sealedSubclasses.associate {
        val resolvableType = ResolvableType.forClass(it.java)

        val responseClass = resolvableType.interfaces[0].generics[0].resolve()!!
        val reqClass = resolvableType.interfaces[0].generics[1].resolve()!!
        val headerClass = resolvableType.interfaces[0].generics[2].resolve()!!

        it.java to RequestInfo(responseClass, reqClass, headerClass)
    }

    private fun makeHeaders(headerInfo: Map<String, String>): HttpHeaders {
        val headers = HttpHeaders()
        val headerForm = LinkedMultiValueMap<String, String>()
        headerInfo.forEach { (k, v) -> headerForm.add(k, v) }
        headers.addAll(headerForm)
        return headers
    }

    private val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(
            SimpleModule().addDeserializer(PriceResponseTemplate::class.java,
                PriceInfoDeserializer())
        )
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)


    fun <Res: ApiResponse, Req: ApiRequestBody, H: ApiHeader>
            getPriceBySubcription(request: ApiTemplate<Res, Req, H>): Mono<PriceResponseTemplate> {
        val headerType = object: TypeReference<Map<String, String>>(){}
        val header = mapper.convertValue(request.header(), headerType)

        val reqBodyType = object: TypeReference<Map<String, String>>() {}
        val reqBody = mapper.convertValue(request.request(), reqBodyType)
        val formData = LinkedMultiValueMap<String, String>()
        reqBody.forEach { (k, v) -> formData.add(k, v) }

        return WebClient.builder()
            .defaultHeaders{ it.addAll(makeHeaders(header)) }
            .codecs { it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper)) }
            .baseUrl(clientProperties.server)
            .build()
            .method(request.method())
            .uri{ builder: UriBuilder ->
                builder.path(request.path())
                    .queryParams(formData).build() }
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError){
                it.bodyToMono(PostException::class.java)
            }
            .bodyToMono(PriceResponseTemplate::class.java)
    }

    fun <Res: ApiResponse, Req: ApiRequestBody, H: ApiHeader>
            getPrice(request: ApiTemplate<Res, Req, H>) : Mono<PriceResponseTemplate> {
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
                it.bodyToMono(PostException::class.java)
            }
            .bodyToMono(PriceResponseTemplate::class.java)

    }

    fun <Res: ApiResponse, Req: ApiRequestBody, H: ApiHeader>
            getOverSeaPrice(request: ApiTemplate<Res, Req, H>) : Mono<OverseaPriceRequest.Response> {
        val headerType = object: TypeReference<Map<String, String>>(){}
        val header = mapper.convertValue(request.header(), headerType)

        val reqBodyType = object: TypeReference<Map<String, String>>() {}
        val reqBody = mapper.convertValue(request.request(), reqBodyType)
        val formData = LinkedMultiValueMap<String, String>()
        reqBody.forEach { (k, v) -> formData.add(k, v) }


        return WebClient.builder()
            .defaultHeaders{ it.addAll(makeHeaders(header)) }
            .baseUrl(clientProperties.server)
            .build()
            .method(request.method())
            .uri{ builder: UriBuilder ->
                builder.path(request.path())
                    .queryParams(formData).build() }
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError){
                it.bodyToMono(PostException::class.java)
            }
            .bodyToMono(OverseaPriceRequest.Response::class.java)

    }
}
