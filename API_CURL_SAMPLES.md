# KitchenDiary API - Sample `curl` Commands

Note: all `/api/**` endpoints require authentication.
Use Basic auth in samples: `rahul@example.com / rahul123`.
`POST/PUT/DELETE` on `/api/**` require `ADMIN` role.

## 1) Health

```bash
curl -X GET "http://localhost:8080/health"
```

## 2) Create Business

```bash
curl -X POST "http://localhost:8080/api/businesses" \
  -H "Content-Type: application/json" \
  -u "rahul@example.com:rahul123" \
  -d '{
    "name": "Demo Kitchen",
    "gstin": "27ABCDE1234F1Z5",
    "address": "Andheri East",
    "city": "Mumbai",
    "state": "MH"
  }'
```

## 3) List My Businesses

```bash
curl -X GET "http://localhost:8080/api/businesses" \
  -u "rahul@example.com:rahul123"
```

## 4) Get Business By ID

```bash
curl -X GET "http://localhost:8080/api/businesses/1" \
  -u "rahul@example.com:rahul123"
```

## 5) Create Platform

```bash
curl -X POST "http://localhost:8080/api/businesses/1/platforms" \
  -H "Content-Type: application/json" \
  -u "rahul@example.com:rahul123" \
  -d '{
    "code": "ZOMATO",
    "name": "Zomato"
  }'
```

## 6) List Platforms

```bash
curl -X GET "http://localhost:8080/api/businesses/1/platforms" \
  -u "rahul@example.com:rahul123"
```

## 7) Create Order (Under a Platform)

```bash
curl -X POST "http://localhost:8080/api/businesses/1/orders/platform/1" \
  -H "Content-Type: application/json" \
  -u "rahul@example.com:rahul123" \
  -d '{
    "orderDate": "2026-02-21",
    "grossAmount": 2400.00,
    "commissionRate": 22.00,
    "gstRateOnComm": 18.00,
    "netReceived": 1768.64,
    "notes": "Dinner rush"
  }'
```

## 8) List Orders (Date Range, Optional `platformId`)

```bash
curl -X GET "http://localhost:8080/api/businesses/1/orders?startDate=2026-02-01&endDate=2026-02-28" \
  -u "rahul@example.com:rahul123"
```

With platform filter:

```bash
curl -X GET "http://localhost:8080/api/businesses/1/orders?startDate=2026-02-01&endDate=2026-02-28&platformId=1" \
  -u "rahul@example.com:rahul123"
```

## 9) Create Expense

```bash
curl -X POST "http://localhost:8080/api/businesses/1/expenses" \
  -H "Content-Type: application/json" \
  -u "rahul@example.com:rahul123" \
  -d '{
    "expenseDate": "2026-02-21",
    "category": "Packaging",
    "amount": 350.00,
    "notes": "Containers and paper bags"
  }'
```

## 10) List Expenses (Date Range, Optional `category`)

```bash
curl -X GET "http://localhost:8080/api/businesses/1/expenses?startDate=2026-02-01&endDate=2026-02-28" \
  -u "rahul@example.com:rahul123"
```

With category filter:

```bash
curl -X GET "http://localhost:8080/api/businesses/1/expenses?startDate=2026-02-01&endDate=2026-02-28&category=Packaging" \
  -u "rahul@example.com:rahul123"
```

## 11) Dashboard (Date Range Summary)

```bash
curl -X GET "http://localhost:8080/api/businesses/1/dashboard?startDate=2026-02-01&endDate=2026-02-28" \
  -u "rahul@example.com:rahul123"
```
