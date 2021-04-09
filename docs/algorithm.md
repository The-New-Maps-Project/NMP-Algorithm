# The New Maps Project Redistricting Algorithm

This page is a high-level explanation of The New Maps Project Redistring Algorithm, detailing how it works and the purpose and reasoning behind it's development.

## The Problem

Every census year, the US Congressional districts are redrawn. However, malicious interests may use the drawing of districts to gain an unfair advatantage in government representation, a practice known as gerrymandering. This is a perennial problem that has been tried to be solved for generations.

The New Maps Project's Algorithm provides a flexible way to redraw districts in a certain state, that tries to optimize the outcome to provide the best possible districts based on a user's needs.

## Goals

This algorithm's goal is to output a list of districts in a state that meet the input objectives.

To understand the input of this algorithm, a couple of parameters and requirements shall be defined:

### Requirements

Common requirements based on laws in US states regarding district drawing. Explained are some **important limitations** that pertain to this algorithm, that are made to model a redistricting solution to meet these requirments

**Precincts as One:** When congressional districts are drawn, they are made up of a collection of precincts, and conversely, each precinct is assigned to a district. It is often that an entire precinct must be in one district, and that the residents of one precinct cannot be assined to multiple different districts, for this is the purpose of precincts. To account for the fact that real precincts, and data for the algorithm input, may differ in population, the algorithm weights precincts by population size and only assigns whole precincts to districts (unless using the point-based redistricting model).

**Continuity of a District:** In most cases, precincts in a district must border other precincts in a district, and one precinct may not be separated from the rest of the district (no "discontinuities"). Data input for this algorithm does not account for the exact boundaries of each precinct, just a point location. Therefore, two precincts are said to be "bordering" one another when at least one precinct's closest precinct, by linear distance, is the other precinct. This is an important limitation to the nature of this algorithm, for this approach is needed to ensure the hgihest probability of the contintuity of districts when no specifc boundaries for each precinct are given. 

### Parameters
Values defined to measure the effectiveness of a district grouping

**Compactness:** Measured by the mean distance of each voter in the district to the population mean. The goal of an optimal district mapping is more compact districts.

**Population Distribution:**: Measured by the mean absolute difference between the average size of a district in the state, and the size of a district (by population). The goal for an optimal district mapping is even population distribution.

## The Algorithm

### Input Data

1. A list of precincts from a text file (.txt) with the following information:
- Name
- Geographical location (latitude and longitude of the precinct), as a point on earth
- Population

Information about the exact boundaries and the exact population distribution or exact location of every individual residing in the precinct is not supported as input into this algorithm. As a result, some important simplifications and assumptions can be made.

NOTE: Sample input files from The New Maps Project (such as those found in the website's datastore) contain coordinates obtained from querying the name of the precinct (and state) with the Google Geocoding API.

2. The number of desired districts to divide the state into

3. A percent threshold as a decimal (Ex: 0.95). This is the percent of the average district size at which the standard model will stop building a district. See the standard model's procedure below. Input "0" to run the point-based model.

### Procedure - Standard Model

1. Start with most populous precinct as it's own new district
2. Add the closest precinct to the district (the location of the district being the center of population for it)
3. Repeat step 2 until the district poplation threshold is exceeded. This threshold is a percentage amount of the average district size (Ex: a 90% threshold of a state with 10 districts and 1 million people would be 1,000,000/10 * 0.9 = 90,000)
4. Repeat steps 1-3 with every district, adding the most populous precinct that has not yet been assigned.
5. All remaining precincts go to the district that their closest precinct is in.

### Mathematical Explanation - Standard Model

This is a greedy, "district-building" in order approach to draw districts. Listed below are the requirements and the parameters this algorithm seeks to optimize.

**Requirements:** Precincts as One, Continuity of Districts.
**Parameters to Optimize:** Compactness, Population Distribution.

Step 1: Start with most populous precinct

Since "precincts as one" are required, the optimal way to build districts is to begin each with the most populous precincts and build up to a threshold. This ensures that the most populous district is not added last, and when precincts are being added when the population of a district is close to the threshold, more granular additions can be made to optimize population distribution

Step 2: Add closest precinct

With the most populous precinct remaining as the lone precinct in the district, call this precinct A, according to the definition above for the continuity of a district, either two additions can be made at this step. so that the requirement of continuity is satisfied:

  1. The closest precinct to precinct A
  2. A precinct whose closest district is precinct A

Sometimes, the same one precinct satisfies both these conditions, in which the optimal solution is to add that precinct. However, when this is not the case, it can be shown that adding closest precinct to precinct A optimizes compactness.



### Procedure - Point-Based Model

1. Compute "most extreme" point, the furthest point from the center of population.
2. Add people to the district, until district is exactly the mean district size. NOTE: This algorithm makes the assumption that all people living in that precinct are concentrated at the point given in the algorithm input. This assumption, while not ideal, is necessary given the limited amount of data provided and the inabilty for The New Maps Project to find the exact location of every resident in the state.
4. Repeat steps 1 and 2 for every district

Please Note: Due to the assumption made in Step 2, optimizations are made in the code. For example, this leads to precinct being divided up between district, and the algorithm output may specify the percentage of the population in a precinct assigned to each district.

### Output Data

A list of precincts, with all the input data, each assigned to a precinct, numbered 1 ... N, where N is the number of districts inputed into the algorithm.

If running the point-based model, and some precincts are dividing between districts, different portions of a precinct will be listed separately, assigned to their respective districts and denoting the percent of the population of the original precinct assigned to that district.




<!-- 
This algorithm is designed to be flexible and not a one-size-fits all approach. We recognize that a mathematically optimal solution in one regards is not optimal for other purposes.

A primary example of this would be the tradeoff between the average compactness and population distribution of districts in a state.  -->
