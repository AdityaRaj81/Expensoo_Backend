# 🔍 Render Deployment Troubleshooting Guide

## ❌ Current Error Analysis

**Error**: `dataSource or dataSourceClassName or jdbcUrl is required`

**Root Cause**: The `DATABASE_URL` environment variable is **NOT SET** on your Render Web Service.

---

## ✅ CRITICAL: Verify DATABASE_URL is Set

### Step 1: Check Environment Variables

1. Go to **Render Dashboard** → Your **Web Service** (expensoo-backend)
2. Click **"Environment"** tab on the left sidebar
3. **VERIFY** you see this environment variable:

   ```
   Key: DATABASE_URL
   Value: postgresql://username:password@hostname/database
   ```

### Step 2: If DATABASE_URL is Missing - ADD IT NOW

**Where to get the value:**

1. Go to **Render Dashboard** → Your **PostgreSQL Database** (expensoo-db)
2. Scroll down to **"Connections"** section
3. Copy the **"Internal Database URL"**

   Example:
   ```
   postgresql://expensoo_db_user:abc123xyz@dpg-xxxxx-a.singapore-postgres.render.com/expensoo_db
   ```

4. Go back to your **Web Service**
5. Click **"Environment"** tab
6. Click **"Add Environment Variable"**
7. Enter:
   ```
   Key: DATABASE_URL
   Value: [paste the Internal Database URL here]
   ```
8. Click **"Save Changes"**

### Step 3: Redeploy

After adding the environment variable:

1. Click **"Manual Deploy"** button
2. Select **"Deploy latest commit"**
3. Wait for build to complete (~3-5 minutes)

---

## 🔍 Verify Configuration in Logs

After deployment, check the logs for these messages:

### ✅ SUCCESS - You should see:

```
Using DATABASE_URL from environment
Database URL parsed successfully: jdbc:postgresql://dpg-xxxxx.singapore-postgres.render.com:5432/expensoo_db
HikariPool-1 - Starting...
HikariPool-1 - Added connection ...
HikariPool-1 - Start completed.
Started ExpenseTrackerApplication in X.XXX seconds
```

### ❌ FAILURE - If you see:

```
Using local database configuration from application.properties
```

→ This means `DATABASE_URL` is **STILL NOT SET** on Render. Go back to Step 2.

---

## 🚨 Common Mistakes

### Mistake 1: Using External Database URL

❌ **WRONG**:
```
postgresql://expensoo_db_user:pass@dpg-xxxxx.oregon-postgres.render.com/expensoo_db
```
(Notice "oregon" - this is external)

✅ **CORRECT**:
```
postgresql://expensoo_db_user:pass@dpg-xxxxx-a.singapore-postgres.render.com/expensoo_db
```
(Notice "-a" and your actual region - this is internal)

**Why?** Internal URLs are faster and don't cost bandwidth. Always use Internal.

---

### Mistake 2: Copy-Paste with Extra Spaces

❌ **WRONG**:
```
 postgresql://user:pass@host/db 
```
(Notice spaces before/after)

✅ **CORRECT**:
```
postgresql://user:pass@host/db
```
(No spaces)

---

### Mistake 3: Setting DATABASE_URL in Wrong Place

❌ **WRONG**: Setting it in the **Database** service's environment variables

✅ **CORRECT**: Setting it in the **Web Service** (backend app) environment variables

---

## 🔧 Advanced Debugging

### Check if DATABASE_URL is Accessible

Add this temporary endpoint to verify (remove after testing):

```java
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @GetMapping("/env")
    public Map<String, String> checkEnv() {
        Map<String, String> result = new HashMap<>();
        String dbUrl = System.getenv("DATABASE_URL");
        
        if (dbUrl == null || dbUrl.isEmpty()) {
            result.put("status", "DATABASE_URL NOT SET");
        } else {
            // Mask password for security
            String masked = dbUrl.replaceAll(":[^@]+@", ":****@");
            result.put("status", "DATABASE_URL IS SET");
            result.put("value", masked);
        }
        
        return result;
    }
}
```

Deploy and visit: `https://your-app.onrender.com/api/debug/env`

Expected response:
```json
{
  "status": "DATABASE_URL IS SET",
  "value": "postgresql://user:****@host/db"
}
```

---

## 📋 Complete Deployment Checklist

### Database Setup

- [ ] PostgreSQL database created on Render
- [ ] Database status is "Available" (not "Creating" or "Failed")
- [ ] Copied **Internal Database URL** from database dashboard
- [ ] Internal URL includes region code (e.g., `dpg-xxxxx-a.singapore-postgres.render.com`)

