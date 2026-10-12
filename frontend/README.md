# SmartAgent CRM (frontend)

Read-only admin CRM for the SmartAgent voice agent. It shows everything the agent has collected from students: leads, conversations, feedback, notes, follow-up calls, exam results, grades, and the university knowledge base.

Built with React 18, React Router and Vite. It talks to the Java backend's `/crm` API (documented in `src/main/java/controller/crm/crm.md`).

## Run it

1. Start the backend so the API is available on `http://localhost:8081/crm`. `Main` must call `new HttpServer().start()`:

   ```
   mvn compile exec:java "-Dexec.mainClass=Main"
   ```

2. Start the CRM:

   ```
   cd frontend
   npm install
   npm run dev
   ```

3. Open http://localhost:5173

If the backend isn't running, every page shows a "CRM server is not reachable" message with a retry button.

## Pointing at another backend

The API address comes from `VITE_CRM_API`, defaulting to `http://localhost:8081/crm`. To change it, for example to your ngrok URL, copy `.env.example` to `.env` and edit it:

```
VITE_CRM_API=https://your-domain.ngrok-free.dev/crm
```

Restart `npm run dev` after changing `.env`.

## Production build

```
npm run build      # outputs to dist/
npm run preview    # serves dist/ on http://localhost:4173
```

## Pages

| Page | Path | API used |
|---|---|---|
| Dashboard | `/` | `/crm/dashboard`, `/crm/admin` |
| Leads | `/leads` | `/crm/leads?search=&page=&size=` |
| Lead profile | `/leads/:id` | `/crm/leads/{id}` |
| Conversations | `/chats` (`?leadId=` for one transcript) | `/crm/chats`, `/crm/leads/{id}` |
| Feedback | `/feedbacks` | `/crm/feedbacks` |
| Notes | `/notes` | `/crm/notes` |
| Follow-ups | `/schedules` | `/crm/schedules` |
| Exams | `/exams` | `/crm/exams` |
| Grades | `/grades` | `/crm/grades` |
| Resources | `/resources` (`?category=&search=&open=`) | `/crm/resources`, `/crm/resources/{id}`, `/crm/dashboard` |
| Admin | `/admin` | `/crm/admin`, `/crm/` |

Search, filters, page and page size are kept in the URL, so refreshing or sharing a link keeps the same view. The record pages also accept `?leadId=` to show one student's records.

## Project layout

```
src/
  api.js                 fetch wrapper (base URL, JSON errors, "server unreachable")
  hooks/useApi.js        loads a route, with loading / error / reload
  hooks/useQueryState.js page, size and filters in the URL
  utils/format.js        dates, times, scores, initials, avatar colours
  components/Layout.jsx  sidebar, top bar with global lead search, dark-mode toggle
  components/ui.jsx      cards, table, pagination, badges, avatars, tabs, modal, empty/error states
  components/Conversation.jsx  chat transcript bubbles
  pages/                 one file per page (Records.jsx holds exams, grades, feedback, notes, follow-ups)
  styles.css             design tokens (light + dark) and all styles
```

## Notes

- **Read-only:** the CRM only uses `GET` requests; it never changes data.
- **No sign-in yet:** anyone who can open the CRM can read all data. Don't expose it publicly until authentication is added.
- **Theme:** follows the system light/dark setting until the toggle in the top bar is used; the choice is remembered in the browser.
