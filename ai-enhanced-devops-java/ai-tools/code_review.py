#!/usr/bin/env python3
"""
AI-powered code review tool that analyzes source code changes
and provides feedback on quality, potential issues, and improvements.
"""

import argparse
import os
import sys
import re
import json
import openai
from pathlib import Path

class AICodeReviewer:
    def __init__(self, model="gpt-4"):
        self.model = model
        # Get API key from environment
        self.api_key = os.getenv("OPENAI_API_KEY")
        if not self.api_key:
            raise ValueError("OPENAI_API_KEY environment variable is required")
        
        openai.api_key = self.api_key
        
    def review_file(self, file_path):
        """Review a single file using AI"""
        if not os.path.exists(file_path):
            return f"File not found: {file_path}"
            
        # Read file content
        with open(file_path, 'r') as f:
            code = f.read()
            
        # Skip files that are too large - API limits
        if len(code) > 10000:
            return f"File too large to review: {file_path} ({len(code)} characters)"
            
        # Determine language based on file extension
        ext = os.path.splitext(file_path)[1].lower()
        language = self._get_language(ext)
            
        # Create prompt for code review
        prompt = f"""
Review this {language} code and provide constructive feedback:
1. Identify any bugs or potential issues
2. Suggest improvements for readability and maintainability
3. Point out any performance concerns
4. Check for security vulnerabilities
5. Verify best practices are followed

File: {file_path}