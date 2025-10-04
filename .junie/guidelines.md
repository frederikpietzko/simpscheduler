# SimpScheduler Development Guidelines

## Build System: Amper

This project uses **Amper** (not Gradle) as its build system. The configuration is in `module.yaml`.

### Build Commands

All build commands use the `./amper` wrapper script:

- `./amper build` - Compile the project
- `./amper run` - Run the application
- `./amper test` - Run all tests
- `./amper clean` - Clean build artifacts

### Configuration Files

- **module.yaml** - Main Amper configuration defining:
  - Product type: `jvm/app`
  - Dependencies (Ktor, Exposed, R2DBC, JUnit)
  - Test dependencies
  - Kotlin language version: 2.2
  - Ktor version: 3.3.0
  - Main class: `io.ktor.server.cio.EngineMain`

- **gradle/libs.versions.toml** - Version catalog for library dependencies
- **resources/application.yaml** - Runtime configuration (server port, database connection)
- **resources/logback.xml** - Logging configuration

### Running the Application

1. **Start PostgreSQL database:**
   ```bash
   docker compose up -d
   ```
   This starts PostgreSQL on localhost:5432 with credentials: pg/pg

2. **Run the application:**
   ```bash
   ./amper run
   ```
   Server will start on http://localhost:8080

## Testing

### Test Structure

- **Test directory:** `test/` (NOT `test@/` or `src/test/`)
- **Test framework:** JUnit Jupiter 5.13.4
- **Test dependencies configured in module.yaml:**
  - `$ktor.server.testHost` - For Ktor server testing
  - `$kotlin.test.junit` - Kotlin test extensions

### Running Tests

**Run all tests:**
```bash
./amper test
```

**Output includes:**
- Compilation phase with language version warnings (2.2 is experimental)
- Test execution with detailed results
- Summary: containers found/started, tests found/passed

### Creating Tests

1. **Directory structure:** Mirror the `src/` structure in `test/`
   ```
   src/users/domain/User.kt
   test/users/domain/UserTest.kt
   ```

2. **Test file structure:**
   ```kotlin
   package com.github.frederikpietzko.users.domain

   import org.junit.jupiter.api.Test
   import org.junit.jupiter.api.Assertions.*

   class UserTest {
       @Test
       fun `should test something`() {
           // Given
           // When
           // Then
       }
   }
   ```

### Example Test

See `test/users/domain/UserTest.kt` for a working example that tests:
- User creation with value classes
- Person entity with email validation
- Data class equality

**Running the example:**
```bash
./amper test
```

Expected output:
```
Started UserTest
Passed should create user with valid data()
Passed should create person with email address()
Passed UserTest
[2 tests successful]
```

## Architecture & Code Patterns

### Command Pattern

The project uses a custom Command/Handler pattern in `src/framework/command/`:

**Command interface:**
```kotlin
interface Command
```

**Handler interface:**
```kotlin
interface CommandHandler<TCommand : Command> {
    val command: KClass<TCommand>
    suspend fun Invoker.handle(command: TCommand)
}
```

**Handler implementation example:**
```kotlin
object CreateUserHandler : CommandHandler<CreateUserCommand> {
    override val command: KClass<CreateUserCommand> = CreateUserCommand::class
    
    override suspend fun Invoker.handle(command: CreateUserCommand) {
        // Implementation using extension function pattern
    }
}
```

### Value Classes for Type Safety

Domain models use inline value classes for type-safe primitives:

```kotlin
@JvmInline
value class UserId(val value: UUID = UUID.randomUUID())

@JvmInline
value class Username(val value: String)

@JvmInline
value class EmailAddress(val value: String)
```

