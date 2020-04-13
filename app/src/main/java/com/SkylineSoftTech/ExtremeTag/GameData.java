package com.SkylineSoftTech.ExtremeTag;

import java.util.Date;
import java.util.Map;

public class GameData {
    //TODO Charith this is the class that needs to be stored in the database
    String d;
    String name;
    String creator;
    boolean isGameDone;
    Map<String,Boolean> online;
    Map<String,Boolean> isInGame;
    String gameStatus;
    //game status states: "finished" "join" "playing"
    public GameData(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameData(String d, String creator,String name, boolean isGameDone, Map<String, Boolean> online, Map<String, Boolean> isInGame, String gameStatus) {
        this.d = d;
        this.name = name;
        this.isGameDone = isGameDone;
        this.online = online;
        this.isInGame = isInGame;
        this.gameStatus = gameStatus;
        this.creator=creator;

    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void addPerson(String name){
        online.put(name,true);
        isInGame.put(name,true);
    }
    public void changeStatus(String name,boolean online,boolean isInGameNow){
        this.online.put(name,online);
        isInGame.put(name,isInGameNow);

    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
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
