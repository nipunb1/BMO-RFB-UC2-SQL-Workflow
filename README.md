# Application Maintenance Workflow

A comprehensive web application for streamlining and securing maintenance tasks, enabling developers to submit application maintenance requests with automated validation, approval workflows, and audit trails.

## 🚀 Overview

The Application Maintenance Workflow tool is designed to reduce time to fix issues, ensure governance, maintain proper record keeping, and implement approval gating for various maintenance operations including:

- **SQL fixes** with automated validation and dry-run execution
- **Config file updates**
- **Scheduled job controls** (batch framework jobs)
- **Log rotation/archiving**
- **Job triggers**
- **Patch deployment**

## 🏗️ Architecture

### Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.x
- Spring Security (JWT Authentication)
- Spring Data JPA
- H2 Database (In-memory)
- Maven

**Frontend:**
- Angular 17+
- TypeScript
- Angular Material
- Standalone Components
- RxJS

### System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular UI    │    │  Spring Boot    │    │   H2 Database   │
│                 │    │     Backend     │    │                 │
│ • Dashboard     │◄──►│                 │◄──►│ • Users         │
│ • Request Forms │    │ • REST APIs     │    │ • Requests      │
│ • Approvals     │    │ • JWT Security  │    │ • Approvals     │
│ • Admin Panel   │    │ • SQL Validation│    │ • Audit Logs    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📋 Features

### Core Functionality
- **User Authentication & Authorization** - JWT-based with role-based access control
- **Maintenance Request Management** - Create, track, and manage maintenance requests
- **SQL Validation Engine** - Automated SQL syntax validation and dry-run execution
- **Multi-level Approval Workflow** - Peer review and manager approval process
- **Audit Trail** - Comprehensive logging of all system activities
- **User Management** - Admin interface for managing users and permissions

### User Roles
- **ADMIN** - Full system access, user management, system configuration
- **MANAGER** - Approve requests, view reports, manage team requests
- **TECH_REVIEWER** - Peer review technical requests, provide technical validation
- **USER** - Submit requests, view own requests, basic dashboard access

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18+ and npm
- Maven 3.6+

### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd maintenance-workflow-backend
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Backend will be available at:** `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd maintenance-workflow-frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   ng serve
   ```

4. **Frontend will be available at:** `http://localhost:4200`

## 🔐 Default Users

The system comes with pre-configured test users:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | System administrator |
| manager | manager123 | MANAGER | Department manager |
| reviewer | reviewer123 | TECH_REVIEWER | Technical reviewer |
| user | user123 | USER | Regular user |

## 📊 API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/logout` - User logout

### Maintenance Requests
- `GET /api/requests` - List all requests
- `POST /api/requests` - Create new request
- `GET /api/requests/{id}` - Get request details
- `PUT /api/requests/{id}` - Update request
- `DELETE /api/requests/{id}` - Delete request

### SQL Validation
- `POST /api/sql/validate` - Validate SQL syntax
- `POST /api/sql/dry-run` - Execute SQL dry-run
- `POST /api/sql/execute` - Execute approved SQL

### Approvals
- `GET /api/approvals/pending` - Get pending approvals
- `POST /api/approvals/{id}/approve` - Approve request
- `POST /api/approvals/{id}/reject` - Reject request

### Admin
- `GET /api/admin/users` - List all users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `GET /api/admin/audit-logs` - View audit logs

## 🔧 Configuration

### Backend Configuration (`application.properties`)

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:maintenancedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (Development only)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
app.jwtSecret=mySecretKey
app.jwtExpirationInMs=86400000
```

### Frontend Configuration

**Development** (`src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

**Production** (`src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-production-api-url.com'
};
```

## 🔄 Workflow Process

### SQL Fix Request Workflow

1. **Request Submission**
   - User submits SQL fix request through web form
   - System validates SQL syntax automatically
   - Request is saved with DRAFT status

2. **Technical Review** (Optional)
   - If peer reviewer is assigned, request goes to PEER_REVIEW status
   - Technical reviewer validates the SQL and business logic
   - Reviewer can approve or request changes

3. **Manager Approval**
   - Request moves to MANAGER_APPROVAL status
   - Manager reviews business justification and technical details
   - Manager can approve, reject, or request modifications

4. **Execution**
   - Approved requests move to APPROVED status
   - Authorized users can execute the SQL
   - System logs all execution details and results

5. **Completion**
   - Request status updated to COMPLETED
   - Audit trail maintained for compliance

## 📁 Project Structure

### Backend Structure
```
maintenance-workflow-backend/
├── src/main/java/com/iris/maintenance/
│   ├── config/              # Configuration classes
│   ├── controller/          # REST controllers
│   ├── dto/                 # Data transfer objects
│   ├── entity/              # JPA entities
│   ├── repository/          # Data repositories
│   ├── security/            # Security configuration
│   └── service/             # Business logic services
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

### Frontend Structure
```
maintenance-workflow-frontend/
├── src/app/
│   ├── core/
│   │   ├── guards/          # Route guards
│   │   ├── interceptors/    # HTTP interceptors
│   │   └── services/        # Core services
│   ├── features/
│   │   ├── admin/           # Admin components
│   │   ├── auth/            # Authentication components
│   │   ├── dashboard/       # Dashboard components
│   │   └── requests/        # Request management components
│   └── environments/        # Environment configurations
└── angular.json
```

## 🧪 Testing

### Backend Testing
```bash
cd maintenance-workflow-backend
mvn test
```

### Frontend Testing
```bash
cd maintenance-workflow-frontend
ng test
```

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file:
   ```bash
   mvn clean package
   ```

2. Run the JAR:
   ```bash
   java -jar target/maintenance-workflow-backend-1.0.0.jar
   ```

### Frontend Deployment
1. Build for production:
   ```bash
   ng build --configuration production
   ```

2. Deploy the `dist/` folder to your web server

## 📝 Database Schema

### Key Entities
- **Users** - System users with roles and permissions
- **MaintenanceRequests** - Core request entity with workflow status
- **SqlValidations** - SQL validation results and execution logs
- **ApprovalWorkflows** - Approval process tracking
- **AuditLogs** - System activity audit trail

## 🔒 Security Features

- **JWT Authentication** - Stateless token-based authentication
- **Role-based Access Control** - Fine-grained permissions
- **SQL Injection Prevention** - Parameterized queries and validation
- **Audit Logging** - Complete activity tracking
- **Input Validation** - Server-side and client-side validation

## 📞 Support

For technical support or questions about the Application Maintenance Workflow system, please contact the development team.

## 📄 License

This project is proprietary software developed for internal use.

---

**Developed by**: Devin AI  
**Requested by**: vishal-kanojia_IRIS (@vishal-kanojia_IRIS)  
**Session**: https://app.devin.ai/sessions/d3cd7306ed6a4eb0995a81a808af9607
