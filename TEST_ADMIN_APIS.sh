# Admin API Test Script
# Run these commands to test the admin endpoints after starting the backend

# Variables
BASE_URL="http://localhost:8080"
ADMIN_EMAIL="admin@expenso.com"
ADMIN_PASSWORD="admin123"

# Step 1: Login as Admin and extract token
echo "🔐 Step 1: Admin Login"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$ADMIN_EMAIL\", \"password\": \"$ADMIN_PASSWORD\"}")

echo "Login Response:"
echo "$LOGIN_RESPONSE" | jq .

ADMIN_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
echo "Token: $ADMIN_TOKEN"
echo ""

# Step 2: Get Admin Dashboard Stats
echo "📊 Step 2: Get Admin Dashboard Stats"
curl -s -X GET "$BASE_URL/api/admin/dashboard" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | jq .
echo ""

# Step 3: Get All Users (paginated)
echo "👥 Step 3: Get All Users"
curl -s -X GET "$BASE_URL/api/admin/users?page=0&size=10" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | jq .
echo ""

# Step 4: Search Users by Email
echo "🔍 Step 4: Search Users by Email"
curl -s -X GET "$BASE_URL/api/admin/users?search=test&page=0&size=10" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | jq .
echo ""

# Step 5: Get All Transactions (paginated)
echo "📈 Step 5: Get All Transactions"
curl -s -X GET "$BASE_URL/api/admin/transactions?page=0&size=10" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | jq .
echo ""

# Step 6: Get System Health
echo "💚 Step 6: Get System Health"
curl -s -X GET "$BASE_URL/api/admin/health" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | jq .
echo ""

# Step 7: Block a User (replace USER_ID with actual UUID)
echo "🚫 Step 7: Block User (example)"
# First, get a user ID from Step 3 response
USER_ID="<replace-with-user-uuid>"
echo "Blocking user: $USER_ID"
curl -s -X PUT "$BASE_URL/api/admin/users/$USER_ID/toggle" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"active\": false}" | jq .
echo ""

# Step 8: Unblock the same User
echo "✅ Step 8: Unblock User"
curl -s -X PUT "$BASE_URL/api/admin/users/$USER_ID/toggle" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"active\": true}" | jq .
