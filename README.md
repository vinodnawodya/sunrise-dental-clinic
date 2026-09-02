# Sunrise Dental Clinic Management System

A staff-facing appointment, billing and reporting system built for CIS6003
(Advanced Programming, Cardiff Met) with Java 17, Spring Boot 3, and MySQL/H2.

## Running it

```bash
./mvnw spring-boot:run        # macOS/Linux
mvnw.cmd spring-boot:run      # Windows
```

Then open http://localhost:8080 and log in with:

- **Username:** `admin`
- **Password:** `Sunrise123!`

### Database

MySQL wasn't reachable in the environment this project was built in, so it
runs against **H2 in file mode** by default (`./data/sunrise.mv.db`) - this
is the documented fallback the assignment brief explicitly allows, and the
JPA entity/repository code is identical either way.

To run against real MySQL 8 instead:

1. Create a database (or let `createDatabaseIfNotExist=true` do it) and make
   sure a MySQL server is reachable at `localhost:3306` with a `root` user
   and no password, or edit `application-mysql.properties`.
2. Run with the `mysql` profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

Either way, `spring.sql.init.mode=always` recreates the schema and reseeds
data on every startup from `src/main/resources/db/`.

#### Stored procedure / trigger / view

`db/schema-mysql.sql` has the canonical MySQL implementations:

- **View** `daily_appointments_view` - today's appointments joined with
  patient/dentist/treatment, used by the Reports page.
- **Stored procedure** `CalculateAppointmentBill(appointment_id)` - returns
  a treatment's base cost via an `OUT` parameter.
- **Trigger** `after_appointment_insert` - writes a row to `audit_log` for
  every new appointment.

H2 has no `DELIMITER`/`BEGIN...END` procedure or trigger syntax, so
`db/schema-h2.sql` implements the same two behaviours as H2-native Java
hooks instead - `CREATE ALIAS` calling
[`BillCalculator`](src/main/java/com/sunrisedental/config/BillCalculator.java)
for the procedure, and `CREATE TRIGGER ... CALL` invoking
[`AuditLogTrigger`](src/main/java/com/sunrisedental/config/AuditLogTrigger.java)
(implements `org.h2.api.Trigger`) for the trigger. The view is standard SQL
and needs no adaptation. Same observable behaviour, different plumbing per
database - documented inline in both schema files.

## Architecture

```
Browser (Thymeleaf UI)          Standalone client (curl / HttpClient)
        |  session + CSRF               |  HTTP Basic
        v                                v
+----------------------- Spring MVC ------------------------+
|  controller (web)          controller.api (REST)          |
+------------------------------+-----------------------------+
                                |
                        service (business logic)
              AppointmentService, PatientService, BillingService,
                       NotificationManager
                                |
        +-----------+----------+-----------+------------+
        |           |                      |            |
  pattern.strategy  pattern.factory     event        repository (DAO)
  BillingStrategy    BillFactory   AppointmentCreatedEvent  (Spring Data JPA)
  Standard/Surgical                 + NotificationListener       |
        |                                                         v
        +---------------------------------------------------> entity (JPA)
                                                                    |
                                                                    v
                                                        MySQL 8 / H2 (db/*.sql:
                                                    schema, stored proc, trigger, view)
```

## Design patterns

| Pattern | Where | Why |
|---|---|---|
| **DAO / Repository** | `repository/*Repository` | Spring Data JPA repositories isolate persistence from the service layer for every entity. |
| **Singleton** | [`NotificationManager`](src/main/java/com/sunrisedental/service/NotificationManager.java) | Spring's default singleton bean scope already gives one instance; a private constructor + static `getInstance()` is added on top so the classic GoF shape is explicit, not just implicit. |
| **Factory** | [`BillFactory`](src/main/java/com/sunrisedental/pattern/factory/BillFactory.java) | `BillFactory.createBill(appointment, strategy)` centralises how a `Bill` is assembled, independent of which pricing strategy produced the total. |
| **Strategy** | [`BillingStrategy`](src/main/java/com/sunrisedental/pattern/strategy/BillingStrategy.java) + `StandardTreatmentPricing` / `SurgicalTreatmentPricing` | Pricing rules vary by `Treatment.category` without an `if/else` ladder in the billing code; `BillingService` picks the strategy at runtime. |
| **Observer** | [`AppointmentCreatedEvent`](src/main/java/com/sunrisedental/event/AppointmentCreatedEvent.java) + `NotificationListener` | `AppointmentService` publishes an event via `ApplicationEventPublisher` when an appointment is saved; the listener reacts by simulating an SMS/email, decoupling notification from appointment creation. |

## Documented assumptions

- **Database:** H2 file mode is used instead of MySQL because MySQL wasn't
  reachable in the build environment (explicitly permitted by the brief).
  The `mysql` Spring profile switches to real MySQL 8 with no code changes.
- **SMS/email:** No real provider is integrated. `NotificationManager` logs
  a `[SIMULATED SMS/EMAIL]` line instead - the brief explicitly allows this.
- **Seeded staff login:** one `STAFF` user, `admin` / `Sunrise123!`, seeded
  via `db/data-*.sql`.

## REST API

See [API.md](API.md) for full endpoint documentation and a standalone curl
client (`scripts/api-client-demo.sh`) that exercises the API independently
of the web UI.

- `GET /api/appointments/{appointmentNumber}`
- `POST /api/appointments`
- `GET /api/bills/{appointmentNumber}`

All require HTTP Basic auth with the same staff credentials as the web UI.

## Testing

```bash
./mvnw test
```

- `AppointmentServiceTest`, `BillingServiceTest` - Mockito-based unit tests
  for the service layer (repositories mocked).
- `AppointmentControllerTest` - `@WebMvcTest` slice test covering
  authentication redirect and form validation re-rendering.
- The double-booking rule (`DentistDoubleBookingException`) was built
  test-first: see the `test: failing test for double-booking rule (TDD red)`
  commit followed by `feat: prevent dentist double-booking (TDD green)` in
  the git history.

## CI

`.github/workflows/ci.yml` runs `mvn -B test` on every push and pull
request via GitHub Actions.
