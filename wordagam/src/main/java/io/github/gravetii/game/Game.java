package io.github.gravetii.game;

import io.github.gravetii.dictionary.Dictionary;
import io.github.gravetii.util.Alphabet;
import io.github.gravetii.util.GridPoint;
import io.github.gravetii.util.GridUnit;
import io.github.gravetii.util.Constants;

import java.util.*;

import static io.github.gravetii.util.Constants.WORDS_COUNT_HIGH;
import static io.github.gravetii.util.Constants.WORDS_COUNT_LOW;

public class Game {

    private io.github.gravetii.dictionary.Dictionary dictionary;

    private GridUnit[][] grid;

    private Map<String, Integer> wordPoints;
    private int totalPoints;
    private Set<String> allWords;
    private Quality quality;

    public Game(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.grid = new GridUnit[4][4];
        this.wordPoints = new HashMap<>();
        this.wordPoints.put("", 0);
        this.totalPoints = 0;
        this.allWords = new HashSet<>();
        this.create();
        this.crawl();
        this.quality = assignQuality();
    }

    public Set<String> getAllWords() {
        return this.allWords;
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    public GridUnit[][] getGrid() {
        return grid;
    }

    private void create() {
        List<GridUnit> alphaUnits = Alphabet.getAll();
        Random random = new Random();
        for (int i=0;i<4;++i) {
            for (int j=0;j<4;++j) {
                int ridx = random.nextInt(alphaUnits.size());
                grid[i][j] = alphaUnits.get(ridx);
            }
        }
    }

    private boolean isValidWord(String word) {
        return word.length() >= Constants.MIN_WORD_LENGTH &&
                this.dictionary.search(word) && !allWords.contains(word);
    }

    private void crawl(GridPoint point, String prefix, boolean[][] visited) {
        int x = point.x; int y = point.y;
        GridUnit unit = grid[x][y];
        visited[x][y] = true;

        String word = prefix + unit.getLetter();
        if (!this.dictionary.prefix(word)) {
            return;
        }

        int points = this.wordPoints.get(prefix) + unit.getPoints();
        this.wordPoints.put(word, points);
        if (isValidWord(word)) {
            this.allWords.add(word);
            this.totalPoints += wordPoints.get(word);
        }

        for (GridPoint n: getNeighbors(point)) {
            if (!visited[n.x][n.y]) {
                boolean [][] v = visited.clone();
                this.crawl(n, word, v);
            }
        }
    }

    private void crawl() {
        for (int i=0;i<4;++i) {
            for (int j=0;j<4;++j) {
                boolean visited[][] = new boolean[4][4];
                for (boolean[] row: visited) {
                    Arrays.fill(row, false);
                }

                this.crawl(new GridPoint(i, j), "", visited);
            }
        }
    }

    private Quality assignQuality() {
        int sz = allWords.size();
        Quality q;

        if (sz >= WORDS_COUNT_HIGH) {
            q = Quality.HIGH;
        }
        else if (sz <= WORDS_COUNT_LOW) {
            q = Quality.LOW;
        }
        else {
            q = Quality.MEDIUM;
        }

        return q;
    }

    public Quality getQuality() {
        return quality;
    }

    private List<GridPoint> getNeighbors(GridPoint point) {
        int x = point.x; int y = point.y;
        int dx[] = {-1, -1, -1, 0, 0, 1, 1, 1};
        int dy[] = {-1, 0, 1, -1, 1, -1, 0, 1};
        List<GridPoint> neighbors = new ArrayList<>(8);
        for (int i=0;i<dx.length;++i) {
            GridPoint n = new GridPoint(x + dx[i], y + dy[i]);
            if (n.isValid()) {
                neighbors.add(n);
            }
        }

        return neighbors;
    }

    @Override
    public String toString() {
        return "Game{" +
                "totalPoints=" + totalPoints +
                ", allWords=" + allWords +
                ", quality=" + quality +
                '}';
    }
}
