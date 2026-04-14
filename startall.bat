@echo off
title ShopSphere Ecosystem Manager
echo ==========================================
echo    SHOPSPHERE MICROSERVICES STARTUP
echo ==========================================

:: 1. Start Eureka Server (Infrastructure)
echo [1/7] Starting Eureka Server (Port: 8761)...
cd infrastructure
start "EUREKA-SERVER" mvn spring-boot:run
cd ..
timeout /t 20 /nobreak > nul

:: 2. Start API Gateway
echo [2/7] Starting API Gateway (Port: 8080)...
cd api-gateway
start "API-GATEWAY" mvn spring-boot:run
cd ..
timeout /t 15 /nobreak > nul

:: 3. Start Auth Service
echo [3/7] Starting Auth Service (Port: 8081)...
cd auth-service
start "AUTH-SERVICE" mvn spring-boot:run
cd ..

:: 4. Start Catalog Service
echo [4/7] Starting Catalog Service (Port: 8082)...
cd catalog-service
start "CATALOG-SERVICE" mvn spring-boot:run
cd ..

:: 5. Start Inventory Service
echo [5/7] Starting Inventory Service (Port: 8083)...
cd inventory-service
start "INVENTORY-SERVICE" mvn spring-boot:run
cd ..

:: 6. Start Logistics Service
echo [6/7] Starting Logistics Service (Port: 8084)...
cd logistics-service
start "LOGISTICS-SERVICE" mvn spring-boot:run
cd ..

:: 7. Start Order Service
echo [7/7] Starting Order Service (Port: 8085)...
cd order-service
start "ORDER-SERVICE" mvn spring-boot:run
cd ..

:: 8. Start Analytics Service
echo [8/7] Starting Analytics Service (Port: 8086)...
cd analytics-service
start "ANALYTICS-SERVICE" mvn spring-boot:run
cd ..

echo ==========================================
echo    ALL SERVICES INITIALIZED
echo ==========================================
echo Dashboard: http://localhost:8761
echo API Gateway (Public Entrance): http://localhost:8080
echo.
echo Individual service URLs (for direct debugging):
echo Auth: http://localhost:8081
echo Catalog: http://localhost:8082
echo Inventory: http://localhost:8083
echo Logistics: http://localhost:8084
echo Order: http://localhost:8085
echo Analytics: http://localhost:8086
echo ==========================================
pause