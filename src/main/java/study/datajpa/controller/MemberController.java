package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @GetMapping("/member/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Optional<Member> member = memberRepository.findById(id);

        return member.map(Member::getUsername).orElse("not found");
    }

    //페이징과 정렬
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    //페이징 DTO로 변환
    @GetMapping("/membersDto")
    public Page<MemberDto> listReturnDto(@PageableDefault(size = 5) Pageable pageable) {
        return  memberRepository.findAll(pageable)
                .map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));
    }


    @PostConstruct
    public void init() {

        Team team = teamRepository.save(new Team("TeamA"));

        for (int i = 0; i < 30; i++) {
            memberRepository.save(new Member("user" + i, i, team));
        }
    }
}
