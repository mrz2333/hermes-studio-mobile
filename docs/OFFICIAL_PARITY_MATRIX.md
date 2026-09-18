# Official HStudio App — Parity Matrix

Baseline: **official HStudio APK v1.0.3** (`com.ekkostudio.ai`, versionCode 120,
SHA-256 `bf069da2…20d62`) — see `docs/reference/apk-1.0.3/README.md` for
provenance. This is the vendor-signed original, *not* this project's own
`HStudio-Direct-v1.9.1.apk`.

## What the official app actually is

This matters more than any individual row, because it sets the size of the task.
The official APK is a **uni-app shell wrapped around the Hermes Studio web
client**. It contains exactly six native pages:

| Route | Role |
|---|---|
| `pages/bootstrap` | splash / boot handoff |
| `pages/login` | account sign-in |
| `pages/devices` | device list, add-device pairing, account menu |
| `pages/index` | **hosts the full Studio web client** (compiled v0.7.13) |
| `pages/about` | about / legal |
| `pages/location-picker` | geolocation helper (uni-app system page) |

Four of those are genuinely native. `pages/index` is not a native screen at all —
its stylesheet is 522 KB and is the compiled output of the upstream web client,
whose feature surface is **38 views and 139 components**.

So "replicate the official app" means two different jobs with two different
ground truths:

- **Shell pages** → ground truth is the APK's own compiled CSS/JS. The uni-app
  source is closed; nothing else specifies these screens.
- **Chat and everything behind it** → ground truth is upstream **v0.7.13** Vue
  source (pinned at `/root/Hermes任务文件/HStudioDirect/ref-v0.7.13`), because
  the APK just renders that project.

## Verification legend

Status here is deliberately split, because "the file exists" and "it matches the
official pixels" are different claims and this project has previously conflated
them.

- **Surface** — does a Compose implementation of this area exist?
  `yes` / `partial` / `no`.
- **Verified** — has it been checked against official ground truth?
  - `css` — compared line-by-line against the APK stylesheet
  - `src` — compared against pinned v0.7.13 Vue source
  - `name` — **inferred from the Compose screen name only; not yet compared**
  - `no` — never checked

> **No row in this document is verified by a device screenshot.** No emulator or
> physical device was available (see *Verification limits*). Every `css`/`src`
> claim below is a static comparison of stylesheet or source values. Static
> comparison cannot catch rendering, font-metric, or platform-inset differences.

---

## Part A — Native shell pages

These are the screens the official app implements natively, so they are the most
directly comparable and the highest-value parity work.

### A1. Login (`pages/login`)

Ground truth: `css/pages-login.css`. The phone layout is the
`@media screen and (max-width: 600px)` block — the app is phone-first, and the
unqualified rules are the *tablet/desktop* variant. This is the single most
common way to get this page wrong.

| Element | Official (phone block) | Compose | Status |
|---|---|---|---|
| `.auth-title` | 18px/26px, weight 600 | `18.sp`/`26.sp` SemiBold | ✅ match |
| `.auth-description` | margin-top 3px, 11px/17px | 3dp, `11.sp`/`17.sp` | ✅ match |
| `.auth-form` | margin-top 16px | 16dp | ✅ match |
| `.field-group + .field-group` | margin-top 12px | 12dp | ✅ match |
| `.field-label` | margin-bottom 6px, 11px, weight 500 | 6dp, `11.sp` Medium | ✅ match |
| `.field-shell` | height 42px, padding 0 11px, radius 7px | 42dp, 11dp, 7dp | ✅ match |
| `.field-icon` | 18×18, margin-right 8px | 18dp, 8dp | ✅ match |
| `.password-toggle` | 28×28 | 28dp | ✅ match |
| `.primary-button` | height 42px, radius 7px, 13px, margin-top 16px | 42dp, 7dp, `13.sp`, 16dp | ✅ match |
| `.banner-logo` | 38×38, radius 8px | 38dp, 8dp | ✅ match |
| `.banner-brand-name` | 13px/19px | `13.sp`/`19.sp` | ✅ match |
| `.banner-kicker` | 7px/10px | `7.sp` | ✅ match |
| `.banner-title` | 21px/29px, letter-spacing −0.4px | `21.sp`/`29.sp`, −0.4sp | ✅ match |
| `.banner-description` | 10px/15px | `10.sp`/`15.sp` | ✅ match |
| `.login-content` | radius 22px 22px 0 0, margin-top −18px, padding 22px 20px | 22dp, −18dp, 22dp/20dp | ✅ match |
| `--ink-focus-ring` halo | `0 0 0 3px` on focus | 3dp halo | ✅ match |
| **`.login-banner` min-height** | **200px** | was 182dp | 🔧 **fixed this pass** |
| **`.banner-inner` padding** | **top 16px, bottom 36px** | was 18dp/18dp | 🔧 **fixed this pass** |
| **`.login-content` padding-bottom** | **14px + safe-area** | was 42dp | 🔧 **fixed this pass** |
| **`.banner-copy` max-width** | **300px** | absent | 🔧 **fixed this pass** |
| **`.banner-description` margin-top** | **5px** | was 4dp | 🔧 **fixed this pass** |
| `.api-route-switch` | cloud↔LAN route selector | not found | ❌ missing |
| `.legal-overlay` / terms | legal text overlay | not found | ❌ missing |
| `.social-login-*`, `.google-login-*`, `.apple-login-*` | social sign-in | n/a | ⛔ intentionally absent |
| `.verification-row` | email verification code | n/a | ⛔ intentionally absent |

