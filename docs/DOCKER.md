## Prerequisites
- Docker 24+ and Docker Compose v2 (`docker compose` command)
- ~2 GB free disk space for images + build cache

## Environment Setup
```bash
cp .env.example .env
```
Edit `.env` and fill in your values before starting.

## Required Environment Variables

| Variable                     | Description                  | Example                                     |
|------------------------------|------------------------------|---------------------------------------------|
| `POSTGRES_DB`                | Database name                | `fitnation`                                 |
| `POSTGRES_USER`              | DB superuser                 | `postgres`                                  |
| `POSTGRES_PASSWORD`          | DB password                  | `strongpassword`                            |
| `SPRING_DATASOURCE_URL`      | JDBC URL                     | `jdbc:postgresql://postgres:5432/fitnation` |
| `SPRING_DATASOURCE_USERNAME` | Spring datasource user       | `postgres`                                  |
| `SPRING_DATASOURCE_PASSWORD` | Spring datasource password   | `strongpassword`                            |
| `JWT_SECRET`                 | HMAC secret, min 32 chars    | `my-super-secret-key-minimum-32-chars!!`    |
| `JWT_EXPIRATION`             | Access token TTL (seconds)   | `3600`                                      |
| `JWT_REFRESH_EXPIRATION`     | Refresh token TTL (seconds)  | `604800`                                    |

## Start
```bash
docker compose up --build
API will be available at http://localhost:8080.


Check running containers:
bash
docker compose ps

Check Liquibase migrations ran successfully:
bash
docker compose logs app | grep -i liquibase

Check API health:
bash
curl http://localhost:8080/actuator/health


Stop containers (data is preserved):
bash
docker compose down

⚠️ **Warning:** The following command also **deletes the database volume** (all data will be lost):
bash
docker compose down -v

bash
docker compose down -v
docker compose up --build

```