package domain
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = DomesticPriceRequest.Response::class, name = "domestic"),
    JsonSubTypes.Type(value = OverseaPriceRequest.Response::class, name = "oversea")
)
interface PriceResponseTemplate{
    fun currentPrice(): String
    fun priceUnit(): String
}

sealed interface ApiResponse
sealed interface ApiHeader
sealed interface ApiRequestBody

sealed interface ApiTemplate<Res : ApiResponse, Req: ApiRequestBody, H: ApiHeader> {
    fun path(): String
    fun method(): HttpMethod
}

data class  DomesticPriceRequest(
    val request: DomesticPriceRequest.Request,
    val header: DomesticPriceRequest.Header
): ApiTemplate<DomesticPriceRequest.Response, DomesticPriceRequest.Request, DomesticPriceRequest.Header> {

    class Response(
        val price: String,
        val priceUnit: String
    ): ApiResponse, PriceResponseTemplate {
        override fun currentPrice() = price
        override fun priceUnit() = priceUnit
    }

    class Request(
        val fid_input_iscd: String,
    ): ApiRequestBody

    class Header(
        val contentType: String = MediaType.APPLICATION_JSON_VALUE,
        @JsonProperty("Connection")
        val connection: String = "close",
    ): ApiHeader

    override fun path(): String = "/price/domestic"

    override fun method(): HttpMethod = HttpMethod.GET
}

data class  OverseaPriceRequest(
    val request: OverseaPriceRequest.Request,
    val header: OverseaPriceRequest.Header
): ApiTemplate<OverseaPriceRequest.Response, OverseaPriceRequest.Request, OverseaPriceRequest.Header> {


    class Response(
        val overseaPrice: String,
        val stockCd: String
    ): ApiResponse, PriceResponseTemplate {
        override fun currentPrice() = overseaPrice
        override fun priceUnit(): String {
            return if(overseaPrice.toDouble() > 10.0) {
                "5"
            } else {
                return "10"
            }
        }
    }

    class Request(
       val symb: String
    ): ApiRequestBody

    class Header(
        val contentType: String = MediaType.APPLICATION_JSON_VALUE,
        @JsonProperty("Connection")
        val connection: String = "close",
    ): ApiHeader

    override fun path(): String = "/price/oversea"

    override fun method(): HttpMethod = HttpMethod.GET
}

data class PostException(
    @JsonProperty("rt_cd")
    val rtCdd: String? ,
    @JsonProperty("msg_cd")
    val msgCdd: String?,
    @JsonProperty("msg1")
    val msg22: String?
): RuntimeException()
