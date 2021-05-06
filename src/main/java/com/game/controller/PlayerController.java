package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.ServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private ServiceImpl playerService;

    public PlayerController(ServiceImpl playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllPlayer(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "race", required = false) Race race,
                                     @RequestParam(value = "profession", required = false) Profession profession,
                                     @RequestParam(value = "after", required = false) Long after,
                                     @RequestParam(value = "before", required = false) Long before,
                                     @RequestParam(value = "banned", required = false) Boolean banned,
                                     @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                     @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                     @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                     @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                     @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                     @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable =  PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.findAll(Specification.where(playerService.filterByName(name))
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByBirthday(after, before))
                .and(playerService.filterByBanned(banned))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel)), pageable).getContent();
    }

    @RequestMapping(value = "/players/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getPlayerCount(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "title", required = false) String title,
                                  @RequestParam(value = "race", required = false) Race race,
                                  @RequestParam(value = "profession", required = false) Profession profession,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "banned", required = false) Boolean banned,
                                  @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                  @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                  @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                  @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                  @RequestParam(value = "order", required = false) PlayerOrder order,
                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber){
        return playerService.findAll(Specification.where(playerService.filterByName(name))
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByBirthday(after, before))
                .and(playerService.filterByBanned(banned))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel))).size();
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody Player player){
        return playerService.addPlayer(player);
    }

    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable("id") String id){
        Long currentId = playerService.checkId(id);
        return playerService.getPlayer(currentId);
    }

    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Player updatePlayer(@PathVariable("id") String id, @RequestBody Player player){
        Long currentId = playerService.checkId(id);
        Player player1 = playerService.editPlayer(currentId, player);
        System.out.println(player1);
        return player1;
    }

    @DeleteMapping("players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable("id") String id){
        Long currentId = playerService.checkId(id);
        playerService.deletePlayer(currentId);
    }
}
