@echo off
title ShopSphere Ecosystem Manager
echo ==========================================
echo    SHOPSPHERE MICROSERVICES STARTUP
echo ==========================================

:: 1. Start Eureka Server (Infrastructure)
echo [1/8] Starting Eureka Server (Port: 8761)...
cd infrastructure
start "EUREKA-SERVER" cmd /k "mvnw.cmd spring-boot:run"
cd ..
timeout /t 20 /nobreak > nul

:: 2. Start API Gateway
echo [2/8] Starting API Gateway (Port: 9090)...
cd api-gateway
start "API-GATEWAY" cmd /k "mvnw.cmd spring-boot:run"
cd ..
timeout /t 15 /nobreak > nul

:: 3. Start Auth Service
echo [3/8] Starting Auth Service (Port: 8081)...
cd auth-service
start "AUTH-SERVICE" cmd /k "mvnw.cmd spring-boot:run"
cd ..

:: 4. Start Catalog Service
echo [4/8] Starting Catalog Service (Port: 8082)...
cd catalog-service
start "CATALOG-SERVICE" cmd /k "mvnw.cmd spring-boot:run"
cd ..

:: 5. Start Inventory Service
echo [5/8] Starting Inventory Service (Port: 8084)...
cd inventory-service
start "INVENTORY-SERVICE" cmd /k "mvnw.cmd spring-boot:run"
cd ..

:: 6. Start Logistics Service
echo [6/8] Starting Logistics Service (Port: 8086)...
cd logistics-service
start "LOGISTICS-SERVICE" cmd /k "mvnw.cmd spring-boot:run"
cd ..

:: 7. Start Order Service
echo [7/8] Starting Order Service (Port: 8083)...
cd order-service
start "ORDER-SERVICE" cmd /k "mvnw.cmd spring-boot:run"
cd ..

:: 8. Start Analytics Service
echo [8/8] Starting Analytics Service (Port: 8087)...
cd analytics-service
start "ANALYTICS-SERVICE" cmd /k "mvnw.cmd spring-boot:run"
cd ..

echo ==========================================
echo    ALL SERVICES INITIALIZED
echo ==========================================
pause