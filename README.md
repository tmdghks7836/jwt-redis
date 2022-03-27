# jwt-redis

## jwt 액세스토큰 재발급 요청
 기존에 가지고있는 액세스토큰 기한이 지나야 재발급을 받을 수 있습니다. 
 
 
 
 
작성  예제
# 공급 도서 API

------------
#  dependencies
  + lombok
  + jpa
  + spring-boot-starter-test
  + querydsl-jpa
  + springfox-swagger
  + h2database
  + mapstruct
------------

# swagger API

+  /swagger-ui.html

## 계약 업체, 공급 도서 API
### Contractor Controller

  + GET /api/v1/contractors 업체 목록 조회
  + GET /api/v1/contractors/{contractorId}/supplies/books 계약 업체별 공급된 도서 조회
  + GET /api/v1/contractors/supplies 공급 도서 목록 조회(공급 내역, 도서 상세 내역 포함)
  + GET /api/v1/contractors/supplies/{supplyId} 공급 도서 상세 조회(공급 내역, 도서 상세 내역 포함)
  + GET /api/v1/contractors/supplies/books 공급된 도서 중 특정 저자가 쓴 도서를 조회

## 기본 도서 API
### Book Controller

  + GET /api/v1/books 도서 조회
  + POST /api/v1/books 도서 등록
  + PUT /api/v1/books/{id} 도서 수정

------------

### [요건]


  + 1. 발행일자가 ‘2018’ 이전 건은 ‘절판’ 상태값으로 조회되도록 한다.
  + 2. 할인율이 적용 된 할인 단가도 조회되는 필드를 추가한다.
  ```
    #판매 상태값: salesStatus (SOLD_OUT, PROCEEDING)
    #할인 단가: discountPrice
     public SupplyResponse(Supply supply,
                          BookDiscountStrategy bookDiscountStrategy,
                          BookSalesStrategy bookSalesStrategy) {


        this.supplyId = supply.getId();
        this.supplyDateTime = supply.getSupplyDateTime();
        this.books = supply.getSupplyBookMaps().stream()
                .map(supplyBookMap -> {

                    Book book = supplyBookMap.getBook();
                    long discountPrice = bookDiscountStrategy.calculation(book);
                    SalesStatus salesStatus = bookSalesStrategy.getSalesStatus(book);

                    return BookMapper.INSTANCE.entityToDetailDto(book, discountPrice, salesStatus);
                })
                .collect(Collectors.toList());
    }
     
  ```
