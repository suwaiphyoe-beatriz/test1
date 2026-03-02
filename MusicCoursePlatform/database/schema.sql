-- Music Course Platform Database Schema
-- CI/CD Compatible Version

-- Create database
CREATE DATABASE IF NOT EXISTS music_lesson_db;
USE music_lesson_db;

-- Disable foreign key checks for clean reset
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables in correct order
DROP TABLE IF EXISTS BOOKING;
DROP TABLE IF EXISTS TIMESLOT;
DROP TABLE IF EXISTS LEARNERPROFILE;
DROP TABLE IF EXISTS TEACHERPROFILE;
DROP TABLE IF EXISTS USERS;

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================
-- USERS TABLE
-- ==============================
CREATE TABLE USERS (
  user_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  user_type VARCHAR(20) NOT NULL,
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  PRIMARY KEY (user_id)
);

-- ==============================
-- TEACHER PROFILE
-- ==============================
CREATE TABLE TEACHERPROFILE (
  teacher_profile_id INT NOT NULL AUTO_INCREMENT,
  biography VARCHAR(500) DEFAULT '',
  instruments_taught VARCHAR(100) NOT NULL,
  years_experience INT NOT NULL DEFAULT 0,
  hourly_rate INT NOT NULL DEFAULT 0,
  location VARCHAR(100) DEFAULT '',
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  updated_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  user_id INT NOT NULL,
  PRIMARY KEY (teacher_profile_id),
  FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ==============================
-- LEARNER PROFILE
-- ==============================
CREATE TABLE LEARNERPROFILE (
  learner_profile_id INT NOT NULL AUTO_INCREMENT,
  instrument VARCHAR(100) DEFAULT '',
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  updated_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  user_id INT NOT NULL,
  PRIMARY KEY (learner_profile_id),
  FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ==============================
-- TIME SLOT
-- ==============================
CREATE TABLE TIMESLOT (
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

-- ==============================
-- BOOKING
-- ==============================
CREATE TABLE BOOKING (
  booking_id INT NOT NULL AUTO_INCREMENT,
  booking_date DATE NOT NULL DEFAULT (CURRENT_DATE),
  booking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  notes VARCHAR(500) DEFAULT '',
  created_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  updated_at DATE NOT NULL DEFAULT (CURRENT_DATE),
  learner_profile_id INT NOT NULL,
  slot_id INT NOT NULL,
  PRIMARY KEY (booking_id),
  FOREIGN KEY (learner_profile_id) REFERENCES LEARNERPROFILE(learner_profile_id) ON DELETE CASCADE,
  FOREIGN KEY (slot_id) REFERENCES TIMESLOT(slot_id) ON DELETE CASCADE
);

-- ==============================
-- INDEXES
-- ==============================
CREATE INDEX idx_users_username ON USERS(username);
CREATE INDEX idx_users_email ON USERS(email);
CREATE INDEX idx_users_type ON USERS(user_type);

CREATE INDEX idx_teacherprofile_user ON TEACHERPROFILE(user_id);
CREATE INDEX idx_learnerprofile_user ON LEARNERPROFILE(user_id);

CREATE INDEX idx_timeslot_teacher ON TIMESLOT(teacher_profile_id);
CREATE INDEX idx_timeslot_date ON TIMESLOT(lesson_date);

CREATE INDEX idx_booking_learner ON BOOKING(learner_profile_id);
CREATE INDEX idx_booking_slot ON BOOKING(slot_id);
