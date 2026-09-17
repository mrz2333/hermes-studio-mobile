# Parity record — Chat composer (`ChatInput.vue`)

**Ground truth**: `docs/reference/apk-1.0.3/css/pages-index.css` — the compiled
web client CSS *as shipped inside the official APK*, so it is version-exact by
construction.
**Compose target**: `MainActivity.kt:2474` (`Composer`), `:2824`
(`ComposerActionButton`).
**Method**: static stylesheet comparison. **No device screenshot.**

## Version-drift check (the reason this page needed re-auditing)

Prior composer work (`8163a5d`, `cf7a5ce`) was done against the v0.7.18 checkout,
while the APK ships **v0.7.13**. `ChatInput.vue` does differ between those tags,
so the concern was real. It turns out to be benign:

| | v0.7.13 | v0.7.18 |
|---|---|---|
| Class family | `.input-*` | `.input-*` |
| Classes present | `input-area`, `input-wrapper`, `input-textarea`, `input-toolbar`, `input-top-bar`, `input-actions`, `send-button`, `attachment-thumb`, … | same set |
| Difference | — | adds `attachment-thumb-button` |

The composer's class vocabulary is **stable across both versions**; v0.7.18 only
adds one wrapper element. So the existing composer work is not invalidated by the
version mismatch. This is a genuine relief rather than a confirmation — it had to
be checked, and it could have gone the other way.

Note `pages-index.css` *also* contains a `composer-*` family (≈90 classes:
`composer-wrapper`, `composer-send-button`, `composer-voice-button`,
`composer-safe-area`, …). **That family is not what v0.7.13 renders** — the
shipped `ChatInput.vue` emits `input-*`. Do not port `composer-*` by mistake;
its presence in the bundle is a red herring from another component or a dead
code path.

## `.input-wrapper` — the composer card

| Property | Official | Compose | Status |
|---|---|---|---|
| `border-radius` | `18px` | `RoundedCornerShape(18.dp)` | ✅ match |
| `box-shadow` | `var(--ink-shadow-lg)` = `0 8px 24px rgba(0,0,0,.09)` | `shadowElevation = 8.dp` | 🔶 approximate |
| `background` | `var(--ink-bg-card)` | via ink token | ✅ |
| `border` | `1px solid var(--ink-input-border)` | referenced in comment; verify | 🔶 unverified |
| `min-height` | `78px` | not asserted | ❌ missing |
| `padding` | `8px 12px 6px` | `12.dp` horizontal, partial | 🔶 partial |
| `gap` | `4px` | `spacedBy(4.dp)` (inner row) | ✅ match |

`min-height: 78px` is the notable gap: it is what stops the composer from
collapsing to a single text line, and Compose does not assert it. Worth fixing.

`shadowElevation` is an honest approximation, not a match — Compose elevation and
CSS `box-shadow` do not render identically, and `--ink-shadow-lg` differs by
theme (`rgba(0,0,0,.09)` light vs `.3` dark), which a fixed elevation cannot
express.

## `.input-textarea`

| Property | Official | Compose | Status |
|---|---|---|---|
| `font-size` | `14px` | — | 🔶 unverified |
| `line-height` | `1.5` | — | 🔶 unverified |
| `min-height` | `24px` | — | 🔶 unverified |
| `background` | `transparent` | — | 🔶 unverified |

Not compared in this pass. The text field is a `BasicTextField` inside the
composer; its metrics were not extracted.

## Why this page is only partially audited

The composer is a composite of ~15 sub-elements (attachment strip, toolbar,
model chip, voice button, send button, context meter, resize handle). Auditing it
properly means extracting each one's rules from `pages-index.css` and pairing
them with the Compose node that renders it. This pass established the ground
truth, resolved the version-drift question, and checked the container only.

Remaining sub-elements to audit: `.input-toolbar`, `.toolbar-button`,
`.toolbar-button--circle`, `.send-button` (+ `--active`/`--disabled`/`--pressed`),
`.composer-voice-button` equivalents, `.attachment-chip`, `.attachment-thumb`,
`.input-context-meter` (+ `--warning`/`--danger`).

## Reproducing

```sh
for s in input-wrapper input-textarea input-toolbar send-button toolbar-button; do
  echo "### .$s"
  scripts/apk/css-rules.py docs/reference/apk-1.0.3/css/pages-index.css ".$s"
done
```
