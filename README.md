
# Password Manager

This is a simple Password Manager application built using Java and Swing. It allows users to securely store and manage passwords for different services or accounts. The application supports user registration, login, password storage, password retrieval, and password deletion.

## Features
- **User Registration**: Register new users with a username and password.
- **Login**: Log in to the application with your registered credentials.
- **Password Management**: Add, view, and delete passwords for different services.
- **Data Persistence**: All user data is stored in a text file (`password_manager_data.txt`).

## Technologies Used
- **Java**
- **Swing** (for the GUI)
- **BufferedReader** and **BufferedWriter** (for file handling)

## Usage

### Login Window:
When you first launch the application, you will see a login window. Enter your username and password to log in, or create a new user account by clicking the "Register" button.

### Menu Window:
Once logged in, you will be presented with a menu that allows you to:
- Add new passwords for various services.
- View passwords associated with a specific key.
- View all stored passwords.
- Delete passwords.
- Log out.

### Password Storage:
Passwords are stored in the `password_manager_data.txt` file and are associated with a "key" (e.g., service name, account name, etc.).

### Example

**Login Screen:**

- Username: `exampleUser`
- Password: `examplePassword`

**Menu Screen:**
- Add Password
- Get Password
- Get All Passwords
- Delete Password
- Logout

**Saving a Password:**

- Key: `Gmail`
- Password: `mySecretPassword123`

