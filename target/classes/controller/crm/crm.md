# SmartAgent CRM API

Read-only JSON API for the SmartAgent CRM. It exposes every record the voice agent stores — leads, exams, grades, feedbacks, chats, notes, follow-up schedules and university resources — so a CRM frontend can display them.

- **Base URL:** `http://localhost:8081/crm` (through ngrok: `https://<your-ngrok-domain>/crm`)
- **Methods:** `GET` only. The CRM cannot create, change or delete data.
- **Format:** every response, including errors, is JSON (`Content-Type: application/json;charset=UTF-8`).
- **Authentication:** none yet. See [Admin account](#admin-account).

---

## Contents

- [Quick start](#quick-start)
- [Conventions](#conventions)
  - [Pagination](#pagination)
  - [Errors](#errors)
  - [Dates and times](#dates-and-times)
  - [Lead reference](#lead-reference)
  - [CORS](#cors)
- [Admin account](#admin-account)
- [Routes](#routes)
  - [GET /crm/](#get-crm)
  - [GET /crm/admin](#get-crmadmin)
  - [GET /crm/dashboard](#get-crmdashboard)
  - [GET /crm/leads](#get-crmleads)
  - [GET /crm/leads/{id}](#get-crmleadsid)
  - [GET /crm/leads/{id}/exams](#get-crmleadsidexams)
  - [GET /crm/leads/{id}/grades](#get-crmleadsidgrades)
  - [GET /crm/leads/{id}/feedbacks](#get-crmleadsidfeedbacks)
  - [GET /crm/leads/{id}/chats](#get-crmleadsidchats)
  - [GET /crm/leads/{id}/note](#get-crmleadsidnote)
  - [GET /crm/leads/{id}/schedule](#get-crmleadsidschedule)
  - [GET /crm/exams](#get-crmexams)
  - [GET /crm/grades](#get-crmgrades)
  - [GET /crm/feedbacks](#get-crmfeedbacks)
  - [GET /crm/chats](#get-crmchats)
  - [GET /crm/notes](#get-crmnotes)
  - [GET /crm/schedules](#get-crmschedules)
  - [GET /crm/resources](#get-crmresources)
  - [GET /crm/resources/{id}](#get-crmresourcesid)
- [Record fields](#record-fields)
- [Frontend examples](#frontend-examples)
- [Where the code lives](#where-the-code-lives)

---

## Quick start

Start the application (it starts the HTTP server on port 8081):

```
mvn compile exec:java "-Dexec.mainClass=Main"
```

Then:

```bash
# list every route
curl http://localhost:8081/crm/

# dashboard numbers
curl http://localhost:8081/crm/dashboard

# first page of leads
curl http://localhost:8081/crm/leads

# one lead with everything linked to it
curl http://localhost:8081/crm/leads/16
```

> Use `/crm/` with a trailing slash for the index. `/crm` without it returns a `301` redirect to `/crm/`.

---

## Conventions

### Pagination

Every route that returns a list is paginated.

| Query parameter | Default | Allowed | Meaning |
|---|---|---|---|
| `page` | `1` | `1` or more | Page number, starting at 1 |
| `size` | `50` | `1` – `500` | Records per page |

Paginated responses always have this shape:

```json
{
  "page": 1,
  "size": 50,
  "total": 15,
  "totalPages": 1,
  "items": [ ... ]
}
```

| Field | Meaning |
|---|---|
| `page` | The page that was returned |
| `size` | Page size that was used |
| `total` | Number of matching records across all pages |
| `totalPages` | `ceil(total / size)`, `0` when nothing matches |
| `items` | The records on this page |

Asking for a page past the end returns `200` with an empty `items` array.

### Errors

Errors use a normal HTTP status code and a JSON body with one field:

```json
{ "error": "Lead 99999 not found" }
```

| Status | When | Example message |
|---|---|---|
| `400 Bad Request` | An id or query parameter is not valid | `lead id must be a number, got 'abc'` |
| | | `page must be 1 or more` |
| | | `size must be between 1 and 500` |
| | | `leadId must be a number, got 'x'` |
| `404 Not Found` | The lead/resource does not exist, the lead has no note/schedule, or the route is unknown | `Lead 99999 not found` |
| | | `Lead 16 has no note` |
| | | `Unknown route /crm/nope` |
| `405 Method Not Allowed` | Any method other than `GET` / `OPTIONS` | `Only GET is supported, the crm is read only` |
| `500 Internal Server Error` | Unexpected failure (for example the database is down). Details are printed in the server console. | `Something went wrong while reading the data` |

### Dates and times

| Type | Format | Example | Used by |
|---|---|---|---|
| Date and time | ISO-8601, no time zone | `"2026-10-12T14:53:07"` | `createdAt` on feedbacks, chats, notes |
| Time of day | `HH:mm:ss` | `"14:30:00"` | `scheduledTime` on schedules |

Times are the server's local time (IST). Fields that were never set are `null`.

### Lead reference

The global record lists (`/crm/exams`, `/crm/chats`, …) attach a short `lead` object to each record so the CRM can show who it belongs to without another request:

```json
"lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
```

Routes that are already scoped to one lead (`/crm/leads/{id}/...`, or a global list filtered with `?leadId=`) leave this field out.

### CORS

All routes allow cross-origin requests, so a frontend on another port (Vite, React, Next.js dev servers) can call the API directly:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
```

Preflight `OPTIONS` requests return `204 No Content`.

---

## Admin account

The CRM currently has one hardcoded admin account, defined in `CrmAdmin.java`:

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin@123` |
| Name | `CRM Admin` |
| Email | `admin@smartagent.local` |
| Role | `ADMIN` |
| Permissions | `READ_ALL` (can read every record) |

Authentication is **not enforced yet**: every route is open to anyone who can reach the server. `CrmAdmin.matches(username, password)` is ready for the login route that will be added later. Because the routes are open, do not share the ngrok URL publicly while it serves `/crm`.

---

## Routes

### GET /crm/

Lists every CRM route with a short description. Useful for checking the server is up.

**Parameters:** none

**Example**

```bash
curl http://localhost:8081/crm/
```

```json
{
  "name": "SmartAgent CRM API",
  "routes": {
    "GET /crm/admin": "hardcoded admin account (no password)",
    "GET /crm/dashboard": "totals, recent leads and feedbacks, upcoming schedules, resources per category",
    "GET /crm/leads": "all leads with record counts (?search=&page=&size=)",
    "...": "..."
  }
}
```

Any unknown path under `/crm` returns `404 { "error": "Unknown route /crm/..." }`.

---

### GET /crm/admin

Returns the admin account the CRM is used with. The password is never included.

**Parameters:** none

**Example**

```bash
curl http://localhost:8081/crm/admin
```

```json
{
  "username": "admin",
  "name": "CRM Admin",
  "email": "admin@smartagent.local",
  "role": "ADMIN",
  "permissions": ["READ_ALL"]
}
```

---

### GET /crm/dashboard

Everything the CRM home page needs in one request.

**Parameters:** none

**Response fields**

| Field | Type | Meaning |
|---|---|---|
| `totals` | object | Number of records in each table: `leads`, `exams`, `grades`, `feedbacks`, `chats`, `notes`, `schedules`, `resources` |
| `recentLeads` | array of [Lead](#lead) | The 5 most recently created leads, newest first |
| `recentFeedbacks` | array of [Feedback](#feedback) | The 5 newest feedbacks, each with its `lead` reference |
| `upcomingSchedules` | array of [Schedule](#schedule) | Up to 10 follow-ups, earliest time first, each with its `lead` reference |
| `resourcesByCategory` | object | Number of resources per category, e.g. `{"COURSE": 10}` |

**Example**

```bash
curl http://localhost:8081/crm/dashboard
```

```json
{
  "totals": {
    "leads": 2, "exams": 0, "grades": 0, "feedbacks": 0,
    "chats": 0, "notes": 0, "schedules": 0, "resources": 15
  },
  "recentLeads": [
    { "id": 18, "phoneNumber": "9999999993", "name": "Amit Patil", "email": null, "location": "Nashik", "schooling": "Diploma" },
    { "id": 16, "phoneNumber": "9999999991", "name": "Test Student", "email": null, "location": "Pune", "schooling": "12th Science" }
  ],
  "recentFeedbacks": [],
  "upcomingSchedules": [],
  "resourcesByCategory": { "ADMISSION": 1, "COURSE": 10, "HOSTEL": 1, "PLACEMENT": 2, "SCHOLARSHIP": 1 }
}
```

---

### GET /crm/leads

All leads (students), newest first, with a summary of their records. Use it for the leads table.

**Query parameters**

| Name | Required | Meaning |
|---|---|---|
| `search` | no | Case-insensitive partial match on name, phone number, email or location |
| `page`, `size` | no | See [Pagination](#pagination) |

**Item fields:** every [Lead](#lead) field, plus:

| Field | Type | Meaning |
|---|---|---|
| `counts` | object | Number of `exams`, `grades`, `feedbacks` and `chats` the lead has |
| `hasNote` | boolean | Whether the agent saved a note for this lead |
| `scheduledTime` | string or `null` | Follow-up call time, if one is scheduled |

**Examples**

```bash
curl "http://localhost:8081/crm/leads"
curl "http://localhost:8081/crm/leads?search=amit"
curl "http://localhost:8081/crm/leads?search=pune&page=2&size=20"
```

```json
{
  "page": 1,
  "size": 50,
  "total": 1,
  "totalPages": 1,
  "items": [
    {
      "id": 18,
      "phoneNumber": "9999999993",
      "name": "Amit Patil",
      "email": null,
      "location": "Nashik",
      "schooling": "Diploma",
      "counts": { "exams": 0, "grades": 0, "feedbacks": 0, "chats": 0 },
      "hasNote": false,
      "scheduledTime": null
    }
  ]
}
```

---

### GET /crm/leads/{id}

One lead with **everything** linked to it. Use it for the lead detail page.

**Path parameters**

| Name | Meaning |
|---|---|
| `id` | Lead id (positive number) |

**Response fields:** every [Lead](#lead) field, plus:

| Field | Type | Order |
|---|---|---|
| `exams` | array of [Exam](#exam) | Oldest first |
| `grades` | array of [Grade](#grade) | Oldest first |
| `feedbacks` | array of [Feedback](#feedback) | Newest first |
| `chats` | array of [Chat](#chat) | Conversation order (oldest first) |
| `note` | [Note](#note) or `null` | — |
| `schedule` | [Schedule](#schedule) or `null` | — |

These nested records do not include a `lead` reference.

This route returns every chat message of the lead in one response. For leads with very long conversations, use [`/crm/leads/{id}/chats`](#get-crmleadsidchats) to page through them instead.

**Example**

```bash
curl http://localhost:8081/crm/leads/16
```

```json
{
  "id": 16,
  "phoneNumber": "9999999991",
  "name": "Test Student",
  "email": null,
  "location": "Pune",
  "schooling": "12th Science",
  "exams": [
    { "id": 3, "examName": "JEE Main", "score": 92.5, "scoreType": "percentile" }
  ],
  "grades": [
    { "id": 1, "qualification": "12th", "percentage": 85.0 }
  ],
  "feedbacks": [
    { "id": 7, "message": "Wants hostel details", "createdAt": "2026-10-12T14:53:07" }
  ],
  "chats": [
    { "id": 1, "role": "user", "message": "Tell me about BCA", "createdAt": "2026-10-12T14:50:00" },
    { "id": 2, "role": "assistant", "message": "BCA is a three-year programme...", "createdAt": "2026-10-12T14:50:02" }
  ],
  "note": { "id": 1, "content": "Interested in CSE, prefers Pune", "createdAt": "2026-10-12T14:55:00" },
  "schedule": { "id": 1, "scheduledTime": "14:30:00" }
}
```

> Values above are illustrative. A lead with no records returns empty arrays and `null` for `note` and `schedule`.

**Errors:** `400` if `id` is not a positive number, `404` if the lead does not exist.

---

### GET /crm/leads/{id}/exams

Paginated exams of one lead, newest first.

**Parameters:** `id` (path), `page`, `size`

```bash
curl http://localhost:8081/crm/leads/16/exams
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    { "id": 3, "examName": "JEE Main", "score": 92.5, "scoreType": "percentile" }
  ]
}
```

**Errors:** `400` bad id, `404` lead not found.

---

### GET /crm/leads/{id}/grades

Paginated grades (academic results) of one lead, newest first.

**Parameters:** `id` (path), `page`, `size`

```bash
curl http://localhost:8081/crm/leads/16/grades
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    { "id": 1, "qualification": "12th", "percentage": 85.0 }
  ]
}
```

**Errors:** `400` bad id, `404` lead not found.

---

### GET /crm/leads/{id}/feedbacks

Paginated feedbacks of one lead, newest first.

**Parameters:** `id` (path), `page`, `size`

```bash
curl http://localhost:8081/crm/leads/16/feedbacks
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    { "id": 7, "message": "Wants hostel details", "createdAt": "2026-10-12T14:53:07" }
  ]
}
```

**Errors:** `400` bad id, `404` lead not found.

---

### GET /crm/leads/{id}/chats

Paginated chat messages of one lead, in **conversation order** (oldest first), so pages read like a transcript.

**Parameters:** `id` (path), `page`, `size`

```bash
curl "http://localhost:8081/crm/leads/16/chats?size=100"
```

```json
{
  "page": 1, "size": 100, "total": 2, "totalPages": 1,
  "items": [
    { "id": 1, "role": "user", "message": "Tell me about BCA", "createdAt": "2026-10-12T14:50:00" },
    { "id": 2, "role": "assistant", "message": "BCA is a three-year programme...", "createdAt": "2026-10-12T14:50:02" }
  ]
}
```

**Errors:** `400` bad id, `404` lead not found.

---

### GET /crm/leads/{id}/note

The note the agent saved for this lead. A lead has at most one note, so this returns the note object itself (not a page).

**Parameters:** `id` (path)

```bash
curl http://localhost:8081/crm/leads/16/note
```

```json
{ "id": 1, "content": "Interested in CSE, prefers Pune", "createdAt": "2026-10-12T14:55:00" }
```

**Errors:** `400` bad id, `404` lead not found, `404 { "error": "Lead 16 has no note" }` if the lead exists but has no note.

---

### GET /crm/leads/{id}/schedule

The follow-up call scheduled for this lead. A lead has at most one schedule, so this returns the schedule object itself.

**Parameters:** `id` (path)

```bash
curl http://localhost:8081/crm/leads/16/schedule
```

```json
{ "id": 1, "scheduledTime": "14:30:00" }
```

**Errors:** `400` bad id, `404` lead not found, `404 { "error": "Lead 16 has no schedule" }` if the lead exists but has no schedule.

---

### GET /crm/exams

All exams across every lead, newest first.

**Query parameters**

| Name | Required | Meaning |
|---|---|---|
| `leadId` | no | Only exams of this lead (the `lead` field is then left out) |
| `page`, `size` | no | See [Pagination](#pagination) |

```bash
curl http://localhost:8081/crm/exams
curl "http://localhost:8081/crm/exams?leadId=16"
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    {
      "id": 3, "examName": "JEE Main", "score": 92.5, "scoreType": "percentile",
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    }
  ]
}
```

Filtering by a `leadId` that does not exist returns an empty page, not a 404. **Errors:** `400` if `leadId` is not a positive number.

---

### GET /crm/grades

All grades across every lead, newest first.

**Query parameters:** `leadId` (optional), `page`, `size`

```bash
curl http://localhost:8081/crm/grades
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    {
      "id": 1, "qualification": "12th", "percentage": 85.0,
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    }
  ]
}
```

---

### GET /crm/feedbacks

All feedbacks across every lead, newest first.

**Query parameters:** `leadId` (optional), `page`, `size`

```bash
curl http://localhost:8081/crm/feedbacks
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    {
      "id": 7, "message": "Wants hostel details", "createdAt": "2026-10-12T14:53:07",
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    }
  ]
}
```

---

### GET /crm/chats

All chat messages across every lead, **newest first** (good for an activity feed).

With `?leadId=` the order switches to **conversation order** (oldest first), the same as [`/crm/leads/{id}/chats`](#get-crmleadsidchats).

**Query parameters:** `leadId` (optional), `page`, `size`

```bash
curl "http://localhost:8081/crm/chats?size=20"
```

```json
{
  "page": 1, "size": 20, "total": 2, "totalPages": 1,
  "items": [
    {
      "id": 2, "role": "assistant", "message": "BCA is a three-year programme...", "createdAt": "2026-10-12T14:50:02",
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    },
    {
      "id": 1, "role": "user", "message": "Tell me about BCA", "createdAt": "2026-10-12T14:50:00",
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    }
  ]
}
```

---

### GET /crm/notes

All notes across every lead, newest first.

**Query parameters:** `leadId` (optional), `page`, `size`

```bash
curl http://localhost:8081/crm/notes
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    {
      "id": 1, "content": "Interested in CSE, prefers Pune", "createdAt": "2026-10-12T14:55:00",
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    }
  ]
}
```

---

### GET /crm/schedules

All follow-up calls across every lead, **earliest time first** (use it as a call list).

**Query parameters:** `leadId` (optional), `page`, `size`

```bash
curl http://localhost:8081/crm/schedules
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    {
      "id": 1, "scheduledTime": "14:30:00",
      "lead": { "id": 16, "name": "Test Student", "phoneNumber": "9999999991" }
    }
  ]
}
```

Schedules store a time of day only (no date).

---

### GET /crm/resources

University information the agent uses to answer questions (courses, hostel, scholarships, placements, admissions), ordered by id.

**Query parameters**

| Name | Required | Meaning |
|---|---|---|
| `category` | no | Exact category, case-insensitive: `COURSE`, `HOSTEL`, `SCHOLARSHIP`, `PLACEMENT`, `ADMISSION` |
| `search` | no | Case-insensitive partial match on the title |
| `page`, `size` | no | See [Pagination](#pagination) |

`category` and `search` can be combined.

```bash
curl http://localhost:8081/crm/resources
curl "http://localhost:8081/crm/resources?category=placement"
curl "http://localhost:8081/crm/resources?search=bca"
curl "http://localhost:8081/crm/resources?category=COURSE&search=b.tech"
```

```json
{
  "page": 1, "size": 50, "total": 1, "totalPages": 1,
  "items": [
    {
      "id": 9,
      "category": "COURSE",
      "title": "Bachelor of Computer Applications (BCA)",
      "description": "MIT-ADT University, Pune. School of Business and Computer Applications. BCA is officially listed..."
    }
  ]
}
```

`description` is long text (up to 5000 characters) and can contain line breaks (`\n`). Render it with `white-space: pre-line` or split on `\n`.

---

### GET /crm/resources/{id}

One resource.

**Path parameters:** `id` (positive number)

```bash
curl http://localhost:8081/crm/resources/9
```

```json
{
  "id": 9,
  "category": "COURSE",
  "title": "Bachelor of Computer Applications (BCA)",
  "description": "MIT-ADT University, Pune. School of Business and Computer Applications. BCA is officially listed..."
}
```

**Errors:** `400` bad id, `404 { "error": "Resource 99999 not found" }`.

---

## Record fields

Any field can be `null` when the agent has not collected it yet, except ids and `phoneNumber`.

### Lead

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Lead id |
| `phoneNumber` | string | Student's phone number, without country code (e.g. `"8459781390"`), unique |
| `name` | string | Student's name |
| `email` | string | Email address |
| `location` | string | City |
| `schooling` | string | Current schooling, e.g. `"12th Science"` |

### Exam

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Exam record id |
| `examName` | string | e.g. `"JEE Main"`, `"MHT-CET"` |
| `score` | number | Score obtained |
| `scoreType` | string | How to read `score`: `"percentile"`, `"percentage"`, `"marks"` |

### Grade

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Grade record id |
| `qualification` | string | e.g. `"10th"`, `"12th"` |
| `percentage` | number | Percentage obtained |

### Feedback

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Feedback id |
| `message` | string | What the agent recorded |
| `createdAt` | date-time | When it was recorded |

### Chat

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Message id |
| `role` | string | Who said it, as stored by the agent (e.g. student or assistant) |
| `message` | string | Message text |
| `createdAt` | date-time | When it was sent |

### Note

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Note id |
| `content` | string | Note text; the agent keeps one note per lead and updates it |
| `createdAt` | date-time | When it was saved |

### Schedule

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Schedule id |
| `scheduledTime` | time | Follow-up call time (`HH:mm:ss`), one per lead |

### Resource

| Field | Type | Meaning |
|---|---|---|
| `id` | number | Resource id |
| `category` | string | `COURSE`, `HOSTEL`, `SCHOLARSHIP`, `PLACEMENT` or `ADMISSION` |
| `title` | string | Name of the course / topic |
| `description` | string | Full details (fees, eligibility, facilities, …) |

---

## Frontend examples

### Small API helper (JavaScript)

```js
const CRM_API = "http://localhost:8081/crm";

async function crm(path, params = {}) {
  const query = new URLSearchParams(
    Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== "")
  ).toString();

  const res = await fetch(`${CRM_API}${path}${query ? `?${query}` : ""}`);
  const body = await res.json();

  if (!res.ok) {
    // every error has the shape { "error": "..." }
    throw new Error(body.error ?? `Request failed with ${res.status}`);
  }
  return body;
}
```

### Usage

```js
// dashboard cards
const { totals, recentLeads, upcomingSchedules } = await crm("/dashboard");

// leads table with search and paging
const leads = await crm("/leads", { search: "pune", page: 1, size: 20 });
console.log(`${leads.total} leads, page ${leads.page} of ${leads.totalPages}`);

// lead detail page
const lead = await crm(`/leads/${id}`);

// note is optional: a 404 just means "no note yet"
let note = null;
try {
  note = await crm(`/leads/${id}/note`);
} catch {
  note = null;
}

// load a whole transcript page by page
async function loadAllChats(leadId) {
  const all = [];
  for (let page = 1; ; page++) {
    const result = await crm(`/leads/${leadId}/chats`, { page, size: 500 });
    all.push(...result.items);
    if (page >= result.totalPages) return all;
  }
}

// course list
const courses = await crm("/resources", { category: "COURSE" });
```

---

## Where the code lives

| File | Purpose |
|---|---|
| `config/HttpServer.java` | Registers every `/crm` route on port 8081 |
| `controller/crm/CrmServlet.java` | Base class: JSON output, errors, CORS, paging and parameter checks |
| `controller/crm/CrmRepository.java` | Read-only Hibernate queries for every table |
| `controller/crm/CrmAdmin.java` | Hardcoded admin account |
| `controller/crm/CrmException.java` | Errors returned with an HTTP status |
| `controller/crm/CrmIndexServlet.java` | `GET /crm/` and the JSON 404 for unknown routes |
| `controller/crm/AdminServlet.java` | `GET /crm/admin` |
| `controller/crm/DashboardServlet.java` | `GET /crm/dashboard` |
| `controller/crm/LeadServlet.java` | `GET /crm/leads` and every `/crm/leads/{id}/...` route |
| `controller/crm/ExamServlet.java` | `GET /crm/exams` |
| `controller/crm/GradeServlet.java` | `GET /crm/grades` |
| `controller/crm/FeedbackServlet.java` | `GET /crm/feedbacks` |
| `controller/crm/ChatServlet.java` | `GET /crm/chats` |
| `controller/crm/NoteServlet.java` | `GET /crm/notes` |
| `controller/crm/ScheduleServlet.java` | `GET /crm/schedules` |
| `controller/crm/ResourceServlet.java` | `GET /crm/resources` and `GET /crm/resources/{id}` |

### Adding a new route

1. Create a class in `controller/crm` that extends `CrmServlet` and implements `handleGet(req)`. Return any map or list; it is written as JSON.
2. Throw `CrmException.badRequest(...)` or `CrmException.notFound(...)` for errors.
3. Register it in `HttpServer.java`, e.g. `crmContext.addServlet(MyServlet.class, "/my-route");`.
4. Add it to the route list in `CrmIndexServlet` and to this document.
