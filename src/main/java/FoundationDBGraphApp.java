import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import java.io.File;

public class FoundationDBGraphApp{
    private JanusGraph graph;
    private GraphTraversalSource g;

    public static void main(String[] args){
        FoundationDBGraphApp app = new FoundationDBGraphApp();
        try{
            app.initializeFoundationDBGraph();
            app.initializeSchema();
            app.loadSampleData();
            app.findStorageSize();
            app.closeGraph();
        }
        catch(Exception error){
            System.err.println("Error for FoundationDB graph");
            error.printStackTrace();
        }
    }

    public void initializeFoundationDBGraph(){
        File file = new File("fdb-data");

        try{
            if (file.exists()){
                deleteDirectory(file);
            }
            file.mkdirs();
        }
        catch(Exception error){
            System.err.println("Failed to initialize FoundationDB");
        }

        //had to do all of this, so it opened properly lol
        graph = JanusGraphFactory.build()
                .set("storage.backend", "org.janusgraph.diskstorage.foundationdb.FoundationDBStoreManager")
                .set("storage.directory", "fdb-data")
                .set("storage.foundationdb.directory", "C:\\ProgramData\\foundationdb")
                .set("storage.foundationdb.cluster-file", "C:\\ProgramData\\foundationdb\\fdb.cluster")
                .set("index.search.backend", null)
                .open();
        g = graph.traversal();
        System.out.println("Successfully loaded FoundationDB graph");
    }

    private boolean deleteDirectory(File directory){
        File[] files = directory.listFiles();
        if(files != null){
            for(int i = 0; i < files.length; i++){
                if(files[i].isDirectory()){
                    deleteDirectory(files[i]);
                }
                else{
                    files[i].delete();
                }
            }
        }

        return directory.delete();
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

    public void findStorageSize(){
        try{
            long totalSize = calculateDirectorySize(new File("berkeley-db"));
            System.out.println(totalSize + " MB" );
        }
        catch(Exception error){
            System.err.println("Error calculating storage size");
        }
    }

    private long calculateDirectorySize(File directory){
        long size = 0;
        File[] files = directory.listFiles();

        if(files != null){
            for(int i = 0; i < files.length; i++){
                if(files[i].isFile()){
                    size += files[i].length();
                }
                else{
                    size += calculateDirectorySize(files[i]);
                }
            }
        }

        return size;
    }

    public void closeGraph(){
        if(graph != null && graph.isOpen()){
            graph.close();
            System.out.println("Graph closed successfully");
        }
    }
}