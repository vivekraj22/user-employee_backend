package com.finolitech;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import com.finolitech.service.UserServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new UserServiceImpl(), ar -> {
      if (ar.succeeded()) {
        System.out.println("Verticle deployed successfully");
      } else {
        System.out.println("Failed to deploy verticle: " + ar.cause());
      }
    });

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    // Get the event bus instance
    EventBus eventBus1 = vertx.eventBus();

    // adding a route for POST requests to create a new user
    router.post("/api/users").handler(BodyHandler.create())
        .handler(routingContext -> myEventBus(routingContext, eventBus1, "create"));

    // adding a route to handle GET requests to retrieve all users
    router.get("/api/users")
        .handler(routingContext -> myEventBus(routingContext, eventBus1, "getall"));

    // adding a route to handle PUT requests to update a user
    router.put("/api/users").handler(BodyHandler.create())
        .handler(routingContext -> myEventBus(routingContext, eventBus1, "update"));

    // adding a route for POST requests to create a new employee
    router.post("/employee").handler(BodyHandler.create())
        .handler(routingContext -> myEventBus(routingContext, eventBus1, "createEmployee"));

    // adding a route to handle GET requests to retrieve all employee
    router.get("/api/employee")
        .handler(routingContext -> myEventBus(routingContext, eventBus1, "getallEmployee"));

    // adding a route to handle PUT requests to update a employee
    router.put("/api/employee").handler(BodyHandler.create())
        .handler(routingContext -> myEventBus(routingContext, eventBus1, "updateEmployee"));

    // starting the server
    server.requestHandler(router).listen(8888, asyncResult -> {
      if (asyncResult.succeeded()) {
        System.out.println("Server started on port 8888");
        System.out.println("inside starting.....");
      } else {
        System.out.println("Failed to start server: " + asyncResult.cause());
      }
    });
  }

  private static void myEventBus(RoutingContext routingContext, EventBus eventBus1, String types) {
    System.out.println(routingContext);
    JsonObject userJson;
    if (routingContext.getBodyAsString() == null) {

      String type = types;

      userJson = new JsonObject();
      JsonObject requestBodyJson = new JsonObject();

      userJson.put("type", type);
      userJson.put("inputJson", requestBodyJson);

    } else {
      Buffer requestBody = routingContext.getBody();
      System.out.println("routingcontext" + " " + routingContext.getBodyAsString());
      System.out.println("requestbody" + " " + requestBody);
      String type = types;
      try {
        userJson = new JsonObject();
        JsonObject requestBodyJson = requestBody.toJsonObject();

        userJson.put("type", type);
        userJson.put("inputJson", requestBodyJson);
      } catch (DecodeException e) {
        routingContext.response()
            .setStatusCode(400)
            .end("Bad Request: Invalid user data");
        return;
      }
    }
    eventBus1.request("eventBus1.add", userJson, reply -> {
      if (reply.succeeded()) {
        JsonObject jObject = (JsonObject) reply.result().body();
        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json")
            .end(jObject.encode());
      } else {
        routingContext.response()
            .setStatusCode(500)
            .end("Failed to create user: " + reply.cause().getMessage());
      }
    });

  }

}
