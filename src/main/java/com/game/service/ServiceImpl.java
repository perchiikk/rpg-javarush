package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.NotFoundException;
import com.game.exception.RequestException;
import com.game.repository.PlayerRepository;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceImpl implements ServicePlayer{
    private PlayerRepository playerRepository;

    public ServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Page<Player> findAll(Specification<Player> specification, Pageable sorted){
        return playerRepository.findAll(specification, sorted);
    }

    @Override
    public List<Player> findAll(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }


    @Override
    public Player addPlayer(Player player) {
        if(player.getName() == null || player.getTitle() == null
        || player.getRace() == null || player.getProfession()==null || player.getExperience()==null
        || player.getBirthday() == null) {
            throw new RequestException("Some param is null");
        }

        checkParam(player);
        Integer level = calculateLevel(player);
        player.setLevel(level);

        Integer untilNextLevel = calculateUntilNextLevel(player);
        player.setUntilNextLevel(untilNextLevel);

        if(player.getBanned() == null){
            player.setBanned(false);
        }

        return playerRepository.save(player);
    }

    @Override
    public Player getPlayer(Long id) {
        Optional<Player> o = playerRepository.findById(id);
        if(o.isPresent()){
            return o.get();
        } else {
            throw new NotFoundException("Not found player with id");
        }
    }

    @Override
    public Player editPlayer(Long id, Player player) {
        if(!playerRepository.existsById(id)){
            throw new NotFoundException("Player is not found");
        }

        Player currentPlayer = getPlayer(id);
        checkParam(player);


        if(player.getName() != null){
            currentPlayer.setName(player.getName());
        }
        if(player.getTitle() != null){
            currentPlayer.setTitle(player.getTitle());
        }
        if(player.getRace() != null){
            currentPlayer.setRace(player.getRace());
        }
        if(player.getProfession() != null) {
            currentPlayer.setProfession(player.getProfession());
        }
        if(player.getExperience() != null){
            currentPlayer.setExperience(player.getExperience());
        }
        if(player.getBirthday() != null){
            currentPlayer.setBirthday(player.getBirthday());
        }
        if(player.getBanned() != null) {
            currentPlayer.setBanned(player.getBanned());
        } else {
            currentPlayer.setBanned(Boolean.FALSE);
        }

        Integer level = calculateLevel(currentPlayer);
        currentPlayer.setLevel(level);

        Integer untilNextLevel = calculateUntilNextLevel(currentPlayer);
        currentPlayer.setUntilNextLevel(untilNextLevel);

        return playerRepository.save(currentPlayer);

    }

    @Override
    public void deletePlayer(Long id) {
        if(playerRepository.existsById(id)){
            playerRepository.deleteById(id);
        }
        else throw new NotFoundException("Player not found");
    }

    private void checkParam(Player player){
        if(player.getName() != null && (player.getName().length() > 12 || player.getName().length() < 1)){
            throw new RequestException("Name is incorrect");
        }
        if(player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30)){
            throw new RequestException("Title is incorrect");
        }
        if(player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10000000)){
            throw new RequestException("Experience is incorrect");
        }

        if (player.getBirthday() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(player.getBirthday());
            if(calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) > 3000){
                throw new RequestException("Date is incorrect");
            }
        }
    }

    private Integer calculateLevel(Player player){
        int level = (int) (Math.sqrt(2500+(200*player.getExperience()))-50)/100;
        return level;
    }

    private Integer calculateUntilNextLevel(Player player){
        int untilNextLevel = 50 * (player.getLevel()+1) * (player.getLevel()+2) - player.getExperience();
        return untilNextLevel;
    }



    @Override
    public Specification<Player> filterByName(String name) {
        return (root, criteriaQuery, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> filterByTitle(String title) {
        return (root, criteriaQuery, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> filterByRace(Race race) {
        return (root, criteriaQuery, criteriaBuilder) ->
                race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> filterByProfession(Profession profession) {
        return (root, criteriaQuery, criteriaBuilder) ->
                profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> filterByBirthday(Long after, Long before) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(after == null && before==null)
                return null;
            if(after == null){
                Date beforeCurrent = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), beforeCurrent);
            }
            if(before == null){
                Date afterCurrent = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), afterCurrent);
            }
            Date beforeCurrent = new Date(before);
            Date afterCurrent = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), afterCurrent, beforeCurrent);
        };
    }

    @Override
    public Specification<Player> filterByBanned(Boolean banned) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(banned == null) {
                return null;
            }
            if(banned){
                return criteriaBuilder.isTrue(root.get("banned"));
            }
            else
                return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    @Override
    public Specification<Player> filterByExperience(Integer min, Integer max) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(min == null && max == null) return null;
            if(min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if(max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> filterByLevel(Integer min, Integer max) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(min == null && max == null) return null;
            if(min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if(max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    @Override
    public Long checkId(String id) {
        if(id == null || id.equals("") || id.equals("0")){
            throw new RequestException("Id is incorrect");
        }
        try{
            Long currentId = Long.parseLong(id);
            return currentId;
        }
        catch (NumberFormatException e){
            throw new RequestException("Id is incorrect", e);
        }
    }


}
