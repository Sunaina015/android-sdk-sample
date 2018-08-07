# Voxeet Android SDK

The SDK is a Java library allowing users to:

  - Create demo/normal conferences
  - Join conferences
  - Change sounds angle and direction for each conference user
  - Broadcast messages to other participants
  - Mute users/conferences
  - Enable/disable camera
  - Screen share
  - Record conferences
  - Replay recorded conferences
  - If you use External login like O365, LDAP, or custom login to retrieve contact details, it is now possible to also add your contact ID with the display name and the photo url avatar.
   This allows you to ask guest users to introduce themselves and provide their display name and for your authenticated users in your enterprise or for your clients the ID that can be retrieved from O365 (name, department, etc).

### Installing the Android SDK using Gradle

To install the SDK directly into your Android project using the Grade build system and an IDE like Android Studio, add the following entry to your build.gradle file as shown below:

```gradle
dependencies {
  compile ('com.voxeet.sdk:toolkit:1.1.5') {
    transitive = true
  }
}
```

The current logic-only (no UI) sdk is available using the following version (used by the current toolkit version) :

```gradle
dependencies {
  compile ('com.voxeet.sdk:public-sdk:1.1.5') {
    transitive = true
  }
}
```

## Migrating from 0.X to 1.X

 - Most calls to the SDK are now using Promises to resolve and manage error
 - it is mandatory to use the following workflow on pre-used methods :
```
SDK.method.call()
.then(<PromiseExec>)
.error(<ErrorPromise>);
```

A complete documentation about the Promise implementation is available on this [Github](https://github.com/codlab/android_promise)

### What's New ?

v1.1.5 :
  - from previous vversion, Media.AudioRoute is now AudioRoute
  - Audio related APIs are now in `VoxeetSdk.getInstance().getAudioService()`
  - fix issues with ids from the SDK
  - add VideoPresentation api
  - sample app : integration of the api and fix with butterknife
  
v1.1.0 :
  - various fixes (issue with speaker button)
  - add screenshare capabilities
  - easier integration
  - dual initialization mode (keys or oauth)

v1.0.4 :
  - upgrade api calls
  - fix issue with answers
  - fix conference alias in history

v1.0.3 :
  - initialize Promises during the Voxeet initialization

v1.0.2 :
  - fix CTA
  - fix issue with crash on same calls
  - fix controllers behaviour

v1.0 :
  - complete rework of most internal method
  - File Presentation management (start, stop, update)
  - event on QualityIndicators with MOS


## Usage

### Recommended settings for API compatibility:

```gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 26+
    defaultConfig {
        minSdkVersion 16
        targetSdkVersion 26+
    }
}
```

### Consumer Key & Secret

Add your keys into your ~/.gradle/gradle.properties file

```gradle
PROD_CONSUMER_KEY=your_key_prod
PROD_CONSUMER_SECRET=your_key_prod_staging
```

And use them in the app using BuildConfig.CONSUMER_KEY and BuildConfig.CONSUMER_SECRET (as shown in the app/build.gradle file)

### Permissions

Add the following permissions to your Android Manifest file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  <uses-permission android:name="android.permission.WAKE_LOCK" />
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.RECORD_AUDIO" />
  <uses-permission android:name="android.permission.INTERACT_ACROSS_USERS_FULL" />
  <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.CAMERA" />

  // Used to change audio routes
  <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
</manifest>
```

In order to target Android API level 21 or later, you will need to ensure that your application requests runtime permissions for microphone and camera access. To do this, perform the following step:

Request microphone and camera permissions from within your activity/fragment :

```java
ActivityCompat.requestPermissions(this,
    new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST_CODE);
}
```

```java
ActivityCompat.requestPermissions(this,
    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
}
```

You can also request both at the same time:

```java
ActivityCompat.requestPermissions(this,
    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
}
```

See the [Official Android Documentation] for more details.

### FCM

Please notice the following steps are required only if you plan on using fcm.
To enable Voxeet notifications (getting a new call, conference ended and so on...) on your applications:
  1. Send us the application fcm token
  2. Add the google.json file to your project
  2. Add this to your Android Manifest:

```xml

