package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByusername")
    List<Member> findByUserame(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUsers (@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsername();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> finduserDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findnames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);        //List
    Member findMemberByUsername(String username);            //Single
    Optional<Member> findMembersByUsername(String username); //Optional

    //페이징
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    //슬라이스
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    //벌크 수정쿼리
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkUpdateAge(@Param("age") int age);

    //fetch join
    @Query("select m from Member m join fetch m.team t")
    List<Member> findFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(@Param("username") String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

}
