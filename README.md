# Contacts V3 — Application Android (Kotlin • Fragments • RecyclerView • OpenStreetMap)

## Description
Contacts V3 est une application mobile de gestion de contacts développée en Kotlin pour Android, reposant sur une architecture moderne et modulaire :
- **Activité unique** (`MainActivity`) hébergeant les fragments dynamiques dans un `FragmentContainerView`.
- **Fragments dynamiques** : `ListeContactsFragment`, `DetailContactFragment`, `CarteContactFragment`.
- **Liste performante** via `RecyclerView` et `ContactAdapter`.
- **Recherche en temps réel** insensible à la casse sur le nom, le prénom et le téléphone.
- **Cartographie OpenStreetMap (osmdroid)** : Affichage de la position GPS des contacts avec marqueur, **sans aucune clé API nécessaire**.
- **Actions multimédias et intents** : Appel téléphonique (`ACTION_DIAL`), envoi d'e-mail (`ACTION_SENDTO`), sélection depuis la galerie (`GetContent`) et prise de photo par la caméra (`TakePicturePreview` avec `FileProvider`).

---

## Architecture et Choix Techniques
- **Langage :** Kotlin
- **UI :** Layouts XML purs (pas de Jetpack Compose)
- **Architecture :** MVVM avec `ViewModel` partagé (`activityViewModels()`) et repository en mémoire (`ContactRepository`).
- **Gestion de la rotation :** Chargement initial du fragment conditionné par `savedInstanceState == null`.
- **Cartographie :** `org.osmdroid:osmdroid-android:6.1.18` (100% gratuit, aucun compte Google Cloud ni clé API requis).

---

## Installation et Lancement
1. Clonez ou téléchargez ce dépôt.
2. Ouvrez le projet dans **Android Studio**.
3. Laissez Gradle synchroniser les dépendances (aucune clé API n'est requise).
4. Lancez l'application sur un émulateur ou un appareil physique (Android 7.0 / API 24 minimum).

---

## Auteur / TP Android
Travail individuel de développement mobile — Développement Mobile Kotlin.
