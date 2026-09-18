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
| `.input-context-meter` | `width: 34px; height: 3px; border-radius: 999px; background: var(--ink-border)` | see below | ❌ not yet |
| `.input-context-meter-fill` | `background: var(--ink-text-muted)` | see below | ❌ not yet |
| `.input-context-status--warning .input-context-meter-fill` | `background: #d59a2d` | see below | ❌ not yet |
| `.input-context-status--danger .input-context-meter-fill` | `background: var(--ink-error)` | see below | ❌ not yet |
| `.toolbar-button[data-v-8aca294f]` | `min-width: 35px; height: 28px; padding: 0 4px 0 6px; gap: 3px; border-radius: 499.5px` | not compared | 🔶 unverified |
| `.toolbar-button--circle` / `.voice-button` / `.send-button` | `width: 30px; min-width: 30px; height: 30px; padding: 0` | not compared | 🔶 unverified |
| `.toolbar-button--disabled` / `.voice-button--disabled` | `opacity: .38` | not compared | 🔶 unverified |
| `.attachment-chip` | `width: 154px; height: 48px; padding: 5px 25px 5px 5px; gap: 7px; border-radius: 9px; background: var(--ink-bg-card-hover)` | not compared | 🔶 unverified |
| `.attachment-chip--image` | `width: 48px; padding: 0; background: transparent` | not compared | 🔶 unverified |
| `.attachment-thumb` / `.attachment-file-icon` | `width: 38px; height: 38px; border-radius: 6px` | not compared | 🔶 unverified |
| `.attachment-chip--image .attachment-thumb` | `width/height: 48px; border-radius: 8px` | not compared | 🔶 unverified |
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

## Not yet done (the honest remainder)

- `.input-context-meter` (34×3px ring, muted fill, `#d59a2d` warning,
  `--ink-error` danger) — **not implemented**. `.input-context-status-*` classes
  are the official way the meter recolours, so a partial version would misreport
  context pressure.
- The whole toolbar button family: `.toolbar-button` (+ `--circle`, `--disabled`),
  `.send-button`, `.voice-button`, `.composer-*` equivalents, and the
  `--ink-send-icon-filter` / `--ink-secondary-icon-filter` recolouring.
- `.attachment-chip` geometry (154×48, `padding: 5px 25px 5px 5px`, radius 9) and
  the image variant (48×48 thumb, radius 8, remove badge at `-5px`).
- `MessageItem.vue` / `MessageList.vue` / `ToolRunCard.vue` /
  `LiveReasoningStatus.vue` / `MessageQueueFloatPanel.vue` have **not** been
  compared against v0.7.13 source in this pass. Only the composer was.

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
