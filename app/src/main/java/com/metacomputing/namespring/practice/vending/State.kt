package com.metacomputing.namespring.practice.vending

enum class State(
    val displayString: String
) { // 책임 상태 행동 ????
    IDLE("대기중입니다."),
    SERVICE("입금중입니다"),
    PROVIDE("주문하신 제품 반환중입니다.");
    //**2
    // 돈투입
    // 제품 선택
    // 제품 제공
    // 거스름돈 반환
    // 상태 디스플레이

    fun display(): String {
        return displayString
    }
}