The five `🔧` rows were corrected in this pass against the phone block; they are
recorded as evidence in `docs/parity/login.md`. Social sign-in and email
verification are **deliberately not replicated** — they authenticate against the
vendor's cloud, which direct-connect removes. Their absence is correct, but note
that the official page's *layout* still reserves space for them.

### A2. Devices (`pages/devices`)

Ground truth: `css/pages-devices.css` (147 classes). **Implemented natively since
v1.9.2** — `DevicesScreen.kt` renders the official page as `Screen.Devices`, and
the earlier project-specific `Screen.Instances` no longer exists. The per-value
audit record is `docs/parity/devices.md`. Statuses below reflect that pass.

| Official block | What it is | Compose status |
|---|---|---|
| `.device-grid`, `.device-card`, `.add-device-card` | device tiles + add tile | ✅ yes |
| `.device-symbol`/`-screen`/`-stand` | illustrated device glyph | ✅ yes |
| `.device-source-tag--local` / `--cloud` | **route provenance badge** | ✅ local on every device; `--cloud` tone exists but is unreachable |
| `.device-endpoint-tag--desktop` / `--web` | endpoint kind badge | ✅ yes |
| `.device-route-tag` | active route badge | ✅ yes |
| `.status-dot`, `.device-status--online` | presence | ✅ yes |
| `.pairing-overlay`, `.pairing-panel`, `.method-list`, `.method-option` | add-device flow | ✅ overlay + panel + manual form; scan-to-add is an entry whose step says it is not wired |
| `.api-route-switch`, `.api-route-compact-option` | **cloud vs LAN route picker** | ✅ yes — cloud option rendered disabled with the reason stated |
| `.account-popover`, `.account-entitlements` | account menu + cloud entitlement list | ✅ popover; entitlement block renders the official "unavailable" state, no fabricated plan |
| `.account-logout`, `.account-delete`, `.account-about` | account actions | ✅ logout + about; delete-account deliberately absent (cloud-only) |
| `.machine-id` | machine identifier row | ✅ yes |
| `.loading-state`, `.error-state`, `.retry-action` | page states | ✅ yes |

**The route-picker gap is now closed.** The official app ships a first-class
`api-route-switch` for choosing between the cloud relay and a LAN/direct route,
plus `device-source-tag--local`/`--cloud` badges that tell the user which route a
device is on. Both landed in v1.9.2 (`DevicesScreen.kt`). The cloud option is
rendered disabled with the reason printed under the switch, and `setApiRoute`
rejects anything other than LAN at the view-model level, so no future call site
can route traffic at the vendor's cloud by mistake.

Responsive breakpoints differ from login: devices uses `max-width: 520px` plus
two landscape queries, not `max-width: 600px`.

### A3. About, bootstrap, location-picker

| Page | Size | Status |
|---|---|---|
| `pages/about` | 2.2 KB CSS | ❌ missing (About exists only as a settings row) |
| `pages/bootstrap` | 343 B CSS | n/a — no equivalent needed |
| `pages/location-picker` | 10 KB CSS | n/a — uni-app system page, not app UI |

---

