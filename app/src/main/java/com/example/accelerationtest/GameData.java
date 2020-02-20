package com.example.accelerationtest;

import java.util.Date;
import java.util.Map;

public class GameData {
    Date d;
    boolean isGameDone;
    Map<String,Boolean> online;
    Map<String,Boolean> isInGame;
    String gameStatus;

    public GameData(Date d, boolean isGameDone, Map<String, Boolean> online, Map<String, Boolean> isInGame, String gameStatus) {
        this.d = d;
        this.isGameDone = isGameDone;
        this.online = online;
        this.isInGame = isInGame;
        this.gameStatus = gameStatus;
    }

    public void addPerson(String name){
        online.put(name,true);
        isInGame.put(name,true);
    }
    public void changeStatus(String name,boolean online,boolean isInGameNow){
        this.online.put(name,online);
        isInGame.put(name,isInGameNow);

    }

    public Date getD() {
        return d;
    }

    public void setD(Date d) {
        this.d = d;
    }

    public boolean isGameDone() {
        return isGameDone;
    }

    public void setGameDone(boolean gameDone) {
        isGameDone = gameDone;
    }

    public Map<String, Boolean> getOnline() {
        return online;
    }

    public void setOnline(Map<String, Boolean> online) {
        this.online = online;
    }

    public Map<String, Boolean> getIsInGame() {
        return isInGame;
    }

    public void setIsInGame(Map<String, Boolean> isInGame) {
        this.isInGame = isInGame;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
