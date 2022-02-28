package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional //테스트 끝나고 모두 롤백, 밑에 rollback false 해주면 롤백이 되지 않는다
//@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {

        Member member = new Member("testname");
        Member save = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(save.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(findMember);

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Optional<Member> findMemeber1 = memberJpaRepository.findById(member1.getId());
        if(findMemeber1.isPresent()) {
            assertThat(findMemeber1.get().getUsername()).isEqualTo(member1.getUsername());
        }

        Optional<Member> findMemeber2 = memberJpaRepository.findById(member2.getId());
        if(findMemeber2.isPresent()) {
            assertThat(findMemeber2.get().getUsername()).isEqualTo(member2.getUsername());
        }

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

    }

    @Test
    public void findByUsernameAndAgeTest() {
        Member member1 = new Member("membera", 10);
        Member member2 = new Member("memberb", 20);
        Member member3 = new Member("membera", 30);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);

        List<Member> findMember1 = memberJpaRepository.findByUsernameAndAge("membera", 5);
        List<Member> findMember2 = memberJpaRepository.findByUsernameAndAge("membera", 15);

        assertThat(findMember1.size()).isEqualTo(2);
        assertThat(findMember2.size()).isEqualTo(1);

    }

    @Test
    public void pagingTest() {
        //given
        memberJpaRepository.save(new Member("멤버1", 10));
        memberJpaRepository.save(new Member("멤버2", 10));
        memberJpaRepository.save(new Member("멤버3", 10));
        memberJpaRepository.save(new Member("멤버4", 10));
        memberJpaRepository.save(new Member("멤버5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long TotalCount = memberJpaRepository.totalCount(10);

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(TotalCount).isEqualTo(5);
    }

}