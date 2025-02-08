package vttp.ws.ws26.model;

public class Game {
    private int gameId;
    private String name;
    
    public Game(int gameId, String name) {
        this.gameId = gameId;
        this.name = name;
    }
    public int getGameId() {
        return gameId;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Game [gameId=" + gameId + ", name=" + name + "]";
    }


}
