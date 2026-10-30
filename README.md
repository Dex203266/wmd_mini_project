# StockSphere — Smart Inventory Management System

A full-stack inventory management system built with **Kotlin + Spring Boot** (backend) and **HTML5 / CSS3 / Vanilla JS** (frontend), backed by **MySQL**. Designed as a complete college practical project demonstrating REST APIs, OOP in Kotlin, collections, JPA, and frontend state management.

---

## Objectives

- Manage inventory items with full CRUD operations
- Track stock IN / OUT movements with transaction history
- Monitor low-stock and out-of-stock alerts in real time
- Generate inventory reports with rule-based recommendations
- Demonstrate Kotlin OOP: abstract classes, inheritance, polymorphism, method overriding
- Demonstrate Kotlin collections: filter, map, flatMap, sumOf, associateBy, groupBy, etc.

---

## Features

| Feature | Details |
|---|---|
| Dashboard | Live stats: total products, units, value, low/out-of-stock counts |
| Inventory Table | Search, filter by category/status, sort by name/qty/price/value |
| Add / Edit Items | Modal form with frontend + backend validation |
| Delete Items | Confirmation dialog; restricted when movement history exists |
| Stock Movements | Record IN / OUT; prevents negative stock; transaction-safe |
| Categories | Visual breakdown by category with value and status counts |
| Reports | Inventory summary, category bar chart, rule-based recommendations |
| REST API | Full CRUD + stats + search + filter + movements + reports |

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.24 (JVM) |
| Framework | Spring Boot 3.2.5 |
| Web | Spring Web (REST) |
| Database ORM | Spring Data JPA + Hibernate |
| Validation | Jakarta Bean Validation |
| Build | Gradle Kotlin DSL |
| Database | MySQL 8+ |
| Frontend | HTML5, CSS3, Vanilla JavaScript (Fetch API) |
| Icons | Font Awesome 6 |
| Testing | JUnit 5, Mockito-Kotlin |

---

## Architecture

```
┌─────────────────────┐     HTTP/JSON      ┌──────────────────────┐
│   Frontend          │ ◄────────────────► │  Spring Boot API     │
│   index.html        │                    │  :8080               │
│   css/style.css     │                    └──────────┬───────────┘
│   js/app.js         │                               │
└─────────────────────┘                    ┌──────────▼───────────┐
                                           │  Controller Layer     │
                                           │  InventoryController  │
                                           │  StockMovementCtrl    │
                                           │  ReportController     │
                                           └──────────┬───────────┘
                                                      │
                                           ┌──────────▼───────────┐
                                           │  Service Layer        │
                                           │  InventoryService     │
                                           │  StockMovementService │
                                           │  ReportService        │
                                           └──────────┬───────────┘
                                                      │
                                           ┌──────────▼───────────┐
                                           │  Repository Layer     │
                                           │  InventoryRepository  │
                                           │  StockMovementRepo    │
                                           └──────────┬───────────┘
                                                      │
                                           ┌──────────▼───────────┐
                                           │  MySQL Database       │
                                           │  inventory_items      │
                                           │  stock_movements      │
                                           └──────────────────────┘
```

---

## Folder Structure

