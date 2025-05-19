#!/usr/bin/env python3
"""
This script trains an anomaly detection model using historical metrics data
and exports it in a format that can be loaded by the Java application.

In a real-world scenario, this would be part of a regular retraining pipeline
to ensure the model stays up-to-date with evolving application behavior.
"""

import os
import pandas as pd
import numpy as np
import joblib
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
import tensorflow as tf
from tensorflow import keras

# Configuration
INPUT_DATA_DIR = "data/metrics/"
OUTPUT_MODEL_DIR = "../src/main/resources/models/"
ANOMALY_MODEL_FILE = "anomaly_detection_model.pkl"
LSTM_MODEL_FILE = "prediction_model.h5"

def load_and_prepare_data():
    """Load historical metrics data and prepare it for training."""
    print("Loading and preparing metrics data...")
    
    # In a real scenario, this would load actual metrics data
    # For this example, we'll simulate some data
    
    # Generate some sample data
    np.random.seed(42)
    n_samples = 1000
    
    # Normal data (80%)
    normal_cpu = np.random.normal(50, 10, int(n_samples * 0.8))
    normal_memory = normal_cpu * 1.2 + np.random.normal(0, 5, int(n_samples * 0.8))
    normal_latency = np.random.normal(100, 20, int(n_samples * 0.8))
    
    # Anomalous data (20%)
    anomalous_cpu = np.random.normal(80, 15, int(n_samples * 0.2))
    anomalous_memory = anomalous_cpu * 1.5 + np.random.normal(10, 10, int(n_samples * 0.2))
    anomalous_latency = np.random.normal(300, 100, int(n_samples * 0.2))
    
    # Combine data
    cpu = np.concatenate([normal_cpu, anomalous_cpu])
    memory = np.concatenate([normal_memory, anomalous_memory])
    latency = np.concatenate([normal_latency, anomalous_latency])
    
    # Create DataFrame
    df = pd.DataFrame({
        'cpu_usage': cpu,
        'memory_usage': memory,
        'api_latency': latency
    })
    
    print(f"Prepared dataset with {len(df)} samples")
    return df

def train_isolation_forest(data):
    """Train an Isolation Forest model for anomaly detection."""
    print("Training Isolation Forest model for anomaly detection...")
    
    # Scale the data
    scaler = StandardScaler()
    scaled_data = scaler.fit_transform(data)
    
    # Train the model
    model = IsolationForest(
        n_estimators=100,
        contamination=0.1,
        random_state=42
    )
    model.fit(scaled_data)
    
    # Save both the model and the scaler
    os.makedirs(OUTPUT_MODEL_DIR, exist_ok=True)
    joblib.dump((model, scaler), os.path.join(OUTPUT_MODEL_DIR, ANOMALY_MODEL_FILE))
    
    print(f"Saved anomaly detection model to {OUTPUT_MODEL_DIR}{ANOMALY_MODEL_FILE}")
    return model, scaler

def create_sequences(data, seq_length):
    """Create sequences for time series prediction."""
    xs, ys = [], []
    for i in range(len(data) - seq_length - 1):
        x = data[i:(i + seq_length)]
        y = data[i + seq_length]
        xs.append(x)
        ys.append(y)
    return np.array(xs), np.array(ys)

def train_prediction_model(data):
    """Train an LSTM model to predict future metric values."""
    print("Training LSTM model for metric prediction...")
    
    # Scale the data
    scaler = StandardScaler()
    scaled_data = scaler.fit_transform(data)
    
    # Create sequences for time series prediction (24 time steps)
    seq_length = 24
    X, y = create_sequences(scaled_data, seq_length)
    
    # Split into train and test sets
    train_size = int(len(X) * 0.8)
    X_train, X_test = X[:train_size], X[train_size:]
    y_train, y_test = y[:train_size], y[train_size:]
    
    # Create and train the LSTM model
    model = keras.Sequential([
        keras.layers.LSTM(50, activation='relu', input_shape=(seq_length, data.shape[1])),
        keras.layers.Dense(data.shape[1])
    ])
    
    model.compile(optimizer='adam', loss='mse')
    
    model.fit(
        X_train, y_train,
        epochs=50,
        batch_size=32,
        validation_data=(X_test, y_test),
        verbose=1
    )
    
    # Save the model and scaler
    os.makedirs(OUTPUT_MODEL_DIR, exist_ok=True)
    model.save(os.path.join(OUTPUT_MODEL_DIR, LSTM_MODEL_FILE))
    joblib.dump(scaler, os.path.join(OUTPUT_MODEL_DIR, "prediction_scaler.pkl"))
    
    print(f"Saved prediction model to {OUTPUT_MODEL_DIR}{LSTM_MODEL_FILE}")
    return model, scaler

def main():
    """Main function to orchestrate the training process."""
    print("Starting model training...")
    
    # Load and prepare the data
    data = load_and_prepare_data()
    
    # Train the anomaly detection model
    anomaly_model, _ = train_isolation_forest(data)
    
    # Train the prediction model
    prediction_model, _ = train_prediction_model(data)
    
    print("Model training complete!")
    
    # Test the anomaly detection model
    test_sample = np.array([[90, 150, 400]])  # High CPU, memory, and latency
    anomaly_score = anomaly_model.decision_function(test_sample)
    print(f"Anomaly model test - score for unusual metrics: {anomaly_score}")
    
    print("Done!")

if __name__ == "__main__":
    main()