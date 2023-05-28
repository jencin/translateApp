package com.example.myapplication.api

data class TranslateResult(val from: String, val to: String, val trans_result: List<trans_Result>,
                           val error_code: String, val error_msg: String) {
    data class trans_Result(val src: String, val dst: String)
}