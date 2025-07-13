# Overview
This project implements a real-time bank fraud detection pipeline using Apache Flink. It consumes streaming transaction data and enriches it with reference datasets (such as alarmed customers and lost card records) using broadcast state patterns. The system identifies and flags suspicious transactions based on multiple criteria including:

Transactions from alarmed customers

Usage of lost or stolen cards

Excessive transaction frequency in a short window

City/location changes for the same customer within a short time

The application is highly scalable and uses Flink’s windowing and stateful processing features to build robust fraud detection logic.