```
stocksphere/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── frontend/
│   ├── index.html
│   ├── css/style.css
│   └── js/app.js
├── postman/
│   └── StockSphere.postman_collection.json
├── database/
│   ├── schema.sql
│   └── sample-data.sql
└── src/
    ├── main/
    │   ├── kotlin/com/stocksphere/
    │   │   ├── StocksphereApplication.kt
    │   │   ├── config/WebConfig.kt
    │   │   ├── controller/
    │   │   │   ├── InventoryController.kt
    │   │   │   ├── StockMovementController.kt
    │   │   │   └── ReportController.kt
    │   │   ├── service/
    │   │   │   ├── InventoryService.kt
    │   │   │   ├── StockMovementService.kt
    │   │   │   └── ReportService.kt
    │   │   ├── repository/
    │   │   │   ├── InventoryRepository.kt
    │   │   │   └── StockMovementRepository.kt
    │   │   ├── entity/
    │   │   │   ├── InventoryEntity.kt
    │   │   │   └── StockMovementEntity.kt
    │   │   ├── dto/
    │   │   │   ├── InventoryRequest.kt
    │   │   │   ├── InventoryResponse.kt
    │   │   │   ├── InventoryStatsResponse.kt
    │   │   │   ├── StockMovementRequest.kt
    │   │   │   └── StockMovementResponse.kt
    │   │   ├── model/
    │   │   │   ├── InventoryItem.kt        ← abstract base class
    │   │   │   ├── ElectronicItem.kt       ← subclass (inheritance)
    │   │   │   ├── OfficeSupplyItem.kt     ← subclass (inheritance)
    │   │   │   └── StockStatus.kt
    │   │   └── exception/
    │   │       ├── ResourceNotFoundException.kt
    │   │       └── GlobalExceptionHandler.kt
    │   └── resources/
    │       ├── application.yml
    │       ├── schema.sql
    │       └── data.sql
    └── test/
        └── kotlin/com/stocksphere/
            ├── model/InventoryItemTest.kt
            └── service/
                ├── InventoryServiceTest.kt
                └── StockMovementServiceTest.kt
```

---

## Requirements

| Requirement | Version |
|---|---|
| JDK | 17 or higher (tested with 17 and 24) |
| MySQL | 8.0 or higher |
| Gradle | Wrapper included (no separate install needed) |

---

## Database Setup

### Option A — Auto-create (recommended)

The application creates the `stocksphere` database automatically if the MySQL user has `CREATE` privilege. Set credentials and the app handles the rest.

### Option B — Manual setup

```sql
CREATE DATABASE stocksphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'stockuser'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON stocksphere.* TO 'stockuser'@'localhost';
FLUSH PRIVILEGES;
```

Then run `database/schema.sql` and `database/sample-data.sql` against the database.

---

## Configuration

### Using environment variables (recommended for shared environments)

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=stocksphere
export DB_USER=root
export DB_PASSWORD=yourpassword
```

### Using application.yml (local development)

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stocksphere?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: yourpassword   # ← change this, do not commit real passwords
```

**Never commit real passwords to version control.** Use environment variables in shared or CI environments.

---

## Running the Backend

```bash
cd stocksphere

# Windows
gradlew.bat bootRun

# Linux / macOS
./gradlew bootRun
```

The API starts at `http://localhost:8080`.

To change the port:
```bash
./gradlew bootRun --args='--server.port=9090'
```

### Build a JAR

```bash
./gradlew bootJar
java -jar build/libs/stocksphere-1.0.0.jar
```

---

## Running the Frontend

Open `frontend/index.html` directly in a browser. The frontend calls `http://localhost:8080` — make sure the backend is running first.

If you encounter CORS issues opening the file from disk, serve it with any static server:

```bash
# Python 3
python -m http.server 3000 --directory frontend

# Then open http://localhost:3000
```

---

## Running Tests

```bash
./gradlew test
```

Test report: `build/reports/tests/test/index.html`

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/inventory` | Get all items |
| GET | `/api/inventory/{id}` | Get item by ID |
| POST | `/api/inventory` | Create item |
| PUT | `/api/inventory/{id}` | Update item |
| DELETE | `/api/inventory/{id}` | Delete item |
| GET | `/api/inventory/stats` | Dashboard statistics |
| GET | `/api/inventory/low-stock` | Low-stock items |
| GET | `/api/inventory/out-of-stock` | Out-of-stock items |
| GET | `/api/inventory/search?q=` | Search inventory |
| GET | `/api/inventory/category/{cat}` | Filter by category |
| GET | `/api/inventory/categories` | List all categories |
| GET | `/api/inventory/movements` | Recent movements |
| GET | `/api/inventory/{id}/movements` | Item movement history |
| POST | `/api/inventory/{id}/movements` | Record stock IN/OUT |
| GET | `/api/inventory/reports/summary` | Full summary report |

---

## Postman Testing

1. Open Postman
2. Import `postman/StockSphere.postman_collection.json`
3. Ensure the backend is running at `http://localhost:8080`
4. Run requests individually or use "Run Collection"

