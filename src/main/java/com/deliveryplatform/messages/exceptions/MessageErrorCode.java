package com.deliveryplatform.messages.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MessageErrorCode implements ErrorCode {

    CONVERSATION_NOT_FOUND(HttpStatus.NOT_FOUND),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND),
    NOT_A_PARTICIPANT(HttpStatus.FORBIDDEN);

    private final HttpStatus status;

    MessageErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}