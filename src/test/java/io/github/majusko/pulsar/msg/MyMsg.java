package io.github.majusko.pulsar.msg;

public class MyMsg {
    private String data;
    public MyMsg(String data) {
        this.data = data;
    }

    public MyMsg() {
        System.out.println();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
