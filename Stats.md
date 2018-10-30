# Local Stats Access

You can access your stats locally using the SDK. 

_Note: calling the stats will trigger a possible lock of 10ms on the thread you are currently using_


Statistics obtained this way can be compared to the standard `RTCStatsReport` obtained from the `WebRTC` implementation.

For reference, you can go to [the Official W3C's WebRTC Stat documentation](https://www.w3.org/TR/webrtc/#rtcstatsreport-object)


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
LocalStats stats = VoxeetSdk.getInstance().getMediaService().getLocalStats(user.getUserId());

if(null != stats) {
    // valid stats, the conference was available and joined
} else {
    // no stats, you are disconnected from any conference
}
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

## Classes available

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
