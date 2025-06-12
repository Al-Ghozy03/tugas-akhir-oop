CREATE TABLE
    patients (
        id INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(255),
        gender VARCHAR(255),
        date_of_birth DATE,
        address VARCHAR(255),
        phone_number VARCHAR(255)
    );

CREATE TABLE
    doctor_schedules (
        id INT PRIMARY KEY AUTO_INCREMENT,
        doctor_name VARCHAR(255),
        specialist VARCHAR(255),
        practice_date DATE,
        start_time TIME,
        end_time TIME,
        room VARCHAR(255),
        remarks VARCHAR(255)
    )
CREATE TABLE
    reservations (
        id INT PRIMARY KEY AUTO_INCREMENT,
        patient_name VARCHAR(255),
        doctor_name VARCHAR(255),
        visit_date DATE,
        visit_time TIME,
        room VARCHAR(255),
        status VARCHAR(255),
        remarks VARCHAR(255)
    )