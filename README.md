# Pentominoes (Group 7)

> Group 7, Phase 1, Version 0

## Purpose of the project

The central topic of Project 1-1 is to create a computer application with a user-friendly
interface for solving the three-dimensional knapsack problem.
Within the scope of this project, the main objective is the development and implemen-
tation of a computer program using Java. The program's core functionality involves pop-
ulating a predefined rectangular space with specific objects known as pentominoes. The
initial phase of the project aims to create efficient algorithms that can arrange a given set
of pentominoes to maximize space utilization.

## Running the project and contributing

- [ ] Complete this section

## Authors

- Pablo Arranz Garcia
- Max Gurbanli
- Magdy Fares
- Ilya Stetsenko
- David Henry Francis Wicker
- Irdi Zeneli
- Zenios Zeniou

## Task Board

Link to the task issue board: [Task Board](https://gitlab.maastrichtuniversity.nl/bcs_group07_2023/pentominoes_phase_1/-/boards)

## Tasks & ideas log

### Max, 10/10/2023
Issue encountered:
- Implicitly asigning IDs for pentominoes resulted in U being printed as a Z
Solution:
- Explicit ID assignment for pentominoes, inserting the correct ID for each pentomino into the map

### Max, 11/10/2023
Issue encountered:
- Manually inputting grid size and pentomino letters rather than hard-coding these values
Solution:
- Created method getInput for validating user input
- Checks for valid grid sizes (multiples of 5)
- Check for valid input letters (must fit within grid size)

## Pentominoes Database

Each line in the CSV file defines one permutation of a pentomino.

- First number is the ID for a pentomino, from 0 to 11.
- Second number is the index of the permutation (rotation, flip, etc.), between 0 to 7.
- Numbers three and four are the X and Y sizes respectively.
- The following X\*Y numbers are a matrix showing which positions in the grid are occupied or empty (defined by the shape of the pentomino).

This file does not contain a header.
The pentominoes should be sorted by ID in increasing order

EXAMPLE:

2,1,3,3,1,0,0,1,1,1,0,0,1

ID: 2
Permutation: 1
X: 3 squares
Y: 3 squares
Shape:
X 0 0
X X X
0 0 X

## Pseudocodes

Link to Back-Tracking Optimized Search Algorithm: [BackTrackingOptimizedSearch](https://docs.google.com/document/d/1wwk5FOKyLLafBVtT9_tLXfCmRmyiZxNemiu4MnZcsXg/edit?usp=sharing)

Link to Back-Tracking Search Algorithm: [BackTrackingSearch](https://docs.google.com/document/d/1QUhCzTZASHgzQBXHoYxiuWhmx9RlNDJkyV1RkCMT9Ic/edit?usp=sharing)


## Sources
https://puzzler.sourceforge.net/docs/pentominoes.html
https://isomerdesign.com/Pentomino/