**Benefits:**
- Zero runtime overhead
- Type safety (can't accidentally pass Username where EmailAddress is expected)
- Clear domain semantics

### Module Organization

The codebase is organized into feature modules:

```
resources/
├── migrations/               # Database migration scripts
├── application.yaml          # Runtime configuration
├── logback.xml               # Logging configuration
src/
├── Application.kt              # Main entry point
├── framework/                  # Shared framework code
│   └── command/               # Command pattern implementation
├── infrastructure/            # Cross-cutting concerns
│   ├── Databases.kt
│   ├── HTTP.kt
│   ├── Migrations.kt
│   ├── Monitoring.kt
│   ├── Routing.kt
│   ├── Security.kt
│   └── Serialization.kt
├── ui/                        # UI layer
│   └── layout/
└── users/                     # Users feature module
    ├── Users.kt              # Module configuration
    ├── commands/
    ├── domain/
    ├── handlers/
    ├── views/                      # Kotlin HTML templates
    │   └── RegisterUserView.kt    # HTML rendering with Ktor DSL
    └── persistence/
```

### Application Configuration Pattern

`Application.kt` uses extension functions for modular configuration:

```kotlin
suspend fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureDatabases()
    configureRouting()
    configureInvoker()
    configureUsers()
}
```

Each `configure*()` function is an extension on `Application` defined in its respective file.

### Database Layer

- **ORM:** Jetbrains Exposed (R2DBC driver for async)
- **Database:** PostgreSQL
- **Migrations:** Flyway-style migrations in `resources/migrations/`
- **Connection:** R2DBC connection pool configured in `application.yaml`

### Naming Conventions

- **Test files:** `*Test.kt` (e.g., `UserTest.kt`)
- **Test methods:** Use backtick strings for readable names: `` `should create user with valid data` ``
- **Commands:** Suffix with `Command` (e.g., `CreateUserCommand`)
- **Handlers:** Suffix with `Handler`, implement as objects (e.g., `CreateUserHandler`)
- **Value classes:** Domain-specific names (e.g., `UserId`, `Username`)

## Development Notes

### Language Version

Kotlin 2.2 is configured but is experimental. Expect warnings during compilation:
```
w: Language version 2.2 is experimental, there are no backwards compatibility guarantees
```

### Ktor Features Enabled

- Server: CIO engine
- Compression (GZIP)
- Default Headers
- CSRF protection
- Authentication
- Content Negotiation (JSON)
- Sessions
- Status Pages
- Call Logging
- Call ID
- HTML DSL (kotlinx.html)
- CORS

### HTML Templating

Uses Ktor's HTML DSL for server-side rendering:
```kotlin
call.respondHtmlTemplate(Page("SimpScheduler")) {
    body {
        h1 { +"Hello World!" }
    }
}
```

### Serialization

kotlinx.serialization is enabled (version 1.9.0) for JSON handling.

## Debugging Tips

1. **Check logs:** Build logs are in `build/logs/amper_*` files
2. **Database connection:** Ensure Docker Compose is running before starting the app
3. **Port conflicts:** Default port 8080 - check `resources/application.yaml` to change
4. **Test compilation:** Tests must be in `test/` directory to be recognized by Amper
5. **Dependency resolution:** Use `$libs.*` notation in module.yaml to reference libs.versions.toml

## Additional Resources

- Module configuration: `module.yaml`
- Runtime config: `resources/application.yaml`
- Database setup: `compose.yaml`
- Dependencies: `gradle/libs.versions.toml`


## Code Styling

- Follow Kotlin coding conventions: https://kotlinlang.org/docs/coding-conventions.html
- Use meaningful names for classes, methods, and variables
- Keep functions small and focused on a single responsibility
- Use comments to explain complex logic, but avoid obvious comments
- Organize imports and remove unused ones regularly
- Use consistent indentation and spacing for readability
- Prefer immutability (val over var) where possible
- Write unit tests for critical functionality and edge cases
- Review code for performance and security considerations
- Use version control effectively with clear commit messages and branching strategies
- Regularly refactor code to improve structure and maintainability

## Web Styling

- Follow best practices for HTML and CSS
- Use semantic HTML elements
- Ensure responsive design for different screen sizes
- Use Bootstrap 5 for styling and layout

## Html Rendering

- Use Ktor's/Kotlin HTML DSL for server-side rendering
- Keep HTML templates organized and reusable
- Separate concerns by keeping business logic out of HTML templates
- Use Bootstrap 5 classes for styling instead of inline styles
- Ensure accessibility by using proper ARIA roles and attributes

## Database Migrations

- Use Flyway-style migrations for database schema changes
- Keep migration scripts in `resources/migrations/`
- Use clear and descriptive names for migration files (e.g., `V1__create_users_table.sql`)
- Migrations are applied automatically on application startup
