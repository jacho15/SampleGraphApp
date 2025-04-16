# JanusGraph Query Execution Analysis
## Overview
The graph I used is JanusGraph which has:
- Data stored as vertices and edges w/ properties
- Data indexed w/ composite and mixed indexes
- Storage handled by a backend server

## Query Execution Analysis

### Query 1: Direct Outbound Routes from LAX
```groovy
g.V().hasLabel("airport").has("code", "LAX").outE("route").inV().valueMap()
```
1. How the first vertex was picked 
   - Picked with the composite index on `label="airport"` and `code="LAX"` to directly locate the LAX vertex
   - Seems to be O(1) runtime

2. Traversing the edges
    - `outE("route")` gets all the outgoing edges from LAX
    - Since it uses an adjacency-list, it requires no index lookup
    - Seems to also be O(1) runtime (multiplied by the number of outgoing edges which was ~200)

3. Retrieving the vertices
    - `inV()` traverses to the destination vertex
    - Accesses vertices by ID 
    - Also seems to be O(1) runtime

4. Property Retrieval
    - `valueMap()` gets the properties for each destination vertex
    - The properties get stored inline w/ vertex records for the smaller sets

Insights:
- Using a composite index for the starting point speeds it up
- Using an adjaceny list for each edge traversal speeds it up
- Using direct ID lookups for connected vertices speeds it up

### Query 2: Specific Route (LAX to NRT)
```groovy
g.V().hasLabel("airport").has("code", "LAX").outE("route").where(inV().has("code", "NRT")).valueMap()
```
1. How the first vertex was picked
   - Same thing as query 1

2. Traversing the edges
   - Same thing as query 1

3. Filter Step
    - `where(inV().has("code", "NRT"))` applies a filter condition
    - For every edge, we go to the connected vertex and check for the code "NRT"

4. Property Retrieval
    - `valueMap()` for matching edges

Insights: 
- Not using an index for "code" makes it a bit slower
- Might have something to do with teh fact that it has to go through all outgoing edges to filter properly for every case which would be a lot slower

### Query 3: Complex Multi-step Query
```groovy
g.V().hasLabel("airport").has("code", "LAX").outE("route").inV().local(outE("route").order().by("distance", desc).limit(5)).inV().dedup().values("code")
```
1. Initial Vertex Selection
    - Same thing as query 1 and 2

2. Traversing the edges
    - Get every outgoing edge from LAX
    - Goes to O(n) since we have to track every outgoing edge

3. Destination Airport Retrieval
    - Traverses to all airports connected to LAX

4. Local Traversal
    - `local()` scope applies the inner traversal to each vertex independently
    - For node connected to LAX:
        - Gets all outgoing edges using (`outE("route")`)
        - Orders them by their distances descending
        - Returns only top 5 longest routes

5. Processing Final Result
    - Gets to the destination airports using (`inV()`)
    - Removes duplicates using (`dedup()`)
    - Returns only the airport codes using (`values("code")`)

Insights:
- Knowledge of how scoped traversals work
- Knowledge of how sorting operations
- Knowledge of how to limit operations to optimize memory usage
- Knowledge of how to skip duplicates by tracking all seen vertices
- Spent a lot of time with sorting operations
- The initial traversals outwards from LAX