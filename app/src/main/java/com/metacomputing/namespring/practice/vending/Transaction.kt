package com.metacomputing.namespring.practice.vending

class Transaction(
    time: Long,
    productId: Int,
    productName: String, //**1
    price: Int,
    paymentAmount: Int,
    change: Int
) {

}

// 중요!! OOP 사고를 할 때 매우 중요한것!!
// 체크리스트 추가: 클래스간 중복되는 상태나 행동이 존재해서는 절대 안된다...................... 매우 심각한 문제가 나중에 발생하게 됨......
// 진짜 조심해야한다....ㄷ\
// 특히 데이터 처리를 하는 모듈에서는 정말정말 중요하고 실수하면 위험...
// 동일한 역할을 하는 값이 여러군데 동시에 지역변수로 선언되어있다면 데이터 불일치가 발생한다...
// 예. 디아블로2 골드 복사
// UI 코드: showGold: Int = 1000000
// 내부 서버/클라이언트 엔진 코드: playerGold: Int = 56000
// 유저 화면: 백만원
// bank.subtract(showGold)
// playGold = 0인데 subtract해서 구매한 아이템은 실제로 백만원.,.
// 결론: 버그로 5만6천원에 100만원짜리 구매한셈..
// -> "중복된 데이터를 여러군데서 같이 정의하지마라."
