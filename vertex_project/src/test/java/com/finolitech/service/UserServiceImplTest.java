package com.finolitech.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class UserServiceImplTest {

    @Test
    void testCreateUserMethod(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("name", "aman");
        jsonObject.put("email", "aman@mail.com");
        jsonObject.put("gender", "male");
        jsonObject.put("status", "active");
        jsonObject.put("timestamp", "12-06-2023");

        // Perform the HTTP request
        Future<HttpResponse<Buffer>> tableData = client.post(8888, "localhost", "/users")
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(jsonObject);

        // Verify the response
        tableData.onComplete(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                testContext.verify(() -> {
                    Assertions.assertEquals(201, response.statusCode());
                    JsonObject responseJson = response.bodyAsJsonObject();
                    Assertions.assertEquals(35, responseJson.getInteger("id"));
                    Assertions.assertEquals("aman", responseJson.getString("name"));
                    Assertions.assertEquals("aman@mail.com", responseJson.getString("email"));
                    Assertions.assertEquals("MALE", responseJson.getString("gender"));
                    Assertions.assertEquals("ACTIVE", responseJson.getString("status"));
                    Assertions.assertEquals("12-06-2023", responseJson.getString("timestamp"));
                });
            } else {
                testContext.failNow(ar.cause());
            }
            testContext.completeNow();
        });
    }

    @Test
    void testGetAllUserMethod(Vertx vertx, VertxTestContext testContext) {

        WebClient client = WebClient.create(vertx);

        JsonObject jsonObject = new JsonObject();

        Future<HttpResponse<Buffer>> tableData = client.get(8888, "localhost", "/api/users")
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(jsonObject);

        tableData.onComplete(res -> {
            if (res.succeeded()) {
                HttpResponse<Buffer> response = res.result();
                testContext.verify(() -> {
                    Assertions.assertEquals(201, response.statusCode());
                    JsonObject responseJson = response.bodyAsJsonObject();
                    JsonArray usersArray = responseJson.getJsonArray("users");
                    Assertions.assertEquals(35, usersArray.size());
                });
            } else {
                testContext.failNow(res.cause());
            }
            testContext.completeNow();
        });
    }

    @Test
    void testUpdateEmployeeMethod(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("empid", 1);
        jsonObject.put("userid", 2);
        jsonObject.put("name", "vivek raj");
        jsonObject.put("email", "vivekraj@mail.com");
        jsonObject.put("gender", "male");
        jsonObject.put("status", "active");
        jsonObject.put("role", "backend developer");
        jsonObject.put("salary", 100000);
        jsonObject.put("timestamp", "12-06-2023");

        // Perform the HTTP request
        Future<HttpResponse<Buffer>> tableData = client.put(8888, "localhost", "/api/employee")
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(jsonObject);

        // Verify the response
        tableData.onComplete(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                testContext.verify(() -> {
                    Assertions.assertEquals(201, response.statusCode());
                    JsonObject responseJson = response.bodyAsJsonObject();
                    Assertions.assertEquals(responseJson.getString("result"), "success");
                });
            } else {
                testContext.failNow(ar.cause());
            }
            testContext.completeNow();
        });
    }

}
