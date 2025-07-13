# Bank Fraud Detection System (Apache Flink + Broadcast State)
## Overview
This project implements a real-time bank fraud detection pipeline using Apache Flink. It consumes streaming transaction data and enriches it with reference datasets (such as alarmed customers and lost card records) using broadcast state patterns. The system identifies and flags suspicious transactions based on multiple criteria including:

1. Transactions from alarmed customers.
2. Usage of lost or stolen cards.
3. Excessive transaction frequency in a short window.
4. City/location changes for the same customer within a short time.

The application is highly scalable and uses Flink’s windowing and stateful processing features to build robust fraud detection logic.

## Key Features:

✅ Real-time fraud detection using Flink's event-streaming APIs

✅ Broadcast state for enriching transactional streams with reference data

✅ Multiple fraud check strategies implemented as modular processors

✅ Windowed computation for detecting abnormal behavior over time

✅ Simple socket stream simulation for development and testing

## 🔧 Tech Stack
Apache Flink
Java 11+
SLF4J (Logging)
Flink Cluster (Optional Deployment)
Socket Stream (for simulation)

## ⚙️ Architecture Diagram

                            ┌───────────────────────────────┐
                            │  Config Properties (param)    │
                            └────────────┬──────────────────┘
                                         │
                                         ▼
 ┌──────────────┐        ┌─────────────────────────┐        ┌──────────────────────────┐
 │ Alarmed      │        │ Lost Card CSV/Stream    │        │ Transaction Stream        │
 │ Customers    │        │                         │        │ (Socket on port 9090)     │
 └─────┬────────┘        └───────────┬─────────────┘        └───────────┬────────────────┘
       │                             │                                  │
       ▼                             ▼                                  ▼
┌──────────────┐          ┌─────────────────────┐           ┌────────────────────────────┐
│ BroadcastMap │◄────────▶│ BroadcastMap        │           │ Input Stream Mapper         │
│ AlarmedCust  │          │ LostCards           │           └─────────────┬──────────────┘
└──────────────┘          └─────────────────────┘                         │
       │                                 │                                ▼
       ▼                                 ▼                   ┌────────────────────────────┐
┌────────────────────┐        ┌────────────────────┐        │ Multiple Fraud Detectors    │
│ AlarmedCustCheck   │        │ LostCardCheck       │        │  - Alarmed Customer Check  │
└────────────────────┘        └────────────────────┘        │  - Lost Card Check         │
                                                             │  - Excessive Txn Check     │
                                                             │  - City Change Check       │
                                                             └────────────┬───────────────┘
                                                                          ▼
                                                        ┌──────────────────────────────────┐
                                                        │       Flagged Transactions       │
                                                        └──────────────────────────────────┘
                                                                          │
                                                                          ▼
                                                        ┌──────────────────────────────────┐
                                                        │ Write to Output File/Storage     │
          
                                                         └──────────────────────────────────┘



## 📂 Project Structure

bank/
├── Bank.java                        # Main Flink job
├── cards/
│   ├── AlarmedCustomer.java
│   └── LostCard.java
├── checks/
│   ├── AlarmedCustCheck.java
│   ├── LostCardCheck.java
│   ├── CityChange.java
│   └── FilterAndMapMoreThan10.java
├── mappers/
│   ├── InputAlarmedMappers.java
│   ├── InputLostCardMapper.java
│   ├── InputDataStreamMapper.java
│   └── ExcessiveTransactionMapper.java
└── resources/
    └── config_prod.properties       # Property file for file paths and config


##  How to Run
1. Update config_prod.properties with correct file paths.
2. Make sure port 9090 is free and accessible for the transaction socket stream.
3. Run the main Flink application: (java -cp target/your-artifact.jar bank.Bank).
4. (Optional) Stream simulated transactions into port 9090 using nc or a custom producer: (nc localhost 9090).

## 📈 Output
The final output of flagged transactions is written to the path specified in: (fraud_detection_output=/your/output/path)

## Future Enhancements

1. Integrate with Apache Kafka for production-grade streaming.
2. Store flagged events in BigQuery or Cloud Storage.
3. Expose results through a REST API.
4. Integrate with GCP Dataflow for serverless execution.

