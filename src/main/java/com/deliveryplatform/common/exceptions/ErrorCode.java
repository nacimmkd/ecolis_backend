package com.deliveryplatform.common.exceptions;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();
    HttpStatus status();
}
