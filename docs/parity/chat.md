# Chat / Composer parity record

Ground truth, in this order:

| Source | What it settles |
|---|---|
| `docs/reference/apk-1.0.3/css/pages-index.css`, scope `data-v-8aca294f` | the official mobile composer stylesheet — every size, radius and colour below |
| scope `data-v-744a5cd2` in the same file | the toolbar button family (`.composer-toolbar-button`, `.composer-voice-button`, `.composer-send-button`) |
| `/root/Hermes任务文件/HStudioDirect/ref-v0.7.13/packages/client/src/components/hermes/chat/` | render structure: `ChatInput.vue`, `MessageItem.vue`, `ChatPanel.vue`, `ToolRunCard.vue`, `LiveReasoningStatus.vue`, `MessageQueueFloatPanel.vue`, `SessionListItem.vue` |
| `docs/OFFICIAL_PARITY_MATRIX.md` §B | the row this pass was scoped against |

Why v0.7.13: the baseline APK (v1.0.3, `com.ekkostudio.ai`, versionCode 120) bundles
the web client at `const Ar="0.7.13"` in
`assets/apps/__UNI__1F41684/www/app-service.js`. Earlier chat work was partly
compared against v0.7.18 source, which is five minor versions ahead of what the
APK actually ships, so this pass re-checks against v0.7.13 and the APK stylesheet.

## Implementation

`android/app/src/main/java/xyz/rflg/hstudiodirect/MainActivity.kt` —
`Composer()` (≈2471), `ComposerActionButton()` (≈2829), `ConversationScreen()`
(≈1509), `MessageBubble()` (≈1873), `ThinkingTimeline()` (≈2028).

## Composer — values verified against the official stylesheet

Extracted rule-by-rule from `pages-index.css`; each official value below was read
out of the stylesheet in this pass, not inferred.

