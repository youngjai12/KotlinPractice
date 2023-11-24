package com.brandon.practice.domain

data class CcldRealTime(
    val MKSC_SHRN_ISCD: String,
    val STCK_CNTG_HOUR: String, //주가체결시간
    val STCK_PRPR: Float,
    val PRDY_VRSS_SIGN: String, // 전일대비 부호
    val PRDY_VRSS: Int, // 전일대비
val PRDY_CTRT: Float, // 전일대비 비율
val WGHN_AVRG_STCK_PRC: Float, // 가중평균 주식가격
val STCK_OPRC: Float,
val STCK_HGPR: Float,
val STCK_LWPR: Float,
val ASKP1: Float,// 매도호가
val BIDP1: Float,
val CNTG_VOL: Float,// 체결거래량
val ACML_VOL: Float,
val ACML_TR_PBMN: Float,
val SELN_CNTG_CSNU: Float, // 매도체결 건수
val SHNU_CNTG_CSNU: Float, // 매수 체결 건수
val NTBY_CNTG_CSNU: Float, // 순매수 체결 건수
val CTTR: Float, // 체결강도
val SELN_CNTG_SMTN: Float,
val SHNU_CNTG_SMTN: Float,
val CCLD_DVSN: String, //체결구분 (1.매수, 3.장전, 5.매도 )
val SHNU_RATE: Float,
val PRDY_VOL_VRSS_ACML_VOL_RATE: Float,
val OPRC_HOUR: String, //시가시간
val OPRC_VRSS_PRPR_SIGN: String,
val OPRC_VRSS_PRPR: Float, // 시가대비 얼마나 올랏는지
val HGPR_HOUR: String,
val HGPR_VRSS_PRPR_SIGN: String,
val HGPR_VRSS_PRPR: Float,
val LWPR_HOUR: String,
val LWPR_VRSS_PRPR_SIGN: String,
val LWPR_VRSS_PRPR: Float,
val BSOP_DATE: String,
val NEW_MKOP_CLS_CODE: String ,  // 신 장운영 구분 코드
val TRHT_YN: String,
val ASKP_RSQN1: Float, // 매도호가 잔량
val BIDP_RSQN1: Float,
val TOTAL_ASKP_RSQN: Float,
val TOTAL_BIDP_RSQN: Float,
val VOL_TNRT: Float,
val PRDY_SMNS_HOUR_ACML_VOL: Int,
val PRDY_SMNS_HOUR_ACML_VOL_RATE: Float,
val HOUR_CLS_CODE: String,
    val MRKT_TRTM_CLS_CODE: String,
    val VI_STND_PRC: String
)

object CcldDeserializer{
    fun deserialize(rawMsg: String): CcldRealTime {
        val values = rawMsg.split("^")
        return CcldRealTime(
            MKSC_SHRN_ISCD = values[0],
            STCK_CNTG_HOUR = values[1],
            STCK_PRPR = values[2].toFloat(),
            PRDY_VRSS_SIGN = values[3],
            PRDY_VRSS = values[4].toInt(),
            PRDY_CTRT = values[5].toFloat(),
            WGHN_AVRG_STCK_PRC = values[6].toFloat(),
            STCK_OPRC = values[7].toFloat(),
            STCK_HGPR = values[8].toFloat(),
            STCK_LWPR = values[9].toFloat(),
            ASKP1 = values[10].toFloat(),
            BIDP1 = values[11].toFloat(),
            CNTG_VOL = values[12].toFloat(),
            ACML_VOL = values[13].toFloat(),
            ACML_TR_PBMN = values[14].toFloat(),
            SELN_CNTG_CSNU = values[15].toFloat(),
            SHNU_CNTG_CSNU = values[16].toFloat(),
            NTBY_CNTG_CSNU = values[17].toFloat(),
            CTTR = values[18].toFloat(),
            SELN_CNTG_SMTN = values[19].toFloat(),
            SHNU_CNTG_SMTN = values[20].toFloat(),
            CCLD_DVSN = values[21],
            SHNU_RATE = values[22].toFloat(),
            PRDY_VOL_VRSS_ACML_VOL_RATE = values[23].toFloat(),
            OPRC_HOUR = values[24],
            OPRC_VRSS_PRPR_SIGN = values[25],
            OPRC_VRSS_PRPR = values[26].toFloat(), // 시가대비 얼마나 올랏는지
            HGPR_HOUR= values[27],
            HGPR_VRSS_PRPR_SIGN= values[28],
            HGPR_VRSS_PRPR= values[29].toFloat() ,
            LWPR_HOUR= values[30],
            LWPR_VRSS_PRPR_SIGN= values[31],
            LWPR_VRSS_PRPR= values[32].toFloat(),
            BSOP_DATE= values[33],
            NEW_MKOP_CLS_CODE= values[34] ,  // 신 장운영 구분 코드
            TRHT_YN= values[35],
            ASKP_RSQN1= values[36].toFloat(), // 매도호가 잔량
            BIDP_RSQN1= values[37].toFloat(),
            TOTAL_ASKP_RSQN= values[38].toFloat(),
            TOTAL_BIDP_RSQN= values[39].toFloat(),
            VOL_TNRT= values[40].toFloat(),
            PRDY_SMNS_HOUR_ACML_VOL= values[41].toInt(),
            PRDY_SMNS_HOUR_ACML_VOL_RATE= values[42].toFloat(),
            HOUR_CLS_CODE= values[43],
            MRKT_TRTM_CLS_CODE= values[44],
            VI_STND_PRC= values[45]
        )
    }
}