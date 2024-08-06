package com.avee.graphqldemo.service;

import com.avee.graphqldemo.model.Player;
import com.avee.graphqldemo.model.Team;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PlayerService {
    private List<Player> players = new ArrayList<>();
    AtomicInteger id = new AtomicInteger(0);

    public List<Player> findAll() {
        return players;
    }

    public Optional<Player> findOne(Integer id) {
        return players.stream()
                .filter(player -> player.Id() == id).findFirst();
    }

    public Player create(String name, Team team) {
        Player player = new Player(id.incrementAndGet(), name, team);
        players.add(player);
        return player;
    }

    public Player delete(Integer id) {
        Player player = players.stream().filter(c -> c.Id() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException());
        players.remove(player);
        return player;
    }

    public Player update(Integer id, String name, Team team) {
        Player updatedPlayer = new Player(id, name, team);
        Optional<Player> optional = players.stream()
                .filter(c -> c.Id() == id).findFirst();

        if (optional.isPresent()) {
            Player player = optional.get();
            int index = players.indexOf(player);
            players.set(index, updatedPlayer);
        } else {
            throw new IllegalArgumentException("Invalid Player");
        }
        return updatedPlayer;
    }

    @PostConstruct
    private void init() {
        players.add(new Player(id.incrementAndGet(), "Novak Djokovic", Team.SERBIA));
        players.add(new Player(id.incrementAndGet(), "Rafel Nadal", Team.SPAIN));
        players.add(new Player(id.incrementAndGet(), "Carlos Alcaraz", Team.SPAIN));
        players.add(new Player(id.incrementAndGet(), "Jannik Sinner", Team.ITALY));
        players.add(new Player(id.incrementAndGet(), "Coco Gauff", Team.USA));
        players.add(new Player(id.incrementAndGet(), "Roger Federer", Team.USA));
    }
}
