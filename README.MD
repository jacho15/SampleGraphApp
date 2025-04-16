# JanusGraph with Storage Plugins Evaluation

## Task 1 - Install Java 8, Maven, and IntelliJ
Accomplished: 
- Installed Java 8, Maven, and IntelliJ

Obstacles: 
- N/A
## Task 2 - Develop Simple Application with JanusGraph
Accomplished:
- Installed required dependencies:
  - janusgraph-core 
  - janusgraph-inmemory
- Created schema for the Air Routes graph using the provided CSV data files
  - Included vertices such as "airport", "country", "continent"
  - Included edges such as "route" and "contains".
- Basic vertex and edge properties (e.g., airport code, city, country) were defined.

Obstacles:
- N/A

Outcome:
- Created an app that uses JanusGraph and in-memory to store data.

## Task 3 - Switch to BerkeleyDB Storage Backend
Accomplished:
- Integrated the janusgraph-berkeleyje storage backend
- Data metrics from BerkeleyDB:
  - Vertices: 3,619
  - Edges: 50,193
  - Time: 32,548 ms
  - Size: 118.82 MB

Obstacles:
- N/A

Outcome:
- Successfully used BerkeleyDB to store data.

## Task 4 - Attempt to Switch to FoundationDB Storage Backend
Accomplished:
- Downloaded janusgraph-foundationdb storage backend
- Correctly compiled given files

Obstacles:
  - I tried using both FoundationDB 6.2.22 and 6.2.30 with the janusgraph-foundationdb.
  - Encountered configuration issues related to the com.apple path
  - When downloading FoundationDB files, they were placed in C:\Users\Jacob\.m2\repository\org\foundationdb\fdb-java, which may not be the correct path. Most online guides suggested using a com.apple path instead.

Attempts to Resolve:
- Refactored code
- Reviewed documentation (was depricated on the foundationDB website but not the repository)
- Reinstalled dependencies, but the issue persisted

Outcome:
- I couldn't use the FoundationDB as the storage backend, so I used the BerkeleyDB for tasks 5 and 6 to do as much as I could.

## Task 5 - Setup JanusGraph Server Mode (With BerkeleyDB)
Accomplished:
- Successfully set up JanusGraph to run as a server using the BerkeleyDB storage backend
- Connected via the Gremlin console to execute queries w/ the graph

Outcome:
- JanusGraph ran as a server with the BerkeleyDB backend, and the queries executed properly.

## Task 6 - Execute Profiler Queries
Accomplished:
- Executed a series of queries with the profile()
- Queries involved finding direct routes, filtering specific routes, and ordering results by distance

Outcome:
- Please check QueryExecutionAnalysis.MD for more details!

Thanks for reading!