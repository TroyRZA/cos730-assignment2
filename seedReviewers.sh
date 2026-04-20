#!/bin/bash

DB="research.db"

sqlite3 "$DB" "CREATE TABLE IF NOT EXISTS reviewers (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    assigned_study_id INTEGER,
    FOREIGN KEY (assigned_study_id) REFERENCES submissions(id)
);"

COUNT=$(sqlite3 "$DB" "SELECT COUNT(*) FROM reviewers;")

if [ "$COUNT" -eq 0 ]; then
    sqlite3 "$DB" "INSERT INTO reviewers (name) VALUES
        ('Alice Mercer'),
        ('Bob Hyland'),
        ('Carol Simms'),
        ('David Osei'),
        ('Eva Brandt'),
        ('Frank Luo'),
        ('Grace Nkosi'),
        ('Henry Patel'),
        ('Irene Walsh'),
        ('James Oduya');"
fi