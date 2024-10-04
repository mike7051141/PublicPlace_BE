package com.springboot.publicplace.exception;

public class UserNotInTeamException extends RuntimeException {
    public UserNotInTeamException(String message) {
        super(message);
    }
}