The collection includes tests for:
- All CRUD operations
- Dashboard stats
- Search and filter
- Stock IN / OUT movements
- Error cases: invalid input, item not found, insufficient stock

---

## Kotlin OOP Concepts Used

| Concept | Where |
|---|---|
| Abstract class | `InventoryItem.kt` — base class with abstract methods |
| Inheritance | `ElectronicItem`, `OfficeSupplyItem` extend `InventoryItem` |
| Polymorphism | `calculateInventoryScore()`, `getDetails()` called on `List<InventoryItem>` |
| Method overriding | Both subclasses override `calculateInventoryScore()` and `getDetails()` |
| Encapsulation | Fields in `InventoryItem` are `val` / controlled `var` |
| Data classes | All DTOs use Kotlin `data class` |
| Sealed / enum class | `StockStatus` enum with companion factory method |
| Null safety | `String?`, `?.`, `?:` throughout the codebase |
| Init blocks | Validation in `InventoryItem.init {}` |
| Extension-style functions | `InventoryEntity.featuresAsList()`, `featuresAsSet()` |

## Kotlin Collections Used

| Operation | Where |
|---|---|
| `filter {}` | Find low-stock / out-of-stock items |
| `map {}` | Transform entities → DTOs, calculate values |
| `flatMap {}` | Extract features across all items |
| `toSet()` | Deduplicate feature tags |
| `sumOf {}` | Total inventory value, total units |
| `average()` | Average inventory score |
| `associateBy {}` | Build item lookup map by ID |
| `groupBy {}` | Group items by category |
| `count {}` | Count items by status |
| `forEach {}` | Process / display inventory records |
| `fold {}` | Accumulate BigDecimal total values |

All collection operations are demonstrated in `ReportService.kt` and `InventoryService.kt` on real database data, and in `InventoryItemTest.kt` on in-memory domain objects.

---

## Stock Status Rules

Applied consistently across frontend, backend, alerts, and reports:

| Condition | Status |
|---|---|
| `quantity == 0` | Out of Stock |
| `0 < quantity <= minimumStockLevel` | Low Stock |
| `quantity > minimumStockLevel` | In Stock |

---

## Deletion Policy

Deleting an item that has stock movement history is **restricted** (HTTP 400). This prevents silent loss of audit records. To delete such an item, first remove or archive its movement records in the database, then retry.

This approach is intentional: stock movement history is an audit trail and should not disappear when an item is deleted.

---

## Screenshots

_(Add screenshots here after running the application)_

- `screenshots/dashboard.png`
- `screenshots/inventory-table.png`
- `screenshots/add-item-form.png`
- `screenshots/stock-movements.png`
- `screenshots/reports.png`

---

## Known Limitations

- Authentication / role-based access is not implemented (out of scope for this practical)
- The frontend is served as a static file; in production it would be served from the Spring Boot `static/` folder or a CDN
- Features are stored as a comma-separated string in MySQL; a normalised child table would be more suitable for production
- The inventory score algorithm is rule-based and transparent — it is not a machine-learning model
- Recommendations are based on current stock levels only; historical trend analysis would require more data

---

## Academic Context

Built as a college full-stack lab project combining:
- **Lab 1** — HTML5 / CSS3 responsive UI
- **Lab 2** — JavaScript Fetch API + DOM state management
- **Lab 3** — Kotlin OOP (classes, inheritance, polymorphism)
- **Lab 4** — Kotlin collections and functional operations
- **Lab 5** — REST API design and Spring Boot CRUD

> StockSphere — Academic Project | Kotlin + Spring Boot + MySQL + Vanilla JS
