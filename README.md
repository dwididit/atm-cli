# ATM CLI Application

This application simulates interactions between customers and a retail bank via a Command Line Interface (CLI). The following commands are available to perform basic ATM operations.

## Features
- Log in as a customer or create a new customer if not existing.
- Deposit money into a customer's account.
- Withdraw money from a customer's account.
- Transfer money between customers.
- Log out of the current session.

## Usage
Run the application and use the commands below to interact with the system.

### Commands

#### `login [name]`
- Logs in as the specified customer.
- If the customer does not exist, a new customer account is created.
- **Example**:
  ```bash
  login Alice
    ```

#### `deposit [amount]`
- Deposits the specified amount into the logged-in customer's account.
- **Example**:
  ```bash
  deposit 100
    ```


#### `withdraw [amount]`
- Withdraws the specified amount from the logged-in customer's account.
- Ensures sufficient balance before withdrawing.
- **Example**:
  ```bash
  withdraw 50
    ```

#### `transfer [target] [amount]`
- Transfers the specified amount from the logged-in customer to the target customer.
- Creates the target customer if they do not already exist.
- **Example**:
  ```bash
  transfer Bob 30
    ```

#### `logout`
- Transfers the specified amount from the logged-in customer to the target customer.
- Creates the target customer if they do not already exist.
- **Example**:
  ```bash
  logout
    ```

## Unit Tests for ATM CLI Application

**Unit tests ensure the application behaves as expected under various scenarios. Below is an overview of the tests implemented for different modules and features.**

### Test Coverage

#### 1. Account Service Tests
   - Class: AccountServiceImplTest 
   - Purpose: To validate the account management logic, such as creating accounts, retrieving balances, and updating account details.
#### 2. Command Service Tests
   - Class: CommandServiceImplTest
   - Purpose: To test the CLI command parsing and execution logic.
#### 3. Bank Service Tests
   - Class: BankServiceImplTest
   - Purpose: To test higher-level operations like managing multiple customers and their accounts.
#### 4. Session Service Tests
   - Class: SessionServiceImplTest
   - Purpose: To test the handling of user sessions during ATM operations.
#### 5. CLI Integration Tests
   - Class: AtmCliTest
   - Purpose: To test the end-to-end behavior of the CLI interface.

## Get Started

**Prerequisites**
- Java: Version 17 or higher
- Build Tool: Apache Maven

**Steps to Run the Application**
#### 1. Clone the Repository
  ```bash
  git@github.com:dwididit/atm-cli.git
  cd atm-cli/
  ```
#### 2. Build the Project Use Maven to compile the application and run tests.
  ```bash
  mvn clean package
  ```
#### 3. Run the Application Execute the application from the command line.
```bash
java -jar target/atm-cli-1.0-SNAPSHOT.jar
```
#### 4. Interact with the CLI Use the commands described in the Usage section to perform operations.