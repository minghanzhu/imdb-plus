# Description

**User**

`GET /user/{id}`
* Description: This endpoint retrieves the user that matches the specified `userId`.
* Sample request: `http://localhost:8083/user/9f26fbec-a289-48cb-a569-04e39f6b187f`


`POST /user/`
* Description: This endpoint receives the user data for a new user to be created and retuns the user data plus the DynamoDB auto-generated userId and accessToken.
* Sample request: `http://localhost:8083/user`
* Sample request body:
```
    {
        "username": "newUser",
        "email": "newUser@on.com",
        "accountSetting": {
            "isPrivate": true,
            "isAdult": true
            }
    }
```


`PUT /user/{id}`
* Description: This endpoint updates the user data for an existing user with `userId` equal to `id`. 
* Sample request: `http://localhost:8083/user/9f26fbec-a289-48cb-a569-04e39f6b187f`
* Sample request body:
```
    {
        "username": "newUser",
        "email": "newUser@on.com",
        "accountSetting": {
            "isPrivate": true,
            "isAdult": true
        }
    }
```


`DELETE /user/{id}`
* Description: This endpoint deletes the user data for an existing user with `userId` equal to `id`. (Requied a header that has `"Authorization"` as key and user `accessToken` as value).
* Sample request: `http://localhost:8083/user/9f26fbec-a289-48cb-a569-04e39f6b187f`



**Timeline**

`GET /timeline/user/{userId}`
* Description: This endpoint retrieves the timeline for the user whose `userId` matches the given `userId`.
* Sample request: `http://localhost:8083/timeline/user/9f26fbec-a289-48cb-a569-04e39f6b187f`


`GET /timeline/media/{mediaId}`
* Description: This endpoint retrieves the timeline for the media whose `mediaId` matches the given `mediaId`.
* Sample request: `http://localhost:8083/timeline/media/tt0000001`


`GET /timeline/{userId}/{mediaId}`
* Description: This endpoint retrieves the timeline for the media whose `mediaId` matches the given `mediaId` as well as the user whose `userId` matched `userId`. 
* Sample request: `http://localhost:8083/timeline/9f26fbec-a289-48cb-a569-04e39f6b187f/tt0000001`


`POST /timeline/`
* Description: This endpoint receives the timeline data for a new timeline to be created. (Requied a header that has `"Authorization"` as key and user `accessToken` as value)
* Sample request: `http://localhost:8083/timeline`
* Sample request body:
```
{
    "timelineId": "9c84d4c4-609e-4db3-b95d-976673985e23-tt0000001",
    "userId": "9c84d4c4-609e-4db3-b95d-976673985e23",
    "mediaId": "tt0000001",
    "status": "DONE",
    "rating": 1,
    "comment": "it was horrible"
}
```


`DELETE /timeline/{userId}/{mediaId}`
* Description: This endpoint deletes the timeline data for an existing timeline with user with specified `userId` and `mediaId`. (Requied a header that has `"Authorization"` as key and user `accessToken` as value)
* Sample request: 
`http://localhost:8083/timeline/9f26fbec-a289-48cb-a569-04e39f6b187f/tt0000001`



# How Has This Been Tested?

All the functional-ready endpoints are in end-to-end tested in the following tests in `ImdbPlusApplicationTests.java` file. Please clone the project and load it in IntelliJ IDEA.

To run all the tests, please 

- `testUserSave()` tests the user `POST` save endpoint with a single test user. The expected result is that the test user is added to the database.
- `testUserSaveDuplicatedUsername()` tests the user save `POST` endpoint with duplicate usernames. The expected behavior is that the second test user with the same username should not be added to the database.
- `testTimelineSave()` tests the timeline `POST` save endpoint with a single test user and a single test timeline. The expected behavior is that the test timeline is added to the database.
- `testTimelineGetByUserId()` tests the timeline `GET` endpoint with a single test user and multiple test timelines. The expected behavior is that all test timelines can be retrieved from the database by `userId`.
- `testTimelineGetByMediaId()` tests the timeline `GET` endpoint with multiple test users add timelines for the same media (`mediaId`). For example, `user1` adds a timeline for `mediaId` `tt0000001`, and `user2` also adds a timeline for `mediaId` `tt0000001`. The expected behavior is that both test timelines can be retrieved from the database by `mediaId`.
- `testTimelineGetTimelineByUserIdAndMediaId()` tests the timeline `GET` endpoint that retrieves a single timeline item by a `userId` and a `mediaId`.

**Test Configuration**:
* Firmware version: Spring Boot 2.7.5
* Hardware: Apple M1 Pro
* Toolchain: IntelliJ IDEA
* SDK: JDK 11 OpenJDK
* Style checker: Google Java Style Guide
* Database: Amazon DynamoDB