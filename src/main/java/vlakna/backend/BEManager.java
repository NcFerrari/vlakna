package vlakna.backend;

import vlakna.Values;
import vlakna.backend.threads.AObycejne;
import vlakna.backend.threads.AbstractThread;
import vlakna.backend.threads.BNekonecne;
import vlakna.backend.threads.CNekonecneSPauzou;
import vlakna.backend.threads.DASynchronni;
import vlakna.backend.threads.ESynchronni;
import vlakna.backend.threads.FVlaknaPoSobe;
import vlakna.backend.threads.GKoordinovana;
import vlakna.backend.threads.HDaemon;
import vlakna.frontend.FEManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BEManager {

    private static BEManager instance;
    private final Map<String, Runnable> threadMap = new HashMap<>();
    private final String[][] threadButtonTitles = {
            {
                    Values.THREAD_TYPE_SIMPLE,
                    Values.THREAD_TYPE_SIMPLE_FALL,
                    Values.THREAD_TYPE_SIMPLE_FADE,
                    Values.THREAD_TYPE_SIMPLE_ROTATE,
                    Values.THREAD_TYPE_SIMPLE_FILL,
                    Values.THREAD_TYPE_SIMPLE_STROKE,
                    Values.THREAD_TYPE_SIMPLE_GRADIENT,
                    Values.THREAD_TYPE_SIMPLE_TRANSFORM,},
            {
                    Values.THREAD_TYPE_INFINITY,
                    Values.THREAD_TYPE_INFINITY_STOP,},
            {
                    Values.THREAD_TYPE_INFINITY_WITH_PAUSE,
                    Values.THREAD_TYPE_INFINITY_WITH_PAUSE_PAUSE,
                    Values.THREAD_TYPE_INFINITY_WITH_PAUSE_STOP,},
            {
                    Values.THREAD_TYPE_ASYNCH,
                    Values.THREAD_TYPE_ASYNCH_FALL,
                    Values.THREAD_TYPE_ASYNCH_TEXTS,
                    Values.THREAD_TYPE_ASYNCH_MOVE_TO_TARGET,
                    Values.THREAD_TYPE_ASYNCH_MORE_BALLS,
                    Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS,
                    Values.THREAD_TYPE_ASYNCH_PAUSE,
                    Values.THREAD_TYPE_ASYNCH_STOP,},
            {
                    Values.THREAD_TYPE_SYNCH,
                    Values.THREAD_TYPE_SYNCH_FALL,
                    Values.THREAD_TYPE_SYNCH_TEXTS,
                    Values.THREAD_TYPE_SYNCH_MOVE_TO_TARGET,
                    Values.THREAD_TYPE_SYNCH_MORE_BALLS,
                    Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS,
                    Values.THREAD_TYPE_SYNCH_PAUSE,
                    Values.THREAD_TYPE_SYNCH_STOP,},
            {
                    Values.THREAD_TYPE_COORDINATION,
                    Values.THREAD_TYPE_COORDINATION_MORE_FALLING_BALLS,
                    Values.THREAD_TYPE_COORDINATION_PAUSE,
                    Values.THREAD_TYPE_COORDINATION_STOP,},
            {
                    Values.THREAD_TYPE_JOINED,
                    Values.THREAD_TYPE_JOINED_FALLING_BALLS,},
            {
                    Values.THREAD_TYPE_NO_DAEMON,
                    Values.THREAD_TYPE_DAEMON,},
            {
                    Values.THREAD_TYPE_SHOW_CURRENT_THREADS,},
    };
    private AObycejne ordinary;
    private AbstractThread infinity;
    private AbstractThread infinityWithPause;
    private DASynchronni aSynchronizationThreads;
    private ESynchronni synchronizationThreads;
    private FVlaknaPoSobe threadAfterThread;
    private GKoordinovana coordinateThreads;
    private FEManager feManager;

    public static BEManager getInstance() {
        if (instance == null) {
            instance = new BEManager();
            instance.fillThreadMap();
        }
        return instance;
    }

    private BEManager() {

    }

    public void fillThreadMap() {

        ordinary = new AObycejne();
        infinity = new BNekonecne();
        infinityWithPause = new CNekonecneSPauzou();
        aSynchronizationThreads = new DASynchronni();
        synchronizationThreads = new ESynchronni();
        threadAfterThread = new FVlaknaPoSobe();
        coordinateThreads = new GKoordinovana();
        HDaemon daemon1 = new HDaemon();
        HDaemon daemon2 = new HDaemon();

        threadMap.put(Values.THREAD_TYPE_SIMPLE, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_FALL, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_FALL));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_FADE, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_FADE));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_ROTATE, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_ROTATE));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_FILL, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_FILL));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_STROKE, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_STROKE));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_GRADIENT, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_GRADIENT));
        threadMap.put(Values.THREAD_TYPE_SIMPLE_TRANSFORM, () -> ordinary.start(Values.THREAD_TYPE_SIMPLE_TRANSFORM));

        threadMap.put(Values.THREAD_TYPE_INFINITY, infinity::start);
        threadMap.put(Values.THREAD_TYPE_INFINITY_STOP, infinity::stop);

        threadMap.put(Values.THREAD_TYPE_INFINITY_WITH_PAUSE, infinityWithPause::start);
        threadMap.put(Values.THREAD_TYPE_INFINITY_WITH_PAUSE_PAUSE, infinityWithPause::switchPause);
        threadMap.put(Values.THREAD_TYPE_INFINITY_WITH_PAUSE_STOP, infinityWithPause::stop);

        threadMap.put(Values.THREAD_TYPE_ASYNCH, () -> aSynchronizationThreads.start(Values.THREAD_TYPE_ASYNCH));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_FALL, () -> aSynchronizationThreads.start(Values.THREAD_TYPE_ASYNCH_FALL));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_TEXTS, () -> aSynchronizationThreads.start(Values.THREAD_TYPE_ASYNCH_TEXTS));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_MOVE_TO_TARGET, () -> aSynchronizationThreads.start(Values.THREAD_TYPE_ASYNCH_MOVE_TO_TARGET));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_MORE_BALLS, () -> aSynchronizationThreads.start(Values.THREAD_TYPE_ASYNCH_MORE_BALLS));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS, () -> aSynchronizationThreads.start(Values.THREAD_TYPE_ASYNCH_MORE_FALLING_BALLS));
        threadMap.put(Values.THREAD_TYPE_ASYNCH_PAUSE, aSynchronizationThreads::switchPause);
        threadMap.put(Values.THREAD_TYPE_ASYNCH_STOP, aSynchronizationThreads::stop);

        threadMap.put(Values.THREAD_TYPE_SYNCH, () -> synchronizationThreads.start(Values.THREAD_TYPE_SYNCH));
        threadMap.put(Values.THREAD_TYPE_SYNCH_FALL, () -> synchronizationThreads.start(Values.THREAD_TYPE_SYNCH_FALL));
        threadMap.put(Values.THREAD_TYPE_SYNCH_TEXTS, () -> synchronizationThreads.start(Values.THREAD_TYPE_SYNCH_TEXTS));
        threadMap.put(Values.THREAD_TYPE_SYNCH_MOVE_TO_TARGET, () -> synchronizationThreads.start(Values.THREAD_TYPE_SYNCH_MOVE_TO_TARGET));
        threadMap.put(Values.THREAD_TYPE_SYNCH_MORE_BALLS, () -> synchronizationThreads.start(Values.THREAD_TYPE_SYNCH_MORE_BALLS));
        threadMap.put(Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS, () -> synchronizationThreads.start(Values.THREAD_TYPE_SYNCH_MORE_FALLING_BALLS));
        threadMap.put(Values.THREAD_TYPE_SYNCH_PAUSE, synchronizationThreads::switchPause);
        threadMap.put(Values.THREAD_TYPE_SYNCH_STOP, synchronizationThreads::stop);

        threadMap.put(Values.THREAD_TYPE_COORDINATION, () -> coordinateThreads.start(Values.THREAD_TYPE_COORDINATION));
        threadMap.put(Values.THREAD_TYPE_COORDINATION_MORE_FALLING_BALLS, () -> coordinateThreads.start(Values.THREAD_TYPE_COORDINATION_MORE_FALLING_BALLS));
        threadMap.put(Values.THREAD_TYPE_COORDINATION_PAUSE, coordinateThreads::switchPause);
        threadMap.put(Values.THREAD_TYPE_COORDINATION_STOP, coordinateThreads::stop);

        threadMap.put(Values.THREAD_TYPE_JOINED, () -> threadAfterThread.start(Values.THREAD_TYPE_JOINED));
        threadMap.put(Values.THREAD_TYPE_JOINED_FALLING_BALLS, () -> threadAfterThread.start(Values.THREAD_TYPE_JOINED_FALLING_BALLS));

        threadMap.put(Values.THREAD_TYPE_NO_DAEMON, () -> {
            daemon1.start(false, 3);
            daemon2.start(false, 1000);
        });
        threadMap.put(Values.THREAD_TYPE_DAEMON, () -> {
            getFeManager().exitApplication();
            daemon1.start(false, 3);
            daemon2.start(true, 1000);
        });

        threadMap.put(Values.THREAD_TYPE_SHOW_CURRENT_THREADS, () -> {
            StringBuilder threadOutput = new StringBuilder();
            Thread.getAllStackTraces().keySet().stream().sorted(Comparator.comparing(Thread::getName)).forEach(thread -> threadOutput.append(thread.getName()).append(Values.BIG_SPACE).append(thread.getState()).append("\n"));
            getFeManager().showMessage(threadOutput.toString());
        });
    }

    public FEManager getFeManager() {
        return feManager;
    }

    public String[][] getThreadButtonTitles() {
        return threadButtonTitles;
    }

    public void setFeManager(FEManager feManager) {
        this.feManager = feManager;
    }

    public void useThread(String name, FEManager feManager) {
        setFeManager(feManager);
        threadMap.get(name).run();
    }
}