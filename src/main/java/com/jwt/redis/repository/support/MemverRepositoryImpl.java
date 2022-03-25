package com.jwt.redis.repository.support;

import com.jwt.redis.model.entity.Member;
import com.jwt.redis.model.entity.QMember;
import com.jwt.redis.repository.support.custom.MemberRepositoryCustom;
import org.springframework.stereotype.Repository;

@Repository
public class MemverRepositoryImpl extends QuerydslRepositorySupportBasic implements MemberRepositoryCustom {

    private final QMember qBuilding = QMember.member;

    public MemverRepositoryImpl() {
        super(Member.class);
    }


//    @Override
//    public BuildingMgtSnDto findMgtSnListBySearchCriteria(BuildingSearchCriteria searchCriteria) {
//
//        List<String> bdMgtSnList = getQueryFactory()
//                .select(qBuilding.bdMgtSn)
//                .from(qBuilding)
//                .where(
//                        baseYmEq(searchCriteria.getBaseYm()),
//                        sidoNmEq(searchCriteria.getSidoNm()),
//                        sigNmEq(searchCriteria.getSigNm()),
//                        rnEq(searchCriteria.getRn()),
//                        buldMnnmEq(searchCriteria.getBuldMnnm()),
//                        buldSlnoEq(searchCriteria.getBuldSlno())
//                ).fetch();
//
//        return new BuildingMgtSnDto(searchCriteria.getBaseYm(), bdMgtSnList);
//    }

//    /**
//     * 시도 구분
//     */
//    public BooleanExpression sidoNmEq(String sidoNm) {
//        return isEmpty(sidoNm) ? null : qBuilding.sidoNm.eq(sidoNm);
//    }
//

}
