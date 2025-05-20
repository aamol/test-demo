#!/usr/bin/env python3
"""
AI-based Test Failure Predictor

This script analyzes test histories and code changes to predict which tests
are likely to fail in the next build, allowing developers to run those tests
first or give them special attention.
"""

import argparse
import json
import os
import sys
import requests
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

class TestFailurePredictor:
    def __init__(self, test_history_file, model_file=None):
        self.test_history_file = test_history_file
        self.model_file = model_file
        self.model = None
        
        # Load historical data
        if os.path.exists(test_history_file):
            self.history_df = pd.read_csv(test_history_file)
        else:
            # Create empty dataframe with expected columns
            self.history_df = pd.DataFrame(columns=[
                'test_name', 'code_churn', 'author_experience', 
                'time_since_last_change', 'coverage_percentage',
                'complexity', 'failed'
            ])
            
        # Load or train model
        if model_file and os.path.exists(model_file):
            self._load_model()
        else:
            self._train_model()
    
    def _load_model(self):
        """Load the trained model from file"""
        import pickle
        with open(self.model_file, 'rb') as f:
            self.model = pickle.load(f)
    
    def _save_model(self):
        """Save the trained model to file"""
        if self.model and self.model_file:
            import pickle
            with open(self.model_file, 'wb') as f:
                pickle.dump(self.model, f)
    
    def _train_model(self):
        """Train a new model based on historical test data"""
        if len(self.history_df) < 10:
            print("Not enough historical data to train a reliable model")
            return
            
        # Prepare features and target variable
        X = self.history_df[['code_churn', 'author_experience', 
                           'time_since_last_change', 'coverage_percentage', 
                           'complexity']]
        y = self.history_df['failed']
        
        # Split data for training and validation
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        # Train the model
        model = RandomForestClassifier(n_estimators=100, random_state=42)
        model.fit(X_train, y_train)
        
        # Validate accuracy
        y_pred = model.predict(X_test)
        accuracy = accuracy_score(y_test, y_pred)
        print(f"Model accuracy: {accuracy:.2f}")
        
        self.model = model
        self._save_model()
    
    def predict_failures(self, changes):
        """
        Predict which tests are likely to fail based on code changes
        
        Args:
            changes: List of dictionaries with file changes and metadata
        
        Returns:
            List of test names that are likely to fail
        """
        if not self.model:
            print("No trained model available")
            return []
            
        # Extract features from changes
        features = []
        test_names = []
        
        for change in changes:
            test_name = change['test_name']
            test_names.append(test_name)
            
            features.append([
                change['code_churn'],
                change['author_experience'],
                change['time_since_last_change'],
                change['coverage_percentage'],
                change['complexity']
            ])
        
        # Make predictions
        predictions = self.model.predict(features)
        probabilities = self.model.predict_proba(features)[:, 1]  # Probability of class 1 (failure)
        
        # Get tests with high fail probability
        likely_failures = []
        for i, test_name in enumerate(test_names):
            if probabilities[i] >= 0.7:  # 70% or higher probability of failure
                likely_failures.append({
                    'test_name': test_name,
                    'failure_probability': float(probabilities[i]),
                    'recommendations': self._generate_recommendations(change)
                })
        
        return likely_failures
    
    def _generate_recommendations(self, change):
        """Generate specific recommendations for a potential test failure"""
        recommendations = []
        
        # Check common failure patterns
        if change['code_churn'] > 200:
            recommendations.append("Large code changes detected. Consider breaking changes into smaller units.")
            
        if change['author_experience'] < 5:
            recommendations.append("Developer has limited experience with this codebase. Consider code review.")
            
        if change['complexity'] > 15:
            recommendations.append("High code complexity detected. Consider refactoring for testability.")
            
        if change['coverage_percentage'] < 60:
            recommendations.append("Low test coverage. Consider adding more test cases.")
            
        return recommendations

def main():
    parser = argparse.ArgumentParser(description='Predict test failures using AI')
    parser.add_argument('--history', required=True, help='Path to test history CSV file')
    parser.add_argument('--changes', required=True, help='Path to JSON file with code changes')
    parser.add_argument('--model', help='Path to save/load the trained model')
    parser.add_argument('--output', help='Path to output JSON file with predictions')
    
    args = parser.parse_args()
    
    # Initialize predictor
    predictor = TestFailurePredictor(args.history, args.model)
    
    # Load changes
    with open(args.changes, 'r') as f:
        changes = json.load(f)
    
    # Predict failures
    predicted_failures = predictor.predict_failures(changes)
    
    # Output results
    if args.output:
        with open(args.output, 'w') as f:
            json.dump(predicted_failures, f, indent=2)
    else:
        print(json.dumps(predicted_failures, indent=2))
    
    # Return exit code based on predicted failures
    return 1 if predicted_failures else 0

if __name__ == "__main__":
    sys.exit(main())