package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(WebConfig.class)
class WebConfigTest {

    @Autowired
    private HiddenHttpMethodFilter hiddenHttpMethodFilter;

    @Test
    void hiddenHttpMethodFilterBeanShouldBeCreated() {
        assertThat(hiddenHttpMethodFilter).isNotNull();
    }
}
