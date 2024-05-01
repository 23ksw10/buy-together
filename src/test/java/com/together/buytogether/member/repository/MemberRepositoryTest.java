package com.together.buytogether.member.repository;

import com.together.buytogether.member.domain.Gender;
import com.together.buytogether.member.domain.Member;
import com.together.buytogether.member.domain.MemberRepository;
import com.together.buytogether.member.dto.request.RegisterMemberDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MemberRepositoryTest {
    private final String NAME = "name";
    private final String LOGIN_ID = "loginId";
    private final String PASSWORD = "password";
    private final String PHONE_NUMBER = "010-0000-0000";
    private final Gender GENDER = Gender.MALE;
    private final String ADDRESS = "경기도 고양시 덕양구 화정로 27";
    private final String DETAIL_ADDRESS = "625동 1004호";
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("db 테스트")
    public void insert() {
        Member member = createMember().toDomain();
        memberRepository.save(member);
        List<Member> members = memberRepository.findAll();

        assertThat(members).isNotNull().hasSize(1);
    }

    private RegisterMemberDTO createMember() {
        return RegisterMemberDTO.builder()
                .name(NAME)
                .loginId(LOGIN_ID)
                .password(PASSWORD)
                .phoneNumber(PHONE_NUMBER)
                .gender(GENDER)
                .address(ADDRESS)
                .detailAddress(DETAIL_ADDRESS)
                .build();
    }
}
