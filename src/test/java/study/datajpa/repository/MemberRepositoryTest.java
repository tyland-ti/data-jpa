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
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

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
}