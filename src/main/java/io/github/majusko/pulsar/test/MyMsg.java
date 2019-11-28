package io.github.majusko.pulsar.test;

public class MyMsg {
    private String data;
    public MyMsg(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
