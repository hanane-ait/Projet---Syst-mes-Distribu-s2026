# 🏦 eBank – Application bancaire en architecture Microservices

> Projet réalisé dans le cadre du module **Systèmes Parallèles et Distribués – 2026**
> Master **Systèmes Distribués et Intelligence Artificielle**
> Auteur : **Hanane Ait Lhaj**

---

## 📑 Table des matières

1. [Présentation du projet](#-1-présentation-du-projet)
2. [Architecture globale](#-2-architecture-globale)
3. [Technologies et versions](#-3-technologies-et-versions)
4. [Structure du projet](#-4-structure-du-projet)
5. [Prérequis](#-5-prérequis)
6. [Guide d'installation pas à pas](#-6-guide-dinstallation-pas-à-pas)
7. [Description détaillée de chaque microservice](#-7-description-détaillée-de-chaque-microservice)
   - [Discovery Service (Eureka)](#71--discovery-service-eureka-server)
   - [Customer Service](#72--customer-service)
   - [eBank Service](#73--ebank-service)
   - [Gateway Service](#74--gateway-service)
   - [eBank Bot (IA, Telegram, Discord)](#75--ebank-bot-agent-ia--telegram--discord)
   - [Front-end Angular](#76--front-end-angular)
8. [Comment les microservices communiquent entre eux](#-8-comment-les-microservices-communiquent-entre-eux)
9. [Gateway : routage statique et dynamique](#-9-gateway--routage-statique-et-dynamique)
10. [Resilience4j : tolérance aux pannes](#-10-resilience4j--tolérance-aux-pannes)
11. [Protocole MCP et agent IA](#-11-protocole-mcp-et-agent-ia)
12. [Tests et scénarios de validation](#-12-tests-et-scénarios-de-validation)
13. [Dépannage (FAQ)](#-13-dépannage-faq)
14. [Points d'attention connus](#-14-points-dattention-connus)
15. [État d'avancement](#-15-état-davancement)

---

## 📌 1. Présentation du projet

**eBank** est une application bancaire construite selon une **architecture microservices**. Chaque fonctionnalité est isolée dans un service indépendant, qui possède sa propre base de données, son propre port et peut être démarré, testé et déployé séparément.

L'application permet de :

- 👤 gérer des **clients** (`customer-service`) ;
- 💳 gérer des **comptes bancaires** rattachés à ces clients (`ebank-service`) ;
- 🔎 **découvrir dynamiquement** les services grâce à un annuaire **Eureka** (`discovery-service`) ;
- 🚪 exposer un **point d'entrée unique** avec une **API Gateway** (`gateway-service`) ;
- 🛡️ rester disponible même si un service tombe en panne grâce à **Resilience4j** (Circuit Breaker) ;
- 🤖 interroger les données bancaires **en langage naturel** grâce à un **agent IA** (Gemini + MCP) accessible via **Telegram**, **Discord** et le **front Angular** (`ebank-bot`) ;
- 🖥️ consulter les comptes et discuter avec le bot depuis une interface web (`angular-front`).

---

## 🏗️ 2. Architecture globale

```text
                         ┌─────────────────────────────────┐
                         │   Utilisateur / Navigateur      │
                         │   Angular  (localhost:4200)     │
                         └───────────────┬─────────────────┘
                                         │ HTTP
                                         ▼
                         ┌─────────────────────────────────┐
                         │      GATEWAY SERVICE  :9999     │
                         │  Routes statiques + dynamiques  │
                         └───┬─────────────┬───────────┬───┘
                             │             │           │
          (via Eureka)       │             │           │ (route statique)
                             ▼             ▼           ▼
                  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
                  │  CUSTOMER    │ │   EBANK      │ │   EBANK-BOT      │
                  │  SERVICE     │◄┤   SERVICE    │ │   :8058          │
                  │  :8056       │ │   :8057      │ │ Agent IA Gemini  │
                  │ H2 customer- │ │ H2 accounts- │ │ Telegram/Discord │
                  │ db           │ │ db           │ └──────┬───────────┘
                  └──────▲───────┘ └──────▲───────┘        │
                         │  OpenFeign     │                 │ MCP (HTTP streamable)
                         │  + Circuit     │                 │ /mcp sur 8056 et 8057
                         │  Breaker       │◄────────────────┘
                         │                │
                         └───────┬────────┘
                                 │ s'enregistrent / se découvrent
                                 ▼
                    ┌─────────────────────────────┐
                    │  DISCOVERY SERVICE  :8761   │
                    │      Eureka Server          │
                    └─────────────────────────────┘
```

### 🔌 Tableau des ports

| Composant | Port | URL |
|---|---|---|
| Discovery Service (Eureka) | `8761` | http://localhost:8761 |
| Customer Service | `8056` | http://localhost:8056 |
| eBank Service | `8057` | http://localhost:8057 |
| eBank Bot | `8058` | http://localhost:8058 |
| Gateway Service | `9999` | http://localhost:9999 |
| Front Angular | `4200` | http://localhost:4200 |

---

## 🛠️ 3. Technologies et versions

| Technologie | Version / détail | Rôle |
|---|---|---|
| ☕ Java | **21** | Langage |
| 🌱 Spring Boot | **4.1.1** | Framework des microservices |
| ☁️ Spring Cloud | **2025.1.3** | Eureka, Gateway, OpenFeign |
| 🧠 Spring AI | **2.0.0** (services) / **2.0.1** (bot) | MCP Server / MCP Client / ChatClient |
| 🌐 Spring Web MVC | – | API REST (customer, ebank, bot) |
| ⚡ Spring Cloud Gateway (WebFlux) | – | API Gateway réactive |
| 🗄️ Spring Data JPA + Hibernate | – | Persistance / ORM |
| 💾 H2 Database | en mémoire | Base de données de chaque service |
| 🔎 Netflix Eureka | Server + Client | Service Discovery |
| 📡 OpenFeign | 4.3.0 | Appel REST déclaratif entre services |
| 🛡️ Resilience4j | 2.3.0 | Circuit Breaker + fallback |
| 📚 springdoc-openapi | 3.0.1 | Swagger UI / OpenAPI |
| 🤖 Google Gemini (GenAI) | `gemini-3.5-flash-lite` | Modèle de langage du bot |
| 💬 Telegram Bots | 6.9.7.1 | Bot Telegram |
| 🎮 Discord (JDA starter) | 5.0.4 | Bot Discord |
| 🅰️ Angular | **22** + Bootstrap 5 + ngx-markdown | Front-end |
| ⚙️ Maven | wrapper `mvnw` | Build |
| 🐙 Git / GitHub | – | Versioning |

---

## 📁 4. Structure du projet

```text
ebank-ms-app/
│
├── discovery-service/        # Serveur Eureka                        (port 8761)
│   └── src/main/java/org/example/discoveryservice/
│       └── DiscoveryServiceApplication.java        (@EnableEurekaServer)
│
├── customer-service/         # Microservice Clients                  (port 8056)
│   └── src/main/java/org/example/customerservice/
│       ├── entities/Customer.java
│       ├── repository/CustomerRepository.java
│       ├── service/CustomerService.java            (+ outils MCP)
│       └── CustomerServiceApplication.java         (données de test)
│
├── ebank-service/            # Microservice Comptes bancaires        (port 8057)
│   └── src/main/java/org/example/ebankservice/
│       ├── entities/BankAccount.java
│       ├── model/Customer.java                     (DTO du client distant)
│       ├── repository/BankAccountRepository.java
│       ├── feign/CustomerRestClient.java           (appel vers customer-service)
│       ├── service/EbankService.java               (+ outils MCP)
│       ├── controllers/EbankRestController.java
│       └── EbankServiceApplication.java            (données de test)
│
├── gateway-service/          # API Gateway                           (port 9999)
│   └── src/main/
│       ├── java/.../GatewayServiceApplication.java (locator dynamique)
│       ├── java/.../gatewayservice/CorsConfig.java (CORS pour Angular)
│       └── resources/application.yml               (routes + CORS + actuator)
│
├── ebank-bot/                # Agent IA + Telegram + Discord         (port 8058)
│   └── src/main/java/org/example/ebankbot/
│       ├── agents/EbankAIAgent.java                (ChatClient + mémoire + MCP)
│       ├── controllers/EbankChatbotController.java (/chatStream en SSE)
│       ├── telegram/TelegramBot.java
│       └── discord/DiscordBot.java
│
├── angular-front/            # Interface web                         (port 4200)
│   └── src/app/
│       ├── accounts/         # Liste des comptes
│       ├── bot-ui/           # Chat avec l'agent IA (streaming)
│       ├── interceptors/     # Intercepteur de chargement
│       ├── services/loading.ts
│       └── model/account.model.ts
│
├── docs/screenshots/         # 📸 Captures d'écran du README (à créer)
├── pom.xml                   # POM racine (non agrégateur, voir §14)
├── mvnw / mvnw.cmd
└── README.md
```

---

## ⚙️ 5. Prérequis

| Outil | Version | Vérification |
|---|---|---|
| JDK | **21** | `java -version` |
| Maven (ou `mvnw` fourni) | 3.9+ | `mvn -v` |
| Node.js | 20+ (LTS récent) | `node -v` |
| npm | 10+ | `npm -v` |
| Angular CLI | 22 | `npx ng version` |
| Git | – | `git --version` |
| IDE | IntelliJ IDEA | – |
| Navigateur | Chrome / Firefox / Edge | – |

**Pour le bot uniquement** (facultatif si vous ne testez pas l'IA) :

- une **clé API Google Gemini** (Google AI Studio) ;
- un **bot Telegram** créé avec `@BotFather` (token + username) ;
- un **bot Discord** créé sur le Discord Developer Portal (token, avec l'intent *Message Content* activé).

---

## 🚀 6. Guide d'installation pas à pas

### Étape 1 – Cloner le projet

```bash
git clone https://github.com/hanane-ait/Projet---Syst-mes-Distribu-s2026.git
cd Projet---Syst-mes-Distribu-s2026
```

📝 **Ce qui est fait :** récupération du code source complet (tous les microservices + le front) sur votre machine.

---

### Étape 2 – Sécuriser la configuration du bot (⚠️ à faire avant de lancer)

📝 **Ce qui est fait :** les secrets (clé Gemini, tokens Telegram et Discord) ne doivent **jamais** être écrits en clair dans `application.properties`. On les lit depuis des **variables d'environnement**.

Dans `ebank-bot/src/main/resources/application.properties`, utilisez :

```properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
discord.token=${DISCORD_TOKEN}
telegram.bot.token=${TELEGRAM_BOT_TOKEN}
telegram.bot.username=${TELEGRAM_BOT_USERNAME:ENSETIABot}
```

Puis définissez les variables avant de lancer le bot.

**Linux / macOS :**
```bash
export GEMINI_API_KEY="votre_cle_gemini"
export DISCORD_TOKEN="votre_token_discord"
export TELEGRAM_BOT_TOKEN="votre_token_telegram"
```

**Windows (PowerShell) :**
```powershell
$env:GEMINI_API_KEY="votre_cle_gemini"
$env:DISCORD_TOKEN="votre_token_discord"
$env:TELEGRAM_BOT_TOKEN="votre_token_telegram"
```

**IntelliJ :** *Run → Edit Configurations → EbankBotApplication → Environment variables*.
---

### Étape 3 – Compiler chaque microservice

Le POM racine n'est **pas** un agrégateur : il faut compiler chaque module séparément.

```bash
cd discovery-service && ./mvnw clean install -DskipTests && cd ..
cd customer-service  && ./mvnw clean install -DskipTests && cd ..
cd ebank-service     && ./mvnw clean install -DskipTests && cd ..
cd gateway-service   && ./mvnw clean install -DskipTests && cd ..
cd ebank-bot         && ./mvnw clean install -DskipTests && cd ..
```

Sous Windows : remplacer `./mvnw` par `mvnw.cmd`.

📝 **Ce qui est fait :** Maven télécharge les dépendances (Spring Boot, Cloud, AI…), compile le code (y compris Lombok) et produit un `.jar` dans `target/` pour chaque service.

---

### Étape 4 – Démarrer le Discovery Service (en premier)

```bash
cd discovery-service
./mvnw spring-boot:run
```

📝 **Ce qui est fait :** démarrage du serveur **Eureka** sur le port `8761`. C'est l'annuaire où tous les autres services vont s'inscrire. Il doit être lancé **en premier**.


---

### Étape 5 – Démarrer le Customer Service

```bash
cd customer-service
./mvnw spring-boot:run
```

📝 **Ce qui est fait :**
1. démarrage sur le port `8056` ;
2. création de la base H2 en mémoire `customer-db` ;
3. insertion de **3 clients de test** (Mohammed, Imane, yassine) par un `CommandLineRunner` ;
4. **inscription automatique** auprès d'Eureka sous le nom `CUSTOMER-SERVICE` ;
5. exposition d'un serveur **MCP** sur `/mcp`.

✅ **Vérification :** `CUSTOMER-SERVICE` apparaît dans Eureka ; http://localhost:8056/swagger-ui/index.html est accessible.
<img width="1690" height="826" alt="image" src="https://github.com/user-attachments/assets/56ec1bf0-4909-43e1-a5b6-9f03299d7ee5" />

<img width="1663" height="479" alt="image" src="https://github.com/user-attachments/assets/b6bcb5ef-8eb9-44a6-ab4b-e42f4b613623" />

---

### Étape 6 – Démarrer l'eBank Service

```bash
cd ebank-service
./mvnw spring-boot:run
```

📝 **Ce qui est fait :**
1. démarrage sur le port `8057` ;
2. création de la base H2 `accounts-db` (console : http://localhost:8057/h2-console) ;
3. génération de **18 comptes de test** : 6 comptes pour chacun des clients 1, 2 et 3 (solde aléatoire entre 1 000 et 61 000, type `CURRENT-ACCOUNT` ou `SAVING-ACCOUNT`) ;
4. pour chaque compte, **appel Feign vers `customer-service`** pour vérifier que le client existe ;
5. inscription dans Eureka sous le nom `EBANK-SERVICE` ;
6. exposition d'un serveur **MCP** sur `/mcp`.

> 💡 Lancez `customer-service` **avant** `ebank-service` : sinon le fallback Resilience4j renverra un client « NoAvailable ».
<img width="1629" height="806" alt="image" src="https://github.com/user-attachments/assets/a2a06055-4aa3-4bf6-9fbd-2b4d59fc8448" />


<img width="1680" height="845" alt="image" src="https://github.com/user-attachments/assets/c2e814f5-b257-4aab-976c-da77ce2b1cb4" />

---

### Étape 7 – Démarrer le Gateway Service

```bash
cd gateway-service
./mvnw spring-boot:run
```

📝 **Ce qui est fait :** démarrage de la passerelle sur le port `9999`. Elle s'inscrit dans Eureka, lit la liste des services, **crée automatiquement une route par service** et ajoute une route statique vers le bot. Le CORS est configuré pour autoriser `http://localhost:4200`.

✅ **Vérification :**
- http://localhost:9999/customer-service/customers
- http://localhost:9999/ebank-service/accounts
- http://localhost:9999/actuator/gateway/routes (liste des routes actives)
#### 🔹 Gateway statique

La Gateway permet de router les requêtes vers les différents microservices en utilisant des routes configurées directement avec les URLs des services.

**Test avec Swagger / Gateway :**
> 📸 **Capture à insérer – appel via la Gateway + `/actuator/gateway/routes`**
<img width="945" height="162" alt="image" src="https://github.com/user-attachments/assets/95080079-ea04-4f7d-bbda-a70f5e1bc164" />

<img width="945" height="347" alt="image" src="https://github.com/user-attachments/assets/190afcc0-9b6f-42a8-bad4-3e693da12592" />

#### 🔹 Gateway dynamique

La Gateway peut également utiliser le service de découverte **Eureka** afin de retrouver dynamiquement les microservices disponibles.

**Test de la Gateway dynamique :**

<img width="945" height="347" alt="image" src="https://github.com/user-attachments/assets/cfb7540b-e1a0-4108-8558-43356958976e" />

<img width="945" height="334" alt="image" src="https://github.com/user-attachments/assets/067d8b25-d0d9-460c-98c7-0c2ab603ef4c" />

---
### 🔗 Connexion entre les deux microservices

Une communication a été mise en place entre les deux microservices :

* **Customer Service**
* **Ebank Service**

La communication entre les services permet notamment à `Ebank Service` de récupérer les informations concernant les clients.

**Tests de la communication entre les microservices :**
<img width="945" height="336" alt="image" src="https://github.com/user-attachments/assets/1f595018-906d-4e04-b106-581ff72ccea6" />

<img width="945" height="129" alt="image" src="https://github.com/user-attachments/assets/70e8e742-05f3-4dee-8c29-5740d360f067" />
---

### Étape 8 – Démarrer l'eBank Bot (facultatif)

```bash
cd ebank-bot
./mvnw spring-boot:run
```

📝 **Ce qui est fait :**
1. démarrage sur le port `8058` ;
2. connexion **MCP client** vers `http://localhost:8056/mcp` et `http://localhost:8057/mcp` pour récupérer les outils (liste des clients, comptes, création…) ;
3. initialisation de l'agent IA Gemini avec mémoire de conversation ;
4. enregistrement du **bot Telegram** (long polling) et du **bot Discord** ;
5. exposition de `GET /chatStream` (streaming SSE).

> 📸 **Capture à insérer – logs de démarrage du bot (Telegram enregistré, outils MCP chargés)**
>
<img width="945" height="721" alt="image" src="https://github.com/user-attachments/assets/c0eec943-93e9-4044-9b58-470dbaf90771" />
<img width="945" height="925" alt="image" src="https://github.com/user-attachments/assets/35308659-a639-4b8e-a90d-22baf2217781" />
<img width="945" height="598" alt="image" src="https://github.com/user-attachments/assets/0cb91c26-15dd-4563-8381-9da100dc49c2" />

##MCP
<img width="945" height="709" alt="image" src="https://github.com/user-attachments/assets/f7c70796-3dda-4673-b6d7-bb0454c464f5" />
<img width="945" height="349" alt="image" src="https://github.com/user-attachments/assets/d2a1219b-8cbd-4773-ab24-c138d1f93607" />

<img width="945" height="388" alt="image" src="https://github.com/user-attachments/assets/c7af5109-9af1-4d4c-af39-2285596a61b5" />
<img width="945" height="614" alt="image" src="https://github.com/user-attachments/assets/1d1ece6c-5961-4ac0-8ef6-039cb1c2ce3b" />
<img width="945" height="450" alt="image" src="https://github.com/user-attachments/assets/375ea71f-3b6b-474b-ae5b-7016c0ec20f0" />
<img width="945" height="566" alt="image" src="https://github.com/user-attachments/assets/e514bcae-c22d-442d-b1e4-b2ef1ad5dbbd" />

##Discord:
<img width="945" height="489" alt="image" src="https://github.com/user-attachments/assets/98eaa457-ca91-49bf-8088-2f89b88e4824" />
<img width="945" height="936" alt="image" src="https://github.com/user-attachments/assets/97d6d933-aa91-4ce7-ad12-59497aeeadb6" />
#Telegram
<img width="945" height="497" alt="image" src="https://github.com/user-attachments/assets/fe34b738-6fb2-45b3-beac-ff294605a370" />

---

### Étape 9 – Démarrer le front Angular

```bash
cd angular-front
npm install
npm start          # équivaut à ng serve
```

📝 **Ce qui est fait :** installation des dépendances npm puis lancement du serveur de développement sur http://localhost:4200. Le front appelle **uniquement la Gateway** (`localhost:9999`).

✅ **Pages :** `/accounts` (tableau des comptes) et `/bot` (chat avec l'agent IA).

> 📸 **Capture à insérer – page Accounts et page Bot**
<img width="945" height="348" alt="image" src="https://github.com/user-attachments/assets/d9aa66bb-7ea0-41ad-ab3b-41f9bb9ca218" />
<img width="945" height="468" alt="image" src="https://github.com/user-attachments/assets/f69f07ac-6fe2-47d8-b98a-eec36474076d" />
<img width="945" height="163" alt="image" src="https://github.com/user-attachments/assets/4ee1b3ab-8340-4f2a-ae7b-282ed3e4f54d" />


---

### 🧭 Ordre de démarrage recommandé

```text
1. discovery-service  (8761)
2. customer-service   (8056)
3. ebank-service      (8057)
4. gateway-service    (9999)
5. ebank-bot          (8058)
6. angular-front      (4200)
```

---

## 🧩 7. Description détaillée de chaque microservice

### 7.1 🔎 Discovery Service (Eureka Server)

**Rôle :** annuaire central des services. Chaque microservice s'y enregistre au démarrage et peut y retrouver l'adresse des autres, sans connaître leur IP ni leur port.

**Dépendances clés :** `spring-cloud-starter-netflix-eureka-server`, `spring-boot-starter-actuator`.

**Code :**

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication { ... }
```

**Configuration :**

```properties
spring.application.name=discovery-service
server.port=8761
eureka.client.register-with-eureka=false   # le serveur ne s'enregistre pas lui-même
eureka.client.fetch-registry=false         # il n'a pas besoin de récupérer le registre
```

**Fonctionnement :**
- les clients envoient un *heartbeat* régulier ;
- si un service n'envoie plus de heartbeat, Eureka le retire du registre ;
- le dashboard http://localhost:8761 affiche les instances `UP`.

---

### 7.2 👤 Customer Service

**Rôle :** gérer les clients de la banque. C'est le service « fournisseur » de données clients.

**Port :** `8056` — **Base :** H2 `jdbc:h2:mem:customer-db`

**Entité `Customer` :**

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant généré automatiquement |
| `name` | `String` | Nom du client |
| `email` | `String` | Adresse e-mail |

**Couches :**

| Couche | Classe | Rôle |
|---|---|---|
| Entity | `Customer` | Mapping JPA (Lombok `@Builder`, `@Getter`, `@Setter`…) |
| Repository | `CustomerRepository` | `JpaRepository<Customer, Long>` (CRUD sans code) |
| Service | `CustomerService` | Logique métier + **outils MCP** (`@McpTool`) |
| Controller | `CustomerRestController` | Endpoints REST |

**Endpoints REST :**

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/customers` | Liste de tous les clients |
| `GET` | `/customers/{id}` | Client par identifiant |
| `POST` | `/customers` | Création d'un client (`{"name":"..","email":".."}`) |

**Outils MCP exposés** (consommés par l'agent IA) : `getAllCustomers`, `findCustomerById`, `saveCustomer`.

**Données de test :** au démarrage, trois clients sont créés : *Mohammed*, *Imane*, *yassine* (e-mail `<nom>@gmail.com`).

**Documentation :** http://localhost:8056/swagger-ui/index.html

Captures déjà disponibles :

<img width="1690" height="826" alt="Customers" src="https://github.com/user-attachments/assets/56ec1bf0-4909-43e1-a5b6-9f03299d7ee5" />

<img width="1663" height="479" alt="Customers 2" src="https://github.com/user-attachments/assets/b6bcb5ef-8eb9-44a6-ab4b-e42f4b613623" />

---

### 7.3 💳 eBank Service

**Rôle :** gérer les comptes bancaires. C'est le service « consommateur » : il **appelle `customer-service`** pour associer chaque compte à son client.

**Port :** `8057` — **Base :** H2 `jdbc:h2:mem:accounts-db` — **Console :** http://localhost:8057/h2-console

**Entité `BankAccount` :**

| Champ | Type | Description |
|---|---|---|
| `id` | `String` | UUID généré dans `save()` |
| `createdAt` | `Date` | Date de création |
| `balance` | `double` | Solde |
| `type` | `String` | `CURRENT-ACCOUNT` ou `SAVING-ACCOUNT` |
| `customerId` | `long` | Référence au client (clé logique, **pas** de clé étrangère SQL : le client est dans une autre base) |
| `customer` | `Customer` | `@Transient` : rempli à la volée via Feign, **non persisté** |

> 💡 C'est le principe du **« database per service »** : le service des comptes ne stocke que l'`id` du client et va chercher le détail chez `customer-service`.

**Couches :**

| Couche | Classe | Rôle |
|---|---|---|
| Entity | `BankAccount` | Mapping JPA |
| Model | `model.Customer` | DTO représentant le client distant |
| Repository | `BankAccountRepository` | CRUD + `findByCustomerId(Long)` |
| Feign | `CustomerRestClient` | Client HTTP déclaratif vers `customer-service` |
| Service | `EbankService` | Logique métier + **outils MCP** |
| Controller | `EbankRestController` | Endpoints REST |

**Endpoints REST :**

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/accounts` | Liste de tous les comptes |
| `GET` | `/accounts/{id}` | Compte par id, **enrichi avec les infos du client** (via Feign) |
| `POST` | `/accounts` | Création d'un compte (voir point d'attention n°2) |

**Logique importante :**

```java
public BankAccount getBankAccountById(String id) {
    BankAccount account = accountRepository.findById(id).orElseThrow(...);
    // appel REST vers customer-service, via Eureka
    account.setCustomer(customerRestClient.getCustomerById(account.getCustomerId()));
    return account;
}
```

**Outils MCP exposés :** `getAllBankAccounts`, `getBankAccountById`, `save`.

**Documentation :** http://localhost:8057/swagger-ui/index.html — OpenAPI : http://localhost:8057/v3/api-docs

> 📸 **Capture à insérer – `GET /accounts/{id}` montrant le champ `customer` rempli**
>
> ![Account + customer](docs/screenshots/13-account-with-customer.png)

Captures déjà disponibles :

<img width="1629" height="806" alt="Accounts" src="https://github.com/user-attachments/assets/a2a06055-4aa3-4bf6-9fbd-2b4d59fc8448" />

<img width="1680" height="845" alt="Accounts 2" src="https://github.com/user-attachments/assets/c2e814f5-b257-4aab-976c-da77ce2b1cb4" />

---

### 7.4 🚪 Gateway Service

**Rôle :** **point d'entrée unique** de l'application. Le front et les clients externes n'appellent que `localhost:9999` ; la Gateway redirige vers le bon microservice.

**Port :** `9999` — **Techno :** Spring Cloud Gateway (WebFlux, réactif).

**Avantages :**
- un seul port à exposer ;
- le front ne connaît pas les adresses internes des services ;
- le **CORS** est géré à un seul endroit ;
- base idéale pour ajouter sécurité, limitation de débit, logs…

**Fonctionnalités configurées :**

| Fonction | Où |
|---|---|
| Route statique vers le bot | `application.yml` → route `ebank-bot` |
| Routes dynamiques via Eureka | `discovery.locator.enabled: true` + bean `DiscoveryClientRouteDefinitionLocator` |
| CORS pour Angular (`localhost:4200`) | `globalcors` dans `application.yml` + `CorsConfig.java` |
| Endpoint de debug des routes | Actuator : `/actuator/gateway/routes` |


---

### 7.5 🤖 eBank Bot (agent IA + Telegram + Discord)

**Rôle :** permettre de poser des questions bancaires **en langage naturel** (« Combien de comptes a Imane ? », « Montre-moi les comptes du client 2 »…).

**Port :** `8058`

**Composants :**

| Classe | Rôle |
|---|---|
| `EbankAIAgent` | Cœur de l'agent : `ChatClient` Spring AI + modèle Gemini + **mémoire de conversation** + **outils MCP** |
| `EbankChatbotController` | `GET /chatStream?query=...` → réponse en **streaming SSE** |
| `TelegramBot` | Bot Telegram (long polling), prend en charge **texte et photos** (analyse d'image), une mémoire par `chatId` |
| `DiscordBot` | Bot Discord, découpe les réponses > 1900 caractères |

**Prompt système de l'agent :**
- il répond aux questions sur les clients et comptes ;
- il **doit utiliser les outils** pour chercher l'information ;
- s'il n'a pas d'information, il répond **« JE NE SAIS PAS »** ;
- il ne doit pas divulguer d'informations bancaires à un utilisateur non autorisé.

**Configuration (extrait, sans secrets) :**

```properties
spring.application.name=ebank-bot
server.port=8058
spring.ai.google.genai.chat.model=gemini-3.5-flash-lite

# Connexions MCP vers les deux microservices
spring.ai.mcp.client.streamable-http.connections.customer.url=http://localhost:8056/mcp
spring.ai.mcp.client.streamable-http.connections.ebank.url=http://localhost:8057/mcp

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

---

### 7.6 🖥️ Front-end Angular

**Techno :** Angular 22 (composants standalone, signals), Bootstrap 5, Bootstrap Icons, `ngx-markdown` (rendu Markdown + KaTeX des réponses du bot).

| Élément | Rôle |
|---|---|
| `/accounts` → `Accounts` | Appelle `GET http://localhost:9999/EBANK-SERVICE/accounts` et affiche le tableau (ID, solde, date, type, customerId) |
| `/bot` → `BotUi` | Zone de saisie, appelle `GET http://localhost:9999/ebank-bot/chatStream?query=...` et affiche la réponse en Markdown au fil de l'eau |
| `LoadingService` + `loadingInterceptor` | Signal global `loading` : un **spinner** s'affiche pendant chaque requête HTTP |
| `account.model.ts` | Interfaces `Account`, `AccountListState`, enum `RequestStatus` |



---

## 🔗 8. Comment les microservices communiquent entre eux

Le projet utilise **trois mécanismes de communication** complémentaires.

### 8.1 Communication synchrone REST : `ebank-service` → `customer-service` (OpenFeign + Eureka)

C'est la communication « métier » principale.

```java
@FeignClient(name = "customer-service")
public interface CustomerRestClient {

    @GetMapping("/customers/{id}")
    @CircuitBreaker(name = "customerService", fallbackMethod = "getDefaultCustomer")
    Customer getCustomerById(@PathVariable Long id);

    default Customer getDefaultCustomer(Long id, Exception e) {
        return new Customer(id, "NoAvailable", "NoAvailable");
    }
}
```

**Déroulement détaillé d'un `GET /accounts/{id}` :**

```text
 1. Le client appelle  GET /accounts/abc-123  sur ebank-service.
 2. EbankService lit le compte dans accounts-db  → customerId = 2.
 3. EbankService appelle CustomerRestClient.getCustomerById(2).
 4. Feign voit @FeignClient(name = "customer-service") :
      → il NE connaît PAS d'URL. Il demande à Eureka l'adresse de "customer-service".
 5. Eureka répond : localhost:8056 (liste des instances disponibles).
 6. Le load-balancer choisit une instance et Feign envoie GET /customers/2.
 7. customer-service renvoie {id:2, name:"Imane", email:"Imane@gmail.com"}.
 8. EbankService place ce client dans le champ transient « customer » du compte.
 9. Le compte enrichi est renvoyé au client.
```

> 💡 Grâce à Eureka, **aucune URL n'est codée en dur** : si `customer-service` change de port ou est lancé en plusieurs instances, `ebank-service` n'a rien à modifier.

### 8.2 Découverte de services : tous → Eureka

Chaque service (customer, ebank, gateway, bot) embarque `spring-cloud-starter-netflix-eureka-client` :

```properties
spring.cloud.discovery.enabled=true
```

Au démarrage, il envoie son nom (`spring.application.name`), son host et son port au serveur Eureka, puis un heartbeat périodique.

### 8.3 Communication IA ↔ services via MCP : `ebank-bot` → `customer-service` / `ebank-service`

Les deux services métier **exposent leurs méthodes comme des outils MCP** (`@McpTool`) sur `/mcp`. Le bot est **client MCP** : il récupère ces outils et les donne au modèle Gemini, qui décide lui-même de les appeler. Détails en [section 11](#-11-protocole-mcp-et-agent-ia).

### 8.4 Récapitulatif des flux

| De | Vers | Protocole | Objectif |
|---|---|---|---|
| Angular | Gateway | HTTP / REST | Point d'entrée unique |
| Gateway | customer / ebank | HTTP via Eureka | Routage dynamique |
| Gateway | ebank-bot | HTTP (route statique) | Accès au chat |
| ebank-service | customer-service | OpenFeign via Eureka | Récupérer le client d'un compte |
| ebank-bot | customer / ebank | **MCP** (HTTP streamable) | Outils pour l'agent IA |
| Tous | discovery-service | Eureka | Enregistrement + découverte |
| Telegram / Discord | ebank-bot | API des plateformes | Chat externe |

---

## 🚪 9. Gateway : routage statique et dynamique

### 9.1 Routage statique

On déclare **explicitement** la route et l'URL cible dans `application.yml` :

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: ebank-bot
              uri: http://localhost:8058
              predicates:
                - Path=/ebank-bot/**
              filters:
                - StripPrefix=1
```

**Lecture :**
- `Path=/ebank-bot/**` : toute requête commençant par `/ebank-bot/` est capturée ;
- `uri: http://localhost:8058` : destination **codée en dur** ;
- `StripPrefix=1` : retire le premier segment (`/ebank-bot`) avant de transmettre.

**Exemple :** `GET :9999/ebank-bot/chatStream?query=Bonjour` → `GET :8058/chatStream?query=Bonjour`.

| ✅ Avantages | ⚠️ Limites |
|---|---|
| Simple, explicite, prévisible | URL en dur : à modifier si le port change |
| Contrôle total (filtres, prédicats) | Pas de load-balancing automatique |
| Fonctionne sans Eureka | Une route à écrire par service |

### 9.2 Routage dynamique (via Eureka)

La Gateway **interroge Eureka** et crée automatiquement une route par service enregistré.

```yaml
discovery:
  locator:
    enabled: true
    lower-case-service-id: true
```

```java
@Bean
DiscoveryClientRouteDefinitionLocator routes(ReactiveDiscoveryClient rdc,
                                             DiscoveryLocatorProperties dp) {
    return new DiscoveryClientRouteDefinitionLocator(rdc, dp);
}
```

**Convention automatique :** `http://localhost:9999/<nom-du-service>/<chemin>`

| URL via la Gateway | Service cible | Chemin transmis |
|---|---|---|
| `:9999/customer-service/customers` | CUSTOMER-SERVICE | `/customers` |
| `:9999/ebank-service/accounts` | EBANK-SERVICE | `/accounts` |
| `:9999/ebank-service/accounts/{id}` | EBANK-SERVICE | `/accounts/{id}` |

> 💡 Le front Angular utilise `EBANK-SERVICE` en majuscules : le routage Gateway n'est pas sensible à la casse des chemins, les deux écritures fonctionnent. Pour plus de cohérence, utilisez la minuscule partout.

| ✅ Avantages | ⚠️ Limites |
|---|---|
| Aucune URL à écrire | Convention de nommage imposée |
| Nouveau service = route créée automatiquement | Moins de contrôle fin (il faut ajouter des filtres globaux) |
| Load-balancing sur les instances | Dépend de la disponibilité d'Eureka |

### 9.3 CORS

Le front tourne sur `http://localhost:4200` et appelle la Gateway sur `:9999` : origines différentes → le navigateur bloquerait les requêtes. La Gateway autorise donc :

- origine `http://localhost:4200` ;
- méthodes `GET, POST, PUT, DELETE, OPTIONS` ;
- tous les en-têtes.

### 9.4 Schéma d'une requête complète

```text
Navigateur ─► GET :9999/ebank-service/accounts
                     │
                     ▼
              Gateway : route « ebank-service » (locator Eureka)
                     │  demande l'adresse à Eureka → localhost:8057
                     ▼
              ebank-service ─► GET /accounts ─► JSON ─► Gateway ─► Navigateur
```

---

## 🛡️ 10. Resilience4j : tolérance aux pannes

**Problème :** si `customer-service` tombe, chaque appel de `ebank-service` vers lui échoue, attend un timeout et peut bloquer ou faire tomber toute la chaîne (effet « cascade »).

**Solution : le Circuit Breaker.** Il surveille les appels et « coupe le circuit » quand trop d'échecs surviennent.

```text
        échecs répétés                 délai d'attente
 CLOSED ───────────────► OPEN ─────────────────────────► HALF-OPEN
 (appels normaux)    (appels bloqués,                (quelques appels test)
      ▲               fallback direct)                     │
      └──────────── succès ◄───────────────────────────────┘
```

| État | Comportement |
|---|---|
| **CLOSED** | Les appels passent normalement |
| **OPEN** | Les appels sont court-circuités, le **fallback** répond immédiatement |
| **HALF-OPEN** | Quelques appels de test : si OK → retour à CLOSED, sinon → OPEN |

**Dans le projet :**

```java
@CircuitBreaker(name = "customerService", fallbackMethod = "getDefaultCustomer")
Customer getCustomerById(@PathVariable Long id);

default Customer getDefaultCustomer(Long id, Exception e) {
    return new Customer(id, "NoAvailable", "NoAvailable");
}
```

Si `customer-service` est indisponible, `ebank-service` **continue de répondre** et renvoie le compte avec un client « `NoAvailable` » au lieu de planter. Dépendance : `resilience4j-spring-boot3` 2.3.0.

> ℹ️ Aucun seuil n'est personnalisé dans `application.properties` : les valeurs par défaut de Resilience4j s'appliquent. Vous pouvez les régler, par exemple :
> ```properties
> resilience4j.circuitbreaker.instances.customerService.sliding-window-size=10
> resilience4j.circuitbreaker.instances.customerService.failure-rate-threshold=50
> resilience4j.circuitbreaker.instances.customerService.wait-duration-in-open-state=10s
> ```

**Test à réaliser :**
1. appeler `GET /accounts/{id}` → le client est bien rempli ;
2. **arrêter** `customer-service` ;
3. rappeler `GET /accounts/{id}` → le client vaut `NoAvailable` (le service ne plante pas) ;
4. **relancer** `customer-service` → le client réapparaît normalement.

> 📸 **Capture à insérer – réponse normale (client rempli)**
>
> ![Resilience OK](docs/screenshots/21-resilience-ok.png)

> 📸 **Capture à insérer – réponse avec `customer-service` arrêté (fallback `NoAvailable`)**
>
> ![Resilience fallback](docs/screenshots/22-resilience-fallback.png)

---

## 🧠 11. Protocole MCP et agent IA

**MCP (Model Context Protocol)** est un protocole standard qui permet à un modèle de langage d'**appeler des outils** exposés par des applications.

### 11.1 Côté serveur (customer-service et ebank-service)

Dépendance : `spring-ai-starter-mcp-server-webmvc`. Les méthodes annotées deviennent des outils :

```java
@McpTool(description = "Get all customers")
public List<Customer> getAllCustomers() { ... }

@McpTool(description = "find customer by id ")
public Customer findCustomerById(@McpToolParam(description = "The customer id") Long id) { ... }
```

Le point d'entrée est `http://localhost:<port>/mcp` (protocole *streamable HTTP*).

| Service | Outils MCP |
|---|---|
| customer-service | `getAllCustomers`, `findCustomerById`, `saveCustomer` |
| ebank-service | `getAllBankAccounts`, `getBankAccountById`, `save` |

### 11.2 Côté client (ebank-bot)

Dépendance : `spring-ai-starter-mcp-client`. Le bot se connecte aux deux serveurs, récupère leurs outils (`ToolCallbackProvider`) et les donne à Gemini :

```java
this.chatClient = chatClientBuilder
    .defaultSystem("Tu es un assistant bancaire ...")
    .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
    .defaultTools(tools)      // outils MCP des deux microservices
    .build();
```

### 11.3 Scénario complet

```text
Utilisateur (Telegram) : « Quels sont les comptes d'Imane ? »
   │
   ▼
ebank-bot ─► Gemini (avec la liste des outils MCP)
   │            Gemini décide : appeler getAllCustomers puis getAllBankAccounts
   ├──► MCP ─► customer-service  → liste des clients (Imane = id 2)
   ├──► MCP ─► ebank-service     → liste des comptes
   │            Gemini filtre customerId = 2 et rédige la réponse
   ▼
Réponse envoyée à l'utilisateur
```

**Mémoire de conversation :** chaque canal a son propre identifiant (`telegram-<chatId>`, `conversation-default` pour Discord et le web), ce qui permet des questions de suivi (« et le plus gros solde ? »).

---

## 🧪 12. Tests et scénarios de validation

### 12.1 Tests via Swagger

| Service | URL |
|---|---|
| customer-service | http://localhost:8056/swagger-ui/index.html |
| ebank-service | http://localhost:8057/swagger-ui/index.html |
| ebank-bot | http://localhost:8058/swagger-ui/index.html |

### 12.2 Tests rapides avec `curl`

```bash
# Via la Gateway (recommandé)
curl http://localhost:9999/customer-service/customers
curl http://localhost:9999/ebank-service/accounts

# Créer un client
curl -X POST http://localhost:9999/customer-service/customers \
     -H "Content-Type: application/json" \
     -d '{"name":"Sara","email":"sara@gmail.com"}'

# Chat avec le bot (streaming)
curl -N "http://localhost:9999/ebank-bot/chatStream?query=Liste%20des%20clients"
```

### 12.3 Check-list de validation

| # | Test | Résultat attendu |
|---|---|---|
| 1 | http://localhost:8761 | 4 services `UP` : CUSTOMER-SERVICE, EBANK-SERVICE, GATEWAY-SERVICE, EBANK-BOT |
| 2 | `GET /customers` | 3 clients de départ |
| 3 | `GET /accounts` | 18 comptes |
| 4 | `GET /accounts/{id}` | Compte avec le champ `customer` rempli |
| 5 | Appel via la Gateway | Mêmes résultats qu'en direct |
| 6 | Arrêt de customer-service | `customer` = `NoAvailable`, pas d'erreur 500 |
| 7 | Page `/accounts` Angular | Tableau des comptes |
| 8 | Page `/bot` Angular | Réponse en streaming |
| 9 | Message Telegram / Discord | Réponse de l'agent IA |

---

## 🩺 13. Dépannage (FAQ)

| Problème | Cause probable | Solution |
|---|---|---|
| `Port already in use` | Un service est déjà lancé | Fermer l'ancien processus ou changer `server.port` |
| Un service n'apparaît pas dans Eureka | Eureka non démarré ou lancé trop tard | Démarrer `discovery-service` d'abord ; attendre ~30 s |
| `404` sur `/customers` | Le contrôleur n'est pas compilé | Voir [point d'attention n°1](#-14-points-dattention-connus) |
| Client = `NoAvailable` | `customer-service` arrêté ou pas encore enregistré | Le démarrer et attendre son inscription dans Eureka |
| Erreur CORS dans le navigateur | Origine ≠ `localhost:4200` | Adapter la config CORS de la Gateway |
| `503` via la Gateway | Service non enregistré dans Eureka | Vérifier le dashboard Eureka |
| Le bot ne démarre pas | Variable d'environnement manquante ou token invalide | Vérifier `GEMINI_API_KEY`, `TELEGRAM_BOT_TOKEN`, `DISCORD_TOKEN` |
| Le bot répond « JE NE SAIS PAS » | MCP non connecté | Démarrer customer et ebank **avant** le bot |
| Bot Discord muet | Intent *Message Content* désactivé | L'activer dans le Developer Portal |
| Erreur Lombok à la compilation | Annotation processing désactivé | IntelliJ : *Settings → Build → Annotation Processors → Enable* |
| `mvn clean install` à la racine ne compile rien | Le POM racine n'a pas de `<modules>` | Compiler chaque module (étape 3) |

---

## ⚠️ 14. Points d'attention connus

Ces points ont été relevés lors de l'analyse du code. Ils sont simples à corriger.

1. **`CustomerRestController` mal placé.** Le fichier est actuellement dans `customer-service/src/main/resources/`, donc **il n'est pas compilé** (absent de `target/classes`) et les routes `/customers` n'existent pas. Il doit être dans :
   `customer-service/src/main/java/org/example/customerservice/controllers/CustomerRestController.java`
2. **`POST /accounts` sans `@RequestBody`.** Dans `EbankRestController.saveAccount`, ajouter `@RequestBody` pour que le JSON soit lu :
   `public BankAccount saveAccount(@RequestBody BankAccount bankAccount)`
3. **🔐 Secrets en clair.** Les tokens **Discord** et **Telegram** sont écrits directement dans `ebank-bot/src/main/resources/application.properties`. S'ils ont été poussés sur GitHub, ils sont compromis : **régénérez-les** (BotFather `/revoke`, Discord Developer Portal → *Reset Token*) puis utilisez des variables d'environnement (étape 2). Ajoutez aussi un fichier `application-local.properties` au `.gitignore` si vous préférez.
4. **POM racine non agrégateur.** Ajouter `<packaging>pom</packaging>` et une section `<modules>` permettrait un `mvn clean install` unique depuis la racine.
5. **Type de compte avec espaces.** Dans `EbankServiceApplication`, `"SAVING-ACCOUNT  "` contient deux espaces finaux : à supprimer.
6. **Endpoint `/chat` absent.** `BotUi.askAgent()` appelle `/ebank-bot/chat`, mais le bot n'expose que `/chatStream`. Cette méthode n'est pas utilisée par l'interface actuelle ; ajouter l'endpoint ou la supprimer.
7. **Clé de l'API Gemini.** Elle est déjà lue depuis `${GEMINI_API_KEY}` ✅ : bon exemple à reproduire pour les deux autres secrets.
8. **Nom des services dans Angular.** `EBANK-SERVICE` (majuscules) fonctionne mais `ebank-service` est plus cohérent avec `lower-case-service-id: true`.

---

## 📈 15. État d'avancement

### ✅ Réalisé

- [x] Projet Spring Boot multi-modules
- [x] `customer-service` (JPA, H2, REST, Swagger, MCP)
- [x] `ebank-service` (JPA, H2, REST, Swagger, MCP)
- [x] `discovery-service` (Eureka Server)
- [x] Enregistrement des services dans Eureka
- [x] `gateway-service` : routes statiques + routes dynamiques + CORS
- [x] Communication `ebank-service` → `customer-service` (OpenFeign + Eureka)
- [x] Resilience4j (Circuit Breaker + fallback)
- [x] Serveurs MCP sur customer-service et ebank-service
- [x] `ebank-bot` : agent IA Gemini, mémoire, Telegram, Discord, streaming
- [x] Front Angular (liste des comptes + chat IA)
- [x] Gestion du code avec Git / GitHub

### 🔄 En cours / à améliorer

- [ ] Corriger les points d'attention du §14
- [ ] Tests unitaires et d'intégration
- [ ] Gestion centralisée des erreurs (`@ControllerAdvice`)
- [ ] Personnalisation fine de Resilience4j (seuils, retry, timeout)
- [ ] Pages Angular pour les clients et la création de comptes

### 🚀 À venir

- [ ] Sécurité (Spring Security / Keycloak, JWT)
- [ ] Base de données persistante (PostgreSQL / MySQL) à la place de H2
- [ ] Config Server centralisé
- [ ] Conteneurisation avec Docker + `docker-compose`
- [ ] Supervision (Actuator, Prometheus, Grafana)
- [ ] CI/CD (GitHub Actions)

---

## 🌿 Gestion des versions

- branche `develop` : développement en cours ;
- branche `main` : version stable / finale.

```bash
git add .
git commit -m "docs: complete README"
git push
```

---

## 👩‍💻 Auteur

**Hanane Ait Lhaj**
Étudiante en Master **Systèmes Distribués et Intelligence Artificielle**
Module : **Systèmes Parallèles et Distribués – 2026**
