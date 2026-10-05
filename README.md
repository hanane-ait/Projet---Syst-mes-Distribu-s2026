# 🏦 Projet Systèmes Distribués – Application eBank

## 📌 Description

Ce projet consiste à développer une application bancaire basée sur une **architecture microservices** dans le cadre du module **Systèmes Parallèles et Distribués**.

L'application est organisée autour de plusieurs microservices indépendants permettant de gérer les différentes fonctionnalités du système bancaire.

Actuellement, le projet contient principalement les deux microservices suivants :

* **`ebank-service`** : gestion des comptes bancaires.
* **`customer-service`** : gestion des clients.

Le projet est développé progressivement et sera enrichi avec d'autres composants et fonctionnalités au fur et à mesure de son avancement.

---

## 🏗️ Architecture du projet

```text
                         ┌──────────────────────┐
                         │      eBank App       │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
          ┌──────────────────┐             ┌──────────────────┐
          │  Customer        │             │     eBank        │
          │  Service         │             │     Service      │
          │                  │             │                  │
          │ Gestion clients  │             │ Gestion comptes  │
          └──────────────────┘             └──────────────────┘
                    │                               │
                    └───────────────┬───────────────┘
                                    │
                                    ▼
                           Architecture REST
```

---

## 📦 Microservices

### 👤 Customer Service

Le microservice `customer-service` est dédié à la gestion des clients de l'application bancaire.

Il constitue un service indépendant pouvant être consommé à travers des APIs REST.

**Fonctionnalités développées :**

* Gestion des clients
* Exposition des données via une API REST
* Documentation de l'API avec Swagger/OpenAPI

---

### 💳 eBank Service

Le microservice `ebank-service` est dédié à la gestion des comptes bancaires.

Il permet notamment de gérer les informations relatives aux comptes bancaires.

**Fonctionnalités développées :**

* Gestion des comptes bancaires
* Création de comptes
* Consultation des comptes
* Recherche de comptes
* API REST
* Persistance des données avec JPA
* Base de données H2
* Documentation de l'API avec Swagger/OpenAPI

---

## 🛠️ Technologies utilisées

| Technologie          | Utilisation                            |
| -------------------- | -------------------------------------- |
| ☕ Java               | Langage de programmation               |
| 🌱 Spring Boot       | Développement des microservices        |
| 🌐 Spring Web        | APIs REST                              |
| 🗄️ Spring Data JPA  | Accès aux données                      |
| 💾 H2 Database       | Base de données                        |
| 🔍 Hibernate         | ORM                                    |
| 📚 Swagger / OpenAPI | Documentation et test des APIs         |
| 🔎 Eureka            | Service Discovery                      |
| ⚙️ Maven             | Gestion des dépendances et compilation |
| 🐙 Git / GitHub      | Gestion du code source                 |
| 💻 IntelliJ IDEA     | Environnement de développement         |

---

## 📁 Structure du projet

```text
ebank-ms-app/
│
├── customer-service/
│   ├── src/
│   └── pom.xml
│
├── ebank-service/
│   ├── src/
│   └── pom.xml
│
├── src/
├── .gitignore
├── .gitattributes
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## ⚙️ Prérequis

Avant de lancer le projet, il faut disposer de :

* Java JDK
* Maven
* Git
* IntelliJ IDEA ou un autre IDE compatible Java
* Un navigateur Web pour accéder à Swagger

---

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone https://github.com/hanane-ait/Projet---Syst-mes-Distribu-s2026.git
```

### 2. Accéder au projet

```bash
cd Projet---Syst-mes-Distribu-s2026
```

### 3. Compiler le projet

Avec Maven :

```bash
mvn clean install
```

ou avec le Maven Wrapper :

```bash
mvnw clean install
```

Sous Windows :

```cmd
mvnw.cmd clean install
```

---

# ▶️ Lancement des microservices

Les microservices doivent être lancés séparément depuis IntelliJ IDEA ou depuis le terminal.

## 💳 Ebank Service

Le microservice `ebank-service` utilise actuellement le port :

```text
8057
```

Une fois le service lancé, l'application est accessible à :

```text
http://localhost:8057
```

---

## 👤 Customer Service

Le port du `customer-service` dépend de la configuration définie dans son fichier `application.properties` ou `application.yml`.

Après le démarrage, l'URL correspondante peut être utilisée pour accéder à ses APIs.

---

# 📖 Documentation Swagger

Swagger permet de visualiser, tester et documenter les APIs REST des microservices.

## 💳 Swagger - Ebank Service

Pour accéder à Swagger du `ebank-service` :

```text
http://localhost:8057/swagger-ui/index.html
```

La documentation OpenAPI peut également être consultée via :

```text
http://localhost:8057/v3/api-docs
```

---

## 👤 Swagger - Customer Service

Pour le `customer-service`, l'URL dépend du port configuré.

Format :

```text
http://localhost:<PORT>/swagger-ui/index.html
```

---

# 🧪 Tests avec Swagger

Les APIs REST ont été testées à l'aide de l'interface Swagger UI.

Swagger permet notamment de :

* consulter les endpoints disponibles ;
* envoyer des requêtes HTTP ;
* tester les méthodes `GET`, `POST`, `PUT` et `DELETE` lorsqu'elles sont disponibles ;
* visualiser les réponses retournées par les services ;
* vérifier le fonctionnement des APIs.

---

## 📸 Captures des tests

