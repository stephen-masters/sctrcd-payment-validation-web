package com.sctrcd.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the payment validation web application. Component scanning covers
 * every <code>com.sctrcd</code> package, as the old XML configuration did.
 */
@SpringBootApplication(scanBasePackages = "com.sctrcd")
public class PaymentValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentValidationApplication.class, args);
    }
}
