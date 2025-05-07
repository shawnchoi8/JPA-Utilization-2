package jpabook.jpashop.service;

import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * Sign Up (회원 가입)
     */
    @Transactional
    public Long join(Member member) {

        validateDuplicateMember(member); // Check for Duplicate User (Assuming users with the same name are duplicates)
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("User already exists");
        }
    }

    /**
     * find user (회원 조회)
     */
    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //단건 조회
    public Member findById(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
