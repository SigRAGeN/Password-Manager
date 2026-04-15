# 🛡️ JavaFX Password Manager – Programming Sidequest

A lightweight, secure, and local-first password management solution built as a programming sidequest to explore Java cryptography, secure data persistence, and GUI design with JavaFX.

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
