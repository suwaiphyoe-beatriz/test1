# Software Engineering Project 1
## Music Course Platform
## Group 6

## Team Members
- **Luo Ying** - Frontend Development, Documentation
- **Chen Yicheng** - Backend Development, DevOps, Technical Documentation
- **Lu Liu** - Backend Development, Database Design, Technical Documentation
- **Su Wai Phyoe** - Testing, Frontend Development, Documentation

**Team:** Group 6

---

## Description

The Music Course Platform is a desktop application that connects music teachers with learners, enabling teachers to manage their profiles and availability while allowing learners to search for teachers and book lessons. 

The application uses **JavaFX** for the user interface, **MariaDB** for database management, and **JDBC** for database interaction, following a three-tier architecture pattern.

### Key Features
- User registration and authentication for teachers and learners
- Teacher profile management (biography, instruments, pricing)
- Time slot scheduling and availability management
- Multi-criteria teacher search (by instrument and availability)
- Lesson booking and confirmation
- Booking management for both teachers and learners

---

## Technology Stack

- **Language:** Java 17+
- **UI Framework:** JavaFX 17+
- **Database:** MariaDB 10.6+
- **Database Connectivity:** JDBC
- **Build Tool:** Maven
- **Version Control:** Git & GitHub
- **Project Management:** Trello
- **Testing:** JUnit 5, TestFX
- **Code Coverage:** JaCoCo
- **CI/CD:** Jenkins (planned for Sprint 3)
- **Containerization:** Docker (planned for Sprint 4)

---

## Documentation

### Project Planning
- [Product Vision](document/Product%20Vision.pdf)
- [Project Plan](document/Software%20Engineering%20Project%20Plan.pdf)
- [AI and Project Management](document/AI%20and%20Project%20Management%20in%20Software%20Engineering.pdf)
- [Use Case Diagram](document/Music%20Course%20Platform%20-%20Use%20Case%20Diagram.pdf)

### Design & Architecture
- [System Architecture](document/Architecture.md) *(Coming in Sprint 2)*
- [Database Schema](document/DatabaseSchema.md) *(Coming in Sprint 2)*
- [UI/UX Design](document/UIDesign.md) *(Coming in Sprint 3)*

### Sprint Reviews
- [Sprint 1 Review](document/Sprint_1_Review_Report.pdf)
- [Sprint 2 Review](document/Sprint_2_Review_Report.pdf) *(Coming Soon)*
- [Sprint 3 Review](document/Sprint_3_Review_Report.pdf) *(Coming Soon)*
- [Sprint 4 Review](document/Sprint_4_Review_Report.pdf) *(Coming Soon)*

### Quality Assurance
- [Testing Strategy](document/Testing.md) *(Coming in Sprint 3)*
- [Code Coverage Reports](document/CodeCoverage.md) *(Coming in Sprint 2)*
- [Code Review Guidelines](document/CodeReview.md) *(Coming in Sprint 3)*

---

## Project Structure

```
MusicCoursePlatform/
├── document/                        # Project documentation
│   ├── Product Vision.pdf
│   ├── Software Engineering Project Plan.pdf
│   ├── AI and Project Management in Software Engineering.pdf
│   ├── Music Course Platform - Use Case Diagram.pdf
│   └── Sprint_1_Review_Report.md
│
└── MusicCoursePlatform/             # Main application directory
    ├── .idea/                       # IntelliJ IDEA configuration
    ├── .mvn/                        # Maven wrapper files
    ├── pom.xml                      # Maven configuration
    │
    ├── database/                    # Database scripts
    │   ├── schema.sql               # Database schema (Sprint 2 ✅)
    │   └── sample_data.sql          # Sample test data
    │
    ├── src/
    │   ├── main/
    │   │   ├── java/                # Java source files
    │   │   │   ├── controller/      # JavaFX controllers (Sprint 2 ✅)
    │   │   │   │   ├── LoginController.java
    │   │   │   │   └── SignupController.java
    │   │   │   ├── dao/             # Data Access Objects (Sprint 2 ✅)
    │   │   │   │   └── UserDAO.java
    │   │   │   ├── model/           # Entity classes (Sprint 2 ✅)
    │   │   │   │   └── User.java
    │   │   │   ├── service/         # Business logic layer (Sprint 2 ✅)
    │   │   │   │   └── UserService.java
    │   │   │   ├── util/            # Utility classes (Sprint 2 ✅)
    │   │   │   │   ├── DatabaseConnection.java
    │   │   │   │   └── PasswordUtil.java
    │   │   │   └── Main.java        # Application entry point (Sprint 2 ✅)
    │   │   │
    │   │   └── resources/
    │   │       └── fxml/            # FXML layout files (Sprint 2 ✅)
    │   │           ├── login.fxml
    │   │           └── signup.fxml
    │   │
    │   └── test/
    │       └── java/                # JUnit test files (Sprint 2 ✅)
    │           ├── dao/
    │           │   └── UserDAOTest.java
    │           ├── CreateDatabase.java
    │           └── TestDatabaseConnection.java
    │
    └── target/                      # Maven build output (generated)
        ├── classes/                 # Compiled classes
        │   ├── controller/
        │   ├── dao/
        │   ├── fxml/
        │   ├── model/
        │   ├── service/
        │   └── util/
        ├── generated-sources/
        │   └── annotations/
        └── maven-status/
            └── maven-compiler-plugin/
                └── compile/
                    └── default-compile/

Notes:
- ✅ indicates completed in Sprint 2
- target/ folder is auto-generated (not in Git)
- .idea/ folder is IDE-specific (not in Git)
```

