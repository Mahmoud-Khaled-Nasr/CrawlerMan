package ranker;

import util.PathGenerator;

import java.io.*;
import java.util.*;

public class StaticRanker {
    public static void updateRanks(Map<Integer, Set<Integer>> newLinks, double dampingFactor, int pageRankIterations) {

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

        Map<Integer, Double> ranks = new HashMap<>();
        for (int i = 0; i < pageRankIterations; i++) {
            for (int urlId : outbound.keySet()) {
                double rank = 0;
                for (int linkId : inbound.getOrDefault(urlId, Collections.emptySet())) {
                    rank += ranks.getOrDefault(linkId, 0.0) / outbound.get(linkId).size();
                }
                rank = (1 - dampingFactor) + dampingFactor * rank;
                ranks.put(urlId, rank);
            }
        }
        // TODO save the ranks to DB

        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(PathGenerator.generate("graph").toFile()));
            stream.writeObject(inbound);
            stream.writeObject(outbound);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
