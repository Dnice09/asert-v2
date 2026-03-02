# AserT API

AserT API

# Running the AserT API Application using Gradle, Java 17, PostgreSQL, and YAML Configuration

This repository contains the source code of the AserT API application developed using the Spring Boot Java framework that is built using Gradle, Java 17, and PostgreSQL. This README file provides instructions on how to run the application using a YAML configuration file, create database migrations, and access Swagger.

# Requirements

Before you can run the Spring Boot application, you need to ensure that the following requirements are met:

1. Gradle 6.x or later is installed on your system
2. Java 17 is installed on your system
3. Spring Boot 2.2 or later is used in the application
4. Gradle Plugin for Spring Boot is added to the Gradle build file
5. PostgreSQL is installed on your system

# Configuring the Application

A sample configuration file containing the required configurations is found in the
resources directory. I is named application.yaml.sample. After cloning the application, duplicate the file and name it application.yaml, then update the database and path configurations to match the ones in your system.

# Creating Database Migrations

To create database migrations, you can use the Flyway plugin for Gradle. The Flyway plugin can generate migration files based on the schema changes you make to your database.
Here's an example of how to create a migration file using Gradle:

```sh
./gradlew migrate -Pn="create table name" or gradle migrate -Pn="create table name"
```

Once you've added the dependencies, you can access Swagger by going to http://localhost:PORT_NUMBER/swagger-ui/index.html#/ in your web browser. Replace PORT_NUMBER with the port number your application is running on.

# Running the Application

To run the Spring Boot application, follow these steps:

1. Clone the repository to your local machine
2. Open a terminal window and navigate to the project directory
3. Run the following command to start the application

```sh
./gradlew bootRun
```

4. Wait for the application to start
   Once the application has started, open a web browser and go to http://localhost:PORT_NUMBER/swagger-ui/index.html#/ to access Swagger.
5. You should be able to access the web APIs of the application

# Gradle tasks

## Migration Task

Creates a new migration file with timestamp

```sh
./gradlew migrate -Pn="create_table_name"
```

## Scaffold Generator Task

The scaffold generator creates a complete module with all required files (entity, repository, service, controller, DTOs).

### Basic Usage

```sh
./gradlew generate -Pm=ModuleName [-Pp=ParentModule] [-Pprops=prop1:Type[:TargetEntity]:constraint1...]
```

### Required Parameters

- `-Pm=ModuleName` - The name of the module to generate (CamelCase)
    - Example: `-Pm=HotelGuest`

### Optional Parameters

- `-Pp=ParentModule` - Parent module under which to create this module

    - Example: `-Pp=hotel`

- `-Pprops=...` - Define entity properties in the format:
  `propertyName:Type[:TargetEntity]:constraint1:constraint2...`

    **Supported types:**

    - String, Integer, Long, Double, Boolean
    - LocalDate, LocalDateTime
    - ManyToOne, OneToMany (requires TargetEntity parameter)

    **Supported constraints:**

    - `required`: Adds NOT NULL in SQL and @NotNull validation
    - `unique`: Creates a unique constraint in the database
    - `length=N`: Sets maximum length for strings

    Example:
    `-Pprops=name:String:required:length=100,email:String:required:unique,age:Integer`

- `-Ph` or `--help` - Show the help message

### Examples

```bash
# Basic module creation
./gradlew generate -Pm=Country

# Create a module under a parent
./gradlew generate -Pm=Country -Pp=setup

# Create a module with properties
./gradlew generate -Pm=User -Pprops=firstName:String:required:length=50,lastName:String:required:length=50,email:String:unique:required

# Create a module with relationships
./gradlew generate -Pm=Order -Pprops=customer:ManyToOne:Customer:required,items:OneToMany:OrderItem,orderDate:LocalDate

# Show help message
./gradlew generate -Ph
```

### Generated Files

The generator creates the following structure:

```
modules/[parent/]moduleName/
├── dtos/
│   ├── ModuleNameRequestDto.java
│   └── ModuleNameResponseDto.java
├── entity/
│   └── ModuleName.java
├── repository/
│   └── ModuleNameRepository.java
├── rest/
│   └── ModuleNameResource.java
└── services/
    ├── ModuleNameService.java
    └── ModuleNameServiceImpl.java
```

And also creates a migration file:

```sh
db/migration/TIMESTAMP__create_module_names_table.sql
```

### Entity Relationships Support

The scaffold generator now supports JPA relationships:

#### ManyToOne Relationships

```java
@ManyToOne(optional = false)  // optional=false for required relationships
@JoinColumn(name = "customer_id", nullable = false)
private Customer customer;
```

- Adds proper foreign key constraints in the migration file
- Creates appropriate DTOs with ID fields for the relationship

#### OneToMany Relationships

```java
@OneToMany(mappedBy = "order")
private List<OrderItem> items;
```

- Maps the bidirectional relationship properly
- No foreign key in the parent entity's table