---

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or later**
  - Download: https://www.oracle.com/java/technologies/downloads/
  - Verify: `java -version`

- **Apache Maven 3.6+**
  - Download: https://maven.apache.org/download.cgi
  - Verify: `mvn -version`

- **MariaDB 10.6+**
  - Download: https://mariadb.org/download/
  - Default port: 3306

- **JavaFX SDK 17+** (if not using Maven)
  - Download: https://gluonhq.com/products/javafx/

- **Git**
  - Download: https://git-scm.com/downloads

---

### Installation & Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/chenyicheng1998/MusicCoursePlatform.git
cd MusicCoursePlatform
```

#### 2. Configure Database Connection

**IMPORTANT:** Update your MariaDB password before running the application.

**File:** `MusicCoursePlatform/src/main/java/util/DatabaseConnection.java`  
**Line 18:** Update the password constant:

```java
private static final String PASSWORD = "your_mariadb_password";
```

**Default Configuration:**
```
Database: music_course_platform
Host: localhost:3306
Username: root
Password: (update in DatabaseConnection.java)
```

#### 3. Create Database (First Time Only)

**Option A: Using Maven (Recommended)**
```bash
cd MusicCoursePlatform
mvn test-compile
java -cp "target/test-classes;target/classes" CreateDatabase
```

**Option B: Using MySQL Client**
```bash
mysql -u root -p
CREATE DATABASE music_course_platform;
USE music_course_platform;
SOURCE database/schema.sql;
```

**Option C: Using Database Management Tool**
- Open MySQL Workbench or DBeaver
- Import `database/schema.sql`

#### 4. Build the Project

```bash
cd MusicCoursePlatform
mvn clean compile
```

#### 5. Run Tests

```bash
mvn test
```

All 16 tests should pass if database is configured correctly.

#### 6. Generate Code Coverage Report

```bash
mvn test jacoco:report
```

View report at: `target/site/jacoco/index.html`

#### 7. Run the Application

**Sprint 2 Update:** JavaFX UI is now available!

```bash
cd MusicCoursePlatform
mvn javafx:run
```

Or run directly from IDE (IntelliJ IDEA / Eclipse):
- Right-click on `Main.java`
- Select "Run Main.main()"

**What you'll see:**
- Login screen with email and password fields
- "Create account" link navigates to signup screen
- Signup screen with username, email, password fields
- "Sign up as Student" and "Sign up as Teacher" buttons
- "Already have an account? Log in" link navigates back

**Note:** Backend integration (UserService) is in progress. Currently shows placeholder alerts.

---

### Test Accounts (For Development)

After running `CreateDatabase`, these test accounts are available:

| Username | Password | Type |
|----------|----------|------|
| teacher_john | password123 | TEACHER |
| teacher_mary | password123 | TEACHER |
| learner_tom | password123 | LEARNER |
| learner_lucy | password123 | LEARNER |

**Example Usage:**
```java
UserService userService = new UserService();

// Register new user
User newUser = userService.registerUser(
    "alice123",
    "password123", 
    "alice@example.com",
    "LEARNER"
);

