# dw Secret Notes

**dw Secret Notes** is an Android app for sending secret messages (text or images) that **destroy themselves automatically after being read once**. You write a message, the app encrypts it and gives you a link. You send that link to someone (WhatsApp, email, SMS — anything). The other person opens the link, reads the message exactly one time, and then it is permanently deleted from the server forever. Nobody — not even the app's developer — can read it again afterwards.

Think of it like a "this message will self-destruct" note from a spy movie, but for real, everyday use: sharing a password, a private thought, or a sensitive photo without leaving a trace anywhere.

---

## 1. The first time you open the app: choosing a language

The very first time you start the app, you see the **Language screen**. It's a simple list of 15 languages (English, German, Spanish, Chinese, Hindi, Arabic, Portuguese, Bengali, Russian, Japanese, French, Urdu, Indonesian, Korean, Italian). Tap the language you want, then tap the button at the bottom ("Confirm" / "Continue") to save your choice and enter the app. You can change the language again at any time later (see the menu, section 3).

---

## 2. The Main Screen — where everything happens

The main screen is split into two halves: **the top half is for sending a secret**, and **the bottom half is for reading a secret**.

### Sending a secret (top half)

- **Text field ("Enter your text")** — Type the message you want to keep secret here.
- **"Attach image (optional)" button** — Tap this to pick a picture from your phone's gallery. You can attach an image instead of text, or together with text. A small preview of the picture appears next to the button once selected; tap the little ✕ on it to remove the picture again.
- **"Encrypt" button** — Once you've typed something (or attached an image), tap this button. The app scrambles (encrypts) your message using bank-grade AES-256 encryption and uploads only the scrambled, unreadable version to the server — your original text never leaves your phone in readable form.

After encrypting, the screen changes to show you the result:
- **Your alias** (Premium users only) — a short, memorable code identifying your message.
- **Link field** — the secret link you need to share. It's read-only (you can't type into it).
- **"Copy" button** — copies the link to your clipboard so you can paste it anywhere.
- **"Share" button** — opens your phone's normal share menu (WhatsApp, email, SMS, etc.) with the link already filled in.
- **"New Message" button** — clears everything and lets you encrypt another secret.

### Reading a secret (bottom half)

- **"Link or alias" text field** — Paste the secret link (or just the short 5-character code) you received here.
- **"Decrypt" button** — Tap this to fetch and unlock the secret message. If the message was already read before (or never existed), you'll see a red "not found" warning.

Once decrypted successfully, you'll see:
- **The secret message text** and/or **image**, revealed just this once. Tap an image to view it fullscreen; tap anywhere to close it again.
- **A countdown ("This message will be deleted in 60 seconds…")** — a visual reminder that this content is temporary and will vanish from your screen shortly. It has **already been permanently deleted from the server** the moment you opened it — the on-screen countdown is just clearing your own view.
- **"Read another message" button** — clears the screen so you can decrypt a different link.

### The banner near the bottom

Below both sections there's a small highlighted box (an ad for Premium, or — if you already bought Premium — a small "thank you" message). Tapping it takes you to the Premium screen (section 3 below).

---

## 3. The menu (⋮ icon, top right corner)

Tap the three dots in the top-right corner of any screen to open a dropdown menu with these options:

| Menu item | What it does |
|---|---|
| **Choose Theme** | Opens a second menu listing 16 color themes (e.g. Classic Blue, Midnight Purple, Forest Green, Cyberpunk Neon, Dracula Dark, Dark Mode, Light Mode, and more). Tap one to instantly re-color the whole app. |
| **Help** | Opens the Help & FAQ screen (see section 4). |
| **Premium** | Opens the Premium screen where you can unlock extra features (see section 5). |
| **Language** | Re-opens the language picker so you can switch the app's language at any time. |
| **TinyURL** | Opens a screen about shortening long web links (see section 6). |
| **Info** | Opens the About screen with version, developer, and legal info (see section 7). |
| **Support** | Opens the "Buy me a coffee" support/tip screen (see section 8). |

---

## 4. Help screen

