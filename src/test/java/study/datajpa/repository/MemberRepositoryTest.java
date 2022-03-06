package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {

        Member member = new Member("testA");
        Member save = memberRepository.save(member);

        Member findMember = memberRepository.findById(save.getId()).get();

        assertThat(member.getId()).isEqualTo(findMember.getId());
        assertThat(member.getUsername()).isEqualTo(findMember.getUsername());

    }

    @Test
    public void queryMethodTest() {
        Member member1 = new Member("membera", 10);
        Member member2 = new Member("memberb", 20);
        Member member3 = new Member("membera", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<Member> findMember = memberRepository.findByUsernameAndAgeGreaterThan("membera", 25);
        assertThat(findMember.get(0).getUsername()).isEqualTo("membera");
        assertThat(findMember.size()).isEqualTo(1);
    }

    @Test
    public void QueryTest() {

        Member member1 = new Member("membera", 10);
        Member member2 = new Member("memberb", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMember = memberRepository.findUsers("membera", 10);

        assertThat(findMember.get(0).getUsername()).isEqualTo("membera");
        assertThat(findMember.size()).isEqualTo(1);

        List<String> username = memberRepository.findUsername();
        username.stream().map(s -> "name = " + s).forEach(System.out::println);

    }

    @Test
    public void testQueryDto() {

        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member member1 = new Member("membera", 10, team);
        Member member2 = new Member("memberb", 20, team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.finduserDto();

        memberDto.stream().map(dto -> "Dto = " + dto.getTeamName()).forEach(System.out::println);
    }

    @Test
    public void findnamesTest() {

        Member member1 = new Member("membera", 10);
        Member member2 = new Member("memberb", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findnames = memberRepository.findnames(Arrays.asList("membera", "memberb"));

        findnames.stream().map(m -> m.getUsername()).forEach(System.out::println);
    }

    @Test
    public void returnTypeTest() {

        Member member1 = new Member("membera", 10);
        Member member2 = new Member("memberb", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //List
        List<Member> listResult = memberRepository.findByUserame("membera");
        assertThat(listResult.get(0).getUsername()).isEqualTo("membera");
        //single
        Member singleResult = memberRepository.findMemberByUsername("membera");
        assertThat(singleResult.getUsername()).isEqualTo("membera");
        //optional
        Optional<Member> optionalResult = memberRepository.findMembersByUsername("membera");

        assertThat(optionalResult.orElseGet(null)).isEqualTo(member1);

    }

    @Test
    public void pagingTest() {
        //given
        memberRepository.save(new Member("멤버1", 10));
        memberRepository.save(new Member("멤버2", 10));
        memberRepository.save(new Member("멤버3", 10));
        memberRepository.save(new Member("멤버4", 10));
        memberRepository.save(new Member("멤버5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        page.map(m -> new MemberDto(m.getId(), m.getUsername(), "AAA"));

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);    //전체 갯수
        assertThat(page.getNumber()).isEqualTo(0);           //현재페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);       //전체 페이지 갯수
        assertThat(page.isFirst()).isTrue();                 //현재 첫번째 페이지 인지
        assertThat(page.hasNext()).isTrue();                 //다음 페이지가 있는지

        //slice 테스트
        Slice<Member> slicePage = memberRepository.findSliceByAge(10, pageRequest);

        List<Member> content2 = slicePage.getContent();

        assertThat(content2.size()).isEqualTo(3);
        //assertThat(slicePage.getTotalElements()).isEqualTo(5);    //전체 갯수
        assertThat(slicePage.getNumber()).isEqualTo(0);           //현재페이지 번호
        //assertThat(slicePage.getTotalPages()).isEqualTo(2);       //전체 페이지 갯수
        assertThat(slicePage.isFirst()).isTrue();                 //현재 첫번째 페이지 인지
        assertThat(slicePage.hasNext()).isTrue();                 //다음 페이지가 있는지

    }

    @Test
    public void bulkUpdateTest() {

        //given
        memberRepository.save(new Member("멤버1", 10));
        memberRepository.save(new Member("멤버2", 12));
        memberRepository.save(new Member("멤버3", 13));
        memberRepository.save(new Member("멤버4", 14));
        memberRepository.save(new Member("멤버5", 15));


        //when
        int updateCount = memberRepository.bulkUpdateAge(11);
        List<Member> all = memberRepository.findAll();
        for(Member m:all) {
            System.out.println("name = "+m.getUsername() + " age = "+m.getAge());
        }

        //then
        assertThat(updateCount).isEqualTo(4);
    }

    @Test
    public void fetchJoinTest() {

        Team save1 = teamRepository.save(new Team("team 1"));
        Team save2 = teamRepository.save(new Team("team 2"));

        memberRepository.save(new Member("멤버1",10,save1));
        memberRepository.save(new Member("멤버2",11,save1));
        memberRepository.save(new Member("멤버3",12,save1));
        memberRepository.save(new Member("멤버4",13,save2));
        memberRepository.save(new Member("멤버5",14,save2));

        em.flush();
        em.clear();

        List<Member> all = memberRepository.findAll();
        for (Member m:all) {
            System.out.println("team name = "+ m.getUsername() + " :: "+m.getTeam().getName());
        }

        List<Member> fetchJoin = memberRepository.findFetchJoin();
        assertThat(fetchJoin.size()).isEqualTo(5);

        List<Member> memberCustom = memberRepository.findMemberCustom();

    }

    @Test
    public void Projections() {

        Member member1 = new Member("user1", 10 );
        Member member2 = new Member("user2", 10 );
        em.persist(member1);
        em.persist(member2);

        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("user1");

        for (UsernameOnly user:result) {
            System.out.println(user.toString());
        }



    }
}