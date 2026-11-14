# Compte Rendu — TP Android : Contacts V3
**Étudiant / Projet :** Contacts V3 (Kotlin • Fragments • RecyclerView • OpenStreetMap)

---

## 1. Introduction et Objectifs
L'application **Contacts V3** fait suite au TP V2 en modernisant l'architecture vers une conception modulaire et orientée composants Android modernes :
- Une **activité unique** (`MainActivity`) agissant en tant que conteneur de fragments (`FragmentContainerView`).
- Une liste performante et fluide via **RecyclerView**.
- Un système de **recherche et filtrage en temps réel**.
- L'intégration des **coordonnées GPS** et de la cartographie interactive **OpenStreetMap (via osmdroid)** pour visualiser la position des contacts sur une carte, sans nécessiter de clé API payante.

---

## 2. Architecture et Choix de Communication

### Architecture des Fragments
L'application repose sur un design pattern Single-Activity et Multi-Fragments :
- **`ListeContactsFragment`** : Affiche la liste des contacts et gère la barre de recherche.
- **`DetailContactFragment`** : Permet la consultation, l'édition des informations (nom, téléphone, e-mail, photo, coordonnées GPS) et l'accès aux actions (Appeler, E-mail, Galerie, Caméra, Carte).
- **`CarteContactFragment`** : Affiche la position géographique du contact sélectionné sur osmdroid (`MapView`).

### Communication entre Fragments et Gestion d'État
Pour assurer le partage des données et la synchronisation après modification sans couplage fort :
- **Choix technique retenu :** Un **`ViewModel` partagé** au niveau de l'activité (`activityViewModels()`) couplé à un repository en mémoire (`ContactRepository`).
- **Justification :** Le ViewModel survit aux changements de configuration (rotation de l'écran) et permet aux fragments (`ListeContactsFragment` et `DetailContactFragment`) d'observer les mêmes données en temps réel (`LiveData`). Toute modification effectuée dans le détail est immédiatement répercutée et rafraîchie dans la liste.
- **Passage de paramètres :** L'identifiant unique du contact (`contactId: Long`) est transmis aux fragments enfants via un `Bundle` dans les arguments du fragment (`arguments = Bundle().apply { putLong("contact_id", id) }`).

---

## 3. RecyclerView et Stratégie de Filtrage

### Remplacement de la ListView par RecyclerView
- **`ContactAdapter`** hérite de `RecyclerView.Adapter<ContactAdapter.ContactViewHolder>` et implémente le pattern **ViewHolder** pour éviter les appels répétés à `findViewById` et garantir un défilement fluide.
- Chaque ligne (`item_contact.xml`) affiche la photo, le nom, le prénom et le numéro de téléphone.

### Filtrage en Temps Réel
- Un champ de recherche (`EditText`) placé au-dessus de la `RecyclerView` écoute chaque frappe via un `TextWatcher`.
- Le filtrage est insensible à la casse (`lowercase()`) et porte simultanément sur :
  1. Le nom (`nom`)
  2. Le prénom (`prenom`)
  3. Le numéro de téléphone (`telephone`)
- **Gestion des cas limites :** Si aucun contact ne correspond, un message explicite (`tvEmptyState`) est affiché. L'effacement du texte réaffiche instantanément l'intégralité de la liste.
- **Évitement des erreurs d'indice :** L'utilisation de l'identifiant unique du contact (`contact.id`) plutôt que sa position dans la liste (index de ligne) garantit qu'en cas de filtrage, le clic sélectionne toujours le bon contact sans erreur de décalage d'indice.

---

## 4. Coordonnées GPS et Intégration OpenStreetMap (osmdroid)

### Modèle de Données et Validation
- Le modèle `Contact` contient 8 contacts de démonstration incluant des villes réelles (Tunis, Paris, Londres, New York, Tokyo, Rome, Berlin) ainsi qu'un contact sans position.
- Lors de l'enregistrement dans le détail du contact, une validation stricte est appliquée :
  - **Latitude :** comprise entre $-90$ et $90$.
  - **Longitude :** comprise entre $-180$ et $180$.
- En cas de saisie invalide, un message d'erreur clair (`Toast`) est affiché à l'utilisateur.

### Intégration osmdroid (OpenStreetMap)
- Le bouton « Voir sur la carte » navigue vers `CarteContactFragment`.
- Utilisation de `org.osmdroid.views.MapView` avec la source de tuiles `TileSourceFactory.MAPNIK` :
  - Si les coordonnées sont valides, la carte se centre sur la position (`GeoPoint`) avec un zoom de niveau $15$ et un `Marker` affichant le nom complet du contact.
  - Si les coordonnées sont absentes ou invalides, un message explicite s'affiche à l'écran (« Coordonnées GPS absentes ou invalides pour ce contact ») et la carte est masquée.
  - **Avantage majeur :** Aucune clé API, aucun abonnement et aucune configuration Google Cloud ne sont nécessaires.

---

## 5. Réponses aux Questions de Compréhension

### 1. Quelle différence existe-t-il entre une Activity et un Fragment ?
- **Activity :** Représente un écran complet de l'application géré par le système Android, doté de sa propre fenêtre et de son cycle de vie global.
- **Fragment :** Représente une portion modulable de l'interface utilisateur ou un sous-comportement encapsulé au sein d'une Activity. Plusieurs fragments peuvent être combinés ou remplacés dynamiquement dans une même Activity.

### 2. Pourquoi utiliser `FragmentContainerView` et `addToBackStack()` ?
- **`FragmentContainerView` :** Composant conteneur spécialement conçu pour les fragments (plus performant et sécurisé que les anciens `FrameLayout`).
- **`addToBackStack()` :** Permet d'empiler la transaction de fragment dans la pile de retour (back stack) de l'application, de sorte que l'utilisateur puisse revenir à l'écran précédent (ex: revenir du détail à la liste) en appuyant sur le bouton Retour physique ou virtuel.

### 3. Quel est le rôle de `RecyclerView.Adapter` et de `ViewHolder` ?
- **`RecyclerView.Adapter` :** Fait la liaison entre la source de données (liste de contacts) et les vues graphiques de la liste, en créant et recyclant les lignes affichées.
- **`ViewHolder` :** Stocke les références des composants graphiques (ImageView, TextView) d'une ligne pour éviter les recherches répétitives (`findViewById`), améliorant ainsi considérablement les performances de défilement.

### 4. Comment éviter une erreur d’indice après filtrage ?
En n'utilisant jamais la position de la ligne dans la liste affichée (`adapterPosition`) comme clé d'accès aux données, mais en transmettant toujours l'**identifiant unique permanent** (`id`) du contact. Ainsi, quel que soit l'état du filtre, le contact recherché est identifié de manière univoque dans la base en mémoire.

### 5. Pourquoi transmettre l’identifiant d’un contact plutôt que la position de sa ligne ?
Parce que lors d'un filtrage ou d'un tri, la position (index) d'un contact change constamment. Transmettre l'identifiant unique garantit l'intégrité des données accédées dans le ViewModel, même si la liste affichée est modifiée ou filtrée.

### 6. Comment garantir qu’une clé API ne soit pas exposée dans GitHub ?
En utilisant des solutions open source comme **osmdroid** (OpenStreetMap) qui ne requièrent aucune clé API, éliminant totalement tout risque d'exposition de secrets.
