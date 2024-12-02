
# Acmeplex - Movie Theatre Backend Application

Acmeplex is a Spring Boot application that This Movie Theater Booking Application allows users to search for movies, select a theater, choose showtimes, select seats, make payments, and receive email confirmations. Registered users are charged annually for using the application.

---

## Features

- **Search for Movies**: Find available movies playing in theaters.
- **Select Theater**: Choose from a list of available theaters.
- **Choose Showtime**: Select the preferred time for a movie.
- **Select Seats**: Pick available seats for the selected movie and showtime.
- **Payment Processing**: Make payments for bookings through secure methods.
- **Email Confirmation**: Receive email reservations after booking.
- **Annual Subscription**: Registered users are charged annually for premium features.

---

## Prerequisites

Ensure you have the following installed:

- **Java**: Version 17
- **Maven**: Version 3.6.3
- **Database**: MySQL
- **Email Service**: JavaMailSender for email notifications

---

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/AyodeleOluwabusola/acmeplex.git
   cd acmeplex
   ```

2. Configure the database:

    - Update the `application.properties` file in `src/main/resources` with your database credentials:

      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/acemplex_db
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      spring.jpa.hibernate.ddl-auto=update
      ```

3. Build the project:

   ```bash
   mvn clean install
   ```

4. Run the application:

   ```bash
   mvn spring-boot:run
   ```

5. The application will be available at:

   ```
   http://localhost:8080
   ```

---

## Usage


## Project Structure

The project is structured as follows:

```
src/
├── main/
│   ├── java/
│   │   ├── com.uofc.acmeplex/
│   │   │   ├── config/
│   │   │   ├── controllers/
│   │   │   ├── dto/
│   │   │   ├── entities/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── logic/
│   │   │      ├── services/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── util/
│   │   │   └── AcmePlexApplication.java
│   ├── resources/
│       ├── application.properties
│       └── templates/
├── test/
    ├── java/
```

---

## Scripts

- **Run Application**: `mvn spring-boot:run`
- **Run Tests**: `mvn test`
- **Build Project**: `mvn clean install`

---

## Technologies Used

- **Framework**: Spring Boot
- **Database**: MySQL (can be configured)
- **Build Tool**: Maven 3.6.3
- **Language**: Java 17

---

## Contributing

We welcome contributions to improve Acmplex Backend! Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add your message"`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a pull request.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

---

## Contact

For any inquiries or feedback, please reach out at:

- GitHub: https://github.com/AyodeleOluwabusola/acmeplex

---

Enjoy using Acmeplex! 🌱
