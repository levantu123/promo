# promo

Spring Boot (3.5.x) + Java 21 backend for a simple promo-enabled shop.  
Supports **admin** product/deal management and **customer** basket/receipt flows with atomic stock control and promotions.

---

## ✅ Requirements coverage

- **Admin**
  - Create/Delete/Update products – `/api/admin/products` ✅
  - Pagination on admin listings – `GET /api/admin/products`, `GET /api/admin/deals` ✅
  - Create deals with expiry – `/api/admin/deals` (`startsAt`/`expiresAt`) ✅
- **Customer**
  - Add/Remove basket items – `/api/baskets/{id}/items` ✅
  - Receipt (items + deals + totals) – `GET /api/baskets/{id}/receipt` ✅
  - Product filters (category/price/availability) – `GET /api/products` ✅
  - Pagination on catalog – `GET /api/products` ✅
  - Limited stock enforced – atomic decrement on add, release on remove ✅
  - Graceful failure on insufficient stock – HTTP **409** with `{"error":"INSUFFICIENT_STOCK"}` ✅
- **Atomicity**
  - All basket mutations are transactional; no partial updates persist ✅
- **Tests**
  - Automated tests cover all above behaviors ✅

---

## 🚀 One command to start the app (in-memory DB)

H2 profile (no external DB):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 console: `http://localhost:8080/h2-console`  
JDBC URL:
```
jdbc:h2:mem:promo;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
```
User: `sa` — Password: *(blank)*

> Uses `src/main/resources/application-h2.properties`.

---

## 🧪 One command to run the tests

```bash
mvn -Dspring.profiles.active=h2 test
```

Tests use H2 in-memory DB and MockMvc.

---

## 🐳 (Option) Start with Docker + Postgres

Build the jar then run compose:

```bash
mvn -DskipTests package
docker compose up --build api
```

- API: `http://localhost:8080`
- Postgres: `localhost:5432` (db/user/password = `promo`)

> See `Dockerfile` and `docker-compose.yml`. The API reads JDBC settings from env vars set in compose.

---

## 🔌 Profiles & config

- **Dev / H2**: `--spring.profiles.active=h2` (in-memory; schema auto `create-drop`)
- **Postgres (default)**: configure `spring.datasource.*` in `application.properties` or environment:
  ```
  SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/promo
  SPRING_DATASOURCE_USERNAME=promo
  SPRING_DATASOURCE_PASSWORD=promo
  ```

---

## 🔑 API quick reference

### Public (Customer)

- `GET /api/products`  
  Query params: `keyword, category, minPriceCents, maxPriceCents, inStockOnly, active, page, size`
- `GET /api/products/{id}`
- `POST /api/baskets?customerId={cid}` → create basket
- `GET /api/baskets/{id}`
- `POST /api/baskets/{id}/items`  
  Body: `{"productId":1, "quantity":2}`
- `DELETE /api/baskets/{id}/items`  
  Body: `{"productId":1, "quantity":1}`
- `GET /api/baskets/{id}/receipt`

### Admin

- `GET /api/admin/products` (paginated search)
- `POST /api/admin/products` – create product
- `PUT /api/admin/products/{id}` – update
- `DELETE /api/admin/products/{id}` – delete *(blocked with `409 PRODUCT_IN_USE` if referenced)*
- `GET /api/admin/deals` (paginated search; filter `activeAt=ISO_INSTANT`)
- `POST /api/admin/deals` – create deal (`type: BUY_X_GET_Y_PCT_OFF_NEXT`)
- `PUT /api/admin/deals/{id}` – update
- `DELETE /api/admin/deals/{id}` – delete

**Error model (examples)**  
- Insufficient stock: `409 {"error":"INSUFFICIENT_STOCK"}`
- Bad request (e.g., negative qty): `400 {"error":"BAD_REQUEST","message":"..."}`  
- Not found: `404 {"error":"NOT_FOUND"}`

---

## 🧠 Promotions (engine summary)

- Current rule: **`BUY_X_GET_Y_PCT_OFF_NEXT`** (e.g., *Buy 1 get 1 50% off*).
- Engine loads **active** deals (`startsAt/expiresAt`) and returns **discount lines**.
- Receipt calculator sums: `subtotal`, `discountCents`, `totalCents`.
- Default policy: **stack discounts across different products** (deterministic order by deal id).

---

## 🧱 Design & performance notes

- **Atomic stock control**: single guarded SQL `UPDATE`  
  - `reserveStock`: `... SET stock = stock - :qty WHERE id=:id AND stock >= :qty`  
  - If 0 rows affected → 409 conflict; transaction rolls back.
  - `releaseStock` on remove in same transaction.
- **All money in cents** (ints) to avoid float drift.
- **Pagination everywhere** for lists.
- **Specifications** for dynamic filtering (category/price/availability).
- **Stateless calculators**: Promotion engine & receipt are pure → easy to test and fast.
- **Graceful errors** via `@RestControllerAdvice`.
- **Referential integrity**: product delete is **blocked** if referenced (`PRODUCT_IN_USE`), basket delete cascades items.

---

## 🧪 Test suite (high level)

- **Controllers**
  - `ProductControllerTest` – filters & pagination, `GET /{id}`
  - `AdminProductControllerTest` – CRUD, pagination, delete guard (`PRODUCT_IN_USE`)
  - `AdminDealControllerTest` – CRUD, pagination, `activeAt` filter
  - `BasketControllerTest` – create/add/remove/receipt, insufficient stock 409
- **Services/Engine**
  - `BasketServiceStockTest` – reserve/release & rollback on failure
  - `PromotionEngineTest` – rule applies within time window
  - `PromotionGroupingTest` – grouping math (Buy2Get1 free with remainder)
  - `DealActiveWindowTest` – future/expired ignored
  - `ReceiptMultipleLinesTest` – stacked discounts across products

Run them with:
```bash
mvn -Dspring.profiles.active=h2 test
```

---

## 📁 Project layout (abridged)

```
com.antulev.promo
├─ model/            # JPA entities + enums
├─ repository/       # JpaRepository + JpaSpecificationExecutor
├─ specification/    # @UtilityClass specs per entity
├─ service/          # ProductService, DealService, BasketService
├─ promo/            # PromotionEngine, rules, ReceiptCalculator, DTOs
├─ web/              # Controllers (public + admin)
├─ config/           # GlobalExceptionHandler
└─ resources/
   ├─ application-h2.properties
   └─ (docker files at repo root)
```

---

## 📦 Build & package

```bash
mvn -DskipTests package
java -jar target/promo-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2
```
