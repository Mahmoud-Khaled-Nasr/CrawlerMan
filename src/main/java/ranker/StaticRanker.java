package ranker;

import model.Node;
import org.mongodb.morphia.query.FindOptions;
import util.DatabaseDriver;

import java.util.*;
import java.util.logging.Logger;

/**
 * A class representing the static ranking functionality.
 * This class is intended to be used as static class with only static methods allowed.
 */
public class StaticRanker {

    private static final Logger LOGGER = Logger.getLogger(StaticRanker.class.getName());
    private static double DAMPING_FACTOR = 0.5;
    private static int PAGE_RANK_ITERATIONS = 100;
    private static Double INITIAL_RANK = 1.0;
    private static Double DEFAULT_RANK = 1 - DAMPING_FACTOR;
    private static List<Node> graph;

    public static void updateRanks (Map<Integer, Set<Integer>> newLinks){
        LOGGER.info("Static Ranker is starting!");
        graph = DatabaseDriver.datastore.createQuery(Node.class).asList();
        updateGraph(newLinks);
        DatabaseController.updatePageRanks(updateGraphRanks());
        LOGGER.info("Static Ranker is terminating!");
    }

    private static void updateGraph (Map <Integer, Set<Integer>> newLinks){
        LOGGER.info("updating Graph!");
        //Get nodes that needs updating
        try {
            Set<Integer> newLinksKeySet = newLinks.keySet();
            Set<Integer> graphChildren = new HashSet<>();
            List<Node> oldNodes = new LinkedList<>();

            //TODO Hamdy can i use foreach here?
            for (int i = 0; i < graph.size(); i++) {
                Integer id = graph.get(i).getUrlId();
                graphChildren.add(id);
                //get the nodes that needs updating
                if (newLinksKeySet.contains(id)){
                    oldNodes.add(graph.get(i));
                }
            }
            //check the elements of the children if they exist in the DB or in the keys set of the newLinks
            graphChildren.addAll(newLinksKeySet);
            for (Integer link : newLinksKeySet){
                Set<Integer> children = newLinks.get(link);
                children.retainAll(graphChildren);
            }

            //update the existing nodes in the graph
            for (Node node : oldNodes) {
                Integer urlId = node.getUrlId();
                Set<Integer> children = newLinks.get(urlId);
                oldNodes.get(urlId).setChildren(children);
                newLinksKeySet.remove(urlId);
            }

            List<Node> newNodes = new LinkedList<>();
            //create new nodes
            for (Integer urlId : newLinksKeySet) {
                newNodes.add(new Node(urlId, newLinks.get(urlId)));
            }

            //update the database
            graph.clear();
            graph.addAll(newNodes);
            graph.addAll(oldNodes);
            DatabaseDriver.datastore.save(graph);
        }catch (Exception e){
            e.printStackTrace();
        }

        LOGGER.info("finish updating Graph!");
    }

    private static Map<Integer, Double> updateGraphRanks (){
        LOGGER.info("updating Graph Ranks!");
        DatabaseDriver.datastore.createQuery(Node.class).asList();
        Map<Integer, Double> ranks = new HashMap<>();
        try {
            //TODO every time static ranker starts is starts from 1 is this right?
            //set the default ranks to 1
            for (Node node : graph) {
                ranks.put(node.getUrlId(), INITIAL_RANK);
            }

            for (int i = 0; i < PAGE_RANK_ITERATIONS; i++) {
                LOGGER.info("Loop " + i);
                for (Node node : graph) {
                    //TODO not sure from the equation here i think i should multiply with something here
                    Double parentRank = ranks.get(node.getUrlId()) / node.getChildren().size();
                    //update the children ranks
                    for (Integer urlId : node.getChildren()) {
                        ranks.put(urlId, ranks.get(urlId) + parentRank);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LOGGER.info("finish updating Graph Ranks!");
        return ranks;
    }
}
