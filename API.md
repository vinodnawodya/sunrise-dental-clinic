# Sunrise Dental Clinic - REST API

All endpoints are under `/api` and require HTTP Basic authentication using
the same staff credentials as the web UI (see README.md). They are
independent of the browser session used by the Thymeleaf UI - this is what
demonstrates the "distributed application / web services" requirement: any
client (a curl script, a mobile app, another service) can call the clinic
system without going through a browser.

CSRF protection is disabled for `/api/**` (see `SecurityConfig`) since these
endpoints authenticate per-request via Basic auth rather than a CSRF-guarded
session.

## `GET /api/appointments/{appointmentNumber}`

Fetch an appointment by its generated number.

```
GET /api/appointments/APT-000001
Authorization: Basic <base64 admin:password>
```

**200 OK**
```json
{
  "appointmentNumber": "APT-000001",
  "patientName": "Kasun Perera",
  "dentistName": "Dr. Priyantha Jayasuriya",
  "treatmentName": "Dental Cleaning",
  "appointmentDate": "2026-09-02",
  "appointmentTime": "09:30:00",
  "status": "SCHEDULED"
}
```

**404 Not Found** if the appointment number doesn't exist.

## `POST /api/appointments`

Create a new appointment.

```
POST /api/appointments
Content-Type: application/json
Authorization: Basic <base64 admin:password>

{
  "patientName": "Kasun Perera",
  "patientAddress": "45 Galle Road, Colombo 03",
  "patientContactNumber": "0771234567",
  "dentistId": 1,
  "treatmentId": 2,
  "appointmentDate": "2026-09-10",
  "appointmentTime": "10:00:00"
}
```

**201 Created** - same response shape as the GET above, with the generated
`appointmentNumber`. Publishes the same `AppointmentCreatedEvent` (observer
pattern) as the web UI, so the simulated SMS/email notification fires either
way.

**400 Bad Request** on validation failure:
```json
{
  "timestamp": "2026-09-02T09:15:00Z",
  "status": 400,
  "message": "Validation failed",
  "fieldErrors": { "patientName": "Patient name is required" }
}
```

## `GET /api/bills/{appointmentNumber}`

Fetch the bill for an appointment, generating it on first request (factory +
strategy pattern) if it doesn't exist yet.

```
GET /api/bills/APT-000001
Authorization: Basic <base64 admin:password>
```

**200 OK**
```json
{
  "appointmentNumber": "APT-000001",
  "patientName": "Kasun Perera",
  "treatmentName": "Dental Cleaning",
  "totalCost": 4500.00,
  "generatedDate": "2026-09-02T09:16:00"
}
```

## Standalone client demo

`scripts/api-client-demo.sh` is a small curl-only client, independent of the
web UI, that exercises all three endpoints end-to-end. Run it against a
running instance:

```
./scripts/api-client-demo.sh admin Sunrise123!
```
