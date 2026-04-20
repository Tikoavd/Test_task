# TestTask — Android Product Catalog

A single-feature Android app that fetches products and categories from the [Escuela JS REST API](https://api.escuelajs.co/api/v1/), lets the user browse and filter them, and shows aggregated statistics.

---

## Architecture Overview

The project follows **Clean Architecture** with an **MVI (Model-View-Intent)** presentation pattern, organized into Gradle modules that enforce strict layer boundaries.

```
┌────────────────────────────────────────┐
│              :app                      │  Entry point, DI wiring
├────────────────────────────────────────┤
│          :feature:home                 │  Feature module
│  ┌──────────────┐  ┌─────────────────┐ │
│  │ Presentation │  │    Domain       │ │
│  │  (MVI / VM) │  │ (UseCases,      │ │
│  │  Compose UI │  │  Models, Repo   │ │
│  └──────┬───────┘  │  interface)    │ │
│         │          └────────┬────────┘ │
│         │                   │          │
│         │          ┌────────▼────────┐ │
│         │          │    Data         │ │
│         │          │ (API, DTOs,     │ │
│         │          │  RepoImpl)      │ │
│         └──────────┴─────────────────┘ │
├────────────────────────────────────────┤
│     :core:domain  :core:data           │
│     :core:presentation  :navigation    │
│     :screens                           │
└────────────────────────────────────────┘
```

### Layers

| Layer | Module | Responsibility |
|---|---|---|
| **Presentation** | `feature/home` | Compose UI, ViewModel, MVI state/intent/effect/reducer |
| **Domain** | `feature/home`, `core/domain` | Use cases, domain models, repository interface, dispatcher abstraction |
| **Data** | `feature/home`, `core/data` | Retrofit API, DTOs, repository implementation with in-memory cache |

---

## Module Structure

```
testtask/
├── app/                    # Application class, DI module list, MainActivity
├── core/
│   ├── data/               # Retrofit factory, OkHttp client, base network setup
│   ├── domain/             # DispatchersProvider, Flow utilities, primitive defaults
│   └── presentation/       # MviBaseViewModel, base state/intent/action/effect types
├── feature/
│   └── home/               # All product-catalog feature code
│       ├── data/
│       │   ├── api/        # ProductsApi (Retrofit interface)
│       │   ├── models/     # ProductDto, CategoryDto (with toDomain() mappers)
│       │   └── repository/ # HomeRepositoryImpl (caching via Mutex)
│       └── domain/
│           ├── models/     # Product, Category, ProductStatistics
│           ├── repository/ # HomeRepository interface
│           └── usecase/    # GetAllProductsUseCase, GetProductsUseCase,
│                           # GetCategoriesUseCase, GetProductStatisticsUseCase
├── navigation/             # NavHost, navigation graph
└── screens/                # Serializable screen destination definitions
```

---

## MVI Flow

```
User interaction
      │
      ▼
  HomeIntent  ─────────────────────────────────────────────────────┐
      │                                                             │
      ▼                                                             │
HomeViewModel                                                       │
  ├─ processes intent                                               │
  ├─ calls UseCase (returns Flow)                                   │
  ├─ maps result to HomeAction                                      │
  └─ passes Action + current State to HomeReducer                   │
                │                                                   │
                ▼                                                   │
          HomeReducer (pure function)                               │
                │                                                   │
                ├──► new HomeState ──► UI recomposes                │
                └──► HomeEffect (one-shot: errors, toasts) ─────────┘
```

Key properties of this MVI implementation:
- **State** is a single immutable data class; UI derives entirely from it.
- **Reducer** is a pure function — no side effects, fully unit-testable.
- **Effects** are separate from state and are consumed exactly once.

---

## Domain Use Cases

| Use Case | Input | Output | Description |
|---|---|---|---|
| `GetAllProductsUseCase` | — | `Flow<List<Product>>` | All products, no filtering |
| `GetProductsUseCase` | `query: String`, `categoryId: Int` | `Flow<List<Product>>` | Products filtered by title (case-insensitive contains) and category |
| `GetCategoriesUseCase` | — | `Flow<List<Category>>` | All categories |
| `GetProductStatisticsUseCase` | — | `Flow<ProductStatistics>` | Item count per category + top 3 most frequent letters across all product titles |

---

## Repository & Caching

`HomeRepositoryImpl` provides a simple **in-memory cache** for products:

- Products are fetched once from the API and stored in `cacheProducts`.
- Concurrent coroutine access is guarded by a `Mutex` (double-checked: skip network if cache is populated, then lock to write).
- Categories are **not** cached — they are fetched fresh on each call.

---

## Tech Stack

| Concern | Library |
|---|---|
| Language | Kotlin 2.3 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Architecture | MVI + Clean Architecture |
| DI | Koin 4.2 with KSP annotations |
| Networking | Retrofit 3 + OkHttp |
| Serialization | kotlinx.serialization |
| Image loading | Coil 2 |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit 4, MockK, kotlinx-coroutines-test |

---

## Running Tests

```bash
./gradlew :feature:home:test
```

Test coverage includes:

- **`HomeRepositoryImplTest`** — verifies API delegation, in-memory caching (API called only once for products), DTO→domain mapping, and null-field defaults.
- **`GetAllProductsUseCaseTest`** — verifies the use case delegates to the repository and passes through results unchanged.
- **`GetCategoriesUseCaseTest`** — verifies delegation and pass-through for categories.
- **`GetProductsUseCaseTest`** — verifies filtering by title (case-insensitive), filtering by category ID, combined filter, and empty/non-matching results.
- **`GetProductStatisticsUseCaseTest`** — verifies category item counts, top-3 character frequency computation, case folding, exclusion of non-letter characters, and empty-input edge case.

---

## API

Base URL: `https://api.escuelajs.co/api/v1/`

| Endpoint | Method | Description |
|---|---|---|
| `/products` | GET | Returns all products |
| `/categories` | GET | Returns all categories |