<?xml version="1.0" encoding="utf-8"?>
<manifest>
  <application>
    <service android:name="voxeet.com.sdk.firebase.VoxeetFirebaseMessagingService">
      <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
      </intent-filter>
    </service>
    <service android:name="voxeet.com.sdk.firebase.VoxeetFirebaseInstanceIDService">
      <intent-filter>
        <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
      </intent-filter>
    </service>
  </application>
</manifest>
```

### Logger

A logger has been added to the SDK allowing users to track events more easily. 3 different levels for 3 different types of informations:

  1. DEBUG for every event dispatched through the eventbus.
  2. INFO to display methods results when calling a SDK method.
  3. ERROR when an error occurs.

Please also note that WebRTC has its own logger for WebRTC related events.

## Initialize the Voxeet SDK

Two methods are currently available to initialize the SDK
- Set the Api Keys in the APP
- Use an Oauth2 server to provide the keys

### `initialize` with keys
Initializes the SDK with your Voxeet user information.

#### Parameters
-   `context` **ApplicationContext** - A NonNull ApplicationContext.
-   `consumerKey` **string** - The consumer key for your app from [your developer account dashboard](https://developer.voxeet.com).
-   `consumerSecret` **string** - The consumer secret for your app from [your developer account dashboard](https://developer.voxeet.com).
-   `userInfo` **object** - A Nullable UserInfo object that contains custom user information.
    -   `userInfo.name` **string** - The user name.
    -   `userInfo.externalId` **string** - An external ID for the user.
    -   `userInfo.avatarUrl` **string** - An avatar URL for the user.

#### Example
```java
UserInfo externalInfo = new UserInfo(externalName, externalName, externalPhotoUrl);

VoxeetSdk.sdkInitialize(context, consumerKey, consumerSecret, externalInfo);
```

### `initialize` with Oauth2
Initializes the SDK with a valid token and a method to refresh it.

#### Parameters
-   `context` **ApplicationContext** - A NonNull ApplicationContext.
-   `tokenAccess` **string** - A valid tokenAccess obtained from your own Oauth2 server.
-   `tokenRefresh` **callable<string>** - A callback which will return a valid tokenAccess when called from the SDK.
-   `userInfo` **object** - A Nullable UserInfo object that contains custom user information.
    -   `userInfo.name` **string** - The user name.
    -   `userInfo.externalId` **string** - An external ID for the user.
    -   `userInfo.avatarUrl` **string** - An avatar URL for the user.

#### Example
```java
UserInfo externalInfo = new UserInfo(externalName, externalName, externalPhotoUrl);

Callable<String> tokenRefresh = new Callable<String>() {
    @Override
    public String call() throws Exception {
        //retrofitService is a valid proxy object from a Retrofit interface
        return retrofitService.refreshToken().toBlocking().single();
    }
};

VoxeetSdk.sdkInitialize(context, tokenAccess, tokenRefresh, externalInfo);
```


### `register`
Registers your app with the SDK.


#### Parameters
-   `context` **context** - A NonNull Context.
-   `object`  **object** - An object which will receive EventBus events

```
VoxeetSdk.getInstance().register(context, object);
```

#### Parameters
-   `context` **context** - A NonNull context.
-   `object` **object** - An object that will receive EventBus messages.


## Conferences
Main access: `VoxeetSdk.getInstance().getConferenceService()`


### `create`
Creates a conference.

#### Returns
`Promise<status: Boolean | Error>` - The conference is created

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().create()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```

### `join`
Joins a conference. If the conference doesn't exist, it is created.

**If you previously used `create`, the conference parameters in the `joinConference` object
are ignored.**

#### Parameters
-   `conference` **string** - The ID or alias of the conference to join.

#### Returns
`Promise<status: Boolean | Error>` - The conference is joined

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().join(conferenceAlias)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `leaveConference`
Leaves the currently running conference.

#### Returns
`Promise<status: Boolean | ConferenceLeftError>` - The conference is left or an error occured

If an error occurs, the session is properly left in the client. There is no need to recheck the `leaveConference` status.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().leave()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `listenConference`
Listen to a conference without sharing your audio or video.

#### Parameters
-   `conference` **string** - The ID or alias of the conference to listen to.

#### Returns
`Promise<status: Boolean | InConferenceException | PromiseConferenceJoinedErrorException>` - The conference is joined in listener mode

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().listenConference(conferenceAlias)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


## Replay conferences


### `replayConference`
Replays a recorded conference.

#### Parameters
-   `conferenceId` **string** - The ID of the conference you want to replay.

