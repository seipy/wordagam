package io.github.gravetii.game;

import io.github.gravetii.dictionary.Dictionary;
import io.github.gravetii.util.AppLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class GameFactory {
  private static final int MAX_GAMES_IN_QUEUE = 5;
  private static volatile GameFactory instance;

  private Dictionary dictionary;
  private LinkedBlockingDeque<Game> queue;
  private ExecutorService executor;

  private GameFactory() {
    this.dictionary = new Dictionary();
    this.queue = new LinkedBlockingDeque<>(MAX_GAMES_IN_QUEUE);
    this.executor = Executors.newFixedThreadPool(1);
    this.bootstrap();
  }

  public static GameFactory get() {
    if (instance == null) {
      synchronized (GameFactory.class) {
        if (instance == null) {
          instance = new GameFactory();
          AppLogger.fine(GameFactory.class.getCanonicalName(), "Created instance of GameFactory");
        }
      }
    }

    return instance;
  }

  public static void close() {
    if (instance != null) {
      instance.shutdown();
      instance = null;
    }
  }

  private Game create() {
    Game game = null;
    Game.Quality q = Game.Quality.LOW;

    while (q == Game.Quality.LOW) {
      game = new Game(this.dictionary);
      q = game.getQuality();
    }

    return game;
  }

  private void bootstrap() {
    this.executor.submit(new GameLoaderTask(MAX_GAMES_IN_QUEUE));
  }

  public synchronized Game fetch() {
    Game game = this.queue.poll();
    if (game == null) {
      game = this.create();
    }

    this.backFill();
    AppLogger.fine(getClass().getCanonicalName(), "Fetched new game: " + game);
    return game;
  }

  private void backFill() {
    int n = MAX_GAMES_IN_QUEUE - queue.size();
    if (n > 0) {
      this.executor.submit(new GameLoaderTask(n));
    }
  }

  public void shutdown() {
    try {
      this.executor.shutdown();
      boolean terminated = this.executor.awaitTermination(2, TimeUnit.SECONDS);
      if (!terminated) {
        this.executor.shutdownNow();
      }

      this.queue.clear();
    } catch (Exception e) {
      AppLogger.severe(getClass().getCanonicalName(), "Error while closing GameFactory: " + e);
    }
  }

  private class GameLoaderTask implements Runnable {

    private int n;

    GameLoaderTask(int n) {
      this.n = n;
    }

    @Override
    public void run() {
      for (int i = 1; i <= n; ++i) {
        Game game = create();
        queue.offerLast(game);
      }
    }
  }
}
