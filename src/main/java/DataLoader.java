import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DataLoader {
    public static LoadStatistics loadAirRoutesData(GraphTraversalSource g, String nodesFilePath, String edgesFilePath){
        long startTime = System.currentTimeMillis();
        LoadStatistics stats = new LoadStatistics();
        Map<String, Vertex> vertices = new HashMap<>();

        try{
            //load vertices
            File nodesFile = new File(nodesFilePath);
            try(Reader reader = new FileReader(nodesFile); CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())){
                for(CSVRecord record : parser){
                    String id = record.get("~id");
                    String label = record.get("~label");
                    Vertex v = g.addV(label).next();

                    for(String column : record.toMap().keySet()){
                        if(!column.startsWith("~") && !record.get(column).isEmpty()){
                            v.property(column, record.get(column));
                        }
                    }

                    vertices.put(id, v);
                    stats.vertexCount++;
                }
            }

            //load edges
            File edgesFile = new File(edgesFilePath);
            try(Reader reader = new FileReader(edgesFile); CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())){
                for(CSVRecord record : parser){
                    String fromId = record.get("~from");
                    String toId = record.get("~to");
                    String label = record.get("~label");

                    Vertex from = vertices.get(fromId);
                    Vertex to = vertices.get(toId);

                    if(from != null && to != null){
                        from.addEdge(label, to);
                        stats.edgeCount++;
                    }
                }
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        stats.loadTimeMs = endTime - startTime;

        return stats;
    }

    public static class LoadStatistics {
        int vertexCount = 0;
        int edgeCount = 0;
        long loadTimeMs = 0;

        @Override
        public String toString(){
            return vertexCount + " vertices\n" + edgeCount + " edges\n" + loadTimeMs + " ms";
        }
    }
}