package com.hk.im.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HK意境
 * @ClassName : Server
 * @date : 2023/10/8 20:20
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
public class Server extends AbstractVerticle {


    public static void main(String[] args) {

        Vertx.vertx().deployVerticle(new Server());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/vert.x").handler(ctx -> {

            // This handler will be called for every request
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello World from Vert.x-Web!");
        });

        router.route("/name").handler(ctx -> {
            // This handler will be called for every request
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello /name!");
        });

        router.route("/hello").handler(ctx -> {
            HttpServerRequest request = ctx.request();
            log.info("request parameter:{}", request.params());

            // 获取请求参数
            String name = request.getParam("name");
            log.info("name parameter:{}", name);

            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end("hello,  "+ name);
        });

        // 返回静态页面
        router.route().handler(StaticHandler.create("static"));


        /*router.route().handler(ctx -> {
            // This handler will be called for every request
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("default Vert.x-Web!");
        });*/

        // 启动服务器，监听端口
        server.requestHandler(router).listen(8080);

    }
}
