import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import java.io.File;

public class SampleGraphApp{
    private JanusGraph graph;
    private GraphTraversalSource g;

    public static void main(String[] args){
        SampleGraphApp app = new SampleGraphApp();
        try{
            app.initializeInMemoryGraph();
            app.initializeSchema();
            app.loadSampleData();
            app.runSampleData();
            app.closeGraph();
        }
        catch (Exception error){
            System.err.println("Error for in-memory graph");
            error.printStackTrace();
        }
    }

    public void initializeInMemoryGraph(){
        graph = JanusGraphFactory.build().set("storage.backend", "inmemory").open();
        g = graph.traversal();
    }

    public void initializeSchema(){
        try{
            SchemaInitializer.initializeAirRoutesSchema(graph);
        }
        catch(Exception error){
            System.err.println("Error initializing schema");
        }
    }

    public void loadSampleData(){
        try{
            DataLoader.LoadStatistics stats = DataLoader.loadAirRoutesData(g, "src/main/resources/air-routes-latest-nodes.csv", "src/main/resources/air-routes-latest-edges.csv");
            System.out.println(stats);
        }
        catch(Exception error){
            System.err.println("Error loading sample data");
        }
    }

    public void runSampleData(){
        long airportCount = g.V().hasLabel("airport").count().next();
        long routeCount = g.E().hasLabel("route").count().next();

        System.out.println("Airports: " + airportCount);
        System.out.println("Routes: " + routeCount);
    }

    public void closeGraph() {
        if(graph != null && graph.isOpen()){
            graph.close();
            System.out.println("Graph closed successfully");
        }
    }
}