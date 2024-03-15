package com.guraband.couponapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    var count = 0

    @GetMapping("/hello")
    fun hello(): String {
        println(++count)
        return "OK"
    }
}