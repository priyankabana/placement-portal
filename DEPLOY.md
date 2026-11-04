# Deploying Placement Portal

The app is a single Docker container. It reads its port from `$PORT` and its database
from Spring profiles, so it runs on any Docker host. The steps below use Render's free tier.

| Item | Value |
|---|---|
| GitHub repository | https://github.com/priyankabana/placement-portal |
| Render service name | `vu-placement-portal` |
| Expected URL | https://vu-placement-portal.onrender.com (may differ: Render adds a suffix if the name is taken) |
| Health check | `/actuator/health` |
| Default profiles | `h2,prod` (in memory sample data, no database needed) |

## 1. Push the code to GitHub

```bash
cd placement-portal
git push origin main      # the repo already has origin = github.com/priyankabana/placement-portal
```

The **CI** workflow starts on the first push; check the
Actions tab shows a green run (tests, then the Docker build).

## 2. Create the service on Render (one click blueprint)

1. Sign in at https://render.com with GitHub.
2. **New > Blueprint**, then connect `priyankabana/placement-portal`.
3. Render reads `render.yaml` and shows one web service, `vu-placement-portal`, plan Free,
   runtime Docker. Click **Apply**.
4. The first build takes a few minutes (Maven downloads the dependencies inside the image).
   When the log shows `Started PlacementPortalApplication` and the health check passes, the
   service is live.

If `vu-placement-portal` is already taken, Render adds a suffix to the URL; use whatever
URL the dashboard shows.

## 3. Check it

```bash
curl https://vu-placement-portal.onrender.com/actuator/health   # {"status":"UP"}
curl https://vu-placement-portal.onrender.com/api/stats
```

Open https://vu-placement-portal.onrender.com and sign in with a demo account from
`docs/login-credentials.txt` (sample data only; passwords are stored as BCrypt hashes).

**Free tier cold start:** a free service sleeps after about 15 minutes without traffic. The
next request wakes it and takes around 50 seconds. Open the URL once before a demo or an
interview. Because the default profile is H2 in memory, every restart resets the data to the
sample season, which is what you want for a public demo.

## 4. Optional: permanent storage with MySQL

1. Create a MySQL database with any provider and note its JDBC URL, user and password.
2. In the Render dashboard, open the service, **Environment**, and set:

   | Key | Value |
   |---|---|
   | `SPRING_PROFILES_ACTIVE` | `mysql,prod` |
   | `DB_URL` | `jdbc:mysql://<host>:3306/placement_db` |
   | `DB_USERNAME` | your database user |
   | `DB_PASSWORD` | your database password |

3. Save; Render redeploys. Hibernate creates the six tables and `data-mysql.sql` inserts the
   sample rows once (`INSERT IGNORE`).

Never commit these values (see `.env.example`). `application-mysql.properties` reads them from the
environment; `DB_PASSWORD` has no default.

## Any other Docker host

```bash
docker build -t placement-portal .
docker run -d -p 8080:8080 -e PORT=8080 -e SPRING_PROFILES_ACTIVE=h2,prod placement-portal
curl localhost:8080/actuator/health
```

Railway, Fly.io, Koyeb, Google Cloud Run and AWS App Runner all accept this image as is;
they set `$PORT` and the app listens on it.
