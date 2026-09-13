# Israel TV — Android TV app

An Android TV (Leanback) app that plays live streams for four Israeli
channels:

- **Kan 11** (public broadcaster)
- **Channel 12 / Keshet**
- **Channel 13 / Reshet**
- **Channel 14**

## How it "finds" each stream URL

Broadcaster CDN links rotate over time, so the app doesn't hard-code them.
Instead, `ChannelRepository` fetches a community-maintained, MIT-licensed
public IPTV playlist at runtime —
[`iptv-org/iptv`'s Israel list](https://github.com/iptv-org/iptv/blob/master/streams/il.m3u) —
parses it (`M3uPlaylistParser`), and matches entries against the four
channels by `tvg-id` (and channel name as a fallback). This happens:

- on app start,
- whenever you select **Options → Refresh Channels** on the home screen,
- and automatically if a channel fails to play (the player screen re-resolves
  before retrying, in case the old link expired).

If the playlist can't be reached, or doesn't list a channel, the app falls
back to the last-known-good URL baked into `ChannelCatalog.kt` so the app
still works offline-first.

Because some of these streams need a browser-like `User-Agent` header (e.g.
Channel 12), the resolver also carries per-channel HTTP headers through to
the player.

## Architecture

```
app/src/main/java/com/baba/israelitv/
  model/     Channel.kt                — ChannelDefinition, ResolvedChannel
  data/      ChannelCatalog.kt         — the 4 channels + fallback URLs
             M3uPlaylistParser.kt      — extended-M3U parser
             ChannelRepository.kt      — fetch + match + fallback logic
  ui/        MainActivity.kt           — hosts the Leanback browse fragment
             ChannelsBrowseFragment.kt — channel grid + refresh action
             ChannelCardPresenter.kt   — leanback card for a channel
             ActionCardPresenter.kt    — leanback card for the refresh action
             PlaybackActivity.kt       — Media3 ExoPlayer HLS playback + retry
```

Playback uses **Media3 ExoPlayer** with the HLS extension
(`androidx.media3:media3-exoplayer-hls`), since all four channels stream
as HLS (`.m3u8`). The UI uses **AndroidX Leanback**, the standard
TV-optimized browse/card UI toolkit.

## Building

Open the project root in Android Studio (Koala or newer) and let it sync —
it will download the Android Gradle Plugin, Kotlin, AndroidX, and Media3
dependencies from Google's and Maven Central's repositories. Or from the
command line:

```
./gradlew assembleDebug
```

> This project was written and reviewed in a sandboxed environment without
> network access to Google's Maven repository or an Android SDK/emulator,
> so it has **not** been compiled or run here. Please build and test it in
> Android Studio (or on a device via `adb install`) before relying on it —
> in particular, verify playback on an actual Android TV device/emulator,
> since HLS playback behavior can't be exercised in this sandbox.

Minimum SDK 21 (Android 5.0), target/compile SDK 34.

## Important notes

- **Geo-restriction**: these are free-to-air Israeli broadcasts, but their
  streaming CDNs may only be reachable from an Israeli IP address. If a
  channel won't load outside Israel, that's the broadcaster's geoblock, not
  a bug in the app.
- **Personal use**: this app streams each broadcaster's own publicly
  reachable live feed for personal viewing; it doesn't download, store, or
  redistribute content. Respect each broadcaster's terms of service.
- **Stream availability**: the underlying CDN links are third-party
  infrastructure outside this app's control and can change or go down
  without notice — that's exactly what the runtime playlist resolution
  (above) is designed to route around, but it isn't a guarantee.
