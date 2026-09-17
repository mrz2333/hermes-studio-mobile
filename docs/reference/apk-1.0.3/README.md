# Official HStudio APK reference (v1.0.3)

Ground-truth assets extracted from the **official, vendor-signed** HStudio Android
app. Everything under this directory is evidence for the parity work in
`docs/OFFICIAL_PARITY_MATRIX.md`; it is read-only reference material, never
compiled into the app.

## Baseline provenance

The official app is a **uni-app** bundle. Its native shell pages are closed
source, so the compiled CSS/JS inside the APK is the *only* authoritative
specification for them. Do not substitute the community Compose client or a
rebuilt APK for it.

| | |
|---|---|
| File | `HStudioDirect/phase1-work/HStudio.apk` |
| SHA-256 | `bf069da21eabd3dcf0db3015c5b73db208cb19248d1e0aaeebc375211dd20d62` |
| Package | `com.ekkostudio.ai` |
| versionName / versionCode | `1.0.3` / `120` |
| App label | `Ekko Studio` |
| Signer | `C=CN, ST=Shanghai, O=EKKO Learn AI, OU=Mobile, CN=Hermes Studio` |
| uni-app id | `__UNI__1F41684` |
| Embedded web client | Hermes Studio **v0.7.13** |

### Why 1.0.3 and not 1.0.1

Two pristine vendor-signed builds exist on this machine. Both carry the same
signing certificate and neither contains any direct-connect patch marker
(`HERMES_APP_ENTITLEMENT`, `skipCloudLogin`, `lanDirect`, …), so both are genuine
official releases rather than earlier rebuilds:

| Build | SHA-256 | versionCode | Label |
|---|---|---|---|
| `HStudio.apk` | `e6308c6ac42098f1b308b6cff99133dbb2f1729405a74ff6800be54ab3c52454` | 101 | HStudio |
| `phase1-work/HStudio.apk` | `bf069da21eabd3dcf0db3015c5b73db208cb19248d1e0aaeebc375211dd20d62` | **120** | Ekko Studio |

**1.0.3 is the baseline** because it is the newer official release, and because
the existing Compose implementation was already targeted at it (commit
`0b831b7 Match HStudio 1.0.3 mobile visual system`).

This is *not* the same file as `HStudio直连App/HStudio-Direct-v1.9.1.apk`
(`d1c9fc50…`), which is this project's own patched build and must never be used
as a visual baseline.

## Web client version pin

The APK embeds web client **v0.7.13**. A local checkout of the upstream repo
only carried v0.7.18, so the exact tag was fetched and materialised as a
read-only worktree for comparison:

```sh
git -C /root/Hermes任务文件/hermes-studio/v0.7.18 \
    fetch --depth 1 origin refs/tags/v0.7.13:refs/tags/v0.7.13
git -C /root/Hermes任务文件/hermes-studio/v0.7.18 \
    worktree add --detach /root/Hermes任务文件/HStudioDirect/ref-v0.7.13 v0.7.13
```

Use `ref-v0.7.13` — **not** v0.7.18 — when reading Vue source for parity work.
`docs/STUDIO_V0712_PARITY.md` predates this and tracks v0.7.12, so it drifts from
the shipped app in both directions.

## Contents

| Path | What it is |
|---|---|
| `theme-tokens-light.json` | The 52 `--ink-*` custom properties defined on `body` in `app.css` — the entire light palette |
| `theme-tokens-dark.json` | The 50 `--ink-*` properties defined on `.theme-dark` |
| `css/app.css` | Base stylesheet: theme tokens, Quill + highlight.js vendor styles |
| `css/pages-login.css` | Login page — includes the `@media (max-width:600px)` phone block |
| `css/pages-devices.css` | Device list / add-device page |
| `css/pages-index.css` | The embedded web client's compiled CSS (522 KB, ≈v0.7.13) |
| `css/pages-about.css`, `css/pages-bootstrap.css`, `css/pages-location-picker.css` | Remaining uni-app shell pages |
| `icons/` | 80 official SVG/PNG icons from `static/icons` |
| `agents/` | 7 agent-family logos (claude, codex, deepseek, ekko, grok, opencode, pi) |
| `uni-manifest.json` | Decoded `manifest.json` from the bundle |

## Reading the CSS

uni-app scopes every selector with `[data-v-<hash>]`. `scripts/apk/css-rules.py`
strips that and lets you query rules directly:

```sh
scripts/apk/css-rules.py docs/reference/apk-1.0.3/css/pages-login.css field-shell
scripts/apk/css-rules.py docs/reference/apk-1.0.3/css/app.css --props --ink-text-primary
```

### The `--ink-*` token contract

Colours are **never hard-coded** in the official stylesheets. Every one resolves
through an `--ink-*` property, and most of those are themselves overridable by a
user wallpaper theme (`var(--app-custom-*, <default>)`). A parity implementation
that inlines a hex value instead of routing through the token loses the custom
theme support the official app has — see `Theme.kt` for the Compose mirror.

Note that `--ink-*` values differ by more than colour between themes; e.g.
`--ink-icon-filter` is `none` in light but `invert(1)` in dark, so icons are
recoloured by a filter rather than by swapping assets.

## Reproducing the extraction

The bundles were unpacked into a scratch directory, never in place:

```sh
unzip -o -q <official.apk> "assets/apps/*" -d /tmp/apkwork/v103
# -> assets/apps/__UNI__1F41684/www/{app.css,app-service.js,pages/,static/}
```

`app-service.js` (3.3 MB) holds every page's compiled Vue render function; it is
the source for structural questions the CSS cannot answer.