#### Returns
`Promise<status: Boolean>` - The conference replay is starting

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().listenConference(conferenceAlias)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


## Record conferences


### `startRecording`
Records the current conference so you can replay it after it ends.

#### Returns
`Promise<status: Boolean | Exception>` - Resolve if no network error occured

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().startRecording()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `stopRecording`
Stops the current recording.

#### Returns
`Promise<status: Boolean | Exception>` - Resolve if no network error occured

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().stopRecording()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `mute`
Mute the microphone currently in use in the conference.

**The conference must be started to use this method.**

#### Parameters
-   `state` **Boolean** - If true, the microphone is currently muted. Otherwise, false.

#### Returns
`boolean` - Returns whether the microphone is mute.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService()
    .muteConference(true);
```


### `muteUser`
Mutes or unmutes the specified user.

**The conference must be started to use this method.**

#### Parameters
-   `userId` **string** - The ID of the user you want to mute or unmute.
-   `state` **Boolean** - `true` if the user is muted. Otherwise, `false`.

#### Returns
`boolean` - Returns whether the user is muted or unmuted.

#### Example
```java
String userId = "d0d237d9-1b1a-47b4-8fae-ae9cb3ce20c9";

VoxeetSdk.getInstance().getConferenceService().muteUser(userId, true);
```


### `isVideoOn`
Checks whether the video (camera) is turned on.

**The conference must be started to use this method.**

#### Returns
`boolean` - Returns whether the current camera is turned on.

#### Example
```java
boolean video_on = VoxeetSdk.getInstance().getConferenceService().isVideoOn();
```


### `startVideo`
Starts the selected or default video (camera).

**The conference must be started to use this method.**

#### Returns
`Promise<Boolean | Exception>` - Returns a Promise to resolve with the result.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().startVideo()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `stopVideo`
Stops the current video (camera).

**The conference must be started to use this method.**

#### Returns
`Promise<Boolean | Exception>` - Returns a Promise to resolve with the result.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().stopVideo()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `switchCamera`
Changes the device camera (front to back or back to front).

**The conference must be started to use this method.**

#### Returns
`Promise<Boolean | Exception>` - Returns a Promise to resolve with the result.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().switchCamera()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```



## Audio devices
Main access: `VoxeetSdk.getInstance().getAudioService()`


### `getAvailableRoutes`
Retrieves the available routes the SDK can use.

#### Returns
`List<AudioRoute>` - A list of every AudioRoute.

#### Example
```java
List<AudioRoute> routes = VoxeetSdk.getInstance().getAudioService().getAvailableRoutes();
```


### `getNameOfFrontFacingDevice`
Retrieves the name of the front-facing camera.

#### Returns
`String?` - The name of the device (or "null" if unavailable).

#### Example
```java
String front = new CameraEnumerator(applicationContext).getNameOfFrontFacingDevice();
```


### `getNameOfBackFacingDevice`
Retrieves the name of the back-facing camera.

#### Returns
`String?` - The name of the device (or "null" if unavailable).

#### Example
```java
String back = new CameraEnumerator(applicationContext).getNameOfBackFacingDevice();
```


### `setDefaultCamera`
Sets the camera device to use during the conference.

**The device must be set before starting the video.**

#### Parameters
-   `cameraName` **string** - The name of the camera device to use.

#### Example
```java
VoxeetSdk.getInstance().getAudioService().setDefaultCamera(nameOfCamera);
```

### `setAudioRoute`
Sets the AudioRoute to use when the conference is already started.

**The conference must be started to use this method.**

#### Parameters
-   `audioRoute` **AudioRoute** - The valid AudioRoute to use.

#### Returns
`boolean` - Returns whether the output can be changed.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().setAudioRoute(AudioRoute.ROUTE_SPEAKER);
```


## Manage users


### `invite`
Invites a list of users to the current conference.

**The conference must be started to use this method.**

#### Parameters
-   `ids` **List<String>** - A valid list of IDs for users to invite. The list can be empty.

#### Returns
`Promise<List<ConferenceRefreshedEvent> | Exception>` - Returns a Promise to resolve with the result for each user ID.

#### Example
```java
List<String> ids = new ArrayList();
ids.add("c55754af-eb79-42bf-8205-142660e34610");

