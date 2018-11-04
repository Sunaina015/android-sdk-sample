# Local Stats Access

You can access your stats locally using the SDK. 

_Note: calling the stats will trigger a possible lock of 10ms on the thread you are currently using_


Statistics obtained this way can be compared to the standard `RTCStatsReport` obtained from the `WebRTC` implementation.

For reference, you can go to [the Official W3C's WebRTC Stat documentation](https://www.w3.org/TR/webrtc/#rtcstatsreport-object)


## Modes

It currently does exist 2 modes of behaviour :
 - The **default/pulling behaviour** is to wait for the developers to pull the stats and interact with them.
 - The **auto fetch** mode which consist of looping while the user is in a conference. It must be activated using the start auto fetch method and can be stopped using the stop auto fetch. This behaviour will send change events


## Index

  - [LocalStatService documentation](#method) - The overall entry point service
  - [LocalStats object](#localstats-object) - represents the raw webrtc stats
  - [Local Stats User Info](#local-stats-user-info) - get the processed stats from `auto fetch mode`
  - [LocalStatsUserTypes](#localstatsusertypes) - the user lifeycle from stats perspectives
  - [Classes availables](#classes-availables) - the availables classes to represent raw stats
  - [LocalStatsUserChangeEvent](#localstatsuserchangeevent) - the various events from the `auto fetch mode`


## Method

### `getLocalStats`
Get every local stats for a specific userId

**In order to use properly this method, a conference must be joined**

#### Parameters
-   `userId` **String** - The ID of the user you want to get local stats

#### Returns
`LocalStats` - Returns a Promise to resolve with the result.

#### Example
```java
LocalStats stats = VoxeetSdk.getInstance().getLocalStatsService().getLocalStats(user.getUserId());

if(null != stats) {
    // valid stats, the conference was available and joined
} else {
    // no stats, you are disconnected from any conference
}
```

### `startAutoFetch`
Start the auto fetch feature

#### Returns
`boolean` - Indicator of the start of the feature. It will only start if not already started

#### Example
```java
boolean start = VoxeetSdk.getInstance().getLocalStatsService().startAutoFetch();

if(start) {
    // just started
} else {
    // was started...
}
```

### `stopAutoFetch`
Stop the auto fetch feature

#### Returns
`boolean` - Indicator of the stop of the feature. It will only stop if it was started

#### Example
```java
boolean stop = VoxeetSdk.getInstance().getLocalStatsService().stopAutoFetch();

if(stop) {
    // stop
} else {
    // was not running...
}
```

### `getUserStatsHolder`
Given a conference, retrieve the whole list of saved stats for each of its users

**In order to use properly this method, a conference must be joined and startAutoFetch used**

#### Parameters
-   `conferenceId` **String** - The conference id to get the infos from

#### Returns
`HashMap<String, LocalStatsUserInfo>` - Returns a Map of each `user->infos`, the result is always valid

#### Example
```java
HashMap<String, LocalStatsUserInfo> stats = VoxeetSdk.getInstance().getLocalStatsService().getLocalStats(user.getUserId());
//the object is always != null
```

### `getUserInfo`
Given a conference and a user id, retrieve the in memory stats history

**In order to use properly this method, a conference must be joined and startAutoFetch used**

#### Parameters
-   `conferenceId` **String** - The conference id to get the infos from
-   `userId` **String** - The ID of the user you want to get local stats

#### Returns
`LocalStatsUserInfo` - Returns a `infos`, the result is always valid

#### Example
```java
LocalStatsUserInfo statsForUser = VoxeetSdk.getInstance().getLocalStatsService().getUserInfo(conference.getConferenceId(), user.getUserId());
//the object is always != null
```


## LocalStats object

### `toJson`

Get the `JSONArray` representation of the obtained stats.

#### Returns
`JSONArray` - Returns the corresponding Array of JSON Objects.


### `getStatsForClass`

Get a `List` of matching statistic classes.

#### Parameters
-   `klass` **Class<STAT>** - The Class matching the needed objects. _STAT being a valid Class extending AbstractStats<JSONObject>_

#### Returns
`List<STAT>` - A valid list with in the worst no elements in it.

#### Example

```
List<RTCMediaStreamTrackStats> track_stats = stats.getStatsForClass(RTCMediaStreamTrackStats.class);

for(RTCMediaStreamTrackStats stat : track_stats) {
  // manage the objects here
}
```


## Local Stats User Info

Save and manage high level infos from low level stats


### `isDisconnected`

Check if the given user is disconnected according to the recorded stats;

**It checks for a 5s window**

#### Returns
`boolean` - true if the user is disconnected according to hi.er stats


### `isFluctuating`

Check if the given user is fluctuating according to the recorded stats;

**It checks for a 2s window**

#### Returns
`boolean` - true if the user is fluctuating according to hi.er stats


### `getCurrentType`

Get the current user type of connectivity according to hi.er stats.

**Infos are updated in autoFetch mode**

#### Returns
`LocalStatsUserTypes` - The corresponding connectivity state


## LocalStatsUserTypes

  - DEFAULT
  - CONNECTING
  - CONNECTED
  - FLUCTUATES
  - DISCONNECTED
  
  Here is the current lifecycle :
  - `DEFAULT` -> `DISCONNECTED`
  - `DEFAULT` -> `CONNECTING`
  - `CONNECTING` -> `DISCONNECTED`
  - `CONNECTING` -> `CONNECTED`
  - `CONNECTED` -> `DISCONNECTED`
  - `CONNECTED` -> `FLUCTUATES`
  - `FLUCTUATES` -> `DISCONNECTED`
  - `FLUCTUATES` -> `CONNECTED`
  - `DISCONNECTED` -> `CONNECTING`
  - `DISCONNECTED` -> `CONNECTED`

## Classes availables

Here is a list of every available classes to use to query the current WebRTC status.

  - RTCCandidatePairStats
  - RTCCertificateStats
  - RTCCodecStats
  - RTCInboundRTPStreamStats
  - RTCLocalCandidateStats
  - RTCMediaStreamTrackStats
  - RTCOutboundRTPStats
  - RTCPeerConnectionStats
  - RTCRemoteCandidateStats
  - RTCStreamStats
  - RTCTransportStats

### RTCCandidatePairStats

#### Fields

-    `transportId` **String** The corresponding `Transport` instance matching this id
-    `localCandidateId` **String** The corresponding `LocalCandicate` instance matching this id
-    `remoteCandidateId` **String** The corresponding `RemoteCandicate` instance matching this id
-    `state` **String** The current state of the Candidate
-    `priority` **long** The priority of the pair
-    `nominated` **String**
-    `writable` **String**
-    `bytesSent` **long** Number of bytes sent
-    `bytesReceived` **long** Number of bytes received
-    `totalRoundTripTime` **double**
-    `currentRoundTripTime` **double**
-    `availableOutgoingBitrate` **long**
-    `requestsReceived` **long**
-    `requestsSent` **long**
-    `responsesReceived` **long**
-    `responsesSent` **long**
-    `consentRequestsSent` **long**

### RTCCertificateStats

#### Fields
-    `fingerprint` **String**
-    `fingerprintAlgorithm` **String**
-    `base64Certificate` **String**

### RTCCodecStats

#### Fields
-    `payloadType` **long**
-    `mimeType` **String**
-    `clockRate` **long**

### RTCInboundRTPStreamStats

#### Fields
-    `ssrc` **long**
-    `isRemote` **boolean**
-    `mediaType` **String**
-    `trackId` **String**
-    `transportId` **String**
-    `codecId` **String**
-    `packetsReceived` **long**
-    `bytesReceived` **long** Number of bytes received
-    `packetsLost` **long** Number of packets lost
-    `jitter` **double**
-    `fractionLost` **double**
-    `roundTripTime` **double**
-    `packetsDiscarded` **long**
-    `packetsRepaired` **long**
-    `burstPacketsLost` **long**
-    `burstPacketsDiscarded` **long**
-    `burstLossCount` **long**
-    `burstDiscardCount` **long**
-    `burstLossRate` **double**
-    `burstDiscardRate` **double**
-    `gapLossRate` **double**
-    `gapDiscardRate` **double**
-    `framesDecoded` **long**
    
### RTCLocalCandidateStats

#### Fields
-    `transportId` **String**
-    `isRemote` **boolean**
-    `ip` **String**
-    `port` **long**
-    `protocol` **String**
-    `candidateType` **String**
-    `priority` **long**
-    `deleted` **boolean**
    
### RTCMediaStreamTrackStats

#### Fields
-    `trackIdentifier` **String**
-    `remoteSource` **boolean**
-    `ended` **boolean**
-    `detached` **boolean**
-    `kind` **String** Inform about the `audio` or `video` type
-    `jitterBufferDelay` **double**
-    `audioLevel` **double**
-    `totalAudioEnergy` **double**
-    `totalSamplesDuration` **double**
-    `concealedSamples` **long**
-    `concealmentEvents` **long**
-    `frameWidth` **long**
-    `frameHeight` **long**
-    `framesPerSecond` **double**
-    `framesSent` **long**
-    `framesReceived` **long**
-    `framesDecoded` **long**
-    `framesDropped` **long**
-    `framesCorrupted` **long**
-    `partialFramesLost` **long**
-    `fullFramesLost` **long**
-    `echoReturnLoss` **double**
-    `echoReturnLossEnhancement` **double**
    
### RTCOutboundRTPStats

#### Fields
-    `ssrc` **long**
-    `isRemote` **boolean**
-    `mediaType` **String**
-    `trackId` **String**
-    `transportId` **String**
-    `codecId` **String**
-    `packetsSent` **long** Number of packets sent
-    `bytesSent` **long** Number of bytes sent
    
### RTCPeerConnectionStats

#### Fields
-    `dataChannelsOpened` **long**
-    `dataChannelsClosed` **long**
    
### RTCRemoteCandidateStats

#### Fields
-    `transportId` **String**
-    `isRemote` **boolean**
-    `ip` **String**
-    `port` **long**
-    `protocol` **String**
-    `candidateType` **String**
-    `priority` **long**
-    `deleted` **boolean**
    
### RTCStreamStats

#### Fields
-    `streamIdentifier` **String**
-    `trackIds` **List<String>**
    
### RTCTransportStats

#### Fields
-    `bytesSent` **long** Number of bytes sent
-    `bytesReceived` **long** Number of bytes received
-    `dtlsState` **String** The DTLS state
-    `selectedCandidatePairId` **String**
-    `localCertificateId` **String**
-    `remoteCertificateId` **String**


## LocalStatsUserChangeEvent

### `getUserId`
Get the event's `userId`

#### Returns
`String` - The corresponding userId to match against the `ConferenceService`

#### Example
```java
String userId = event.getUserId();
```

### `getUserInfo`
Get the event's `LocalStatsUserInfo`

#### Returns
`LocalStatsUserInfo` - The corresponding stats info which emitted this event

#### Example
```java
LocalStatsUserInfo stats = event.getUserInfo();
```

### `getLastKnownType`
Get the previous user connectivity infos from the stats

#### Returns
`LocalStatsUserTypes` - The last known state

#### Example
```java
LocalStatsUserTypes previousState = event.getLastKnownType();
```

### `getCurrentKnownType`
Get the new user connectivity infos from the stats

#### Returns
`LocalStatsUserTypes` - The new known state

#### Example
```java
LocalStatsUserTypes newState = event.getCurrentKnownType();
```