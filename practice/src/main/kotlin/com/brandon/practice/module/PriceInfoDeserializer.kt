package com.brandon.practice.module

import com.brandon.practice.client.PriceApiTemplate
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory

class PriceInfoDeserializer : JsonDeserializer<PriceApiTemplate.PriceResponseTemplate>() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun deserialize(jsonParser: JsonParser?, ctxt: DeserializationContext): PriceApiTemplate.PriceResponseTemplate {
        val node: JsonNode? = jsonParser?.codec?.readTree(jsonParser)
        val stockCode = node?.get("stockCd")?.asText()
        logger.info("### stockCode : ${stockCode}")

        val d: JsonParser? = node?.traverse().also { it?.nextToken() }

        return when (stockCode?.matches("[a-zA-Z]+".toRegex())) {
             true -> ctxt.readValue(node.traverse().also { it.nextToken() }, PriceApiTemplate.OverseaPriceRequest.Response::class.java)
            //true -> ctxt.readValue(node.traverse(), PriceApiTemplate.OverseaPriceRequest.Response::class.java)
            //false -> ctxt.readValue(node.traverse(), PriceApiTemplate.DomesticPriceRequest.Response::class.java)
            else -> ctxt.readValue(node?.traverse(), PriceApiTemplate.DomesticPriceRequest.Response::class.java)
        }
    }

}
