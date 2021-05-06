package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ServicePlayer {

    Page<Player> findAll(Specification<Player> specification, Pageable sorted);

    List<Player> findAll(Specification<Player> specification);

    Player addPlayer(Player player);

    Player getPlayer(Long id);

    Player editPlayer(Long id, Player player);

    void deletePlayer(Long id);

    Specification<Player> filterByName(String name);

    Specification<Player> filterByTitle(String title);

    Specification<Player> filterByRace(Race race);

    Specification<Player> filterByProfession(Profession profession);

    Specification<Player> filterByBirthday(Long after, Long before);

    Specification<Player> filterByBanned(Boolean banned);

    Specification<Player> filterByExperience(Integer min, Integer max);

    Specification<Player> filterByLevel(Integer min, Integer max);

    Long checkId(String id);
}
