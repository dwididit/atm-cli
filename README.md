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



