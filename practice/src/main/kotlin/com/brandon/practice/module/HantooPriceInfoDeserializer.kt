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
        val stockPrice = node?.get("output")?.get("stck_prpr")?.asText()
        logger.info("### hantoo stockCode : ${stockPrice}")

        return stockPrice?.let {
            ctxt.readValue(node.traverse().also { it.nextToken() }, HantooPriceTemplate.DomesticPriceRequest.Response::class.java)
        } ?: run {
            ctxt.readValue(node!!.traverse().also { it.nextToken() }, HantooPriceTemplate.OverseaPriceRequest.Response::class.java)
        }
    }

}
