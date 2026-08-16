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

Below both sections there's a small highlighted box rotating through short messages about the app's security features (encryption strength, self-destruction, etc.).

---

## 3. The menu (⋮ icon, top right corner)

Tap the three dots in the top-right corner of any screen to open a dropdown menu with these options:

| Menu item | What it does |
|---|---|
| **Choose Theme** | Opens a second menu listing 16 color themes (e.g. Classic Blue, Midnight Purple, Forest Green, Cyberpunk Neon, Dracula Dark, Dark Mode, Light Mode, and more). Tap one to instantly re-color the whole app. |
| **Help** | Opens the Help & FAQ screen (see section 4). |
| **Language** | Re-opens the language picker so you can switch the app's language at any time. |
| **TinyURL** | Opens a screen about shortening long web links (see section 5). |
| **Info** | Opens the About screen with version, developer, and legal info (see section 6). |

---

## 4. Help screen

A simple FAQ page that explains, in plain language:
- **How it works** — type a message, tap Encrypt, share the link; the recipient opens it, the message decrypts once, and is then permanently deleted.
- **Security** — everything is encrypted with AES 256-bit, the same standard banks and governments use. The server only ever stores the scrambled version, and your original text never leaves your phone.
- **FAQ questions** such as: "Can the message be read twice?" (No — it self-destructs after the first read), and "Can a deleted message be recovered?" (No, there is no backup).

---

## 5. TinyURL screen

This screen explains a separate, related feature: shortening any long web link down to just 5 characters, hosted on the developer's own website (domezos-ware.com). It shows:
- A short description of the feature.
- Info on the free/registered/paid tiers offered by that external shortener website itself — unrelated to this app's own billing, which does not exist.
- An **"Open"** button that takes you to the domezos-ware.com website in your phone's browser to actually create a short link (this part happens outside the app).

---

## 6. Info screen (About)

A simple reference screen listing:
- **Version** — the exact version number of the app you have installed.
- **Developer** — Michael Bergfeld.
- **Website** — domezos-ware.com.
- **License** — Proprietary (i.e., the code is not open-source).
- A short note reiterating that everything is protected with AES-256-bit encryption and that self-destructing links guarantee your secrets disappear forever after being read once.

---

## 7. Home screen widgets

You can add two small widgets to your phone's home screen (outside the app itself), by long-pressing your home screen and choosing "Widgets":
- **Quick Encrypt widget** — lets you type a message and get a share link directly from your home screen, without even opening the app fully.
- **Launcher widget** — a simple icon that just opens the app when tapped.

---

## 8. Why is this safe? (in plain words)

- Your message is scrambled (encrypted) with **AES-256**, the same encryption strength used by banks and governments — before it ever leaves your phone.
- The server only ever stores the scrambled, unreadable version.
- The instant someone opens and reads the message, it is **permanently and irrevocably deleted** — there is no way to read it a second time, and no backup exists anywhere.
- The app is fully free, with no accounts and no in-app purchases — nothing to track you by.

---

*Website: domezos-ware.com*
