import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


def load_data(file_path, threshold_file_path):
    """Load data from CSV files."""
    data = pd.read_csv(file_path)
    threshold_data = pd.read_csv(threshold_file_path)
    return data, threshold_data


def calculate_thread_stats(data):
    """Calculate mean and standard deviation for ForkJoin and Parallel based on number of threads."""
    mean_forkjoin_times = []
    std_forkjoin_times = []
    mean_parallel_times = []
    std_parallel_times = []

    mean_sequential_time = np.mean(data["Sequential"].dropna())
    std_sequential_time = np.std(data["Sequential"].dropna())

    for threads in data["Threads"].unique():
        forkjoin_times = data[data["Threads"] == threads]["ForkJoin"].dropna()
        mean_forkjoin_times.append(forkjoin_times.mean())
        std_forkjoin_times.append(forkjoin_times.std())

        parallel_times = data[data["Threads"] == threads]["Parallel"].dropna()
        mean_parallel_times.append(parallel_times.mean())
        std_parallel_times.append(parallel_times.std())

    return (
        mean_forkjoin_times,
        std_forkjoin_times,
        mean_parallel_times,
        std_parallel_times,
        mean_sequential_time,
        std_sequential_time,
    )


def plot_thread_comparison(
    threads,
    mean_forkjoin_times,
    std_forkjoin_times,
    mean_parallel_times,
    std_parallel_times,
    mean_sequential_time,
    std_sequential_time
):
    """Plot ForkJoin vs Parallel vs Sequential comparison based on the number of threads."""

    plt.figure(figsize=(12, 6))
    plt.errorbar(
        threads,
        mean_parallel_times,
        linestyle="--",
        marker="s",
        yerr=std_parallel_times,
        label="Parallel",
    )

    plt.errorbar(
        threads,
        mean_forkjoin_times,
        yerr=std_forkjoin_times,
        linestyle="--",
        marker="o",
        label="ForkJoin",
    )



    plt.plot(threads, [mean_sequential_time - std_sequential_time] * len(threads), "g--",linewidth=1)
    plt.plot(threads, [mean_sequential_time] * len(threads), "g--", label="Sequential", linewidth=2)
    plt.plot(threads, [mean_sequential_time + std_sequential_time] * len(threads), "g--",linewidth=1)

    plt.title("ForkJoin vs Parallel vs Sequential Matrix Multiplication Times")
    plt.xlabel("Number of Threads")
    plt.ylabel("Time (ms)")
    plt.xticks(threads)
    plt.grid()
    plt.legend()
    plt.tight_layout()

    plt.savefig("output/forkjoin_vs_parallel_comparison.png")


def calculate_threshold_stats(threshold_data):
    """Calculate mean and standard deviation for ForkJoin based on threshold."""
    threshold_stats = {}

    for threads in threshold_data["Threads"].unique():
        thread_data = threshold_data[threshold_data["Threads"] == threads]
        thresholds = thread_data["Threshold"]

        mean_threshold_times = []
        std_threshold_times = []

        for threshold in thresholds:
            threshold_times = thread_data[thread_data["Threshold"] == threshold]["Time"]
            mean_threshold_times.append(threshold_times.mean())
            std_threshold_times.append(threshold_times.std())

        threshold_stats[threads] = (
            thresholds,
            mean_threshold_times,
            std_threshold_times,
        )

    return threshold_stats


def plot_threshold_variation(threshold_stats):
    """Plot ForkJoin time variation based on threshold for different number of threads."""
    thread_groups = [range(1, 5), range(5, 9), range(9, 13), range(13, 17)]
    line_styles = ["-", "--", "-.", ":"]

    _, axs = plt.subplots(2, 2, figsize=(14, 10))
    axs = axs.flatten()

    for idx, thread_group in enumerate(thread_groups):
        ax = axs[idx]

        times = []
        for i, threads in enumerate(thread_group):
            if threads in threshold_stats:
                thresholds, mean_threshold_times, std_threshold_times = threshold_stats[
                    threads
                ]

                ax.errorbar(
                    thresholds,
                    mean_threshold_times,
                    yerr=std_threshold_times,
                    linestyle=line_styles[i % len(line_styles)],
                    marker="o",
                    markersize=2,
                    label=f"{threads} Threads",
                )

                for threshold, mean_time in zip(thresholds, mean_threshold_times):
                    times.append((threshold, mean_time))

        ax.set_title(f"Thread Group {min(thread_group)}-{max(thread_group)}")
        ax.set_xscale("log", base=2)
        ax.set_xlabel("Threshold")
        ax.set_ylabel("Time (ms)")
        ax.set_xlim([1, 540])

        times_in_range = [time for time in times if 1 <= time[0] <= 512]
        max_time = max(times_in_range, key=lambda x: x[1])
        min_time = min(times_in_range, key=lambda x: x[1])
        ax.set_ylim([min_time[1] - 10, max_time[1] + 10])
        


        ax.grid(True)
        ax.legend()

    plt.tight_layout()
    plt.savefig("output/threshold_variation_comparison_subplots.png")


def main():
    file_path = "output/thread_results.csv"
    threshold_file_path = "output/threshold_results.csv"

    data, threshold_data = load_data(file_path, threshold_file_path)

    (
        mean_forkjoin_times,
        std_forkjoin_times,
        mean_parallel_times,
        std_parallel_times,
        mean_sequential_time,
        std_sequential_time
    ) = calculate_thread_stats(data)

    threads = data["Threads"].unique()
    plot_thread_comparison(
        threads,
        mean_forkjoin_times,
        std_forkjoin_times,
        mean_parallel_times,
        std_parallel_times,
        mean_sequential_time,
        std_sequential_time
    )

    thread_stats = calculate_threshold_stats(threshold_data)

    plot_threshold_variation(thread_stats)


# Run the main function
if __name__ == "__main__":
    main()
