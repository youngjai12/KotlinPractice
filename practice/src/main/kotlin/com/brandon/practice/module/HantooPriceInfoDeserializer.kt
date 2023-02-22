package com.brandon.practice.module

import com.brandon.practice.client.PriceApiTemplate
import com.brandon.practice.hantoo.HantooPriceTemplate
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory

class HantooPriceInfoDeserializer: JsonDeserializer<HantooPriceTemplate.PriceResponse>() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun deserialize(jsonParser: JsonParser?, ctxt: DeserializationContext): HantooPriceTemplate.PriceResponse {
        val node: JsonNode? = jsonParser?.codec?.readTree(jsonParser)
        val stockCode = node?.get("stockCd")?.asText()
        logger.info("### stockCode : ${stockCode}")

        return when (stockCode?.matches("[a-zA-Z]+".toRegex())) {
            true -> ctxt.readValue(node.traverse().also { it.nextToken() }, HantooPriceTemplate.OverseaPriceRequest.Response::class.java)
            //true -> ctxt.readValue(node.traverse(), PriceApiTemplate.OverseaPriceRequest.Response::class.java)
            //false -> ctxt.readValue(node.traverse(), PriceApiTemplate.DomesticPriceRequest.Response::class.java)
            else -> ctxt.readValue(node?.traverse(), HantooPriceTemplate.DomesticPriceRequest.Response::class.java)
        }
    }

}
