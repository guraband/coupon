package com.guraband.couponapi.controller

import com.guraband.couponapi.dto.CouponIssueRequest
import com.guraband.couponapi.dto.CouponIssueResponse
import com.guraband.couponapi.service.CouponIssueRequestService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CouponIssueController(
    private val couponIssueRequestService: CouponIssueRequestService,
) {

    @PostMapping("/v1/issue")
    fun issueV1(@RequestBody request: CouponIssueRequest): CouponIssueResponse {
        couponIssueRequestService.issueRequestV1(request)
//        couponIssueRequestService.issueRequestV2(request)

        return CouponIssueResponse(true)
    }

    @PostMapping("/v1/issue-async")
    fun asyncIssueV1(@RequestBody request: CouponIssueRequest): CouponIssueResponse {
        couponIssueRequestService.asyncIssueRequestUsingSortedSet(request)

        return CouponIssueResponse(true)
    }

    @PostMapping("/v1/issue-async-set")
    fun asyncIssueUsingSetV1(@RequestBody request: CouponIssueRequest): CouponIssueResponse {
        couponIssueRequestService.asyncIssueRequestUsingSetV1(request)

        return CouponIssueResponse(true)
    }

    @PostMapping("/v2/issue-async-set")
    fun asyncIssueUsingSetV2(@RequestBody request: CouponIssueRequest): CouponIssueResponse {
        couponIssueRequestService.asyncIssueRequestUsingSetV2(request)

        return CouponIssueResponse(true)
    }
}