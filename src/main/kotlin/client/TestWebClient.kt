package client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import domain.*
import org.slf4j.LoggerFactory
import org.springframework.core.ResolvableType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono

class TestWebClient(
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
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)


    fun <Res: ApiResponse, Req: ApiRequestBody, H: ApiHeader>
            getPrice(request: ApiTemplate<Res, Req, H>) : Mono<PriceResponseTemplate> {
        // 각 API 요청 폼마다 request, response 를 따로 정의해둔 nested class가 있기 때문에, api요청결과를 해당 class의 response로
        // 특정해주기 위해서 각 inner class에 접근하기 위해서

        val fieldType = object : TypeReference<Map<String, Map<String, String>>>() {}
        val fieldInfoMap: Map<String, Map<String, String>> = mapper.convertValue(request, fieldType)

        val reqBodyType = object: TypeReference<Map<String, String>>() {}
        val reqBody = mapper.convertValue(fieldInfoMap["request"], reqBodyType)

        val formData = LinkedMultiValueMap<String, String>()
        val headerType = object: TypeReference<Map<String, String>>(){}
        val header = mapper.convertValue(fieldInfoMap["header"], headerType)

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
            .bodyToMono(PriceResponseTemplate::class.java)

    }
}
