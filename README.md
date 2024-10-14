# Concurrent Matrix Multiplication Analysis

This project analyzes the performance of matrix multiplication with varying thread counts using three algorithms: sequential, parallel, and fork-join. It measures how the performance responds to the number of threads used and identifies optimal thresholds for the fork-join method.

## Requirements

- Java Development Kit (JDK)
- Maven
- Python 3
- Pandas
- NumPy
- Matplotlib

## Usage

1. **Build the Project**: The project uses Maven for building. Make sure Maven is installed.
2. **Run the Java Application**: The main application will perform matrix multiplication and generate CSV files with the results.
3. **Analyze Results**: A Python script parses the CSV files and generates plots comparing the performance of the algorithms.

To run the project, execute the following command in the terminal:

```bash
./run.sh
```

This script checks for the necessary tools, builds the Java project, runs the matrix multiplication application, and finally analyzes the results using the Python script.

## Output

- **CSV Files**: 
  - `output/thread_results.csv`: Contains performance results comparing sequential, parallel, and fork-join algorithms based on the number of threads.
  - `output/threshold_results.csv`: Contains analysis results for determining the optimal threshold for the fork-join method.

- **Plots**: 
  - `output/forkjoin_vs_parallel_comparison.png`: Comparison of ForkJoin, Parallel, and Sequential performance.
  - `output/threshold_variation_comparison_subplots.png`: Variation of ForkJoin times based on threshold for different thread counts.

## Analysis

The Python script `analyze.py` performs the following:
- Loads the CSV data.
- Calculates mean and standard deviation for each algorithm based on thread count.
- Plots the results for visual analysis.





