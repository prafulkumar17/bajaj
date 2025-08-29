# bajaj
Spring boot app

# BFH Qualifier 1 — Spring Boot Auto-Submitter

This Spring Boot application implements the exact workflow required by the BFH Qualifier problem statement.

---

## 🚀 Features
- On startup, the app:
  1. **Generates a webhook** by POSTing your details (name, regNo, email) to the given API.
  2. Reads the response (`webhook` + `accessToken`).
  3. Based on the **last two digits of your regNo**:
     - Odd → loads SQL from `src/main/resources/queries/question1.sql`
     - Even → loads SQL from `src/main/resources/queries/question2.sql`
  4. Saves the query in an H2 in-memory database (`Solution` table).
  5. Submits `{ "finalQuery": "..." }` to the webhook with the JWT token in the `Authorization` header.

---

## ⚙️ Configuration
Set your details in `src/main/resources/application.yml`:

```yaml
app:
  name: "Your Name"
  regNo: "YOUR_REG_NO"
  email: "your@email.com"
  dryRun: false
