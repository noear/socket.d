package org.noear.socketd.transport.core;

import org.noear.socketd.utils.IoConsumer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Session 包装器
 *
 * @author noear
 * @since 2.1
 */
public class SessionWrapper implements Session {
    protected final Session real;

    public SessionWrapper(Session real) {
        this.real = real;
    }

    @Override
    public boolean isValid() {
        return real.isValid();
    }

    @Override
    public InetSocketAddress remoteAddress() throws IOException {
        return real.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() throws IOException {
        return real.localAddress();
    }

    @Override
    public Handshake handshake() {
        return real.handshake();
    }

    @Override
    public String param(String name) {
        return real.param(name);
    }

    @Override
    public String paramOrDefault(String name, String def) {
        return real.paramOrDefault(name, def);
    }

    @Override
    public String path() {
        return real.path();
    }

    @Override
    public void pathNew(String pathNew) {
        real.pathNew(pathNew);
    }

    @Override
    public Map<String, Object> attrMap() {
        return real.attrMap();
    }

    @Override
    public boolean attrHas(String name) {
        return real.attrHas(name);
    }

    @Override
    public <T> T attr(String name) {
        return real.attr(name);
    }

    @Override
    public <T> T attrOrDefault(String name, T def) {
        return real.attrOrDefault(name, def);
    }

    @Override
    public <T> Session attrPut(String name, T value) {
        return real.attrPut(name, value);
    }

    @Override
    public String sessionId() {
        return real.sessionId();
    }

    @Override
    public void reconnect() throws IOException {
        real.reconnect();
    }

    @Override
    public void sendPing() throws IOException {
        real.sendPing();
    }

    @Override
    public void sendAlarm(Message from, String alarm) throws IOException {
        real.sendAlarm(from, alarm);
    }

    @Override
    public void send(String event, Entity content) throws IOException {
        real.send(event, content);
    }

    @Override
    public Reply sendAndRequest(String event, Entity content) throws IOException {
        return real.sendAndRequest(event, content);
    }

    @Override
    public Reply sendAndRequest(String event, Entity content, long timeout) throws IOException {
        return real.sendAndRequest(event, content, timeout);
    }

    @Override
    public Stream sendAndRequest(String event, Entity content, IoConsumer<Reply> consumer) throws IOException {
        return real.sendAndRequest(event, content, consumer);
    }

    @Override
    public Stream sendAndRequest(String event, Entity content, IoConsumer<Reply> consumer, long timeout) throws IOException {
        return real.sendAndRequest(event, content, consumer, timeout);
    }

    @Override
    public Stream sendAndSubscribe(String event, Entity content, IoConsumer<Reply> consumer) throws IOException {
        return real.sendAndSubscribe(event, content, consumer);
    }

    @Override
    public Stream sendAndSubscribe(String event, Entity content, IoConsumer<Reply> consumer, long timeout) throws IOException {
        return real.sendAndSubscribe(event, content, consumer, timeout);
    }

    @Override
    public void reply(Message from, Entity content) throws IOException {
        real.reply(from, content);
    }

    @Override
    public void replyEnd(Message from, Entity content) throws IOException {
        real.replyEnd(from, content);
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}