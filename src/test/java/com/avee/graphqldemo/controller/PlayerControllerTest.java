package com.avee.graphqldemo.controller;

import com.avee.graphqldemo.model.Player;
import com.avee.graphqldemo.model.Team;
import com.avee.graphqldemo.service.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.junit.jupiter.api.Assertions.*;

@Import(PlayerService.class)
@GraphQlTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    GraphQlTester tester;

    @Autowired
    PlayerService playerService;

    @Test
    void testFindAllPlayerShouldReturnAllPlayers() {
        String document = """
                query MyQuery {
                  findAll {
                    id
                    name
                    team
                  }
                }
                """;

        tester.document(document)
                .execute()
                .path("findAll")
                .entityList(Player.class)
                .hasSizeGreaterThan(3);
    }

    @Test
    void testValidIdShouldReturnPlayer() {
        String document = """
                query findOne($id: ID) {
                  findOne(id: $id) {
                    id
                    name
                    team
                  }
                }
                """;

        tester.document(document)
                .variable("id",1)
                .execute()
                .path("findOne")
                .entity(Player.class)
                .satisfies(player -> {
                    Assertions.assertEquals("Novak Djokovic", player.name());
                    Assertions.assertEquals(Team.SERBIA, player.team());
                });
    }

    @Test
    void testInvalidIdShouldReturnNull() {
        String document = """
                query findOne($id: ID) {
                  findOne(id: $id) {
                    id
                    name
                    team
                  }
                }
                """;

        tester.document(document)
                .variable("id", 100)
                .execute()
                .path("findOne")
                .valueIsNull();
    }

    @Test
    void testShouldCreateNewPlayer() {
        int currentCount = playerService.findAll().size();
        String document = """
                mutation create($name: String, $team: Team) {
                  create(name: $name, team: $team) {
                    id
                    name
                    team
                  }
                }
                """;

        tester.document(document)
                .variable("name","Steffie Graff")
                .variable("team", Team.GERMANY)
                .execute()
                .path("create")
                .entity(Player.class)
                .satisfies(player -> {
                    Assertions.assertEquals("Steffie Graff", player.name());
                    Assertions.assertEquals(Team.GERMANY, player.team());
                });

        Assertions.assertEquals(currentCount + 1 , playerService.findAll().size());
    }

    @Test
    void testShouldUpdateExistingPlayer() {
        String document = """
                mutation update($id: ID, $name: String, $team: Team) {
                  update(id: $id, name: $name, team: $team) {
                    id
                    name
                    team
                  }
                }
                """;
        tester.document(document)
                .variable("id",3)
                .variable("name","Updated NAOMI OSAKA")
                .variable("team", Team.AUSTRALIA)
                .execute()
                .path("update")
                .entity(Player.class);

        Player updatePlayer = playerService.findOne(3).get();
        Assertions.assertEquals("Updated Naomi Osaka", updatePlayer.name());
        Assertions.assertEquals(Team.AUSTRALIA, updatePlayer.team());
    }

    @Test
    void testShouldRemovePlayerWithValidId() {
        int currentCount = playerService.findAll().size();

        String document = """
                mutation delete($id: ID) {
                  delete(id: $id) {
                    id
                    name
                    team
                  }
                }
                """;

        tester.document(document)
                .variable("id", 3)
                .executeAndVerify();

        Assertions.assertEquals(currentCount -1, playerService.findAll().size());
    }
}