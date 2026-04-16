# 🏥 Hospital Management System

## 📌 Overview

The **Hospital Management System** is a backend application designed to streamline hospital operations such as patient management, doctor scheduling, appointment booking, and secure access control.

This project is built using **Java, Spring Boot, Spring Security, and REST APIs**, following clean architecture and best practices. It aims to provide a scalable, secure, and efficient system for managing hospital workflows.

---

## 🚀 Features

### 👨‍⚕️ Patient Management

* Register new patients
* View and update patient details
* Maintain medical history records

### 👩‍⚕️ Doctor Management

* Add and manage doctor profiles
* Assign specializations
* Manage availability and schedules

### 📅 Appointment Management

* Book, update, and cancel appointments
* Assign doctors to patients
* Track appointment history

### 🔐 Authentication & Authorization (Spring Security)

* Secure login and registration system
* Password encryption using BCrypt
* Session management

---

## 🔒 Role-Based Authorization

The system uses **Spring Security with Role-Based Access Control** to ensure that only authorized users can access specific resources.

### 👥 User Roles

#### 1. ADMIN

* Full access to the system
* Manage doctors, patients, and appointments
* Manage users and roles

#### 2. DOCTOR

* View assigned patients
* Manage appointments

#### 3. PATIENT

* Register and login
* Book appointments

---

## 🔑 Security Implementation

### ✅ Spring Security Features Used

* **Authentication** using username & password
* **Authorization** using roles (ADMIN, DOCTOR, PATIENT)
* **Password Encryption** using BCryptPasswordEncoder
* **JWT (JSON Web Token)** for stateless authentication 
* **Custom UserDetailsService** for loading user-specific data
* **Security Filters** for request validation

---

## 🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA / Hibernate
* REST APIs

### Database

* MySQL

### Tools & Technologies

* Maven
* Git & GitHub
* Postman (API Testing)

---

## 📂 Project Structure

```
src/main/java/com/hms
│
├── controller        # REST Controllers
├── service           # Business Logic
├── repository        # Data Access Layer (JPA Repositories)
├── entity            # Database Entities
├── dto               # Data Transfer Objects
├── config            # Security & App Configurations
└── exception         # Global Exception Handling
```

---

## ⚙️ Setup & Installation

1. Clone the repository:

```bash
git clone https://github.com/your-username/hospital-management-system.git
```

2. Navigate to the project directory:

```bash
cd hospital-management-system
```

3. Configure database in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hms
spring.datasource.username=root
spring.datasource.password=yourpassword
```

4. Run the application:

```bash
mvn spring-boot:run
```

---

## 📡 API Endpoints (Sample)

| Method | Endpoint             | Description            | Role Required |
| ------ | -------------------- | ---------------------- | ------------- |
| POST   | /api/auth/register   | Register user          | Public        |
| POST   | /api/auth/login      | Login user             | Public        |
| GET    | /api/admin/users     | Get all users          | ADMIN         |
| GET    | /api/doctor/patients | View assigned patients | DOCTOR        |
| POST   | /api/patient/book    | Book appointment       | PATIENT       |

---

## 🧪 Testing

* Unit Testing using JUnit
* API Testing using Postman
* Validate role-based access scenarios

---

## 📈 Future Enhancements

* Integration with frontend (React)
* Email/SMS notifications
* Payment gateway integration
* Dashboard & analytics
* Microservices architecture

---

## 👨‍💻 Author

**Sushant Rokade**
B.Tech IT Graduate | Software Developer

---

## ⭐ Conclusion

The Hospital Management System demonstrates a strong implementation of **secure, scalable backend architecture** using Spring Boot and Spring Security. The integration of **role-based authorization** ensures data privacy and controlled access, making it suitable for real-world healthcare applications.

---