| Selector | Official value | Compose | Status |
|---|---|---|---|
| `.chat-input-area` | `padding: 8px 12px 0` | `start 12 / end 12 / top 8` | ✅ match |
| `.chat-input-area` (gutter block) | `padding-right/left: 20px` in the `min-width:769px` media block | not applied on phones | ✅ n/a (phone-first) |
| `.chat-input-area` bottom | `padding-bottom: 0` + separate `.safe-area-bottom` block (`height: env(safe-area-inset-bottom); min-height: 12px`) | `navigationBarsPadding()` + 7dp | 🔶 approximation |
| `.input-wrapper` | `min-height: 78px` | `heightIn(min = 78.dp)` | ✅ **fixed this pass** |
| `.input-wrapper` | `padding: 8px 12px 6px` | `top 8 / start 12 / end 12 / bottom 6` | ✅ **fixed this pass** |
| `.input-wrapper` | `flex-direction: column; gap: 4px` | `Column` + `spacedBy(4.dp)` on inner rows | ✅ match |
| `.input-wrapper` | `background: var(--ink-bg-card)` | `inkCardBg(inkDark)` | ✅ match |
| `.input-wrapper` | `border: 1px solid var(--ink-input-border)` | `BorderStroke(1.dp, inkInputBorder(inkDark))` | ✅ match |
| `.input-wrapper` | `border-radius: 18px` | `RoundedCornerShape(18.dp)` | ✅ match |
| `.input-wrapper` | `box-shadow: var(--ink-shadow-lg)` | `shadowElevation = 8.dp` | 🔶 approximation (CSS box-shadow ≠ Compose elevation) |
| `.input-textarea` | `min-height: 24px` | `heightIn(min = 24.dp)` | ✅ **fixed this pass** |
| `.input-textarea` | `background: transparent` | `BasicTextField` with no container | ✅ **fixed this pass** |
| `.input-textarea` | `border: 0; padding: 0` | no container, no inner padding | ✅ **fixed this pass** |
| `.input-textarea` | `font-size: 14px` | `14.sp` | ✅ **fixed this pass** |
| `.input-textarea` | `line-height: 1.5` | `lineHeight = 21.sp` (14 × 1.5) | ✅ **fixed this pass** |
| `.input-textarea` | `flex: 1; width: 100%` | `fillMaxWidth()` | ✅ match |
| `.input-placeholder` | `color: var(--ink-text-muted)` | `inkTextMuted(inkDark)` + `nowrap` ellipsis | ✅ match |
| `.input-toolbar` | `min-height: 30px; margin-top: auto` | `Arrangement.SpaceBetween` pins the toolbar to the bottom | ✅ **fixed this pass** |
| `.input-toolbar` | `justify-content: space-between; gap: 12px` | `SpaceBetween`; row gap not asserted | 🔶 partial |
| `.input-context-status` | `display: flex; align-items: center; gap: 6px; color: var(--ink-text-muted)` | `Row` + `spacedBy(6.dp)`, muted | ✅ **fixed this pass** (was a `Column`) |
| `.input-context-label` | `max-width: 112px; font-size: 9px; line-height: 14px; ellipsis; nowrap` | `widthIn(max = 112.dp)`, `9.sp`/`14.sp`, ellipsis | ✅ **fixed this pass** |
| `.input-context-meter` | `width: 34px; height: 3px; border-radius: 999px; background: var(--ink-border)` | `34.dp`×`3.dp`, `999.dp` radius, `inkBorder` track | ✅ **fixed this pass** (was a full-width bar, 84–122dp) |
| `.input-context-meter-fill` | `height: 100%; background: var(--ink-text-muted)` | `fillMaxHeight().fillMaxWidth(pct)` | ✅ **fixed this pass** (fill was the *label's* colour, not muted) |
| `.input-context-status--warning` | `color: #d59a2d` (whole row) | `CONTEXT_WARNING = 0xFFD59A2D` on row + fill | ✅ **fixed this pass** (was `0xFFC28A30`) |
| `.input-context-status--danger` | `color: var(--ink-error)` (whole row) | `inkError(inkDark)` on row + fill | ✅ **fixed this pass** (was `colorScheme.error`) |
| `.input-context-status--pressed` / `--disabled` | `opacity: .66` / `.52` | not applied | 🔶 unverified |
| `.input-context-status` thresholds | `app-service.js`: `--warning: pct > 60 && pct <= 80`, `--danger: pct > 80` | same thresholds | ✅ match (confirmed in the bundle this pass) |
| `.input-context-status` interaction | `role="button"`, aria-name 点击编辑上下文长度 | rendered read-only, no click handler | ⛔ intentionally absent (editing the context window is a Studio-side control this build does not open) |
| `.toolbar-button[data-v-8aca294f]` | `min-width: 35px; height: 28px; padding: 0 4px 0 6px; gap: 3px; border-radius: 499.5px` | not compared | 🔶 unverified |
| `.toolbar-button--circle` / `.voice-button` / `.send-button` | `width: 30px; min-width: 30px; height: 30px; padding: 0` | not compared | 🔶 unverified |
| `.toolbar-button--disabled` / `.voice-button--disabled` | `opacity: .38` | not compared | 🔶 unverified |
| `.attachment-chip` | `width: 154px; height: 48px; padding: 5px 25px 5px 5px; gap: 7px; border-radius: 9px; background: var(--ink-bg-card-hover)` | `Surface(154×48, radius 9, card-hover) + Row(padding 5/25/5/5, gap 7)` | ✅ match |
| `.attachment-chip--image` | `width: 48px; padding: 0; background: transparent` | `48.dp` Surface, `Color.Transparent`, zero padding | ✅ match |
| `.attachment-thumb` / `.attachment-file-icon` | `width: 38px; height: 38px; border-radius: 6px` | `38.dp` Box, `RoundedCornerShape(6.dp)` | ✅ match |
| `.attachment-chip--image .attachment-thumb` | `width/height: 48px; border-radius: 8px` | `48.dp` Box, `RoundedCornerShape(8.dp)` | ✅ match |
| `.attachment-file-icon` | `display:flex; align-items:center; justify-content:center; color:var(--ink-text-muted); background:var(--ink-bg-code); font-size:11px; font-weight:700` | `inkCodeBg` fill, `inkTextMuted` 11sp/700, centered | ✅ match |
| `.attachment-copy` | `display:flex; min-width:0; flex:1; flex-direction:column; gap:2px` | `Column` + `Modifier.weight(1f)` + `spacedBy(2.dp)` | ✅ match |
| `.attachment-name` | `overflow:hidden; text-overflow:ellipsis; white-space:nowrap; color:var(--ink-text-primary); font-size:11px; line-height:15px` | `maxLines=1, overflow=Ellipsis`, 11sp/15sp, primary | ✅ match |
| `.attachment-size` | `color:var(--ink-text-muted); font-size:9px; line-height:13px` | intentionally absent | ⛔ Upload has no byte-size field |
| `.attachment-remove` | `position:absolute; top:3px; right:5px; width:17px; height:17px; color:var(--ink-text-muted); font-size:16px; line-height:16px; text-align:center` | `offset(0,3) + padding(end=5)`, 17.dp box, 16.dp muted Close | ✅ match |
| `.attachment-chip--image .attachment-remove` | `top:-5px; right:-5px; color:#fff; background:rgba(0,0,0,.66); border-radius:999px; font-size:14px; line-height:16px` | `offset(5,-5) + CircleShape + rgba(0,0,0,.66) + 14dp white Close` | ✅ match |
| `.composer-toolbar-button` / `-voice-button` / `-send-button` (`744a5cd2`) | `30×30; border-radius: 999px; color: var(--ink-text-secondary)` | not compared | 🔶 unverified |
| `.composer-send-button` | `color: #fff; background: var(--ink-text-muted)` | not compared | 🔶 unverified |
| `.composer-send-button--active` | `color: var(--ink-on-accent); background: var(--ink-accent)` | not compared | 🔶 unverified |
| `.composer-send-button > uni-image` | `17×17` | not compared | 🔶 unverified |
| `.composer-send-button--active uni-image` | `filter: var(--ink-send-icon-filter)` | not compared | 🔶 unverified |
| `.composer-toolbar-button uni-image` | `16×16; filter: var(--ink-secondary-icon-filter, var(--ink-icon-filter))` | not compared | 🔶 unverified |
| `.composer-toolbar-button--active` | `color: var(--ink-accent); background: var(--ink-selected-bg)` | not compared | 🔶 unverified |

### The one structural correction this pass made

`.input-textarea` is `background: transparent; border: 0; padding: 0`. The
previous implementation used an `OutlinedTextField` with
`inkInputBg` fill, an outline in `--ink-input-border`, and its own
`RoundedCornerShape(10.dp)` — i.e. **a second rounded box nested inside
`.input-wrapper`**, which the official composer does not have. It is now a
`BasicTextField` inside a transparent `Box`, so the only painted surface is
`.input-wrapper` itself. The 10px radius and `inkInputBg` fill were not
"close enough": the official card is one 18px-radius surface, not a box in a box.

## HistoryView (session list) — values verified against the official stylesheet

`SessionRow()` (≈1067) and the `ChatsScreen` list body (≈944) in `MainActivity.kt`,
checked against `pages-index.css` scope `b5cd62b3` and `SessionListItem.vue`.

| Selector | Official value | Compose | Status |
|---|---|---|---|
| `.session-item` | `display:flex; align-items:center; width:100%; margin-bottom:2px; padding:8px 10px; color:var(--ink-text-secondary); border-radius:6px` | `Row`-rooted `Column`, `clip(RoundedCornerShape(6.dp))`, `padding(horizontal=10, vertical=8)`, list `spacedBy(2.dp)` for the `margin-bottom` | ✅ match (was 13/11 padding, no radius, in a grouped card) |
| `.session-item--active` | `color:var(--ink-text-primary); background:var(--ink-bg-secondary)` | `inkSecondaryBg` fill + `inkTextPrimary` title | ✅ match |
| `.session-item--active .session-title` | `font-weight:550` | `FontWeight(550)` on the title | ✅ match (was `W500`) |
| `.session-item--pressed` | `background:var(--ink-pressed)` | `combinedClickable` ripple | 🔶 approximation (no explicit pressed tint) |
| `.session-title` | `font-size:13px; line-height:18px; text-overflow:ellipsis; white-space:nowrap` | `13.sp`/`18.sp`, `maxLines=1`, `Ellipsis` | ✅ match (was `titleSmall`, `maxLines=2`) |
| `.session-time` | `flex-shrink:0; color:var(--ink-text-muted); font-size:11px; line-height:16px` | `labelSmall` 11sp/16sp, `inkTextMuted` | ✅ match (was `onSurfaceVariant`) |
| `.session-item-unread-dot` | `6px; border-radius:50%; background:var(--ink-accent)` | 6dp `CircleShape`, `inkAccent` fill (reused as the running marker) | ✅ match |
| `.session-list` | `padding:0 6px 12px` | list `contentPadding` horizontal 6dp + the `StudioHorizontalPadding` gutter | 🔶 approximation |
| `.session-loading` / `.session-empty` | `padding:16px 10px; font-size:12px; color:$text-muted; text-align:center` | `EmptyNote` / `LoadingRow` (existing centered muted rows) | 🔶 approximation (text matches i18n `chat.noSessions`/`common.loading`) |

The grouped-card + divider list (`StudioGroupedCard` + `StudioCardDivider`) was
replaced with standalone 6dp-radius rows spaced 2dp apart, which is how the
official `.session-item` rows sit in `.session-items` (each its own
`margin-bottom:2px` tile, not a single card with hairline dividers). The desktop
`SessionListItem.vue` carries an 18px per-agent logo, a 16px profile avatar, and
a category tag in a `.session-item-agent-row`; this mobile build keeps a 34dp
profile avatar as the leading mark (the 18px agent-logo needs a per-agent SVG
asset not bundled here) and drops the trailing chevron the official row does not
have. That is a documented adaptation, not a parity claim.

## ChatView message list — values verified against the official stylesheet

`MessageBubble()` (≈1935), `ConversationScreen()` empty state (≈1687),
`SessionRunningBar()` (≈1170) in `MainActivity.kt`, checked against
`pages-index.css` scope `c0c550c3` and `MessageItem.vue`.

| Selector | Official value | Compose | Status |
|---|---|---|---|
| `.message.user .msg-body` | `max-width:75%` | `fillMaxWidth(0.75f)` | ✅ **fixed this pass** (was `fillMaxWidth(1f)`) |
| `.message.assistant .msg-body` | `max-width:80%` | `fillMaxWidth(0.8f)` | ✅ **fixed this pass** (was `fillMaxWidth(1f)`) |
| `.message-bubble` | `padding:10px 14px; border-radius:10px; background:var(--ink-bg-message)` | `Card` `padding(14,10)`, `RoundedCornerShape(10.dp)`, `inkMessageBg` | ✅ match |
| `.message-bubble--error` | `color:var(--ink-error); background:var(--ink-error-soft); border:1px solid var(--ink-error-soft)` | `inkErrorSoft` fill + `inkError` text | ✅ match |
| `.message-author` | `min-height:22px; margin:0 0 4px 2px; gap:6px; --ink-text-secondary; 12px` | `Row` `padding(2,4)`, 12sp/16sp Medium, `inkTextSecondary` | ✅ **fixed this pass** (was an `.agent-badge` pill, 8sp/600) |
| `.message-author-name` | `color:var(--ink-text-secondary); font-size:12px; font-weight:500; line-height:16px` | 12sp/16sp `FontWeight.Medium`, `inkTextSecondary` | ✅ **fixed this pass** |
| `.msg-avatar` (assistant) | `width:22px; height:22px; border-radius:50%` | `ProfileAvatar(size=22.dp)`, now inside the author row above the bubble | ✅ **fixed this pass** (was beside the bubble) |
| `.empty-state` | `flex column centered; gap:12px; color:--ink-text-muted` | `Column` centered, `spacedBy(12.dp)`, `inkTextMuted` | ✅ **fixed this pass** (was a `💬` emoji + `Space`) |
| `.empty-logo` | `48×48; opacity:.25` | `ProfileAvatar(size=48.dp)` in a `.alpha(0.25f)` Box | ✅ **fixed this pass** |
| `.session-running-track` | `width:100%; height:2px; margin-top:2px; background:var(--ink-selected-bg); border-radius:999px` | `fillMaxWidth`, `height(2.dp)`, `padding(top=2.dp)`, `inkSelectedBg`, `999.dp` | ✅ **fixed this pass** (track was `surfaceVariant α.3`) |
| `.session-running-light` | `width:50%; rainbow gradient; transform:translate3d(-110%,0,0); animation 1.8s linear` | `fillMaxWidth(0.5f)`, rainbow `linearGradient`, `graphicsLayer{translationX = size.width*(p*2.2-1.1)}`, 1800ms | ✅ **fixed this pass** (offset was a fixed dp guess) |

### What this pass corrected in the message list

- **Bubble width.** The previous build let every bubble span the full row
  (`fillMaxWidth(1f)`) with a comment claiming the 75%/80% caps belonged to the
  composer's `8aca294f` scope. They do not — `.message.user .msg-body{max-width:75%}`
  and `.message.assistant .msg-body{max-width:80%}` are message-list rules in
  scope `c0c550c3`, shadowing an earlier `max-width:100%` further up the cascade.
- **Author row.** The assistant sender was rendered as an `.agent-badge` pill
  (8sp/600, `--ink-bg-secondary`) with the 22dp avatar floating beside the
  bubble. The official `.message-author` is a 12sp/500 `--ink-text-secondary`
  text line above the bubble, with the 22dp `.msg-avatar` inside that row. Both
  roles now have an author row; the user row mirrors `.user-message-author`
  (name + avatar, right-aligned).
- **Empty conversation.** Replaced the `💬` emoji with the official
  `.empty-state` layout: a 48dp agent avatar at opacity .25 above the muted
  14sp empty-state text.
- **Running bar.** The `.session-running-track` fill is `--ink-selected-bg`
  (was `surfaceVariant` α.3), and the light band now sweeps `-110%→+110%` of
  its own width via `graphicsLayer` `translationX` (was a fixed
  `progress*200-50 dp` offset that did not track the band width).

`ToolRunCard()` (≈2254), `ToolRunItem()` (≈2383) and `ThinkingTimeline()` (≈2069)
were already aligned to `.tool-run-card`/`.tool-run-header`/`.tool-run-items`
and `.thinking-block`/`.thinking-header` in a prior pass and re-checked against
the stylesheet this pass; no change was needed.

## Not yet done (the honest remainder)

- The whole toolbar button family: `.toolbar-button` (+ `--circle`, `--disabled`),
  `.send-button`, `.voice-button`, `.composer-*` equivalents, and the
  `--ink-send-icon-filter` / `--ink-secondary-icon-filter` recolouring.
- The official **mobile chat-header** (`.chat-header` `@media (max-width:768px)`
  is `padding:calc(16px + safe-area-top) 12px 16px 52px` and hides
  `.header-session-title`) is not yet matched — `ConversationTopBar` still uses
  a Material `TopAppBar` with an always-visible title.
- The official **streaming indicator** (`LiveReasoningStatus` — `.thinking-status`
  shimmer label + `.live-reasoning-detail` 26px pill) is not yet replicated as a
  list-level indicator; `ThinkingTimeline` covers the in-bubble `.thinking-block`
  but the standalone `streaming-indicator` row is still a plain spinner.
- `MessageQueueFloatPanel.vue` (`.queue-float-panel`) has not been compared; the
  current queue rendering is a horizontal scroll of `Surface` chips, not the
  official floating panel.

## Verification

| Check | Result |
|---|---|
| Official CSS values vs `MainActivity.kt` | read out of `pages-index.css` scope `8aca294f` rule by rule in this pass |
| Kotlin compile | **not run on this host** |
| Unit tests | **not run on this host** |
| Device/emulator | **not available** |

### Build and test limits

Unchanged from `docs/parity/baseline-tests.md`: this host is aarch64 and AGP 8.9.2
resolves an x86-64-only `aapt2`, so nothing here can be compiled locally. The
authoritative check is the Android CI workflow, which runs
`testDebugUnitTest` + `lintDebug` + `assembleRelease` on x86-64 `ubuntu-latest`.

**No claim in this document is backed by a screenshot.** The last device-verified
pass was v1.9.2 (`releases/v1.9.2-*.png`); nothing after it has been run on a
device.
