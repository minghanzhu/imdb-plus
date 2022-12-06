# Description

**User**

`GET /user/{id}`
* Description: This endpoint retrieves the user that matches the specified `userId`.
* Sample request: `http://localhost:8083/user/b4bd0ba7-3b8e-4634-ae69-9e487909eab1
* Sample response:
```
{
    "userId": "b4bd0ba7-3b8e-4634-ae69-9e487909eab1",
    "accessToken": "6f4ac569-3b27-4362-8103-2bf4fc2b0ba9",
    "username": "sraipura",
    "email": "sr3962@columbia.edu",
    "accountSetting": {
        "isPrivate": true,
        "isAdult": true
    }
}
```


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
Sample response:
```
{
    "userId": "0d3cee16-3cb7-407a-a738-0212686c84d8",
    "accessToken": "b0b1e22a-93bf-40f2-b46c-029375a35b49",
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
* Sample request: `http://localhost:8083/user/0d3cee16-3cb7-407a-a738-0212686c84d8`
* Sample request body:
```
    {
        "username": "newUser1",
        "email": "newUser@on.com",
        "accountSetting": {
            "isPrivate": true,
            "isAdult": true
        }
    }
```


`DELETE /user/{id}`
* Description: This endpoint deletes the user data for an existing user with `userId` equal to `id`. (Requied a header that has `"Authorization"` as key and user `accessToken` as value).
* Sample request: `http://localhost:8083/user/0d3cee16-3cb7-407a-a738-0212686c84d8`
* Sample response:
  `User deleted successfully`



**Timeline**

`GET /timeline/user/{userId}`
* Description: This endpoint retrieves the timeline for the user whose `userId` matches the given `userId`.
* Sample request: `http://localhost:8083/timeline/user/9f26fbec-a289-48cb-a569-04e39f6b187f`
* Sample response:
```
[
    {
        "timelineId": "c0cba403-b9c8-4181-9e7c-be9ab80a67c3-tt0000019",
        "userId": "c0cba403-b9c8-4181-9e7c-be9ab80a67c3",
        "mediaId": "tt0000019",
        "creationTime": "2022-12-05T18:08:34.778+00:00",
        "lastUpdate": "2022-12-05T18:08:34.778+00:00",
        "status": "DONE",
        "rating": 4,
        "comment": "It was good"
    },
    {
        "timelineId": "c0cba403-b9c8-4181-9e7c-be9ab80a67c3-tt0000003",
        "userId": "c0cba403-b9c8-4181-9e7c-be9ab80a67c3",
        "mediaId": "tt0000003",
        "creationTime": "2022-12-05T18:08:34.629+00:00",
        "lastUpdate": "2022-12-05T18:08:34.629+00:00",
        "status": "IN_PROGRESS",
        "rating": null,
        "comment": null
    }
]
```


`GET /timeline/media/{mediaId}`
* Description: This endpoint retrieves the timeline for the media whose `mediaId` matches the given `mediaId`.
* Sample request: `http://localhost:8083/timeline/media/tt0000001`
* Sample response:
```
[
    {
        "timelineId": "b4bd0ba7-3b8e-4634-ae69-9e487909eab1-tt0000011",
        "userId": "b4bd0ba7-3b8e-4634-ae69-9e487909eab1",
        "mediaId": "tt0000011",
        "creationTime": "2022-12-05T18:08:34.110+00:00",
        "lastUpdate": "2022-12-05T18:08:34.110+00:00",
        "status": "WISHLIST",
        "rating": null,
        "comment": null
    },
    {
        "timelineId": "fc3bb87d-34be-4053-820f-df581b468b1e-tt0000011",
        "userId": "fc3bb87d-34be-4053-820f-df581b468b1e",
        "mediaId": "tt0000011",
        "creationTime": "2022-12-05T18:08:34.930+00:00",
        "lastUpdate": "2022-12-05T18:08:34.930+00:00",
        "status": "WISHLIST",
        "rating": null,
        "comment": null
    }
```


`GET /timeline/{userId}/{mediaId}`
* Description: This endpoint retrieves the timeline for the media whose `mediaId` matches the given `mediaId` as well as the user whose `userId` matched `userId`.
* Sample request: `http://localhost:8083/timeline/9f26fbec-a289-48cb-a569-04e39f6b187f/tt0000001`


`POST /timeline/`
* Description: This endpoint receives the timeline data for a new timeline to be created. (Requied a header that has `"Authorization"` as key and user `accessToken` as value)
* Sample request: `http://localhost:8083/timeline`
* Sample request body:
```
{
    "timelineId": "b4bd0ba7-3b8e-4634-ae69-9e487909eab1-tt00000026",
    "userId": "b4bd0ba7-3b8e-4634-ae69-9e487909eab1",
    "mediaId": "tt00000026",
    "status": "DONE",
    "rating": 1,
    "comment": "it was horrible"
}
```


`DELETE /timeline/{userId}/{mediaId}`
* Description: This endpoint deletes the timeline data for an existing timeline with user with specified `userId` and `mediaId`. (Requied a header that has `"Authorization"` as key and user `accessToken` as value)
* Sample request:
  `http://localhost:8083/timeline/9f26fbec-a289-48cb-a569-04e39f6b187f/tt0000001`


**Analysis**

`GET /api/v1/analysis/highest-rated`
* Description: This endpoint retrieves the highest rated media based on the user timelines so far.
* Sample request: `http://localhost:8083/api/v1/analysis/highest-rated`
* Sample response:
```
{
    "mediaId": "tt0000001",
    "title": "The Shawshank Redemption",
    "release_date": "1994-09-23",
    "genre": "Drama"
}
```


`GET /api/v1/analysis/most/?status=IN_PROGRESS`
* Description: This endpoint retrieves the most watched/in progress or wishlisted media based on the user timelines so far.
* Sample request: `http://localhost:8083/api/v1/analysis/most/?status=IN_PROGRESS`
* Sample response:
```
{
    "mediaId": "tt0000003",
    "title": "Split",
    "release_date": "9/11/16",
    "genre": "Thriller"
}
```
* Sample request: `http://localhost:8083/api/v1/analysis/most/?status=WISHLIST`
* Sample response:
```
{
    "mediaId": "tt0000011",
    "title": "Fantastic Beasts and Where to Find Them",
    "release_date": "9/19/16",
    "genre": "Adventure"
}
```



`GET /api/v1/analysis/top-ten/`
* Description: This endpoint retrieves the top ten most watched/in progress or wishlisted media based on the user timelines so far.
* Sample request: `http://localhost:8083/api/v1/analysis/top-ten/?status=WISHLIST`
* Sample response:
```
[
    {
        "mediaId": "tt0000019",
        "title": "Lion",
        "release_date": "9/27/16",
        "genre": "Biography"
    },
    {
        "mediaId": "tt0000011",
        "title": "Fantastic Beasts and Where to Find Them",
        "release_date": "9/19/16",
        "genre": "Adventure"
    }
]
```
* Sample request: `http://localhost:8083/api/v1/analysis/top-ten/?status=DONE`
* Sample response:
```
[
    {
        "mediaId": "tt0000019",
        "title": "Lion",
        "release_date": "9/27/16",
        "genre": "Biography"
    },
    {
        "mediaId": "tt0000006",
        "title": "The Great Wall",
        "release_date": "9/14/16",
        "genre": "Action"
    },
    .....
    {
        "mediaId": "tt0000017",
        "title": "Hacksaw Ridge",
        "release_date": "9/25/16",
        "genre": "Biography"
    },
    {
        "mediaId": "tt0000022",
        "title": "Manchester by the Sea",
        "release_date": "9/30/16",
        "genre": "Drama"
    }
]
```


`GET /api/v1/analysis/userprofile/{id}`
* Description: This endpoint returns  a map of media and the user with userId `id`'s genre preference and number of media with that.
* Sample request: `http://localhost:8083/api/v1/analysis/userprofile/b4bd0ba7-3b8e-4634-ae69-9e487909eab1`
* Sample response:
```
{
    "Adventure": 1,
    "Drama": 2,
    "Comedy": 1
}
```


**Media**

`GET /api/v1/media/{id}`
* Description: This endpoint retrieves the media that matches the specified `mediaId`.
* Sample request: `http://localhost:8083/api/v1/media/tt0000001`
* Sample response
```
{
    "mediaId": "tt0000001",
    "title": "The Shawshank Redemption",
    "release_date": "1994-09-23",
    "genre": "Drama"
}
```


`POST /api/v1/media`
* Description: This endpoint receives the media data for a new media to be created and retuns the media data.
* Sample request: `http://localhost:8083/api/v1/media`
* Sample request body:
```
    {
    "mediaId": "tt00000026",
    "title": "The Shawshank Redemption2",
    "release_date": "1994-09-23",
    "genre": "Drama"
}
```
* Sample response:
```
{
    "mediaId": "tt00000026",
    "title": "The Shawshank Redemption2",
    "release_date": "1994-09-23",
    "genre": "Drama"
}
```


`PUT api/v1/media/{id}`
* Description: This endpoint updates the media data for an existing media with `mediaId` equal to `id`.
* Sample request: `http://localhost:8083/api/v1/media/tt00000026`
* Sample request body:
```
    {
    "mediaId": "tt0000001",
    "title": "The Shawshank Redemption1",
    "release_date": "1994-09-23",
    "genre": "Drama"
}
```
* Sample response:
  `tt00000026`


`DELETE /api/v1/media/{id}`
* Description: This endpoint deletes the media data for an existing user with `mediaId` equal to `id`.
* Sample request: `http://localhost:8083/api/v1/media/tt00000026`
* Sample response:
  `Media deleted successfully`



# Build Instruction

* First, make sure you have installed Maven.
    * (If you are on a Mac, you can try `mvn -v` to see what version of Maven you already have installed. Otherwise, you can run `brew install maven` to install.)
* Next, execute the following command to install all the necessary dependencies to execute imdb-plus:
  `mvn install`
* Next, run the following command to build your application with dependecy jar files:
  `mvn package`
* Next, ensure you have DynamoDB AccessKey and SecretKey:
    * Configure your database connection in:
      `org.opencsd.imdbplus.config.DynamoDBConfig`
    * Use of Enviornment variables is good idea here
* Finally, run the following command to deploy the service:
  `java -jar target/imdb-plus-<snapshot-version>.jar`


# Coverage, Bugs, Code Smells Analysis

* We used Github Actions and SonarCloud for CI/CD. We run build, tests, and code analysis when a commit to master or a pull request is made.
* SonarCloud analysis on Master branch. We recived an A grade from SonarCloud and have minimal code smells. We also have got over 90% test coverage on our code.

![](https://i.imgur.com/2qtFwlt.png)






# Frontend Client

* We created the frontend web application using Thymeleaf.
* Run the following command to run the imdp-plus application:
  `mvn spring-boot:run`
* Go to the http://localhost:8083/home where homepage is.
  ![](https://i.imgur.com/2dCRtIN.png)


    There are three services, User, Timeline, Analysis.

* For the User service, user can choose to create a user, get a user's information by ID, or delete a user.
  ![](https://i.imgur.com/EGUWUXQ.png)


* In the create user page, user types the information. After click `Create User`, the data created in the DynamoDB, and also shows the result under the `Home Paghe` button.
  ![](https://i.imgur.com/t87oYZR.png)


* In the Get user by ID page, user types User ID. After click `Get Information`, backedend fetch data from DynamoDB, and also shows under the `Home Paghe` button.
  ![](https://i.imgur.com/d0LtLI1.png)

* In the Delete User page, user types User ID and Access Token. After click `Delete`, backedend delete data in DynamoDB, and also shows `USER DELETE SUCCESSFULLY! `under the `Home Paghe` button.
  ![](https://i.imgur.com/sp92fb7.png)

* For the Timeline service, user can choose to create, finds, or delete a Timeline.
  ![](https://i.imgur.com/6DEOIt1.png)

* User input Timeline information to create Timeline:
  ![](https://i.imgur.com/Ci0TeLY.png)

* User can find Timeline by entering User ID:
  ![](https://i.imgur.com/x5lbRak.png)

* User can find Timeline by entering Media ID:
  ![](https://i.imgur.com/IPoqi3i.png)

* User can find Timeline by mediaId matches the given mediaId as well as the user whose userId matched userId:
  ![](https://i.imgur.com/H3PRtHB.png)

* User can delete a Timeline by entering Timeline ID and Access Token:
  ![](https://i.imgur.com/hWnbKg4.png)


* For the Analysis service, there are four founctions to choose, as the following.
  ![](https://i.imgur.com/XRdZHAs.png)


* The results of the hightest-rated movie, most watched movie, and top ten movies will appears in the bottom of the Home Page:
  ![](https://i.imgur.com/Mcevckv.png)
  ![](https://i.imgur.com/lef265e.png)
  ![](https://i.imgur.com/tPrr905.png)

* User can click `Get User Preference by ID` button to enter next page, and enter User ID to get the user's preference:
  ![](https://i.imgur.com/Vh2rU3B.png)



# How Has This Been Tested?

All the functional-ready endpoints are in end-to-end tested in the following tests in files `AnalysisControllerTest.java`, `MediaControllerTest.java`, `TimelineControllerTest.java`, `UserControllerTest.java`, `WebControllerTest.java`, `TimelineTest.java`, `AnalysisRepositoryTest.java`, `TimelineRepositoryTest.java`, `AnalysisServiceTest.java`, `TimelineServiceTest.java`, `ImdbPlusApplicationIntegrationTests.java`,   `ImdbPlusApplicationSystemTests.java` files. Please clone the project and load it in IntelliJ IDEA and run individual file to run tests in respective files.

To run all the tests, please run `mvn test`


**Test Configuration**:
* Firmware version: Spring Boot 2.7.5
* Hardware: Apple M1 Pro
* Toolchain: IntelliJ IDEA
* SDK: JDK 11 OpenJDK
* Style checker: Google Java Style Guide
* Database: Amazon DynamoDB