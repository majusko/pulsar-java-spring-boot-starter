package io.github.majusko.pulsar.mock;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MockConsumer<T> implements Consumer<T> {
    @Override
    public String getTopic() {
        return "mock-topic";
    }

    @Override
    public String getSubscription() {
        return "mock-subscription";
    }

    @Override
    public void unsubscribe() throws PulsarClientException {

    }

    @Override
    public CompletableFuture<Void> unsubscribeAsync() {
        return null;
    }

    @Override
    public Message<T> receive() throws PulsarClientException {
        return null;
    }

    @Override
    public CompletableFuture<Message<T>> receiveAsync() {
        return null;
    }

    @Override
    public Message<T> receive(int timeout, TimeUnit unit) throws PulsarClientException {
        return null;
    }

    @Override
    public Messages<T> batchReceive() throws PulsarClientException {
        return null;
    }

    @Override
    public CompletableFuture<Messages<T>> batchReceiveAsync() {
        return null;
    }

    @Override
    public void acknowledge(MessageId messageId) throws PulsarClientException {

    }

    @Override
    public void acknowledge(Messages<?> messages) throws PulsarClientException {

    }

    @Override
    public void negativeAcknowledge(MessageId messageId) {

    }

    @Override
    public void negativeAcknowledge(Messages<?> messages) {

    }

    @Override
    public void acknowledgeCumulative(MessageId messageId) throws PulsarClientException {

    }

    @Override
    public CompletableFuture<Void> acknowledgeAsync(MessageId messageId) {
        return null;
    }

    @Override
    public CompletableFuture<Void> acknowledgeAsync(Messages<?> messages) {
        return null;
    }

    @Override
    public CompletableFuture<Void> acknowledgeCumulativeAsync(MessageId messageId) {
        return null;
    }

    @Override
    public ConsumerStats getStats() {
        return null;
    }

    @Override
    public void close() throws PulsarClientException {

    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        return null;
    }

    @Override
    public boolean hasReachedEndOfTopic() {
        return false;
    }

    @Override
    public void redeliverUnacknowledgedMessages() {

    }

    @Override
    public void seek(MessageId messageId) throws PulsarClientException {

    }

    @Override
    public void seek(long timestamp) throws PulsarClientException {

    }

    @Override
    public CompletableFuture<Void> seekAsync(MessageId messageId) {
        return null;
    }

    @Override
    public CompletableFuture<Void> seekAsync(long timestamp) {
        return null;
    }

    @Override
    public MessageId getLastMessageId() throws PulsarClientException {
        return null;
    }

    @Override
    public CompletableFuture<MessageId> getLastMessageIdAsync() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public String getConsumerName() {
        return null;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public CompletableFuture<Void> acknowledgeCumulativeAsync(Message message) {
        return null;
    }

    @Override
    public CompletableFuture<Void> acknowledgeAsync(Message message) {
        return null;
    }

    @Override
    public void acknowledgeCumulative(Message message) throws PulsarClientException {

    }

    @Override
    public void negativeAcknowledge(Message message) {

    }

    @Override
    public void acknowledge(Message message) throws PulsarClientException {

    }
}