// Authenticate user
User user = userService.authenticateUser("teacher_john", "password123");
if (user.isTeacher()) {
    // Teacher dashboard (coming in Sprint 3)
} else {
    // Learner dashboard (coming in Sprint 3)
}
```

---

## Development Workflow

### Branch Strategy

- `main` - Production-ready code
- `develop` - Development branch (Sprint work)
- `feature/*` - Feature branches (e.g., `feature/user-login`)
- `bugfix/*` - Bug fix branches

### Coding Standards

- Follow Java naming conventions (CamelCase for classes, camelCase for methods)
- Write meaningful comments for complex logic
- All public methods must have JavaDoc comments
- Maximum line length: 120 characters
- Use 4 spaces for indentation (no tabs)

### Commit Message Format

```
<type>(<scope>): <subject>

Examples:
feat(login): Add user authentication
fix(booking): Prevent double-booking
docs(readme): Update installation instructions
test(user): Add unit tests for UserDAO
```

---

## Testing

### Run Unit Tests

```bash
mvn test
```

### Generate Code Coverage Report

```bash
mvn jacoco:report
```

View report: `target/site/jacoco/index.html`

### Test Coverage Goals
- **Sprint 2:** 40% code coverage
- **Sprint 3:** 60% code coverage
- **Sprint 4:** 70%+ code coverage

---

## Trello Board

**Project Management:**

### Sprint Boards (Each Sprint has its own board)

| Sprint | Scrum Master | Trello Board | Status |
|--------|--------------|--------------|--------|
| **Sprint 1** | Chen Yicheng | [Sprint 1 Board](https://trello.com/b/YnjfjBxd/sep1musiccourseplatform) | ✅ Completed |
| **Sprint 2** | Luo Ying | [Sprint 2 Board](https://trello.com/b/IMIZmc7K/sep1musiccourseplatform-sprint2) | 🔄 In Progress |
| **Sprint 3** | TBD | Sprint 3 Board (to be created) | 📅 Planned |
| **Sprint 4** | TBD | Sprint 4 Board (to be created) | 📅 Planned |

### Board Structure (Standard for all sprints)
- **Product Backlog** - All user stories
- **Sprint X - To Do** - Current sprint tasks
- **Sprint X - In Progress** - Tasks being worked on
- **Sprint X - Done** - Completed tasks
- **Archive** - Completed sprints

**Current Sprint Board:** [Sprint 2](https://trello.com/b/IMIZmc7K/sep1musiccourseplatform-sprint2)

---

## Sprint Timeline

| Sprint | Weeks | Scrum Master | Status | Focus |
|--------|-------|--------------|--------|-------|
| **Sprint 1** | Week 1-2 | Chen Yicheng | ✅ Completed | Project planning, documentation, tool setup |
| **Sprint 2** | Week 3-4 | Luo Ying | 🔄 In Progress | Database schema, user management, basic UI |
| **Sprint 3** | Week 5-6 | TBD | 📅 Planned | Booking system, search functionality, CI/CD |
| **Sprint 4** | Week 7-8 | TBD | 📅 Planned | Testing, Docker, final polish, documentation |

**Current Sprint:** Sprint 2  
**Current Scrum Master:** Luo Ying  
**Sprint Goal:** Implement user registration, login, and database foundation with JavaFX UI

---

## Key Deliverables

### Sprint 1 (Completed)
**Scrum Master:** Chen Yicheng  
**Trello Board:** [Sprint 1](https://trello.com/b/YnjfjBxd/sep1musiccourseplatform)

- [x] Product Vision Document
- [x] Project Plan (12 pages)
- [x] Trello Board with 28 User Stories
- [x] Use Case Diagram
- [x] GitHub Repository Setup
- [x] Technology Stack Selection

### Sprint 2 (In Progress)
**Scrum Master:** Luo Ying  
**Trello Board:** [Sprint 2](https://trello.com/b/IMIZmc7K/sep1musiccourseplatform-sprint2)

- [x] Database schema implementation
- [x] User registration and login functionality
- [x] UserDAO with CRUD operations
- [x] UserService business logic layer
- [x] Password hashing (BCrypt)
- [x] Unit tests with JUnit (16 tests, all passing)
- [x] JaCoCo code coverage report
- [x] Maven configuration
- [x] JavaFX UI framework (login and signup screens)
- [x] Controller integration (LoginController, SignupController)
- [x] Screen navigation between login and signup
- [ ] Backend-UI integration (UserService connection)
- [ ] Complete login/signup functionality

### Sprint 3 (Upcoming)
**Scrum Master:** TBD  
**Trello Board:** To be created

- [ ] Teacher profile management
- [ ] Time slot management
- [ ] Teacher search functionality
- [ ] Lesson booking system
- [ ] Jenkins CI/CD pipeline
- [ ] Docker image (local testing)

### Sprint 4 (Upcoming)
**Scrum Master:** TBD  
**Trello Board:** To be created

- [ ] Integration testing
- [ ] System testing and bug fixes
- [ ] Docker image published to Docker Hub
- [ ] Final UI polish
- [ ] Complete technical documentation
- [ ] User manual
- [ ] Final presentation