VoxeetSdk.getInstance().getConferenceService().invite(ids)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable List<ConferenceRefreshedEvent> refreshed_list, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `setUserPosition`
Sets user position for the spatialized audio mode.

**The conference must be started to use this method.**

#### Parameters
-   `id` **string** - The ID for the user whose position you want to set.
-   `angle` **double** - The user's X position. Must be a value between `-1.0` and `1.0`.
-   `distance` **double** - The user's Y position. Must be a value between `0.0` and `1.0`.

#### Returns
`boolean` - Returns true if the user's position was successfully changed. Otherwise, false.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().setUserPosition(ids, 3*Math.PI/4, 12);
```


### `getPeerVuMeter`
Retrieves the current VU value for the specified user.

**The conference must be started to use this method.**

#### Parameters
 -   `id` **String** - The ID for the user whose VU value you want to retrieve.

#### Returns
`int` - Returns the user's current VU value.

#### Example
```java
int value = VoxeetSdk.getInstance().getConferenceService().getPeerVuMeter("c55754af-eb79-42bf-8205-142660e34610");
```


## Broadcast messages


### `sendConferenceMessage`
Broadcasts message to all conference participants.

#### Parameters
-   `message` **string** - The message to broadcast.

#### Returns
`Promise<status: Boolean | Exception>` - Resolve the current message status.

#### Example
```java
VoxeetSdk.getInstance().getConferenceService().sendBroadcastMessage(message)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


## File-sharing


### `convertFile`
Converts files users select through an input file box.

#### Parameters
-   `file` **file** - A file object.

#### Returns
`Promise<status: FilePresentationConverted | Exception>` - Retrieves a representation of the converted file.

