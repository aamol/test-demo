#!/bin/bash
# AI-powered documentation generator
# This script analyzes Java code and generates human-readable documentation
# using an AI language model

set -e

# Configuration
PROJECT_ROOT=$(git rev-parse --show-toplevel)
SOURCE_DIR="$PROJECT_ROOT/src/main/java"
DOCS_DIR="$PROJECT_ROOT/docs"
OPENAI_API_KEY=${OPENAI_API_KEY:-$(cat "$PROJECT_ROOT/.env" | grep OPENAI_API_KEY | cut -d '=' -f2)}
MODEL="gpt-4"

# Create docs directory if it doesn't exist
mkdir -p "$DOCS_DIR"

# Function to extract class/method information using javap
extract_class_info() {
    local file="$1"
    local class_name=$(grep -E "^public class|^public interface|^public enum" "$file" | head -n 1 | awk '{print $3}')
    
    # Skip if no class found
    if [ -z "$class_name" ]; then
        return
    fi
    
    # Get package name
    local package=$(grep -E "^package" "$file" | head -n 1 | sed 's/package\s*\([^;]*\);/\1/')
    local fully_qualified="${package}.${class_name}"
    
    echo "Processing $fully_qualified"
    
    # Extract class details using javap if compiled
    local class_file=$(find "$PROJECT_ROOT/target/classes" -name "${class_name}.class" 2>/dev/null)
    
    if [ -n "$class_file" ]; then
        javap -private "$class_file" > "$DOCS_DIR/${class_name}_structure.txt"
    else
        # Fallback to manual parsing if class file not found
        grep -E "class|method|field|constructor" "$file" | grep -v "import" > "$DOCS_DIR/${class_name}_structure.txt"
    fi
    
    # Extract JavaDoc comments
    grep -E "/\*\*" -A 20 "$file" | grep -E "\*/|@" -B 20 > "$DOCS_DIR/${class_name}_comments.txt"
    
    # Extract the actual code
    cat "$file" > "$DOCS_DIR/${class_name}_code.txt"
    
    return 0
}

# Function to generate documentation using AI
generate_documentation() {
    local class_name="$1"
    
    # Prepare prompt for the AI
    local prompt="Generate comprehensive documentation for the following Java class. Include:
    
    1. Class purpose and overview
    2. Main functionality
    3. Key methods and their usage
    4. Integration points with other systems
    5. Any important implementation details
    
    Structure the documentation as Markdown.
    
    Here is the class structure:
    $(cat "$DOCS_DIR/${class_name}_structure.txt")
    
    Here are the JavaDoc comments:
    $(cat "$DOCS_DIR/${class_name}_comments.txt")
    
    And here is the code:
    $(cat "$DOCS_DIR/${class_name}_code.txt")"
    
    # Call OpenAI API to generate documentation
    curl -s https://api.openai.com/v1/chat/completions \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $OPENAI_API_KEY" \
      -d '{
        "model": "'"$MODEL"'",
        "messages": [
          {
            "role": "system",
            "content": "You are a technical documentation specialist with deep knowledge of Java."
          },
          {
            "role": "user",
            "content": "'"${prompt//\"/\\\"}"'"
          }
        ],
        "temperature": 0.3,
        "max_tokens": 2000
      }' | jq -r '.choices[0].message.content' > "$DOCS_DIR/${class_name}.md"
    
    echo "Documentation generated for $class_name"
}

# Main script execution
echo "Starting AI-powered documentation generation"
echo "Scanning Java source files..."

# Find all Java files and process them
find "$SOURCE_DIR" -name "*.java" | while read file; do
    class_name=$(basename "$file" .java)
    extract_class_info "$file" && generate_documentation "$class_name"
done

# Generate index file
echo "Generating documentation index..."
{
    echo "# Project Documentation"
    echo ""
    echo "This documentation was automatically generated using AI analysis."
    echo ""
    echo "## Classes"
    echo ""
    
    for doc in "$DOCS_DIR"/*.md; do
        base=$(basename "$doc" .md)
        if [ "$base" != "index" ]; then
            title=$(head -n 1 "$doc" | sed 's/# //')
            echo "* [$title]($base.md)"
        fi
    done
} > "$DOCS_DIR/index.md"

echo "Documentation generation complete. Results in $DOCS_DIR/"