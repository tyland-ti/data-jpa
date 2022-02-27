package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void CRUDtest() {

        Team team1 = new Team("TeamA");
        Team team2 = new Team("TeamB");
        Team team3 = new Team("TeamC");
        Team saveTeam1 = teamRepository.save(team1);
        Team saveTeam2 = teamRepository.save(team2);
        Team saveTeam3 = teamRepository.save(team3);

        assertThat(team1.getName()).isEqualTo(saveTeam1.getName());
        assertThat(team2.getName()).isEqualTo(saveTeam2.getName());

        List<Team> all = teamRepository.findAll();
        long count = teamRepository.count();

        assertThat(all.stream().count()).isEqualTo(3);
        assertThat(count).isEqualTo(3);

        teamRepository.delete(team1);

        long count1 = teamRepository.count();
        assertThat(count1).isEqualTo(2);

        List<Team> allTeam = teamRepository.findAll();
        List<Long> ids = allTeam.stream()
                            .map(Team::getId)
                            .collect(Collectors.toList());

        System.out.printf("ids ==" + ids);
        List<Team> allById = teamRepository.findAllById(ids);
        allById.stream().map(t -> "Team name = " + t.getName()).forEach(System.out::println);


    }
}