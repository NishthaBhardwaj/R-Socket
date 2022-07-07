package com.nishthasoft.rsocket.dto;

public class RequestDto {

    private int input;

    public RequestDto(int input) {
        this.input = input;
    }

    public RequestDto() {
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "RequestDto{" +
                "input=" + input +
                '}';
    }
}
