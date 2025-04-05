# Crypto Trade Sim
### Crypto Trade Simulator with real time price updates from Kraken API



https://github.com/user-attachments/assets/089e5099-3360-4ae5-8bb8-ec7a7983bfc0


# How To Run:

## Prerequisites
Make sure you have the following installed:
- Node.js
- Java JDK
- Maven or use the maven wrapper
- MySQL
- VSCode
  - Spring Boot Extension Pack
  - Extension Pack for Java

## MySQL Database
Start the MySql server and run the DB code located in folder DB code


## Running the Spring Boot Backend
1. Go to ```backend``` folder
2. Build the project:
   ```sh
   ./mvnw clean install
   ```
   or mvnw.cmd
3. Run the application:
   ```sh
   ./mvnw spring-boot:run
   ```
    or mvnw.cmd

   it's running on http://localhost:8080
## Running the Frontend
1. Navigate to ```frontend```
2. Install dependencies:
   ```sh
   npm i
   ```
3. Start the development server:
   ```sh
   npm run dev
   ```
4. `http://localhost:5173/`.


