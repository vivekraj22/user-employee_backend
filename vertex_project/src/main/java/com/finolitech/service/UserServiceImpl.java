//-------------------------------------------------using promise and future----------------------------------------------
package com.finolitech.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import com.finolitech.model.Gender;
import com.finolitech.model.Status;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class UserServiceImpl extends AbstractVerticle {

    private PgPool pool;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("finolitech")
                .setUser("postgres")
                .setPassword("admin");

        // Pool options
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        pool = PgPool.pool(vertx, connectOptions, poolOptions);

        vertx.eventBus().consumer("eventBus1.add", this::eventBus1Handler);

        startPromise.complete();

    }

    private Future<PgConnection> placingConnection() {
        Promise<PgConnection> promise = Promise.promise();
        pool.getConnection(res -> {
            if (res.succeeded()) {
                promise.complete((PgConnection) res.result());
            } else {
                promise.fail(res.cause());
            }
        });
        return promise.future();
    }

    public <T> void eventBus1Handler(Message<T> message) {
        JsonObject requestBody = (JsonObject) message.body();
        String type = requestBody.getString("type");
        if (type != null) {
            switch (type) {
                case "create":
                    createUserMethod(message);
                    break;
                case "getall":
                    getAllUserMethod(message);
                    break;
                case "createEmployee":
                    createEmployeeMethod(message);
                    break;
                case "getallEmployee":
                    getAllEmployeeMethod(message);
                    break;
                case "updateEmployee":
                    updateCompleteEmployeeMethod(message);
                    break;
            }
        }

    }

    public <T> void updateCompleteEmployeeMethod(Message<T> message) {
        JsonObject input = (JsonObject) message.body();
        JsonObject requestJson = input.getJsonObject("inputJson");

        Integer empid = requestJson.getInteger("empid");
        Integer userid = requestJson.getInteger("userid");
        String role = requestJson.getString("role");
        Integer salary = requestJson.getInteger("salary");
        String name = requestJson.getString("name");
        String email = requestJson.getString("email");
        String gender = requestJson.getString("gender");
        String status = requestJson.getString("status");
        String timestamp = requestJson.getString("timestamp");

        placingConnection().compose(connection -> {
            Promise<Void> promise = Promise.promise();

            // Begin the transaction
            connection.begin()
                    .onSuccess(transaction -> {
                        Future<Void> updateEmployeeFuture = updateEmployeeMethod(message, connection);
                        Future<Void> updateUserFuture = updateUserMethod(message, connection);

                        CompositeFuture.join(updateUserFuture, updateEmployeeFuture)
                                .onSuccess(result -> {
                                    // Commit the transaction if all updates succeed
                                    transaction.commit()
                                            .onSuccess(commitResult -> {
                                                JsonObject responseJson = new JsonObject()
                                                        .put("empid", empid)
                                                        .put("userid", userid)
                                                        .put("name", name)
                                                        .put("role", role)
                                                        .put("salary", salary)
                                                        .put("email", email)
                                                        .put("gender", gender)
                                                        .put("status", status)
                                                        .put("timestamp", timestamp);

                                                promise.complete();
                                                connection.close();
                                                message.reply(responseJson);
                                            })
                                            .onFailure(commitError -> {
                                                // Rollback the transaction in case of commit failure
                                                transaction.rollback()
                                                        .onSuccess(rollbackResult -> {
                                                            JsonObject errObj = new JsonObject().put("error",
                                                                    "Commit failed. Transaction rolled back.");
                                                            message.reply(errObj);
                                                            connection.close();
                                                            promise.fail("Commit failed");
                                                        })
                                                        .onFailure(rollbackError -> {
                                                            JsonObject errObj = new JsonObject().put("error",
                                                                    "Commit failed. Rollback failed.");
                                                            message.reply(errObj);
                                                            promise.fail("Commit failed. Rollback failed");
                                                        });
                                            });
                                })
                                .onFailure(updateError -> {
                                    // Rollback the transaction if any of the updates fail
                                    transaction.rollback().onSuccess(rollbackResult -> {
                                        JsonObject errObj = new JsonObject().put("error",
                                                "One of the updates failed. Transaction rolled back.");
                                        connection.close();
                                        message.reply(errObj);
                                        promise.fail("One of the updates failed");
                                    })
                                            .onFailure(rollbackError -> {
                                                System.out.println(rollbackError + " " + 238);
                                                JsonObject errObj = new JsonObject().put("error",
                                                        "One of the updates failed. Rollback failed.");
                                                message.reply(errObj);
                                                promise.fail("One of the updates failed. Rollback failed");
                                            });
                                });
                    })
                    .onFailure(beginError -> {
                        JsonObject errObj = new JsonObject().put("error", "Failed to begin the transaction.");
                        message.reply(errObj);
                        promise.fail("Failed to begin the transaction");
                    });

            return promise.future();
        }).onComplete(result -> {
            if (result.succeeded()) {
                JsonObject responseJson = new JsonObject().put("result", "success");
                message.reply(responseJson);
            } else {
                JsonObject errObj = new JsonObject().put("error", "Failed to update employee and user.");
                message.reply(errObj);
            }
        });
    }

    public <T> Future<Void> updateUserMethod(Message<T> tMessage, PgConnection connection) {
        Promise<Void> promise = Promise.promise();

        JsonObject input = (JsonObject) tMessage.body();
        JsonObject requestJson = input.getJsonObject("inputJson");

        Integer id = requestJson.getInteger("userid");

        Promise<Integer> selectPromise = Promise.promise();

        String selectSql = "SELECT * FROM user_info WHERE id = $1";
        Tuple selectParam = Tuple.of(id);

        connection.preparedQuery(selectSql).execute(selectParam, selectRes -> {
            if (selectRes.succeeded()) {
                if (selectRes.result().rowCount() > 0) {

                    String updateSql = "UPDATE user_info SET name = $1, email = $2, gender = $3, status = $4, timestamp = $5 WHERE id = $6";
                    Tuple updateParams = Tuple.of(
                            requestJson.getString("name"),
                            requestJson.getString("email"),
                            Gender.valueOf(requestJson.getString("gender").toUpperCase()),
                            Status.valueOf(requestJson.getString("status").toUpperCase()),
                            requestJson.getString("timestamp"),
                            id);

                    connection.preparedQuery(updateSql).execute(updateParams, updateRes -> {
                        if (updateRes.succeeded()) {
                            selectPromise.complete(id);
                        } else {
                            selectPromise.fail("Update query is not working");
                        }
                    });
                } else {
                    selectPromise.fail("Provided userID does not exist");
                }
            } else {
                selectPromise.fail("Connection is not established");
            }
        });

        selectPromise.future().onSuccess(userId -> {
            JsonObject responseJson = new JsonObject()
                    .put("id", id)
                    .put("name", requestJson.getString("name"))
                    .put("email", requestJson.getString("email"))
                    .put("gender", requestJson.getString("gender"))
                    .put("status", requestJson.getString("status"))
                    .put("timestamp", requestJson.getString("timestamp"));

            tMessage.reply(responseJson);
            promise.complete();
        }).onFailure(error -> {
            JsonObject errObj = new JsonObject();
            errObj.put("error", error.getMessage());
            tMessage.reply(errObj);
            promise.fail(error.getMessage());
        });

        return promise.future();
    }

    public <T> Future<Void> updateEmployeeMethod(Message<T> tMessage, PgConnection connection) {
        Promise<Void> promise = Promise.promise();

        JsonObject input = (JsonObject) tMessage.body();
        JsonObject requestJson = input.getJsonObject("inputJson");

        Integer empid = requestJson.getInteger("empid");
        Integer userid = requestJson.getInteger("userid");
        String role = requestJson.getString("role");
        Integer salary = requestJson.getInteger("salary");

        Promise<Integer> selectPromise = Promise.promise();

        String sql = "SELECT * FROM employee_info WHERE empid = $1";
        Tuple param = Tuple.of(empid);

        connection.preparedQuery(sql).execute(param, res -> {
            if (res.succeeded()) {
                if (res.result().rowCount() > 0) {
                    String query = "UPDATE employee_info SET role = $1, salary = $2 WHERE userid = $3 AND empid = $4";
                    Tuple params = Tuple.of(role, salary, userid, empid);

                    connection.preparedQuery(query).execute(params, res1 -> {
                        if (res1.succeeded()) {
                            selectPromise.complete(empid);
                        } else {
                            selectPromise.fail("Update query is not working");
                        }
                    });
                } else {
                    selectPromise.fail("Provided empid does not exist");
                }
            } else {
                selectPromise.fail("Connection could not be established");
            }
        });

        selectPromise.future().onSuccess(empId -> {
            JsonObject obj = new JsonObject();
            obj.put("result", "success");
            tMessage.reply(obj);
            promise.complete();
        }).onFailure(error -> {
            JsonObject errObj = new JsonObject();
            errObj.put("error", error.getMessage());
            tMessage.reply(errObj);
            promise.fail(error.getMessage());
        });

        return promise.future();
    }

    public <T> void getAllEmployeeMethod(Message<T> message) {
        List<JsonObject> empList = new ArrayList<>();
        placingConnection()
                .compose(connection -> connection.begin()
                        .compose(tx -> connection.query("SELECT * FROM employee_info")
                                .execute()
                                .compose(res1 -> {
                                    RowSet<Row> rows = res1.value();
                                    for (Row row : rows) {
                                        Integer empid = row.getInteger("empid");
                                        Integer userid = row.getInteger("userid");
                                        String role = row.getString("role");
                                        Integer salary = row.getInteger("salary");
                                        Tuple param = Tuple.of(userid);
                                        connection.preparedQuery("SELECT * FROM user_info where id=$1").execute(param,
                                                res2 -> {
                                                    if (res2.succeeded()) {

                                                        RowSet<Row> rowse = res2.result();
                                                        for (Row row_user : rowse) {
                                                            JsonObject userJson = new JsonObject()
                                                                    .put("empid", empid)
                                                                    .put("userid", userid)
                                                                    .put("name", row_user.getString("name"))
                                                                    .put("email", row_user.getString("email"))
                                                                    .put("gender", row_user.getString("gender"))
                                                                    .put("status", row_user.getString("status"))
                                                                    .put("role", role)
                                                                    .put("salary", salary)
                                                                    .put("timestamp", row_user.getString("timestamp"));

                                                            empList.add(userJson);
                                                        }
                                                    } else {
                                                        JsonObject errObj = new JsonObject().put("error",
                                                                "user info extraction error");
                                                        message.reply(errObj);
                                                    }
                                                });
                                    }
                                    return tx.commit()
                                            .compose(res4 -> {
                                                connection.close();
                                                return Future.succeededFuture();
                                            });
                                }))
                        .onSuccess(rows -> {
                            JsonObject responseJson = new JsonObject().put("employees", empList);
                            message.reply(responseJson);
                        })
                        .onFailure(error -> {
                            JsonObject errObj = new JsonObject().put("error", error.getMessage());
                            message.reply(errObj);
                        }));
    }

    public <T> void createEmployeeMethod(Message<T> message) {

        JsonObject input = (JsonObject) message.body();
        JsonObject userJson = input.getJsonObject("inputJson");
        // extracting employee details
        int userid = userJson.getInteger("userId");
        String role = userJson.getString("role");
        int salary = userJson.getInteger("salary");

        placingConnection()
                .compose(connection -> connection.begin()
                        .compose(tx -> connection.preparedQuery("SELECT * FROM user_info where id=$1")
                                .execute(Tuple.of(userid))
                                .compose(res1 -> {
                                    RowSet<Row> rows = res1;
                                    System.out.println("table: " + res1.columnsNames());

                                    if (!rows.iterator().hasNext()) {
                                        // User does not exist
                                        JsonObject errObj = new JsonObject();
                                        errObj.put("error", "User does not exists");
                                        message.reply(errObj);
                                        return Future.failedFuture("User already exists");
                                    }

                                    // User exist
                                    return connection
                                            .preparedQuery(
                                                    "INSERT INTO employee_info (userid, role, salary) VALUES ($1, $2, $3)")
                                            .execute(Tuple.of(userid, role, salary))
                                            .compose(res2 -> {
                                                if (res2.rowCount() != 1) {
                                                    // Error occurred while inserting the employee record
                                                    JsonObject errObj = new JsonObject();
                                                    errObj.put("error", "Failed to insert employee record");
                                                    message.reply(errObj);
                                                    return Future.failedFuture("Failed to insert employee record");
                                                }

                                                return connection
                                                        .preparedQuery(
                                                                "SELECT empId FROM employee_info WHERE userid = $1")
                                                        .execute(Tuple.of(userid))
                                                        .compose(res3 -> {
                                                            if (res3.rowCount() != 1) {
                                                                // Error occurred while retrieving empId
                                                                JsonObject errObj = new JsonObject();
                                                                errObj.put("error", "Failed to retrieve empId");
                                                                message.reply(errObj);
                                                                return Future.failedFuture("Failed to retrieve empId");
                                                            }

                                                            // Process completed successfully
                                                            return tx.commit();
                                                        });
                                            })
                                            .compose(res4 -> {
                                                connection.close();
                                                return Future.succeededFuture();
                                            });
                                })))
                .onSuccess(s -> {
                    JsonObject sObj = new JsonObject();
                    sObj.put("result", "success");
                    message.reply(sObj);
                })
                .onFailure(error -> {
                    JsonObject errObj = new JsonObject();
                    errObj.put("error", error.getMessage());
                    message.reply(errObj);
                });

    }

    public <T> void getAllUserMethod(Message<T> message) {
        placingConnection().compose(connection -> {
            Promise<RowSet<Row>> promise = Promise.promise();

            String sql = "SELECT id, name, email, gender, status, timestamp FROM user_info";

            connection.query(sql).execute(res -> {
                if (res.succeeded()) {
                    RowSet<Row> rows = res.result();
                    promise.complete(rows);
                } else {
                    promise.fail(res.cause());
                }

                // Close the database connection
                connection.close();
            });

            return promise.future();
        }).onSuccess(rows -> {
            List<JsonObject> usersList = new ArrayList<>();

            for (Row row : rows) {
                Integer id = row.getInteger("id");
                String name = row.getString("name");
                String email = row.getString("email");
                String gender = row.getString("gender");
                String status = row.getString("status");
                String timestamp = row.getString("timestamp");

                JsonObject userJson = new JsonObject()
                        .put("id", id)
                        .put("name", name)
                        .put("email", email)
                        .put("gender", gender)
                        .put("status", status)
                        .put("timestamp", timestamp);

                usersList.add(userJson);
            }

            JsonObject responseJson = new JsonObject().put("users", usersList);
            message.reply(responseJson);
        }).onFailure(error -> {
            JsonObject errObj = new JsonObject().put("error", error.getMessage());
            message.reply(errObj);
        });
    }

    public <T> void createUserMethod(Message<T> tMessage) {

        JsonObject input = (JsonObject) tMessage.body();
        JsonObject userJson = input.getJsonObject("inputJson");

        if (userJson.size() != 5) {
            JsonObject errObj1 = new JsonObject();
            errObj1.put("error", "Data provided is not correct");
            tMessage.reply(errObj1);
            return;
        } else {

            // extracting the user properties from the JsonObject
            String name = userJson.getString("name");
            String email = userJson.getString("email");
            String gen = userJson.getString("gender").toUpperCase();
            Gender gender = Gender.valueOf(gen);
            String sta = userJson.getString("status").toUpperCase();
            Status status = Status.valueOf(sta);
            String timestamp = userJson.getString("timestamp");

            // inserting the new user into the database
            placingConnection().compose(connection -> {
                Promise<Integer> promise = Promise.promise();

                String sql = "INSERT INTO user_info (name, email, gender, status, timestamp) VALUES ($1, $2, $3, $4, $5)";

                Tuple params = Tuple.of(name, email, gender.toString(), status.toString(), timestamp);

                connection.preparedQuery(sql).execute(params, res2 -> {
                    if (res2.succeeded()) {
                        if (res2.result().rowCount() > 0) {
                            String selectSql = "SELECT id FROM user_info ORDER BY id DESC LIMIT 1";
                            connection.query(selectSql).execute(res3 -> {
                                if (res3.succeeded()) {
                                    RowSet<Row> rows = res3.result();
                                    if (rows.iterator().hasNext()) {
                                        Integer userId = rows.iterator().next().getInteger("id");
                                        promise.complete(userId);
                                    } else {
                                        promise.fail("Failed to retrieve generated ID");
                                    }
                                } else {
                                    promise.fail(res3.cause());
                                }
                            });
                        } else {
                            promise.fail("No rows were affected by the INSERT query");
                        }
                    } else {
                        promise.fail(res2.cause());
                    }

                    // Close the database connection
                    connection.close();
                });

                return promise.future();
            }).onSuccess(userId -> {
                JsonObject responseJson = new JsonObject()
                        .put("id", userId)
                        .put("name", name)
                        .put("email", email)
                        .put("gender", gender.toString())
                        .put("status", status.toString())
                        .put("timestamp", timestamp);

                tMessage.reply(responseJson);
            }).onFailure(error -> {
                JsonObject errObj = new JsonObject();
                errObj.put("error", error.getMessage());
                tMessage.reply(errObj);
            });
        }
    }

}