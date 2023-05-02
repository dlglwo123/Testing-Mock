package com.codestates.homework;

import com.codestates.member.dto.MemberDto;
import com.codestates.member.dto.MemberPostDto;
import com.codestates.member.entity.Member;
import com.codestates.member.mapper.MemberMapper;
import com.codestates.member.repository.MemberRepository;
import com.codestates.member.service.MemberService;
import com.codestates.stamp.Stamp;
import com.codestates.utils.UriCreator;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.sun.javadoc.MemberDoc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

    @MockBean // 가짜 객체 주입
    private MemberService memberService;

    @MockBean // 가짜 객체 주입
    private MemberMapper mapper;

    @Test
    void patchMemberTest() throws Exception {
        //given  // 아래있는 값을 수정할 것이다.
       MemberDto.Patch patch = new MemberDto.Patch(1,"이광수","010-2222-2222", Member.MemberStatus.MEMBER_ACTIVE);

        // 수정되는 응답값이 맞으면 성공이다.
        MemberDto.response response =
                MemberDto.response.builder()
                        .memberId(1L)
                        .name("이광수")
                        .phone("010-2222-2222")
                        .memberStatus(Member.MemberStatus.MEMBER_ACTIVE)
                        .stamp(new Stamp())
                        .build();


       given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());

       given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());

       given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

       String content = gson.toJson(patch);

       //when

       ResultActions actions =
               mockMvc.perform(
                       patch("/v11/members/{member-id}",1)
                               .accept(MediaType.APPLICATION_JSON)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(content)
               );

       // then
       actions
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.memberId").value(patch.getMemberId()))
               .andExpect(jsonPath("$.data.name").value(patch.getName()))
               .andExpect(jsonPath("$.data.phone").value(patch.getPhone()));

    }

    @Test
    void getMemberTest() throws Exception {
        // given
        // 1. 멤버 생성
        String email = "Test@naver.com" , name = "이희재" , phone = "010-2356-3234";
        Member member = new Member(email,name,phone);
        member.setMemberId(1L);

        // Controller에 대한 테스트기 떄문에 Entity -> Dto 클래스로 변경
        MemberDto.response response = MemberDto.response.builder()
                .email(email)
                .phone(phone)
                .name(name)
                .memberStatus(Member.MemberStatus.MEMBER_ACTIVE)
                .stamp(new Stamp())
                .build();

        String content = gson.toJson(response);

        // 실질적으로 필요하지 않기 때문에
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/").build().toUri();


        // when

        ResultActions actions =
                mockMvc.perform(
                        get(uri + "{member-id}",1)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(member.getEmail()));


    }

    @Test
    void getMembersTest() throws Exception {

        Member member = new Member("Test@test.com","Test","000-0000-0000");
        member.setStamp(new Stamp());
        member.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        Member member1 = new Member("Test1@test.com","Test1","111-1111-1111");
        member1.setStamp(new Stamp());
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members").build().toUri();

        Page<Member> memberPage =
                new PageImpl<>(List.of(member,member1)
                , PageRequest.of(0,10, Sort.by("memberId").descending()),2);


        List<MemberDto.response> responses =
                List.of(new MemberDto.response(1L,
                                "Test@test.com",
                        "Test",
                        "000-0000-0000",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()),
                        new MemberDto.response(2L,
                        "Test1@test.com",
                        "Test1",
                        "111-1111-1111",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp())
                );


        //stubbing by Mockito

        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(memberPage);

        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);


        String page = "1";
        String size = "10";
        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page",page);
        queryParams.add("size",size);

    //when
        ResultActions actions =
                mockMvc.perform(
                        get(uri)
                                .params(
                                        queryParams
                                )
                                .accept(MediaType.APPLICATION_JSON)
                );

        //then
        MvcResult result = actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(), is(2));
    }

    @Test
    void deleteMemberTest() throws Exception {
        // given
        long memberId = 1L;

        // memberService 객체의 deleteMember() 메서드를 호출할 때 그 결과로 아무런 작업도 수행하지 않고록 모킹한다.
        // -> 즉 데이터 베이스에 영향을 주지 않고 동작만으로 테스트하는 것이라고 생각하면 된다.
        doNothing().when(memberService).deleteMember(memberId);

        //when
        ResultActions actions = mockMvc.perform(delete("/v11/members/" + memberId));

        //then
        actions.andExpect(status().isNoContent());
    }
}
