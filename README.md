# Description

**Client**

`GET /client/{id}`

* Description: This endpoint retrieves the client that matches the specified `clientId`.
* Sample request: `http://localhost:8083/client/9f26fbec-a289-48cb-a569-04e39f6b187f`

`POST /client/`

* Description: This endpoint receives the client data for a new client to be created and returns the
  added client item plus the DynamoDB auto-generated `clientId` and `accessToken` on successful
  operation.
* Sample request: `http://localhost:8083/client`
* Sample request body:

```
    {
        "clientname": "newClient",
        "email": "newClient@on.com",
        "accountSetting": {
            "isPrivate": true,
            "isAdult": true
            }
    }
```

`PUT /client/{id}`

* Description: This endpoint updates the client data for an existing client with `clientId` equal to `id`.
* Sample request: `http://localhost:8083/client/9f26fbec-a289-48cb-a569-04e39f6b187f`
* Sample request body:

```
    {
        "clientname": "newClient",
        "email": "newClient@on.com",
        "accountSetting": {
            "isPrivate": true,
            "isAdult": true
        }
    }
```

`DELETE /client/{id}`

* Description: This endpoint deletes the client data for an existing client with `clientId` equal to `id`
  . (Requied a header that has `"Authorization"` as key and client `accessToken` as value).
* Sample request: `http://localhost:8083/client/9f26fbec-a289-48cb-a569-04e39f6b187f`

**Timeline**

`GET /timeline/client/{clientId}`

* Description: This endpoint retrieves the timeline for the client whose `clientId` matches the
  given `clientId`.
* Sample request: `http://localhost:8083/timeline/client/9f26fbec-a289-48cb-a569-04e39f6b187f`

`GET /timeline/media/{mediaId}`

* Description: This endpoint retrieves the timeline for the media whose `mediaId` matches the
  given `mediaId`.
* Sample request: `http://localhost:8083/timeline/media/tt0000001`

`GET /timeline/{clientId}/{mediaId}`

* Description: This endpoint retrieves the timeline for the media whose `mediaId` matches the
  given `mediaId` as well as the client whose `clientId` matched `clientId`.
* Sample request: `http://localhost:8083/timeline/9f26fbec-a289-48cb-a569-04e39f6b187f/tt0000001`

`POST /timeline/`

* Description: This endpoint receives the timeline data for a new timeline to be created. (Requied a
  header that has `"Authorization"` as key and client `accessToken` as value)
* Sample request: `http://localhost:8083/timeline`
* Sample request body:

```
{
    "timelineId": "9c84d4c4-609e-4db3-b95d-976673985e23-tt0000001",
    "clientId": "9c84d4c4-609e-4db3-b95d-976673985e23",
    "mediaId": "tt0000001",
    "status": "DONE",
    "rating": 1,
    "comment": "it was horrible"
}
```

`DELETE /timeline/{clientId}/{mediaId}`

* Description: This endpoint deletes the timeline data for an existing timeline with client with
  specified `clientId` and `mediaId`. (Requied a header that has `"Authorization"` as key and
  client `accessToken` as value)
* Sample request:
  `http://localhost:8083/timeline/9f26fbec-a289-48cb-a569-04e39f6b187f/tt0000001`

# How Has This Been Tested?

All the functional-ready endpoints are end-to-end tested in the following tests
in `ImdbPlusApplicationTests.java` file. Please clone the project and load it in IntelliJ IDEA.

To run all the tests in IntelliJ IDEA: Replace the empty string in `DynamoDBConfig.java` with your
access key and secret key. Then run the main method in `ImdbPlusApplication.java` file. Then run the
test file `ImdbPlusApplicationTests.java`.

- `testClientSave()` tests the client `POST` save endpoint with a single test client. The expected result
  is that the test client is added to the database.
- `testClientSaveDuplicatedClientname()` tests the client save `POST` endpoint with duplicate clientnames.
  The expected behavior is that the second test client with the same clientname should not be added to
  the database.
- `testTimelineSave()` tests the timeline `POST` save endpoint with a single test client and a single
  test timeline. The expected behavior is that the test timeline is added to the database.
- `testTimelineGetByClientId()` tests the timeline `GET` endpoint with a single test client and multiple
  test timelines. The expected behavior is that all test timelines can be retrieved from the
  database by `clientId`.
- `testTimelineGetByMediaId()` tests the timeline `GET` endpoint with multiple test clients add
  timelines for the same media (`mediaId`). For example, `client1` adds a timeline
  for `mediaId` `tt0000001`, and `client2` also adds a timeline for `mediaId` `tt0000001`. The
  expected behavior is that both test timelines can be retrieved from the database by `mediaId`.
- `testTimelineGetTimelineByClientIdAndMediaId()` tests the timeline `GET` endpoint that retrieves a
  single timeline item by a `clientId` and a `mediaId`.

**Test Configuration**:

* Firmware version: Spring Boot 2.7.5
* Hardware: Apple M1 Pro
* Toolchain: IntelliJ IDEA
* SDK: JDK 11 OpenJDK
* Database: Amazon DynamoDB
* Style
  checker: [Google Java Style Guide](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) (
  using [CheckStyle-IDEA plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)):
  previous style check run
  result screenshots can be found [here](https://ibb.co/Hgv6gb8)
  and [here](https://ibb.co/xqdSpdP)
* Test framework: JUnit Vintage Engine