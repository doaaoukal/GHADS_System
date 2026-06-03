# GHADS – Humanitarian Aid Distribution System

---

## 📌 System Name & Purpose

**GHADS (Humanitarian Aid Distribution System)** is a desktop-based application developed using JavaFX and MySQL.

The main purpose of the system is to manage and organize humanitarian aid distribution processes efficiently by handling:
- Beneficiary families
- Humanitarian organizations
- System users (Admin & Coordinator)
- Aid distribution records

The system ensures structured data management and improves the efficiency of humanitarian operations.

---

## ❗ Problem It Solves

Humanitarian organizations often face problems such as:
- Manual data entry errors
- Difficulty tracking aid distribution
- Lack of organized beneficiary records
- Inefficient communication between teams

GHADS solves these problems by providing:
- A centralized digital system
- Automated tracking of aid distribution
- Secure role-based access control
- Organized database management

---

## 🛠️ Technologies Used

- Java (OOP)
- JavaFX (User Interface)
- FXML (UI Design)
- MySQL (Database)
- JDBC (Database Connectivity)
- CSS (Styling)
- NetBeans IDE

---

## 🏗️ Architecture Pattern

The system follows a **Layered Architecture (MVC-like structure)**:

- **Model:** Represents data (Entities like Users, Families, Organizations)
- **View:** JavaFX FXML interfaces
- **Controller:** Handles logic and user interactions
- **DAO Layer:** Manages database operations
- **Utility Layer:** Handles database connection and helpers

This separation improves:
- Code maintainability
- Scalability
- Organization of logic

---

## 👥 User Roles

### 🔵 Admin
- Manage users
- Manage organizations
- Manage families
- Manage aid distribution
- Full system access

### 🟢 Coordinator
- View families
- View aid records
- Limited system access

---

## 📸 System Screenshots

### 🔐 Login Page
![Login](screenshots/login.png)

### 📊 Admin Dashboard
![Dashboard](screenshots/dashboard.png)

### 👨‍👩‍👧 Families Management
![Families](screenshots/families.png)

### 🏢 Organizations Management
![Organizations](screenshots/organizations.png)

### 🎁 Aid Distribution
![Aid Distribution](screenshots/aid.png)

---

## 🚀 How to Run

1. Open project in NetBeans
2. Import MySQL database
3. Configure DB connection in `DBConnection.java`
4. Run the main class

---

## 👩‍💻 Developer

**Name:** Doaa Oukal  