## Part B — Embedded web client surface (v0.7.13, 38 views)

The Compose app implements **39 screens**; the official client has **38 views**
plus 139 components. Name-level correspondence is high. **No row in this table
has been compared against v0.7.13 source yet** — status is `name` at best. This
table is a work plan, not a completion record.

| Official view | Compose screen | Surface | Verified |
|---|---|---|---|
| `LoginView` | `Login` | yes | css |
| `hermes/ChatView` | `Conversation` | yes | src (partial) |
| `hermes/GroupChatView` | `Room` | yes | name |
| `hermes/SharedGroupChatView` | `Groups` | partial | no |
| `hermes/GroupChatLinkView` | `Connections` | partial | no |
| `hermes/HistoryView` | `Chats` | partial | name |
| `hermes/DevicesView` | `Instances` / `Connections` | partial | name |
| `hermes/SettingsView`, `HermesSettingsView` | `Settings`, `SettingsGroup` | yes | name |
| `hermes/ChannelsView` | `Channels`, `Channel` | yes | name |
| `hermes/JobsView` | `CronJobs`, `CronJob`, `CronHistory` | yes | name |
| `hermes/KanbanView` | `Kanban`, `KanbanTask` | yes | name |
| `hermes/SkillsView` | `Skills`, `Skill` | yes | name |
| `hermes/SkillsUsageView` | `Insights` | partial | no |
| `hermes/PluginsView` | `Plugins` | yes | name |
| `hermes/McpManagerView` | `Mcp` | yes | name |
| `hermes/MemoryView` | `SettingsGroup.Memory` | partial | no |
| `hermes/ModelsView` | (inside `SettingsGroup.Models`) | partial | no |
| `hermes/PerformanceView` | `Insights` | partial | no |
| `hermes/PetdexView` | `Pets` | yes | name |
| `hermes/ProfilesView` | `Profiles` | yes | name |
| `hermes/ThemeView` | `Appearance` | yes | name |
| `hermes/UsageView` | `Insights` | partial | no |
| `hermes/VersionPreviewView` | `RuntimeVersions` | yes | name |
| `hermes/WorkflowView` | `Workflows` | yes | name |
| `hermes/JourneyView` | `Journey` | yes | name |
| `hermes/FilesView` | `Files` | yes | name |
| `hermes/LogsView` | `Logs` | yes | name |
| `hermes/AgentManagerView` | `AgentRuntimes`, `AgentHub` | yes | name |
| `hermes/GlobalAgentView` | `GlobalAgent` | yes | name |
| `ekko/{Settings,Memory,Skills,Mcp}View` | `EkkoHub` | yes | name |
| `hermes/DesktopBrowserView` | — | no | n/a — desktop-only surface |
| `hermes/DesktopPetView` | `Pets` | partial | no |
| `hermes/TerminalView` | — | no | n/a — desktop-only surface |
| `social-messages/SocialMessagesView` | — | no | ❌ missing |

`Webhooks` and `Journey` have no upstream view of their own — webhooks are a
settings component and Journey is `JourneyView`. `Onboarding` and `Loading` are
this project's own additions with no official counterpart.

---

## Part C — Design system fidelity

The official stylesheets contain **no hard-coded colours**. Every colour resolves
through an `--ink-*` custom property, and most are themselves user-overridable
(`var(--app-custom-*, <default>)`). Extracted as machine-readable tokens:

- `theme-tokens-light.json` — 52 tokens (defined on `body`)
- `theme-tokens-dark.json` — 50 tokens (defined on `.theme-dark`)

Status: **Compose mirrors the token set** in `Theme.kt`
(`inkTextPrimary`, `inkTextMuted`, `inkLoginBg`, `inkFocusRing`, …), which is the
right architecture — it preserves user wallpaper theming. The mirror was audited
mechanically against the extracted tokens: **23 of 23 comparable tokens match**.
The audit found one real defect, now fixed:

- `inkSelectedBg(dark)` was `0x1FFFFFFF` (α 0.12); the official
  `--ink-selected-bg` dark is `rgba(255,255,255,.18)` (α 0.18). The wrong value
  is exactly `--ink-focus-ring`'s dark alpha — a copy-paste from the adjacent
  function. Dark-mode selection tints were ~33% too faint.
  `OfficialInkTokenTest` now pins the distinction.

Two caveats worth tracking:

