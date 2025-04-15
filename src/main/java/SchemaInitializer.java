import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.core.schema.JanusGraphManagement;
import java.util.concurrent.ExecutionException;

public class SchemaInitializer {
    public static void initializeAirRoutesSchema(JanusGraph graph) throws ExecutionException, InterruptedException {
        System.out.println("Initializing air routes schema");
        JanusGraphManagement management = graph.openManagement();

        try{
            //create vertices
            if(management.getVertexLabel("airport") == null){
                management.makeVertexLabel("airport").make();
            }
            if(management.getVertexLabel("country") == null){
                management.makeVertexLabel("country").make();
            }
            if(management.getVertexLabel("continent") == null){
                management.makeVertexLabel("continent").make();
            }

            //create edges
            if(management.getEdgeLabel("route") == null){
                management.makeEdgeLabel("route").directed().multiplicity(Multiplicity.MULTI).make();
            }
            if(management.getEdgeLabel("contains") == null){
                management.makeEdgeLabel("contains").directed().multiplicity(Multiplicity.MULTI).make();
            }

            //create property keys and indices
            createPropertyKeys(management);
            createIndices(management);

            //commit the schema
            management.commit();
            processIndices(graph);
        }
        catch(Exception error){
            if(management != null && management.isOpen()){
                management.rollback();
            }
            System.err.println("Error initializing air routes schema");
        }
    }

    //helper functions for air routes schema
    private static void createPropertyKeys(JanusGraphManagement management) {
        createStringProperty(management, "city");
        createStringProperty(management, "dist");
        createStringProperty(management, "identity");
        createStringProperty(management, "type");
        createStringProperty(management, "code");
        createStringProperty(management, "icao");
        createStringProperty(management, "desc");
        createStringProperty(management, "region");
        createStringProperty(management, "country");

        createIntProperty(management, "runways");
        createIntProperty(management, "longest");
        createIntProperty(management, "elev");
        createIntProperty(management, "distance");
        createDoubleProperty(management, "lat");
        createDoubleProperty(management, "lon");
    }

    private static void createStringProperty(JanusGraphManagement management, String name){
        if(management.getPropertyKey(name) == null){
            management.makePropertyKey(name).dataType(String.class).cardinality(Cardinality.SINGLE).make();
        }
    }

    private static void createIntProperty(JanusGraphManagement management, String name){
        if(management.getPropertyKey(name) == null){
            management.makePropertyKey(name).dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        }
    }

    private static void createDoubleProperty(JanusGraphManagement management, String name){
        if(management.getPropertyKey(name) == null){
            management.makePropertyKey(name).dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        }
    }

    private static void createIndices(JanusGraphManagement management){
        PropertyKey identityKey = management.getPropertyKey("identity");
        if(management.getGraphIndex("Idx_comidx_Vertex_identity_unique") == null){
            management.buildIndex("Idx_comidx_Vertex_identity_unique", Vertex.class).addKey(identityKey).unique().buildCompositeIndex();
        }

        PropertyKey typeKey = management.getPropertyKey("type");
        if(management.getGraphIndex("Idx_comidx_Vertex_type_airport") == null){
            management.buildIndex("Idx_comidx_Vertex_type_airport", Vertex.class).addKey(typeKey).indexOnly(management.getVertexLabel("airport")).buildCompositeIndex();
        }

        PropertyKey codeKey = management.getPropertyKey("code");
        if(management.getGraphIndex("Idx_comidx_Vertex_code") == null){
            management.buildIndex("Idx_comidx_Vertex_code", Vertex.class).addKey(codeKey).buildCompositeIndex();
        }

        PropertyKey icaoKey = management.getPropertyKey("icao");
        if(management.getGraphIndex("Idx_comidx_Vertex_icao") == null){
            management.buildIndex("Idx_comidx_Vertex_icao", Vertex.class).addKey(icaoKey).buildCompositeIndex();
        }

        PropertyKey countryKey = management.getPropertyKey("country");
        if(management.getGraphIndex("Idx_comidx_Vertex_country") == null){
            management.buildIndex("Idx_comidx_Vertex_country", Vertex.class).addKey(countryKey).buildCompositeIndex();
        }

        PropertyKey cityKey = management.getPropertyKey("city");
        if(management.getGraphIndex("Idx_comidx_Vertex_city") == null){
            management.buildIndex("Idx_comidx_Vertex_city", Vertex.class).addKey(cityKey).buildCompositeIndex();
        }

        if(management.getGraphIndex("Idx_comidx_Edge_identity") == null){
            management.buildIndex("Idx_comidx_Edge_identity", Edge.class).addKey(identityKey).buildCompositeIndex();
        }
    }

    //had to delay since i was getting errors since it was running too fast or something
    private static void processIndices(JanusGraph graph) throws ExecutionException, InterruptedException {
        String[] indices = {"Idx_comidx_Vertex_identity_unique", "Idx_comidx_Vertex_type_airport", "Idx_comidx_Vertex_code", "Idx_comidx_Vertex_icao", "Idx_comidx_Vertex_country", "Idx_comidx_Vertex_city", "Idx_comidx_Edge_identity"};
        for(int i = 0; i < indices.length; i++){
            JanusGraphManagement mgmt = graph.openManagement();

            try{
                if(mgmt.getGraphIndex(indices[i]) != null){
                    mgmt.updateIndex(mgmt.getGraphIndex(indices[i]), SchemaAction.REINDEX).get();
                }
            }
            finally{
                if (mgmt.isOpen()) {
                    mgmt.commit();
                }
            }
        }
    }
}