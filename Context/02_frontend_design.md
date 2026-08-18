# Smart Grocery App — System Design
## 2. Android Frontend (Presentation / Client Layer)

The Android app is the presentation and client-side state layer. It communicates with
the backend exclusively via the REST API — it holds no direct database access.

### Conceptual Flow (Client Side)

```mermaid
flowchart TD
    A["User Action"] --> B["Compose Screen<br/>(Jetpack Compose UI)"]
    B --> C["ViewModel<br/>(MVVM · StateFlow)"]
    C --> D["Repository<br/>(Remote API Client)"]
    D --> E["REST API<br/>HTTPS / JSON"]
    E --> F["Ktor Backend"]

    F -.->|"JSON Response"| D
    D -.->|"Update State"| C
    C -.->|"StateFlow emits"| B
    B -.->|"Recompose UI"| A

    style A fill:#e8f4fd
    style B fill:#e8f4fd
    style C fill:#e8f4fd
    style D fill:#e8f4fd
    style E fill:#fff3cd
    style F fill:#fff3cd
```

### Internal Structure

- **Jetpack Compose UI** — declarative screens
- **Screens** — Authentication, Home, Categories, Products, Cart, Orders, User Profile
- **ViewModels** — one per screen/feature, expose `StateFlow` of UI state
- **Repositories** — abstract the remote API client from ViewModels
- **Remote API Client** — issues REST calls to the Ktor backend

### Major Application Features (Screens)

```mermaid
flowchart LR
    APP["Android App"] --> AUTH["Authentication"]
    APP --> HOME["Home"]
    APP --> CAT["Categories"]
    APP --> PROD["Products"]
    APP --> CART["Cart"]
    APP --> ORD["Orders"]
    APP --> PROF["User Profile"]
```

> The frontend diagram is intentionally kept high-level — its purpose is to show
> **how the client communicates with the backend**, not internal Compose widget trees.