- `--ink-icon-filter` is `none` in light but `invert(1)` in dark, i.e. the
  official app recolours icons with a CSS filter rather than swapping assets.
  A Compose port must reproduce that per-theme recolouring or icons will drift.
- 8 further tokens are overridden by the wallpaper surfaces
  (`.app-local-theme-surface--wallpaper` and its conversation/wide variants),
  which the token extraction records but Compose does not yet model.

---

## Part D — Direct-connect capability (must not regress)

This is the project's reason to exist, so it is tracked separately from visual
parity. The official app already contains the LAN capability; this project
unlocks it.

| Capability | Where | Status |
|---|---|---|
| Studio URL + account/password sign-in | `LoginScreen`, `AppViewModel.login` | ✅ present |
| Multi-instance switching | `Screen.Instances`, `Instances.kt` | ✅ present |
| REST to Studio | `HermesApi.kt` | ✅ present |
| Chat socket | `ChatSocket.kt` | ✅ present |
| Group socket | `GroupSocket.kt` | ✅ present |
| Chinese-first UI | `Locales.kt`, `values-zh-rCN/strings.xml` | ✅ present |
| Encrypted credential store | `Store.kt` | ✅ present |
| Official route picker UI | `api-route-switch` | ✅ present since v1.9.2 (cloud option disabled by design) |

---

## Verification limits

Recorded plainly, because several earlier commits in this repo describe work as
"深度复刻" (deep replication) on the strength of static comparison alone.

1. **No device or emulator verification.** No row here is confirmed by a
   screenshot. Font metrics, gesture insets, IME behaviour, and dark-mode
   transitions are all unverified.
2. **No build or unit-test run at the time of writing.** The host has JDK 8
   only, no Gradle, and an Android SDK with no `platforms/`. A toolchain was
   installed into `/opt/android-buildenv` during this pass
   (`scripts/apk/setup-build-env.sh`); results are reported in
   `docs/parity/baseline-tests.md`.
3. **Part B is name-level only.** Treat every `name` row as unstarted work.
4. **The chat screen is the largest unverified area.** It was partly compared
   against v0.7.18 source, which is *five minor versions ahead* of the v0.7.13
   the APK actually ships. Some of that work may match the wrong version.
5. **Version drift is now correctable** — `ref-v0.7.13` is pinned locally, so
   re-auditing the chat surface against the right tag is unblocked.

## Highest-value next steps

1. ~~Implement the devices page (`pages/devices`) natively, including
   `api-route-switch` and the `device-source-tag--local/--cloud` badges.~~
   **Done in v1.9.2** — see §A2 and `docs/parity/devices.md`.
2. Re-audit `Conversation`/`Chats` against **v0.7.13** (not v0.7.18), and record
   in `docs/parity/chat.md`. This is the largest unverified area.
3. Finish the Composer audit: `docs/parity/composer.md` lists the unextracted
   sub-elements (`.input-toolbar`, `.send-button`, `.attachment-chip`,
   `.input-context-meter`) and the unasserted `.input-wrapper` `min-height: 78px`,
   which is what stops the composer collapsing to one text line.
4. Then Settings/Appearance, which are mostly token-driven and cheap.
5. `pages/about` is still unimplemented (§A3) and
   `social-messages/SocialMessagesView` has no Compose surface at all.
6. Everything else in Part B is still `name`-level: each row needs a real
   `v0.7.13` source comparison before it can be called replicated.

## Build/test evidence (updated 2026-09-18)

`docs/parity/baseline-tests.md` records that **this host** cannot build Android
(aarch64 host; AGP resolves an x86-64-only `aapt2`, and Debian's ARM64 `aapt2`
is build-tools 29.0.3 and cannot parse the android-35 resource table). That is
still true.

CI is the authoritative verification path. Every push to `main` runs
`testDebugUnitTest` + `lintDebug` + `assembleRelease` on x86-64 `ubuntu-latest`
and publishes a signed APK. Android CI on `2d7d72f` (v1.9.5) is **green**, so the
Kotlin compiles and the 12 unit tests pass. Claims in `docs/parity/*.md` that say
"not compiled" / "not run" describe the *host*, not CI — read them with that in
mind. No row anywhere in this document is yet verified by a device screenshot
except the v1.9.2 devices pass captured in `releases/v1.9.2-*.png`.
