# 🛡️ JavaFX Password Manager – Programming Sidequest

A lightweight, secure, and local-first password management solution built as a programming sidequest to explore Java cryptography, secure data persistence, and GUI design with JavaFX. Use at your own risk!

## Key Features
- **Two-Scene Architecture:** Implements a strict separation between the Login (Authentication) and Main (Data Management) layers to prevent data exposure before successful authorization.
- **AES-GCM Encryption:** Leverages Advanced Encryption Standard in Galois/Counter Mode (AES-GCM) to ensure both data confidentiality and authenticity (authenticated encryption).
- **Zero-Storage Master Password:** The Master Password is never stored on disk. Authentication is handled via a **PBKDF2 with HMAC-SHA256** derivation (65,536 iterations) against a stored cryptographic hash.
- **Cryptographic Salt Management:** Uses a unique, randomly generated 16-byte salt stored in `secret.salt`. This "Safety Plug" ensures that even if a Master Password is weak, the database remains resilient against pre-computed rainbow table attacks.

## Tech Stack
- **Java 21**
- **JavaFX 21** (FXML, Scene Builder)
- **Maven** (Dependency management & Build automation)
- **Java Cryptography Architecture (JCA)**

## How to Create a Desktop Shortcut
To run the application as a standalone desktop app without opening an IDE:

1. **Build the JAR:** Run `mvn clean package` in your terminal or use the Maven tool window in IntelliJ (**Lifecycle -> package**).
2. **Locate the Executable:** Go to the `/target` folder and find `PasswordManager-1.0-SNAPSHOT-shaded.jar`.
3. **Setup Folder:** Copy this JAR to a permanent folder (e.g., `C:/Apps/PasswordManager`).
4. **Create Shortcut:** Right-click the JAR file -> **Send to -> Desktop (create shortcut)**.
5. **Icon & Name:** Right-click the new desktop shortcut -> **Properties -> Change Icon** to customize its look, and rename it to "Password Manager".

*Note: Make sure you have Java 21+ installed and associated with .jar files.*
