# 🛒 Grocery Pickbot Store

A web application for managing grocery products, creating orders, and visualizing the path of a picking robot. Built using **Spring Boot** for the backend and **HTML/CSS/JavaScript** for the frontend.

---

## 📌 Features

- ✅ Create, read, update, and delete (CRUD) grocery products
- ✅ Create orders with selected products
- ✅ Handle orders that cannot be fulfilled due to missing stock
- ✅ Display robot route for successful orders

---

## 🛠️ Technologies Used

- Java 17+
- Spring Boot
- Spring Data JPA
- RESTful API
- MySQL
- HTML, CSS, JavaScript (Vanilla)

---

## 📦 How to Run

1. Clone the repository
2. Setup h2 database - In groceryPickbot/target/classes/application.properties file add:
- spring.datasource.url=jdbc:h2:mem:testdb
- spring.datasource.driverClassName=org.h2.Driver
- spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
- spring.h2.console.enabled=true
#### Hibernate config
- spring.jpa.show-sql=true
- spring.jpa.hibernate.ddl-auto=update
* You can access the database here - http://localhost:8080/h2-console
3. In groceryPickbot/target/classes/application.properties file add:
- jwt.secret=hereAddYourSecretKey // for generating key you can go here - https://jwtsecrets.com/
- jwt.expiration=3600000
4. Run the Spring Boot application (`GroceryApplication.java`)
5. Open `(http://localhost:8080/index.html)` in a browser

Name: Kostadin Harizanov
GitHub: github.com/KoKi0210

