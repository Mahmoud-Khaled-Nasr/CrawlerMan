package ranker;

import util.PathGenerator;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * A class representing the static ranking functionality.
 * This class is intended to be used as static class with only static methods allowed.
 */
public class StaticRanker {

    private static final Logger LOGGER = Logger.getLogger(StaticRanker.class.getName());


    private static Map<Integer, Double> pageRank(HashMap<Integer, Set<Integer>> inbound, HashMap<Integer, Set<Integer>> outbound) {
        final double DAMPING_FACTOR = 0.5;
        final int PAGE_RANK_ITERATIONS = 100;

        Map<Integer, Double> ranks = new HashMap<>();
        for (int i = 0; i < PAGE_RANK_ITERATIONS; i++) {
            for (int urlId : outbound.keySet()) {
                double rank = ranks.getOrDefault(urlId, 1.0);
                for (int linkId : inbound.getOrDefault(urlId, Collections.emptySet())) {
                    rank += ranks.getOrDefault(linkId, 1.0) / outbound.get(linkId).size();
                }
                // TODO double-check this equation
                rank = (1 - DAMPING_FACTOR) + DAMPING_FACTOR * rank;
                ranks.put(urlId, rank);
            }
        }
        return ranks;
    }

    /**
     * Updates the graph with new links.
     * @param newLinks The new links to be added/updated
     */
    @SuppressWarnings("unchecked") // I hate this line, but the casting is necessary
    public static void updateRanks(Map<Integer, Set<Integer>> newLinks) {

        LOGGER.info("Static Ranker is starting!");

        HashMap<Integer, Set<Integer>> inbound;
        HashMap<Integer, Set<Integer>> outbound;
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(PathGenerator.generate("graph").toFile()));
            inbound = (HashMap<Integer, Set<Integer>>) stream.readObject();
            outbound = (HashMap<Integer, Set<Integer>>) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            inbound = new HashMap<>();
            outbound = new HashMap<>();
        }

        for (int urlId : newLinks.keySet()) {
            Set<Integer> newOutboundIds = newLinks.get(urlId);
            Set<Integer> outboundIds = outbound.getOrDefault(urlId, Collections.emptySet());
            if (!newOutboundIds.equals(outboundIds)) {
                Set<Integer> addedIds = new HashSet<>(newOutboundIds);
                addedIds.removeAll(outboundIds);
                for (int id : addedIds) {
                    inbound.putIfAbsent(id, new HashSet<>());
                    inbound.get(id).add(urlId);
                }
                Set<Integer> removedIds = new HashSet<>(outboundIds);
                removedIds.removeAll(newOutboundIds);
                for (int id : removedIds) {
                    inbound.get(id).remove(urlId);
                }
                outbound.put(urlId, newOutboundIds);
            }
        }

        DatabaseController.updatePageRanks(pageRank(inbound, outbound));

        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(PathGenerator.generate("graph").toFile()));
            stream.writeObject(inbound);
            stream.writeObject(outbound);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Static Ranker is shutting down normally!");
    }
}