### Web Service Setup

- [ ] Web Service created and connected to repository
- [ ] Runtime set to **Docker**
- [ ] Region matches database region (e.g., Singapore)
- [ ] Environment variable `DATABASE_URL` is **SET** in Web Service
- [ ] Value is the **Internal** Database URL (not External)
- [ ] No extra spaces in the DATABASE_URL value
- [ ] Clicked "Save Changes" after adding environment variable

### Code Setup

- [ ] Latest code pushed to repository (with updated DatabaseConfig.java)
- [ ] Dockerfile uses `eclipse-temurin:21-jre-alpine`
- [ ] pom.xml has `<java.version>21</java.version>`
- [ ] DatabaseConfig.java exists in `src/main/java/com/expenso/expense_tracker/config/`

### Deployment

- [ ] Triggered manual deploy or pushed to trigger auto-deploy
- [ ] Build completed successfully (green checkmark)
- [ ] Logs show "Using DATABASE_URL from environment"
- [ ] Logs show "HikariPool-1 - Start completed"
- [ ] Logs show "Started ExpenseTrackerApplication"
- [ ] No errors in logs

---

## 🎯 Quick Fix Command List

If you need to redeploy quickly:

```bash
# 1. Commit your changes
git add .
git commit -m "Fix database configuration"

# 2. Push to trigger deploy
git push origin main

# 3. While deploying, double-check Render:
# - PostgreSQL database is running
# - DATABASE_URL is set in Web Service environment variables
# - Internal URL is used (not External)
```

---

## 💡 Why DatabaseConfig.java is Better

The new `DatabaseConfig.java` will:

1. ✅ Check if `DATABASE_URL` environment variable exists
2. ✅ Parse Render's `postgresql://` format
3. ✅ Convert to Spring Boot's `jdbc:postgresql://` format
4. ✅ Add `?sslmode=require` automatically
5. ✅ Print debug messages to logs
6. ✅ Fall back to local config for development
7. ✅ Provide clear error messages if something is wrong

---

## 🆘 Still Not Working?

### Check These in Order:

1. **Database Status**: Is it "Available" or stuck in "Creating"?
2. **Environment Variable**: Is `DATABASE_URL` visible in Web Service → Environment tab?
3. **Build Logs**: Does it show "Using DATABASE_URL from environment"?
4. **Connection Logs**: Any firewall or SSL errors after "Using DATABASE_URL"?
5. **Database Region**: Does Web Service region match Database region?

### Get the Full Log Output

In Render Dashboard → Web Service → Logs:

1. Click the **"Download Logs"** button
2. Look for these specific lines:
   ```
   Using DATABASE_URL from environment
   Database URL parsed successfully: jdbc:postgresql://...
   ```

If you see:
```
Using local database configuration from application.properties
```

→ **DATABASE_URL IS NOT SET**. Go back and add it properly.

---

## ✅ Success Indicators

Once everything is working, you'll see:

```
2026-06-30T14:20:15.123Z  INFO 1 --- [main] DatabaseConfig: Using DATABASE_URL from environment
2026-06-30T14:20:15.124Z  INFO 1 --- [main] DatabaseConfig: Database URL parsed successfully: jdbc:postgresql://dpg-xxxxx-a.singapore-postgres.render.com:5432/expensoo_db
2026-06-30T14:20:16.234Z  INFO 1 --- [main] com.zaxxer.hikari.HikariDataSource: HikariPool-1 - Starting...
2026-06-30T14:20:17.456Z  INFO 1 --- [main] com.zaxxer.hikari.pool.HikariPool: HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@abc123
2026-06-30T14:20:17.789Z  INFO 1 --- [main] com.zaxxer.hikari.HikariDataSource: HikariPool-1 - Start completed.
2026-06-30T14:20:20.123Z  INFO 1 --- [main] com.expenso.expense_tracker.ExpenseTrackerApplication: Started ExpenseTrackerApplication in 15.234 seconds
```

Your backend is now live! 🎉

---

## 📞 Need More Help?

1. Share the **full deployment logs** (especially the first 50 lines after "Starting...")
2. Verify `DATABASE_URL` is set by visiting the debug endpoint (if you added it)
3. Check Render Status Page: https://status.render.com/
4. Contact Render Support: support@render.com

**Most common solution**: Just set the `DATABASE_URL` environment variable properly! 90% of issues are solved by this.
