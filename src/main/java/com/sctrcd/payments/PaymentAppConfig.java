package com.sctrcd.payments;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "paymentAppConfig")
@ComponentScan(basePackages = { "com.sctrcd" })
public class PaymentAppConfig {

}
