-- Revert the VARCHAR representation while preserving every supported role.
ALTER TABLE users MODIFY role ENUM('STUDENT', 'TEACHER', 'ADMIN') NULL;
