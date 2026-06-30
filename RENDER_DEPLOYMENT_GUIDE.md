# 🚀 Render Deployment Guide - Expensoo Backend

## ✅ What's Fixed

1. **Java 21 LTS** - Downgraded from Java 25 to ensure Docker image availability
2. **Database URL Parser** - Added `DatabaseConfig.java` to automatically convert Render's PostgreSQL URL format
3. **Optimized HikariCP** - Better connection pool settings for cloud deployment
4. **SSL Support** - Automatic `sslmode=require` for secure database connections

---

## 📋 Step-by-Step Deployment on Render

### Step 1: Create PostgreSQL Database

1. Log in to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"PostgreSQL"**
3. Configure your database:
   ```
   Name: expensoo-db
   Database: expensoo_db
   User: (auto-generated)
   Region: Singapore (or closest to you)
   PostgreSQL Version: 16 (latest)
   Plan: Free (or Starter $7/month for better performance)
   ```
4. Click **"Create Database"**
5. ⏳ Wait 1-2 minutes for provisioning

### Step 2: Copy Database Connection URL

After database creation, you'll see connection details. Copy the **Internal Database URL**:

```
postgresql://expensoo_db_user:xxxxxxxxxxx@dpg-xxxxx-a.singapore-postgres.render.com/expensoo_db
```

**IMPORTANT**: Use the **Internal Database URL** (not External) - it's faster and free within Render's network.

---

### Step 3: Create Web Service

1. Go back to Render Dashboard
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub/GitLab repository
4. Configure the service:

   ```
   Name: expensoo-backend
   Region: Singapore (same as database)
   Branch: main (or your deployment branch)
   Root Directory: Expensoo_Backend (if monorepo, otherwise leave blank)
   Runtime: Docker
   Plan: Free (or Starter $7/month)
   ```

5. **Environment Variables** - Add these:

   | Key | Value |
   |-----|-------|
   | `DATABASE_URL` | Paste the Internal Database URL from Step 2 |
   | `PORT` | `8080` |

   Example DATABASE_URL:
   ```
   postgresql://expensoo_db_user:xxxxxxxxxxx@dpg-xxxxx-a.singapore-postgres.render.com/expensoo_db
   ```

6. Click **"Create Web Service"**

---

### Step 4: Wait for Deployment

Render will:
1. ✅ Clone your repository
2. ✅ Build Docker image using your Dockerfile
3. ✅ Run `mvn clean package` inside the container
4. ✅ Start your Spring Boot application
5. ✅ Connect to PostgreSQL database

**Build time**: ~3-5 minutes for first deployment

---

## 🔍 Verify Deployment

### Check Logs

In your Web Service dashboard, click **"Logs"** tab. You should see:

✅ **Successful startup logs:**
```
HikariPool-1 - Start completed
Started ExpenseTrackerApplication in X.XXX seconds
Tomcat started on port 8080
```

❌ **Error to avoid:**
```
Connection is not available, request timed out
```
→ This means DATABASE_URL is not set correctly

---

## 🧪 Test Your API

Once deployed, Render gives you a URL like:
```
https://expensoo-backend.onrender.com
```

### Test Health Endpoint

```bash
curl https://expensoo-backend.onrender.com/api/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### Test Authentication Endpoint

```bash
curl -X POST https://expensoo-backend.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test1234"
  }'
```

---

## 🚨 Troubleshooting

### Issue 1: Database Connection Timeout

**Symptom:**
```
HikariPool-1 - Connection is not available, request timed out after 30000ms
```

**Solution:**
1. Verify `DATABASE_URL` environment variable is set in Render
2. Make sure you used the **Internal Database URL** (not External)
3. Check database status - ensure it's running (not suspended)

---

### Issue 2: "Password authentication failed"

**Symptom:**
```
FATAL: password authentication failed for user "..."
```

**Solution:**
- Re-copy the DATABASE_URL from Render dashboard
- Ensure no extra spaces or characters when pasting
- Database password might have special characters - the URL should be properly encoded

---

### Issue 3: SSL Connection Error

**Symptom:**
```
The connection attempt failed: FATAL: no pg_hba.conf entry for host
```

**Solution:**
- The `DatabaseConfig.java` automatically adds `?sslmode=require`
- If issue persists, manually add to DATABASE_URL:
  ```
  postgresql://user:pass@host:5432/db?sslmode=require
  ```

---

### Issue 4: Port Binding Error

**Symptom:**
```
Web service failed to bind to $PORT within 90 seconds
```

**Solution:**
- Ensure `PORT` environment variable is set to `8080`
- Check `application.properties` has `server.port=8080`
- Dockerfile exposes port 8080: `EXPOSE 8080`

---

## 🔐 Security Checklist

Before going to production:

- [ ] Change default PostgreSQL password
- [ ] Set up proper JWT secret in environment variables
- [ ] Enable HTTPS (Render provides this automatically)
- [ ] Add rate limiting for API endpoints
- [ ] Set up CORS properly for your frontend domain
- [ ] Review and limit database user permissions
- [ ] Enable database backups (Render Pro plan)

---

## 📊 Monitoring

### View Metrics

In Render Dashboard → Your Service → **Metrics** tab:

- CPU usage
- Memory usage
- Request count
- Response times

### Set Up Alerts

1. Go to **Settings** → **Notifications**
2. Add email/Slack webhook
3. Get notified on:
   - Deploy failures
   - Service crashes
   - High resource usage

---

## 🔄 Continuous Deployment

Render automatically deploys when you push to your configured branch:

```bash
git add .
git commit -m "Update backend"
git push origin main
```

Render will:
1. Detect the push
2. Start a new build
3. Run tests (if configured)
4. Deploy the new version
5. Keep old version running until new one is healthy

---

## 💰 Cost Optimization

### Free Tier Limitations

- **Web Service**: Sleeps after 15 minutes of inactivity
- **Database**: 90-day expiration for free instances
- **First request**: May take 30-60 seconds (cold start)

### Upgrade to Starter ($7/month each)

Benefits:
- ✅ No sleep/downtime
- ✅ Persistent database
- ✅ Better performance
- ✅ Daily backups
- ✅ Faster builds

---

## 🆘 Getting Help

1. **Render Docs**: https://render.com/docs
2. **Render Support**: support@render.com
3. **Community Forum**: https://community.render.com/
4. **Status Page**: https://status.render.com/

---

## ✅ Deployment Checklist

- [ ] PostgreSQL database created and running
- [ ] DATABASE_URL environment variable set
- [ ] Web service connected to repository
- [ ] Build successful (check logs)
- [ ] Application started (check for "Started ExpenseTrackerApplication")
- [ ] Health endpoint returns 200 OK
- [ ] Database tables created automatically (check logs for Hibernate DDL)
- [ ] Frontend connected to backend URL
- [ ] CORS configured for frontend domain

---

## 🎉 You're Done!

Your backend is now live and connected to PostgreSQL. Next steps:

1. Test all API endpoints
2. Connect your frontend
3. Monitor logs for any issues
4. Set up proper environment variables for production
5. Consider upgrading to Starter plan for better reliability

**Need help?** Check the logs first - they usually tell you exactly what's wrong!
