# HOTEL MANAGEMENT SYSTEM

## Features

- ...

## Technologies used

- Backend: Java Spring
- Frontend:
- Database: MySQL
- Build tool: Maven

## Getting started

### Set up MySQL

- Step 1: Tạo Database

  ```sql
  CREATE DATABASE hotelms
  ```

- Step 2: Copy file `application.properties.example` trong foler `\src\main\resources` và đổi tên thành `application.properties`.
- Step 3: Điền thông tin Database Connection trên máy của mình vào.
- Step 4: Chạy lệnh `mvn spring-boot:run`
  Khi chạy các bảng sẽ được tạo tự động.
- Step 5: Tạo data mẫu, copy lệnh SQL trong file `\src\main\resources\data_mau.sql` để chạy trong mySQL.

### Run

```bash
mvn spring-boot:run
```
