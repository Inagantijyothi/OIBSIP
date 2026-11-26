# Library Management System – Web Application

## 📖 Project Overview

The Library Management System is a web-based application developed to simplify and manage library operations digitally. It provides separate functionalities for administrators and users, making it easier to manage books, reservations, issued books, returns, fines, and user information.

The application aims to reduce manual work, improve record management, and provide an organized library management experience.

## 🚀 Features

### 👨‍💼 Admin Module
- Admin dashboard
- Add, manage, and view books
- Manage registered users
- Manage book reservations
- Handle book return requests
- Manage fines
- View library reports
- Manage contact messages

### 👤 User Module
- User registration and login
- User dashboard
- Browse available books
- Reserve books
- View reservation details
- View issued books
- View fines
- Manage user profile
- Cancel reservations
- Submit contact messages

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend development |
| JSP | Dynamic web pages |
| Servlets | Request handling and application logic |
| HTML | Web page structure |
| CSS | Styling and layout |
| JavaScript | Client-side functionality |
| Maven | Project and dependency management |
| Apache Tomcat | Web application server |
| Database | Data storage and management |

## 📂 Project Structure

```text
LibraryManagementWeb/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/library/
│   │   │       ├── config/
│   │   │       ├── dao/
│   │   │       ├── model/
│   │   │       ├── servlet/
│   │   │       ├── test/
│   │   │       └── util/
│   │   │
│   │   └── webapp/
│   │       ├── css/
│   │       ├── js/
│   │       ├── index.html
│   │       ├── login.html
│   │       ├── register.html
│   │       ├── admin-dashboard.jsp
│   │       └── user-dashboard.jsp
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

## ⚙️ Installation and Setup

### Prerequisites

- Java Development Kit (JDK)
- Apache Maven
- Apache Tomcat
- A database configured for the application
- IntelliJ IDEA or another Java IDE

### Steps to Run

1. Clone the repository:

```bash
git clone https://github.com/Inagantijyothi/OIBSIP.git
```

2. Open the project folder `LibraryManagementWeb` in your IDE.

3. Configure the database connection according to your local database settings.

4. Install the required Maven dependencies:

```bash
mvn clean install
```

5. Configure Apache Tomcat in your IDE.

6. Build and deploy the web application to Tomcat.

7. Start the Tomcat server.

8. Open the application in your browser using the deployed application URL.

## 🎯 Project Objectives

- Digitize library management operations.
- Simplify book and reservation management.
- Provide separate admin and user functionalities.
- Maintain organized library records.
- Reduce manual administrative work.

## 🔮 Future Enhancements

- Online book renewal functionality.
- Email notifications for due dates.
- Advanced book search and filtering.
- Online payment integration for fines.
- Enhanced reporting and analytics.

## 👩‍💻 Developer

**Name:** K. Hema Tulasi

**Internship:** Oasis Infobyte (OIBSIP)

**Project:** Library Management Web Application

## 📌 Repository

GitHub: https://github.com/Inagantijyothi/OIBSIP

---

*This project was developed as part of my learning and internship experience.*
