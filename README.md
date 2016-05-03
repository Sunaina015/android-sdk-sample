# Voxeet Android SDK

The SDK is a Java library allowing users to:

  - Create demo/normal conferences
  - Join conferences
  - Change sounds angle and direction for each conference user
  - Broadcast messages to other participants

### Installing the Android SDK using Gradle

To install the SDK directly into your Android project using the Grade build system and an IDE like Android Studio, add the following entry: "compile 'com.voxeet.sdk:core:0.8.42'" to your build.gradle file as shown below:

```java
dependencies {
    compile 'com.voxeet.sdk:core:0.8.42'
}
```
### Recommended settings for API compatibility:

```java
apply plugin: 'com.android.application'

android {
    compileSdkVersion 21+
    defaultConfig {
        minSdkVersion 16
        targetSdkVersion 21+
    }
}
```
### Permissions

Add the following permissions to your Android Manifest file:

```java
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.WAKE_LOCK" />
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.RECORD_AUDIO" />
```

In order to target Android API level 23 or later, you will need to ensure that your application requests runtime permissions for microphone access. To do this, perform the following step:

Request microphone permissions from within your application code:

```java
ActivityCompat.requestPermissions(this,
    new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST_CODE);
}
```

See the [Official Android Documentation] for more details.

### Consumer Key & Secret

Add your consumer key & secret to the xml string file of your application.

```java
 <string name="consumer_key">your consumer key</string>
 <string name="consumer_password">your consumer password</string>
```

## Available methods

### Initializing  

```java
// To be called from the application class
VoxeetSdk.sdkInitialize(Context context, String consumerKey, String consumerSecret);
```

### Creating a demo conference  

```java
VoxeetSdk.createDemoConference();
```

### Creating a conference  

```java
VoxeetSdk.createConference();
```

### Joining a conference  

```java
// Used to join someone's conference otherwise joining is automatic
VoxeetSdk.joinConference(String conferenceId);
```

### Leaving a conference  

```java
VoxeetSdk.leaveConference();
```

### Changing user position  

```java
// Change user position using an angle and a distance
// Values for x, y are between : x = [-1, 1] and y = [0, 1]
VoxeetSdk.changePeerPosition(String userId, double x, double y);
```

### Playing a sound

```java
// Sound has to be placed in the asset folder
VoxeetSdk.playSound(String path, double angle, double distance);
```

### Sending message in a conference

```java
// Send messages such as JSON commands...
VoxeetSdk.sendBroadcastMessage(String message);
```

### Getting current conference users

```java
// Get current conference users
VoxeetSdk.getConferenceUsers();
```

### Registering the SDK

```java
// Susbcribe to the SDK events
VoxeetSdk.register(Context context);
```

### Unregistering the SDK

```java
// Unsusbcribe from the SDK events
VoxeetSdk.unregister(Context context);
```

## SDK Initialization

Initialize the SDK in the onCreate() method of your application class:

```java
@Override
public void onCreate() {
    super.onCreate();
    VoxeetSdk.sdkInitialize(this, consumerKey, consumerSecret);
}
```

## Activity Structure

In order to work properly, it is necessary to register and unresgister the SDK respectively in the onCreate() and onDestroy() methods of your activity/fragment holding the conference.

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    VoxeetSdk.register(this);  
}

@Override
protected void onDestroy() {
    super.onDestroy();
    VoxeetSdk.unregister(this);  
}   
```

## Events

The SDK will dispatch events to the suscribed classes such as activities and fragments holding the conferences. To get notified, the only necessary step is to add those 4 methods below:


### Conference joined

```java
@Subscribe
public void onEvent(final ConferenceJoined event) {
    // Action to be called when joining successfully the conference
}
```
### Conference joined error

```java
@Subscribe
public void onEvent(final ConferenceJoinedError event) {
    // Action to be called if joining failed
}
```

### Conference left

```java
@Subscribe
public void onEvent(final ConferenceLeft event) {
    // Action to be called when leaving successfully the conference
}
```

### Conference left error

```java
@Subscribe
public void onEvent(final ConferenceLeftError event) {
    // Action to be called when leaving the conference failed
}
```

### Participant added
```java
@Subscribe
public void onEvent(final ParticipantAdded event) {
    // Action to be called when a new participant joins the conference
}
```

### Participant status updated
```java
@Subscribe
public void onEvent(final ParticipantUpdated event) {
    // Action to be called when a participant has left for example
}
```

### Message received
```java
@Subscribe
public void onEvent(MessageReceived event) {
    // Action to be called when a message is received
}
```

## Version
0.8.42

## Tech

The Voxeet Android SDK uses a number of open source projects to work properly:

* [Retrofit2] - A type-safe HTTP client for Android and Java.
* [GreenRobot/EventBus] - Android optimized event bus.
* [Jackson] - Jackson is a suite of data-processing tools for Java.
* [RxAndroid] - RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.

## Sample Application

A sample application is available on this [public repository][sample] on GitHub.

© Voxeet, 2016

   [Official Android Documentation]: <http://developer.android.com/training/permissions/requesting.html>
   [sample]: <https://github.com/voxeet/android-sdk-sample.git>
   [GreenRobot/EventBus]: <https://github.com/greenrobot/EventBus>
   [Jackson]: <https://github.com/FasterXML/jackson>
   [RxAndroid]: <https://github.com/ReactiveX/RxAndroid>
   [Retrofit2]: <http://square.github.io/retrofit/>
