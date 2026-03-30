# CPU Scheduling Simulator

This project is a CPU Scheduling Simulator implemented in Java.  
The goal of the project is to simulate how an operating system schedules jobs and manages system resources such as memory and devices.

The simulator reads jobs from an input file, processes them using different scheduling algorithms, and prints the system status and results to an output file.

## Scheduling Algorithms

### Static Round Robin (SRR)

- Uses a fixed time quantum (13).
- Each job receives the same CPU time slice.
- Simple and fair but does not adapt to different job sizes.

### Dynamic Round Robin (DRR)

- The time quantum changes dynamically.
- It is calculated based on the average remaining burst time of jobs in the ready queue.
- This helps improve performance when jobs have different execution times.

## Job Lifecycle

Each job in the system goes through several stages:

1. Arrival – The job enters the system through the input file.
2. Hold Lists – If resources are not available, the job waits in:
   - Hold List 1 (higher priority)
   - Hold List 2 (lower priority)
3. Ready Queue – Jobs that have enough resources move to the ready queue.
4. CPU Execution – The scheduler selects jobs using Round Robin.
5. Finish – When the job completes, its resources are released.

## Resource Management

Before a job can enter the Ready Queue, the system checks if enough resources are available:

- Memory
- Devices

If the resources are not available, the job is placed in a Hold List until resources become free.

When a job finishes execution, its resources are immediately returned to the system.

## System Output

The simulator generates an output file that shows:

- Current system status
- Available memory and devices
- Jobs in Hold Lists
- Jobs in Ready Queue
- Finished jobs

It also calculates important performance metrics such as:

- Turnaround Time
- Waiting Time

## Technologies Used

- Java
- Object-Oriented Programming (OOP)
- CPU Scheduling Concepts
- File Input / Output

## Purpose of the Project

This project was developed as part of an Operating Systems course.  
The main goal was to better understand how CPU scheduling works and how operating systems manage processes and system resources.
