package crawler;

import util.Channel;
import util.Pair;

import java.util.Set;

public class Leg implements Runnable {

    private Channel<String> URLs;
    private Channel<Pair<String, Set<String>>> candidateURLSets;

    public Leg(Channel<String> URLs, Channel<Pair<String, Set<String>>> candidateURLSets) {
        this.URLs = URLs;
        this.candidateURLSets = candidateURLSets;
    }

    @Override
    public void run() {
        //do stuff
    }
}
