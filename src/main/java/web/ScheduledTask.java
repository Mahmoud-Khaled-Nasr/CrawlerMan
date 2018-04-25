package web;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class ScheduledTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledTask.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedDelay = 100000)
    public void runTheCrawlerAndIndexer() throws FileNotFoundException, InterruptedException {
        LOGGER.warning("The scheduled crawler and indexer are running now " + dateFormat.format(new Date()));
        Application.update(Application.SEED_FILE_NAME, Application.MAX_DOUCMENTS_COUNT, Application.NUMBER_OF_THREADS);
    }

}
