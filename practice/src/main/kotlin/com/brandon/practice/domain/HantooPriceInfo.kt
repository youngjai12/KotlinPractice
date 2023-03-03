package com.brandon.practice.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class DomesticHantooPrice(
    @JsonProperty("msg1")
    val msg1: String? = null,
    @JsonProperty("msg_cd")
    val msgCd: String? = null,
    @JsonProperty("output")
    val output: Output,
    @JsonProperty("rt_cd")
    val rtCd: String? = null
) {
    data class Output(
        @JsonProperty("acml_tr_pbmn")
        val acmlTrPbmn: String? = null,
        @JsonProperty("acml_vol")
        val acmlVol: String? = null,
        @JsonProperty("aspr_unit")
        val asprUnit: String? = null,
        @JsonProperty("bps")
        val bps: String? = null,
        @JsonProperty("bstp_kor_isnm")
        val bstpKorIsnm: String? = null,
        @JsonProperty("clpr_rang_cont_yn")
        val clprRangContYn: String? = null,
        @JsonProperty("cpfn")
        val cpfn: String? = null,
        @JsonProperty("cpfn_cnnm")
        val cpfnCnnm: String? = null,
        @JsonProperty("crdt_able_yn")
        val crdtAbleYn: String? = null,
        @JsonProperty("d250_hgpr")
        val d250Hgpr: String? = null,
        @JsonProperty("d250_hgpr_date")
        val d250HgprDate: String? = null,
        @JsonProperty("d250_hgpr_vrss_prpr_rate")
        val d250HgprVrssPrprRate: String? = null,
        @JsonProperty("d250_lwpr")
        val d250Lwpr: String? = null,
        @JsonProperty("d250_lwpr_date")
        val d250LwprDate: String? = null,
        @JsonProperty("d250_lwpr_vrss_prpr_rate")
        val d250LwprVrssPrprRate: String? = null,
        @JsonProperty("dmrs_val")
        val dmrsVal: String? = null,
        @JsonProperty("dmsp_val")
        val dmspVal: String? = null ,
        @JsonProperty("dryy_hgpr_date")
        val dryyHgprDate: String? = null,
        @JsonProperty("dryy_hgpr_vrss_prpr_rate")
        val dryyHgprVrssPrprRate: String? = null,
        @JsonProperty("dryy_lwpr_date")
        val dryyLwprDate: String? = null,
        @JsonProperty("dryy_lwpr_vrss_prpr_rate")
        val dryyLwprVrssPrprRate: String? = null,
        @JsonProperty("elw_pblc_yn")
        val elwPblcYn: String? = null,
        @JsonProperty("eps")
        val eps: String? = null,
        @JsonProperty("fcam_cnnm")
        val fcamCnnm: String? = null,
        @JsonProperty("frgn_hldn_qty")
        val frgnHldnQty: String? = null,
        @JsonProperty("frgn_ntby_qty")
        val frgnNtbyQty: String? = null,
        @JsonProperty("grmn_rate_cls_code")
        val grmnRateClsCode: String? = null,
        @JsonProperty("hts_avls")
        val htsAvls: String? = null,
        @JsonProperty("hts_deal_qty_unit_val")
        val htsDealQtyUnitVal: String? = null,
        @JsonProperty("hts_frgn_ehrt")
        val htsFrgnEhrt: String? = null,
        @JsonProperty("invt_caful_yn")
        val invtCafulYn: String? = null,
        @JsonProperty("iscd_stat_cls_code")
        val iscdStatClsCode: String? = null,
        @JsonProperty("last_ssts_cntg_qty")
        val lastSstsCntgQty: String? = null,
        @JsonProperty("lstn_stcn")
        val lstnStcn: String? = null,
        @JsonProperty("marg_rate")
        val margRate: String? = null,
        @JsonProperty("mrkt_warn_cls_code")
        val mrktWarnClsCode: String? = null,
        @JsonProperty("oprc_rang_cont_yn")
        val oprcRangContYn: String? = null,
        @JsonProperty("ovtm_vi_cls_code")
        val ovtmViClsCode: String? = null,
        @JsonProperty("pbr")
        val pbr: String? = null,
        @JsonProperty("per")
        val per: String? = null,
        @JsonProperty("pgtr_ntby_qty")
        val pgtrNtbyQty: String? = null,
        @JsonProperty("prdy_ctrt")
        val prdyCtrt: String? = null,
        @JsonProperty("prdy_vrss")
        val prdyVrss: String? = null,
        @JsonProperty("prdy_vrss_sign")
        val prdyVrssSign: String? = null,
        @JsonProperty("prdy_vrss_vol_rate")
        val prdyVrssVolRate: String? = null,
        @JsonProperty("pvt_frst_dmrs_prc")
        val pvtFrstDmrsPrc: String? = null,
        @JsonProperty("pvt_frst_dmsp_prc")
        val pvtFrstDmspPrc: String? = null,
        @JsonProperty("pvt_pont_val")
        val pvtPontVal: String? = null,
        @JsonProperty("pvt_scnd_dmrs_prc")
        val pvtScndDmrsPrc: String? = null,
        @JsonProperty("pvt_scnd_dmsp_prc")
        val pvtScndDmspPrc: String? = null,
        @JsonProperty("rprs_mrkt_kor_name")
        val rprsMrktKorName: String? = null,
        @JsonProperty("rstc_wdth_prc")
        val rstcWdthPrc: String? = null,
        @JsonProperty("short_over_yn")
        val shortOverYn: String? = null,
        @JsonProperty("sltr_yn")
        val sltrYn: String? = null,
        @JsonProperty("ssts_yn")
        val sstsYn: String? = null,
        @JsonProperty("stac_month")
        val stacMonth: String? = null,
        @JsonProperty("stck_dryy_hgpr")
        val stckDryyHgpr: String? = null,
        @JsonProperty("stck_dryy_lwpr")
        val stckDryyLwpr: String? = null,
        @JsonProperty("stck_fcam")
        val stckFcam: String? = null,
        @JsonProperty("stck_hgpr")
        val stckHgpr: String? = null,
        @JsonProperty("stck_llam")
        val stckLlam: String? = null,
        @JsonProperty("stck_lwpr")
        val stckLwpr: String? = null,
        @JsonProperty("stck_mxpr")
        val stckMxpr: String? = null,
        @JsonProperty("stck_oprc")
        val stckOprc: String? = null,
        @JsonProperty("stck_prpr")
        val stckPrpr: String? = null,
        @JsonProperty("stck_sdpr")
        val stckSdpr: String? = null,
        @JsonProperty("stck_shrn_iscd")
        val stckShrnIscd: String? = null,
        @JsonProperty("stck_sspr")
        val stckSspr: String? = null,
        @JsonProperty("temp_stop_yn")
        val tempStopYn: String? = null,
        @JsonProperty("vi_cls_code")
        val viClsCode: String? = null,
        @JsonProperty("vol_tnrt")
        val volTnrt: String? = null,
        @JsonProperty("w52_hgpr")
        val w52Hgpr: String? = null,
        @JsonProperty("w52_hgpr_date")
        val w52HgprDate: String? = null,
        @JsonProperty("w52_hgpr_vrss_prpr_ctrt")
        val w52HgprVrssPrprCtrt: String? = null,
        @JsonProperty("w52_lwpr")
        val w52Lwpr: String? = null,
        @JsonProperty("w52_lwpr_date")
        val w52LwprDate: String? = null,
        @JsonProperty("w52_lwpr_vrss_prpr_ctrt")
        val w52LwprVrssPrprCtrt: String? = null,
        @JsonProperty("wghn_avrg_stck_prc")
        val wghnAvrgStckPrc: String? = null,
        @JsonProperty("whol_loan_rmnd_rate")
        val wholLoanRmndRate: String? = null
    )
}

