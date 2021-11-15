package com.jwt.radis.repository;

import com.jwt.radis.model.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member findMember = em.find(Member.class, id);
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

//    public List<Member> findAll_Querydsl() {
//        return queryFactory.selectFrom(member)
//                .fetch();
//    }

    public Optional<Member> findByUsername(String username) {
        return Optional.ofNullable(em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getSingleResult());
    }

//    public List<Member> findByUsername_Querydsl(String username) {
//        return queryFactory.selectFrom(member)
//                .where(member.username.eq(username))
//                .fetch();
//    }
//
//    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
//
//        BooleanBuilder builder = new BooleanBuilder();
//        if (hasText(condition.getUsername())) {
//            builder.and(member.username.eq(condition.getUsername()));
//        }
//
//        if (hasText(condition.getTeamName())) {
//            builder.and(team.name.eq(condition.getTeamName()));
//        }
//
//        if (condition.getAgeGoe() != null) {
//            builder.and(member.age.goe(condition.getAgeGoe()));
//        }
//
//        if (condition.getAgeLoe() != null) {
//            builder.and(member.age.loe(condition.getAgeLoe()));
//        }
//
//
//        return queryFactory
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(builder)
//                .fetch();
//    }
//
//    public List<MemberTeamDto> search(MemberSearchCondition condition) {
//
//        QuerydslRepositorySupport querydslRepositorySupport;
//        return queryFactory
//                .select(new QMemberTeamDto(
//                        member.id.as("memberId"),
//                        member.username,
//                        member.age,
//                        team.id.as("teamId"),
//                        team.name.as("teamName")))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .fetch();
//    }
//
//    public List<Member> searchMember(MemberSearchCondition condition) {
//        return queryFactory
//                .selectFrom(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getUsername()),
//                        ageBetween(condition.getAgeGoe(), condition.getAgeGoe()))
//                .fetch();
//    }

    //  private BooleanExpression ageBetween(int ageLoe, int ageGoe) {
//        return ageGoe(ageLoe).and(ageGoe(ageGoe));
//    }

//    private BooleanExpression ageLoe(Integer ageLoe) {
//        return ageLoe != null ? member.age.loe(ageLoe) : null;
//    }
//
//    private BooleanExpression ageGoe(Integer ageGoe) {
//        return ageGoe != null ? member.age.goe(ageGoe) : null;
//    }
//
//    private BooleanExpression teamNameEq(String teamName) {
//        return isEmpty(teamName) ? null : team.name.eq(teamName);
//    }
//
//    private BooleanExpression usernameEq(String username) {
//        return isEmpty(username) ? null : member.username.eq(username);
//    }


}
