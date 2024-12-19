# Bank Loan API

This project provides a backend API for managing loans in a bank. It allows employees to create, list, and pay loans for customers. The application is built with the Spring Boot framework and uses an H2 database.

## 1. Overview

The **Bank Loan API** is designed to allow a bank's employees to:
- Create loans for customers, ensuring that the customer has sufficient credit limit.
- List loans and installments for a given customer.
- Make payments for installments, including handling early payment discounts and late payment penalties.

## 2. Features

The API includes the following functionalities:

### 1. Create Customer
- Allows creation of a customer with parameters :
  - `name`
  - `surname`
  - `creditLimit`: (Default will be zero)
  - `usedCreditLimit`: (Default will be zero)

### 2. Create Loan
- Allows the creation of a loan for a given customer, amount, interest rate, and number of installments.
- The system checks if the customer has enough credit to get the new loan.
- The number of installments must be one of: 6, 9, 12, or 24.
- Interest rate should be between 0.1 and 0.5.
- The loan amount is calculated with the formula: `amount * (1 + interestRate)`.
- The due dates for installments are set to the first day of each month, starting with the next month.

### 3. List Loans
- Lists loans for a given customer, with optional filters such as:
  - `customerId`: Customer ID
  - `installments`: Number of installments (optional)
  - `isPaid`: Whether the loan is paid (optional)

### 4. List Installments
- Lists installments for a given loan.

### 5. Pay Loan
- Allows payments for a loan, with conditions:
  - Installments are paid fully or not at all.
  - The earliest installment is paid first. If there’s excess payment, it applies to the next installment.
  - Payments are restricted to installments due within the last 3 months.
  - **Early Payment Discount:** If an installment is paid **before** the due date, a discount is applied equal to:
    - `installmentAmount * 0.001 * (number of days before due date)`

  - **Late Payment Penalty:** If an installment is paid **after** the due date, a penalty is applied equal to:
    - `installmentAmount * 0.001 * (number of days after due date)`

## 3. Prerequisites

Before running the application, ensure you have the following tools installed:
- **Java 17** 
- **Gradle** for building and managing the project
- **Spring Boot** 3.2.1 for the backend framework
- A modern **IDE** (e.g., IntelliJ IDEA, Eclipse)

## 4. Installation

### Steps
1. Clone the repository:
  - `git clone https://github.com/dilemunal/CreditModule.git`
  - `cd CreditModule`
2. Install dependencies using Gradle:
  - `./gradlew build`

3. Run the application.
  - `./gradlew bootRun`

By default, the application will be available at http://localhost:8080.

## 5. Configuration
The project uses H2 as an in-memory database, and you can configure the application via the application.properties file. The most important properties are:
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

app.security.user.admin.username=admin
app.security.user.admin.password=admin

```

## 6. Usage & Sample Endpoints

### Database Access
The project uses H2 as an in-memory database for testing purposes. You can access the H2 database console at:

http://localhost:8080/h2-console

Use the following connection settings:
```
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave empty)
```
The database will reset each time the application is restarted.

### Endpoints
Here are some sample endpoints to interact with the API:
```
    BasicAuth : 
    - **Username:** `admin`
    - **Password:** `admin`
```
-  cURL to Create a Customer
 ```
   curl --location 'http://localhost:8080/createCustomer' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic YWRtaW46YWRtaW4=' \
   --header 'Cookie: JSESSIONID=83888F572587AB46822820F18C44BC94' \
   --data '{
   "name": "customer name",
   "surname": "customer surname",
   "creditLimit": 10000,
   "usedCreditLimit":0.0
   }
  ```
-  cURL to Create Loan

 ```
curl --location 'http://localhost:8080/api/loan/createLoan' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--header 'Cookie: JSESSIONID=83888F572587AB46822820F18C44BC94' \
--data '{
"loanAmount": 7000,
"numberOfInstallment": 6,
"interestRate": 0.5,
"customerId": 1
}
 ```
-  cURL to List Loans of a Customer

 ```
curl --location 'http://localhost:8080/api/loan/listLoans' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--header 'Cookie: JSESSIONID=83888F572587AB46822820F18C44BC94' \
--data '{
"customerId": 1
}

 ```
-  cURL to List Installments

 ```
curl --location 'http://localhost:8080/api/loan/listInstallments?loanId=1' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--header 'Cookie: JSESSIONID=83888F572587AB46822820F18C44BC94'

 ```
-  cURL to Pay Loan

 ```
curl --location 'http://localhost:8080/api/loan/payLoan' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--header 'Cookie: JSESSIONID=83888F572587AB46822820F18C44BC94' \
--data '{
"loanId": 1,
"paymentAmount": 250.0,
"paymentDate": "2024-12-18"
}
 ```

## 7. Technologies

This project was built using the following technologies:

- Spring Boot 3.x (Backend framework)
- H2 Database (In-memory database)
- JPA (Java Persistence API for database access)
- Spring Security (Authentication)
- Gradle (Build tool)
- JUnit5 (Unit testing)