A simple FAQ page that explains, in plain language:
- **How it works** — type a message, tap Encrypt, share the link; the recipient opens it, the message decrypts once, and is then permanently deleted.
- **Security** — everything is encrypted with AES 256-bit, the same standard banks and governments use. The server only ever stores the scrambled version, and your original text never leaves your phone.
- **FAQ questions** such as: "Can the message be read twice?" (No — it self-destructs after the first read), "Can a deleted message be recovered?" (No, there is no backup), "What do Premium links look like?" (Short, memorable 5-character codes), and "Is Premium tied to my account?" (No — Premium is tied anonymously to your device only; reinstalling the app or switching phones means you'd need to purchase it again, since the app deliberately stores no personal account data).

---

## 5. Premium screen

Premium is an optional upgrade you can buy. This screen shows:
- **A features list** with a checkmark next to each benefit: unlimited messages, short 5-digit memorable links, a secure encrypted link database, AES 256-bit encryption, and self-destructing messages.
- **Two plan cards you can tap to choose between:**
  - **Monthly plan** — a one-time purchase (despite the name, it's a single payment, not a recurring subscription) with a clearly shown price.
  - **Subscription plan** — a recurring subscription, often with a free trial period badge shown on the card, and its price shown per month.
- Depending on which plan card you've selected, a details box underneath explains exactly what you get: the price, duration, whether it renews automatically, and that it's entirely optional.
- **The buy button** at the bottom (its text changes depending on your selection: "Subscribe" or a one-time purchase label) opens Google Play's official payment screen. If you already own Premium, this button is disabled and simply shows "Premium Active".
- Small print at the very bottom links to the terms of the purchase.

All prices you see are fetched live from Google Play and shown in your own local currency — they are never hardcoded, so what you see is always accurate for your country.

---

## 6. TinyURL screen

This screen explains a separate, related feature: shortening any long web link down to just 5 characters, hosted on the developer's own website (domezos-ware.com). It shows:
- A short description of the feature.
- A box describing the Premium version of TinyURL (e.g. links that never expire), with its own separate monthly price.
- A **"Open in browser"** button that takes you to the domezos-ware.com website in your phone's browser to actually create a short link (this part happens outside the app).

---

## 7. Info screen (About)

A simple reference screen listing:
- **Version** — the exact version number of the app you have installed.
- **Developer** — Michael Bergfeld.
- **Website** — domezos-ware.com.
- **License** — Proprietary (i.e., the code is not open-source).
- A short note reiterating that everything is protected with AES-256-bit encryption and that self-destructing links guarantee your secrets disappear forever after being read once.

---

## 8. Support screen ("Buy me a coffee")

This screen is a **pure, voluntary donation** — it's important to understand it unlocks nothing at all. It exists only so people who like the app can help keep it running. It shows:
- **What this is** — a plain explanation that this is not a purchase of any feature; you get nothing in return except the good feeling of supporting the app.
- **Running costs** — a transparent breakdown of what actually costs money to keep the service alive each month (e.g. the server, the database, the SSL certificate, and maintenance/development work).
- **Anonymous** — a note confirming your tip is not tied to your identity in any way.
- **The tip price**, shown live from Google Play in your local currency.
- **A disclaimer**, repeating clearly that no feature, unlock, or reward is given in exchange.
- **"Send tip" button** — opens Google Play's payment screen to send the small one-time tip. If you've sent tips before, a small green badge above shows how many times you've supported the app already.

---

## 9. Home screen widgets

You can add two small widgets to your phone's home screen (outside the app itself), by long-pressing your home screen and choosing "Widgets":
- **Quick Encrypt widget** — lets you type a message and get a share link directly from your home screen, without even opening the app fully.
- **Launcher widget** — a simple icon that just opens the app when tapped.

---

## 10. Why is this safe? (in plain words)

- Your message is scrambled (encrypted) with **AES-256**, the same encryption strength used by banks and governments — before it ever leaves your phone.
- The server only ever stores the scrambled, unreadable version.
- The instant someone opens and reads the message, it is **permanently and irrevocably deleted** — there is no way to read it a second time, and no backup exists anywhere.
- Premium status is tied to an anonymous code generated on your device, not to your name, email, or any personal account — so the app cannot track who you are.

---

*Website: domezos-ware.com*
