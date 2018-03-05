package crawler;

import util.Channel;

public class Leg implements Runnable {

    private Channel<URL> URLs;
    private Channel<String> candidateURLs;

    public Leg(Channel<URL> URLs, Channel<String> candidateURLs) {
        this.URLs = URLs;
        this.candidateURLs = candidateURLs;
    }

    @Override
    public void run() {
        //do stuff
    }
}
