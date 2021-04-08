# The New Maps Project Redistricting Algorithm

This page is a high-level explanation of The New Maps Project Redistring Algorithm, detailing how it works and the purpose and reasoning behind it's development.

## The Problem

Every census year, the US Congressional districts are redrawn. However, malicious interests may use the drawing of districts to gain an unfair advatantage in government representation, a practice known as gerrymandering. This is a perennial problem that has been tried to be solved for generations.

The New Maps Project's Algorithm provides a flexible way to redraw districts in a certain state, that tries to optimize the outcome to provide the best possible districts based on a user's needs.

## Goals

This algorithm's goal is to output a list of districts in a state that meet the input objectives.

To understand the input of this algorithm, a couple of parameters and requirements shall be defined:

### Requirements

**Precincts as One:** When congressional districts are drawn, they are made up of a collection of precincts, and conversely, each precinct is assigned to a district. It is often that an entire precinct must be in one district, and that the residents of one precinct cannot be assined to multiple different districts, for this is the purpose of precincts. To account for the fact that real precincts, and data for the algorithm input, may differ in population, the algorithm weights precincts by population size and only assigns whole precincts to districts (unless using the point-based redistricting model).

**Continuity of a District:**

### Parameters
Values defined to measure the effectiveness of a district grouping

**Compactness:** 

**Population Distribution:**: 



<!-- 
This algorithm is designed to be flexible and not a one-size-fits all approach. We recognize that a mathematically optimal solution in one regards is not optimal for other purposes.

A primary example of this would be the tradeoff between the average compactness and population distribution of districts in a state.  -->
