package com.hk.im.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;

/**
 * @author : HK意境
 * @ClassName : EventBusServer
 * @date : 2023/10/8 21:14
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class EventBusServer extends AbstractVerticle {

    public static void main(String[] args) {

        Vertx.vertx().deployVerticle(new EventBusServer());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        // event bus 地址
        PermittedOptions permittedOptions = new PermittedOptions()
            .setAddress("chatroom");

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        SockJSBridgeOptions bridgeOptions = new SockJSBridgeOptions()
            .addInboundPermitted(permittedOptions)
            .addOutboundPermitted(permittedOptions);

        router.route("/chat/*")
            .subRouter(sockJSHandler.bridge(bridgeOptions));
        // 返回静态页面
        router.route().handler(StaticHandler.create("static"));


        // 启动服务器，监听端口
        server.requestHandler(router).listen(8080);
    }
}