#### Example
```java
VoxeetSdk.getInstance().getFilePresentationService().convertFile(file)
                .then(new PromiseExec<FilePresentationConverted, Object>() {
                    @Override
                    public void onCall(@Nullable FilePresentationConverted converted_object, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `startPresentation`
Starts a file presentation.

#### Parameters
-   `file` **file** - A file object.
-   `position` **integer** - The page in the file to present.

#### Returns
`Promise<status: FilePresentationStarted | Exception>` - Presentation started or exception.

#### Example
```java
VoxeetSdk.getInstance().getFilePresentationService().startFilePresentation(converted_file)
                .then(new PromiseExec<FilePresentationStarted, Object>() {
                    @Override
                    public void onCall(@Nullable FilePresentationStarted started_object, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```

### `updatePresentation`
Updates the current file presentation.

#### Parameters
-   `fileId` **string** - The ID of the file to update.
-   `offset` **integer** - The new page to present.

#### Returns
`Promise<status: FilePresentationUpdated | Exception>` - The presentation updated.

#### Example
```java
VoxeetSdk.getInstance().getFilePresentationService().updateFilePresentation(fileId)
                .then(new PromiseExec<FilePresentationUpdated, Object>() {
                    @Override
                    public void onCall(@Nullable FilePresentationUpdated updated_presentation, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `stopPresentation`
Stops the current file presentation.

#### Parameters
-   `fileId` **string** - The ID of the file you want to stop presenting.

#### Returns
`Promise<status: FilePresentationStopped | Exception>` - Presentation stopped or exception.

#### Example
```java
VoxeetSdk.getInstance().getFilePresentationService().stopFilePresentation(fileId)
                .then(new PromiseExec<FilePresentationStopped, Object>() {
                    @Override
                    public void onCall(@Nullable FilePresentationStopped stopped_object, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `getThumbnail`
Retrieves a thumbnail image URL for a file page.

#### Parameters
-   `fileId` **string** - The ID for the corresponding file.
-   `pageNumber` **integer** - The file page number to retrieve.

#### Returns
`String` - The valid URL.

#### Example
```java
String thumbnail_url = VoxeetSdk.getInstance().getFilePresentationService().getThumbnail(fileId, pageId);
```


### `getImage`
Retrives an image for a file page.

#### Parameters
-   `fileId` **string** - The ID for the corresponding file.
-   `pageNumber` **integer** - The file page number to retrieve.

#### Returns
`String` - The valid URL.

#### Example
```java
String image_url = VoxeetSdk.getInstance().getFilePresentationService().getImage(fileId, pageId);
```

## Video presentation

### `startVideoPresentation`
Starts a video presentation.

#### Parameters
-   `url` **string** - The direct url to the video.

#### Returns
`Promise<status: VideoPresentationStarted | Exception>` - Presentation started or exception.

#### Example
```java
VoxeetSdk.getInstance().getVideoPresentation().VideoPresentationStarted(url)
                .then(new PromiseExec<VideoPresentationStarted, Object>() {
                    @Override
                    public void onCall(@Nullable VideoPresentationStartedVideoPresentationStarted started_object, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```


### `stopPresentation`
Stops the current video presentation.

#### Returns
`Promise<status: VideoPresentationStopped | Exception>` - Presentation stopped or exception.

#### Example
```java
VoxeetSdk.getInstance().getVideoPresentationService().stopVideoPresentation()
                .then(new PromiseExec<VideoPresentationStopped, Object>() {
                    @Override
                    public void onCall(@Nullable VideoPresentationStopped stopped_object, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```

### `playVideoPresentation`
Play the video.

#### Returns
`Promise<status: VideoPresentationPlay | Exception>` - The presentation's video plays.

#### Example
```java
VoxeetSdk.getInstance().getVideoPresentationService().playVideoPresentation()
                .then(new PromiseExec<VideoPresentationPlay, Object>() {
                    @Override
                    public void onCall(@Nullable VideoPresentationPlay updated_presentation, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```
### `pauseVideoPresentation`
Pause the video.

#### Returns
`Promise<status: VideoPresentationPaused | Exception>` - The presentation's video is now paused.

#### Example
```java
VoxeetSdk.getInstance().getVideoPresentationService().pauseVideoPresentation()
                .then(new PromiseExec<VideoPresentationPaused, Object>() {
                    @Override
                    public void onCall(@Nullable VideoPresentationPaused updated_presentation, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```

### `seekVideoPresentation`
Change the current timestamp of the video.

#### Returns
`Promise<status: VideoPresentationSeek | Exception>` - The presentation's video plays.

#### Example
```java
VoxeetSdk.getInstance().getVideoPresentationService().seekVideoPresentation()
                .then(new PromiseExec<VideoPresentationSeek, Object>() {
                    @Override
                    public void onCall(@Nullable VideoPresentationSeek updated_presentation, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```

## ScreenShare
Main access: `VoxeetSdk.getInstance().getScreenShareService()`

### `startScreenShare`
Start a screen share from an `Intent` received from the system authorization popup.

Note that to make this the easiest integration process, every `Activity` from your app should extends `VoxeetAppCompatActivity` (available in the SDK Toolkit) which manage the result from the user's approval. If you prefer handling those steps by yourself, you can refer to the [Google Sample](https://github.com/googlesamples/android-ScreenCapture/tree/master/Application/src/main/java/com/example/android/screencapture).

#### Parameters
-   `intent` **intent** - The system authorization result.

#### Returns
`Promise<status: Boolean | Exception>` - Resolve the status after the user authorized the app.

#### Example
```java
//in the Activity's onActivityResult callback
mTemporaryIntent = data; //considering data is the 3rd parameter of onActivityResult
...
//After the activity's onResume() or super.onResume() has been called
VoxeetSdk.getInstance().getScreenShareService().startScreenShare(mTemporaryIntent)
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {

                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
```

### `stopScreenShare`
Stop a previously started session.

#### Returns
`Promise<status: Boolean | Exception>` - Resolve if the whole stop process has been stopped. It will at least stop locally.

#### Example
```java
VoxeetSdk.getInstance().getScreenShareService().stopScreenShare()
                .then(new PromiseExec<Boolean, Object>() {
                    @Override
                    public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {
                      //here the whole server & local screen session are now ended
                    }
                })
                .error(new ErrorPromise() {
                    @Override
                    public void onError(@NonNull Throwable throwable) {
                      //here, at least the local screen share session is ended
                    }
                });
```

### `sendUserPermissionRequest`

Given a specified `Activity`, this method will call the `MediaProjectionManager` available in `Android API 19+`.

#### Parameters
-   `activity` **activity** - The Activity from which the call must be done.

#### Returns
`boolean` - Return if the current `Android API level` is high enough.

#### Example
```java
boolean permission_sent = VoxeetSdk.getInstance().getScreenShareService()
    .sendUserPermissionRequest(mActivity);
```

### `sendRequestStartScreenShare`

This method will emit a new `ScreenShareService.RequestScreenSharePermissionEvent` event. This event must be catch in the current `Activity` to trigger `VoxeetSdk.getInstance().getScreenShareService()
                .sendUserPermissionRequest(**Activity**)`

#### Example
```java
VoxeetSdk.getInstance().getScreenShareService().sendRequestStartScreenShare();
```

### `onActivityResult`

This method will manage the `MediaProjectionManager` result available in the `Activity`'s `onActivityResult`. When it is called by an `Activity`, it will store the `Intent data` to help process it later before starting the `ScreenShare` session.

#### Parameters
-   `requestCode` **int** - The sub-`Activity` requested code.
-   `resultCode` **int** - The result the sub-`Activity` sent.
-   `data` **Intent** - Optional data information the sub-`Activity` sent.

#### Returns
`boolean` - Return if the various codes and data match a valid `ScreenShare` authorization.


#### Example
```java
@Override
public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  boolean managed = VoxeetSdk.getInstance().getScreenShareService()
      .onActivityResult(requestCode, resultCode, data);

  if(managed) {
    Log.d(TAG, "The user granted the permission");
    return true;
  }

  return super.onActivityResult(requestCode, resultCode, data);
}
```

### `consumeRightsToScreenShare`

This method will consume any `awaiting` Intent data corresponding to a `ScreenShare` which have been manage by `onActivityResult`. It can be called from `Activity`'s `onResume` after the `super.onResume()` call has been made. When using the SDK Toolkit's `VoxeetAppCompatActivity`, this call and the previous calls are automatically managed if the Activities inherits this last class.

#### Example
```java
VoxeetSdk.getInstance().getScreenShareService().consumeRightsToScreenShare();
```

## Events


### Conference events

#### `ConferenceCreationSuccess`
Sent when the conference is successfully created.

#### `ConferenceCreatedError`
Sent when the conference creation fails.

#### `ConferenceJoinedSuccessEvent`
Sent when a user joins the conference.

#### `ConferenceJoinedError`
Sent when a user could not join the conference.

#### `ConferenceLeftSuccessEvent`
Sent when a user leaves the conference.

#### `ConferenceLeftError`
Sent when a user leaves the conference, but with errors.

#### `ConferenceEndedEvent`
Sent when the conference is ended.

#### `GetConferenceStatusErrorEvent`
Sent when the conference status could not be retrieved.

#### `GetConferenceHistoryEvent`
Sent with the history of the conference.

#### `GetConferenceHistoryErrorEvent`
Sent when the conference history could not be retrieved.

#### `CameraSwitchSuccessEvent`
Sent when a user's camera was successfully switched.

#### `CameraSwitchErrorEvent`
Sent when a user's camera could not be switched.

#### `SubscribeConferenceEvent`
Sent when a subscription to the specified conference is dispatched.

#### `SubscribeConferenceErrorEvent`
Sent when a subscription to the specified conference could not be dispatched.

#### `UnSubscribeConferenceAnswerEvent`
Sent when an unsubscribe request for the conference could not be dispatched.

#### `UnSubscribeConferenceAnswerEvent`
Sent with an unsubscribe request answer.

#### `SubscribeForCallConferenceAnswerEvent`
Sent with a subscription for call answers.

#### `SubscribeForCallConferenceErrorEvent`
Sent after a call subscription fails.

#### `UnSubscribeFromConferenceAnswerEvent`
Sent after a sucessful call unsubscribe request.

#### `UnsubscribeFromCallConferenceErrorEvent`
Sent after a call unsubscribe request fails.

#### `SendBroadcastResultEvent`
Sent with the result of a broadcast message.

#### `QualityIndicators`
Sent with the conference status.

#### `ConferenceUserQualityUpdatedEvent`
Sent with the quality of the specified user in the conference.


### Participant events

#### `ConferenceUserJoinedEvent`
Sent when a user joins the conference.

#### `ConferenceUserUpdatedEvent`
Sent when a user is updated.

#### `ConferenceUserLeftEvent`
Sent when a user leaves the conference.

#### `AddConferenceParticipantResultEvent`
Sent when a specific user is added to the conference.

#### `ConferenceRefreshedEvent`
Sent when conference user information is refreshed.

#### `ConferenceUsersInvitedEvent`
Sent when the list of users is invited.

#### `ConferenceUserCallDeclinedEvent`
Sent when a user declines the conference.

#### `DeclineConferenceResultEvent`
Sent when the server declines a dispatch from the device.


### Recording events

#### `StartRecordingResultEvent`
Sent when the conference recording starts.

#### `StopRecordingResultEvent`
Sent when the conference recording stops.

#### `RecordingStatusUpdateEvent`
Sent when the conference recording status is changed.

#### `ReplayConferenceErrorEvent`
Sent when the conference replay cannot start.


### Video events

#### `StartVideoAnswerEvent`
Sent when the video starts.

#### `StopVideoAnswerEvent`
Sent when the video cannot start.


### Screen-sharing events

#### `ScreenStreamAddedEvent`
Sent when screen-sharing from another conference participant is received.

#### `ScreenStreamRemovedEvent`
Sent when screen-sharing from another participant stops.


### Permissions events

#### `PermissionRefusedEvent`
Sent when the app should request a specific user permission.


### User events

#### `SdkLogoutSuccessEvent`
Sent when a user is successfully logged out from the SDK.

#### `SdkLogoutErrorEvent`
Sent in case of a logout with network issues.

**In this case, the user can still receive push notifications in case of Firebase implementation.**


### File-sharing events

#### `FileConvertedEvent`
Sent when a file is converted.

#### `FilePresentationStartEvent`
Sent when a file presentation is dispatched from the server.

#### `FilePresentationStopEvent`
Sent when a file presentation stops from the server.

### Video presentation events

*Note : those events must be managed in the UI to reflect the actions on the player ( for instance `Android's VideoView` )*

#### `VideoPresentationStartedEvent`
Sent when a Video Presentation begins in the conference

#### `VideoPresentationPlayEvent`
Sent when the presented video plays

#### `VideoPresentationPausedEvent`
Sent when the presented video is now paused

#### `VideoPresentationSeekEvent`
Sent when the presented video current timestamp changes


## Conference event flow

1. ConferenceCreatedEvent (if you're the one creating the conference, joining it is automatic)

2. ConferenceJoinedSuccessEvent or ConferenceJoinedErrorEvent after joining it

3.  a. ConferenceUserJoinedEvent when someone joins the conf

    b. ConferenceUserUpdatedEvent when someone starts/stop streaming

    c. ConferenceUserLeftEvent when someone left

4. ConferenceLeftSuccessEvent or ConferenceLeftErrorEvent after leaving the conference

5. ConferenceEndedEvent if a conference has ended such a replay ending

6. ConferenceDestroyedEvent when the conference is destroyed

## Best practice regarding conferences

Only one instance of a conference is allowed to be live. Leaving the current conference before creating or joining another one is mandatory. Otherwise, a IllegalStateException will be thrown.

## Version


public-sdk: 1.1.5
toolkit: 1.1.5

## Tech

The Voxeet Android SDK uses a number of open source projects to work properly:

* [Retrofit2] - A type-safe HTTP client for Android and Java.
* [GreenRobot/EventBus] - Android optimized event bus.
* [Jackson] - Jackson is a suite of data-processing tools for Java.
* [Butterknife] - Bind Android views and callbacks to fields and methods.
* [Picasso] - A powerful image downloading and caching library for Android.
* [Recyclerview] - An android support library.
* [Apache Commons] - Collection of open source reusable Java components from the Apache/Jakarta community.
* [RxAndroid] - RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.
* [SimplePromise] - A low footprint simple Promise implementation for Android: easy and reliable Promises with chaining and resolution

## Sample Application

A sample application is available on this [public repository][sample] on GitHub.

© Voxeet, 2018

   [Official Android Documentation]: <http://developer.android.com/training/permissions/requesting.html>
   [sample]: <https://github.com/voxeet/android-sdk-sample.git>
   [GreenRobot/EventBus]: <https://github.com/greenrobot/EventBus>
   [Jackson]: <https://github.com/FasterXML/jackson>
   [Picasso]: <http://square.github.io/picasso>
   [Recyclerview]: <https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html>
   [Butterknife]: <http://jakewharton.github.io/butterknife>
   [Apache Commons]: <https://commons.apache.org>
   [RxAndroid]: <https://github.com/ReactiveX/RxAndroid>
   [Retrofit2]: <http://square.github.io/retrofit/>
   [SimplePromise]: <https://github.com/codlab/android_promise>