Les captures d'écran des tests Swagger seront  :


### 💳 Ebank Service

#### Consultation des comptes bancaires
<img width="1629" height="806" alt="image" src="https://github.com/user-attachments/assets/a2a06055-4aa3-4bf6-9fbd-2b4d59fc8448" />


<img width="1680" height="845" alt="image" src="https://github.com/user-attachments/assets/c2e814f5-b257-4aab-976c-da77ce2b1cb4" />


---

### 👤 Customer Service

#### Consultation des clients
<img width="1690" height="826" alt="image" src="https://github.com/user-attachments/assets/56ec1bf0-4909-43e1-a5b6-9f03299d7ee5" />

<img width="1663" height="479" alt="image" src="https://github.com/user-attachments/assets/b6bcb5ef-8eb9-44a6-ab4b-e42f4b613623" />


---

# 🗄️ Base de données

Le microservice `ebank-service` utilise actuellement **H2 Database** pour la persistance des données.

La configuration utilise une base H2 en mémoire :

```text
jdbc:h2:mem:accounts-db
```

La console H2 est accessible depuis :

```text
http://localhost:8057/h2-console
```

---

# 🔎 Service Discovery

Le projet utilise **Eureka** afin de permettre la découverte des services dans l'architecture microservices.

Les différents services peuvent ainsi être enregistrés auprès du serveur Eureka et découverts dynamiquement.

Cette partie du projet est développée progressivement.

---

# 🌿 Gestion des versions avec Git

Le développement du projet est effectué progressivement avec Git et GitHub.

La branche principale de développement actuelle est :

```text
develop
```

Les modifications sont enregistrées régulièrement avec des commits afin de suivre l'évolution du projet.

Exemple :

```bash
git add .
git commit -m "Add bank account REST controller"
git push
```

La branche `main` sera utilisée pour la version stable/finale du projet.

---

# 📈 État actuel du projet

Le projet est actuellement **en cours de développement**.

### ✅ Déjà réalisé

* [x] Création du projet Spring Boot
* [x] Création du `ebank-service`
* [x] Création du `customer-service`
* [x] Mise en place de Spring Data JPA
* [x] Configuration de la base H2
* [x] Création des APIs REST
* [x] Documentation avec Swagger/OpenAPI
* [x] Première intégration avec Eureka
* [x] Gestion du projet avec Git et GitHub

### 🔄 En cours

* [ ] Finalisation du `customer-service`
* [ ] Communication entre les microservices
* [ ] Finalisation de la configuration Eureka
* [ ] Tests complets des APIs
* [ ] Amélioration de l'architecture
* [ ] Ajout d'autres fonctionnalités

### 🚀 À venir

* [ ] API Gateway
* [ ] Communication inter-microservices
* [ ] Gestion des erreurs
* [ ] Sécurité
* [ ] Tests unitaires et tests d'intégration
* [ ] Conteneurisation avec Docker
* [ ] Amélioration de la documentation

---

# 👩‍💻 Auteur

**Hanane Ait Lhaj**

Étudiante en Master **Systèmes Distribués et Intelligence Artificielle**

Projet réalisé dans le cadre du module :

**Systèmes Parallèles et Distribués – 2026**

---

## 🚀 Mise à jour du projet

### Gateway

#### 🔹 Gateway statique

La Gateway permet de router les requêtes vers les différents microservices en utilisant des routes configurées directement avec les URLs des services.

**Test avec Swagger / Gateway :**

<img width="945" height="162" alt="image" src="https://github.com/user-attachments/assets/95080079-ea04-4f7d-bbda-a70f5e1bc164" />

<img width="945" height="347" alt="image" src="https://github.com/user-attachments/assets/190afcc0-9b6f-42a8-bad4-3e693da12592" />


#### 🔹 Gateway dynamique

La Gateway peut également utiliser le service de découverte **Eureka** afin de retrouver dynamiquement les microservices disponibles.

**Test de la Gateway dynamique :**

<img width="945" height="347" alt="image" src="https://github.com/user-attachments/assets/cfb7540b-e1a0-4108-8558-43356958976e" />

<img width="945" height="334" alt="image" src="https://github.com/user-attachments/assets/067d8b25-d0d9-460c-98c7-0c2ab603ef4c" />

### 🔎 Discovery Service

Le **Discovery Service** basé sur Eureka permet l'enregistrement et la découverte des microservices dans l'architecture.

**Test du Discovery Service :**

!<img width="945" height="430" alt="image" src="https://github.com/user-attachments/assets/2c1ecce7-e5eb-473d-956f-fe8b2329ceea" />


### 🔗 Connexion entre les deux microservices

Une communication a été mise en place entre les deux microservices :

* **Customer Service**
* **Ebank Service**

La communication entre les services permet notamment à `Ebank Service` de récupérer les informations concernant les clients.

**Tests de la communication entre les microservices :**
<img width="945" height="336" alt="image" src="https://github.com/user-attachments/assets/1f595018-906d-4e04-b106-581ff72ccea6" />

<img width="945" height="129" alt="image" src="https://github.com/user-attachments/assets/70e8e742-05f3-4dee-8c29-5740d360f067" />


### 🛡️ Resilience4j

**Resilience4j** a également été intégré afin d'améliorer la tolérance aux pannes et gérer les problèmes de communication entre les microservices.

Le mécanisme permet notamment de gérer les situations où un microservice devient temporairement indisponible.

