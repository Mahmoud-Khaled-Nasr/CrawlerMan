package ranker;

import model.Node;

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
    private static Double INITIAL_RANK = 1.0 - DAMPING_FACTOR;
    private static List<Node> graph;

    public static void updateRanks (Map<Integer, Set<Integer>> newLinks){
        LOGGER.info("Static Ranker is starting!");
        graph = DatabaseController.getGraph();
        updateGraph(newLinks);
        DatabaseController.updatePageRanks(updateGraphRanks());
        LOGGER.info("Static Ranker is terminating!");
    }

    private static void updateGraph (Map <Integer, Set<Integer>> newLinks) {
        LOGGER.info("updating Graph!");
        //Get nodes that needs updating

        Set<Integer> newLinksKeySet = newLinks.keySet();
        Set<Integer> graphChildren = new TreeSet<>();
        List<Node> oldNodes = new LinkedList<>();

        for (Node node : graph) {
            Integer id = node.getUrlId();
            graphChildren.add(id);
            //get the nodes that needs updating
            if (newLinksKeySet.contains(id)) {
                oldNodes.add(node);
            }
        }
        //check the elements of the children if they exist in the DB or in the keys set of the newLinks
        graphChildren.addAll(newLinksKeySet);
        for (Integer link : newLinksKeySet) {
            Set<Integer> children = newLinks.get(link);
            children.retainAll(graphChildren);
        }

        //update the existing nodes in the graph
        for (Node node : oldNodes) {
            Integer urlId = node.getUrlId();
            node.setChildren(newLinks.get(urlId));
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
        DatabaseController.saveGraph(graph);

        LOGGER.info("finish updating Graph!");
    }

    private static Map<Integer, Double> updateGraphRanks () {
        LOGGER.info("updating Graph Ranks!");
        graph = DatabaseController.getGraph();
        Map<Integer, Double> ranks = new TreeMap<>();
        Map<Integer, Double> oldRanks = new TreeMap<>();

        //set the default ranks to 1
        for (Node node : graph) {
            oldRanks.put(node.getUrlId(), INITIAL_RANK);
        }

        //Calculate the Ranks
        for (int i = 0; i < PAGE_RANK_ITERATIONS; i++) {
            ranks = new TreeMap<>();
            for (Node node : graph) {
                ranks.put(node.getUrlId(), INITIAL_RANK);
            }
            for (Node node : graph) {
                Double parentRank = DAMPING_FACTOR * (oldRanks.get(node.getUrlId()) / node.getChildren().size());
                //update the children ranks
                for (Integer urlId : node.getChildren()) {
                    ranks.put(urlId, ranks.get(urlId) + parentRank);
                }
            }
            oldRanks = ranks;
        }

        LOGGER.info("finish updating Graph Ranks!");
        return ranks;
    }
}
