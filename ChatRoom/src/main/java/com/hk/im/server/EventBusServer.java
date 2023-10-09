package com.hk.im.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class EventBusServer extends AbstractVerticle {

    public static Object LOCK1 = new Object();

    public static Object LOCK2 = new Object();


    public static void main(String[] args) throws InterruptedException {

        Vertx.vertx().deployVerticle(new EventBusServer());
        // 启动死锁
        deadlockTest();
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
            .handler(ctx -> {
                RequestBody body = ctx.body();
                JsonObject entries = body.asJsonObject();
                log.info("chat room received message:{}", entries);
            })
            .subRouter(sockJSHandler.bridge(bridgeOptions));
        // 返回静态页面
        router.route().handler(StaticHandler.create("static"));


        // 启动服务器，监听端口
        server.requestHandler(router).listen(8080);
    }



    public static void deadlockTest() throws InterruptedException {

        Thread threadA = new Thread(() -> {

            synchronized (LOCK1) {
                System.out.println("已经获取锁1");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
                synchronized (LOCK2) {
                    System.out.println("已经获取锁2");
                }
            }

        }, "deadlock-test-thread-0");


        Thread threadB = new Thread(() -> {
            synchronized (LOCK2) {
                System.out.println("已经获取锁1");
                synchronized (LOCK1) {
                    System.out.println("已经获取锁2");
                }
            }
        }, "deadlock-test-thread-1");

        // 启动线程
        threadA.start();
        Thread.sleep(1000);
        threadB.start();

    }

}
