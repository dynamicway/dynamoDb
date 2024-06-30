package me.study.dynamodb.event.domain;

public class EnterEventException extends RuntimeException {
    public EnterEventException(String message) {
        super(message);
    }
}
