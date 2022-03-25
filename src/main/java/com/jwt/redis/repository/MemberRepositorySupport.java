package com.jwt.redis.repository;

import com.jwt.redis.model.entity.Member;
import com.jwt.redis.model.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    private final QMember qMember = QMember.member;

    public MemberRepositorySupport(JPAQueryFactory queryFactory){
        super(Member.class);
        this.queryFactory = queryFactory;

    }

    public void save(Member member) {
        getEntityManager().persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member findMember = getEntityManager().find(Member.class, id);
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll() {
        return getEntityManager().createQuery("select m from Member m", Member.class)
                .getResultList();
    }

//    public List<Member> findAll_Querydsl() {
//        return queryFactory.selectFrom(member)
//                .fetch();
//    }

    public Optional<Member> findByUsername(String username) {

        Member member = queryFactory.selectFrom(qMember)
                .where(qMember.username.eq(username))
                .fetchOne();

        return Optional.ofNullable(member);
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
