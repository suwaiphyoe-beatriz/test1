-- Music Course Platform Database Schema
-- Based on team member's design

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables
DROP TABLE IF EXISTS BOOKING;
DROP TABLE IF EXISTS TIMESLOT;
DROP TABLE IF EXISTS LEARNERPROFILE;
DROP TABLE IF EXISTS TEACHERPROFILE;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS time_slots;
DROP TABLE IF EXISTS teacher_profiles;
DROP TABLE IF EXISTS users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Users table
CREATE TABLE USERS
(
  user_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  user_type VARCHAR(20) NOT NULL,
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  PRIMARY KEY (user_id)
);

-- Teacher Profile table
CREATE TABLE TEACHERPROFILE
(
  teacher_profile_id INT NOT NULL AUTO_INCREMENT,
  biography VARCHAR(500),
  instruments_taught VARCHAR(100) NOT NULL,
  years_experience INT NOT NULL DEFAULT 0,
  hourly_rate INT NOT NULL DEFAULT 0,
  location VARCHAR(100),
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  updated_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  user_id INT NOT NULL,
  PRIMARY KEY (teacher_profile_id),
  FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- Learner Profile table
CREATE TABLE LEARNERPROFILE
(
  learner_profile_id INT NOT NULL AUTO_INCREMENT,
  instrument VARCHAR(100),
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  updated_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  user_id INT NOT NULL,
  PRIMARY KEY (learner_profile_id),
  FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- Time Slot table
CREATE TABLE TIMESLOT
(
  slot_id INT NOT NULL AUTO_INCREMENT,
  lesson_date DATE NOT NULL,
  start_time VARCHAR(20) NOT NULL,
  end_time VARCHAR(20) NOT NULL,
  slot_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  teacher_profile_id INT NOT NULL,
  PRIMARY KEY (slot_id),
  FOREIGN KEY (teacher_profile_id) REFERENCES TEACHERPROFILE(teacher_profile_id) ON DELETE CASCADE
);

-- Booking table
CREATE TABLE BOOKING
(
  booking_id INT NOT NULL AUTO_INCREMENT,
  booking_date DATE NOT NULL DEFAULT (CURRENT_DATE),
  booking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  notes VARCHAR(500),
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  updated_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  learner_profile_id INT NOT NULL,
  slot_id INT NOT NULL,
  PRIMARY KEY (booking_id),
  FOREIGN KEY (learner_profile_id) REFERENCES LEARNERPROFILE(learner_profile_id) ON DELETE CASCADE,
  FOREIGN KEY (slot_id) REFERENCES TIMESLOT(slot_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON USERS(username);
CREATE INDEX idx_users_email ON USERS(email);
CREATE INDEX idx_users_type ON USERS(user_type);
CREATE INDEX idx_teacherprofile_user ON TEACHERPROFILE(user_id);
CREATE INDEX idx_learnerprofile_user ON LEARNERPROFILE(user_id);
CREATE INDEX idx_timeslot_teacher ON TIMESLOT(teacher_profile_id);
CREATE INDEX idx_timeslot_date ON TIMESLOT(lesson_date);
CREATE INDEX idx_booking_learner ON BOOKING(learner_profile_id);
CREATE INDEX idx_booking_slot ON BOOKING(slot_id);

-- Sample data for testing
INSERT INTO USERS (username, password_hash, email, user_type) VALUES
('teacher1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqYrQSCDa', 'teacher1@example.com', 'TEACHER'),
('teacher2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqYrQSCDa', 'teacher2@example.com', 'TEACHER'),
('learner1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqYrQSCDa', 'learner1@example.com', 'LEARNER'),
('learner2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqYrQSCDa', 'learner2@example.com', 'LEARNER');

INSERT INTO TEACHERPROFILE (biography, instruments_taught, years_experience, hourly_rate, location, user_id) VALUES
('Experienced piano teacher with classical background', 'Piano', 10, 50, 'New York', 1),
('Guitar instructor specializing in rock and blues', 'Guitar,Bass', 8, 45, 'Los Angeles', 2);

INSERT INTO LEARNERPROFILE (instrument, user_id) VALUES
('Piano', 3),
('Guitar', 4);
