package com.guraband.couponconsumer

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@TestPropertySource(properties = ["spring.config.name=application-core"])
@Transactional
@SpringBootTest
class TestConfig {
}
