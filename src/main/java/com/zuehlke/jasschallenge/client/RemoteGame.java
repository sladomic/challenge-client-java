package com.zuehlke.jasschallenge.client;

import com.zuehlke.jasschallenge.client.game.Player;
import com.zuehlke.jasschallenge.client.websocket.RemoteGameHandler;
import com.zuehlke.jasschallenge.client.websocket.RemoteGameSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemoteGame {

    private static final Logger logger = LoggerFactory.getLogger(RemoteGame.class);
    private static final int MIN_NEEDED_THREAD_COUNT = 5;
    public static final int CLOSE_TIMEOUT_MIN = 5;
    private final Player player;
    private final String targetUrl;

    public RemoteGame(String targetUrl, Player player) {
        this.targetUrl = targetUrl;
        this.player = player;
    }

    public void start() throws Exception {
        final WebSocketClient client = new WebSocketClient(Executors.newFixedThreadPool(MIN_NEEDED_THREAD_COUNT));
        try {
            RemoteGameSocket socket = new RemoteGameSocket(new RemoteGameHandler(player));
            client.start();

            URI uri = new URI(targetUrl);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, uri, request);
            logger.debug("Connecting to: {}", uri);
            socket.awaitClose(CLOSE_TIMEOUT_MIN, TimeUnit.MINUTES);
        } finally {
            client.stop();
        }
    }

}
