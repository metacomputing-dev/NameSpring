package com.metacomputing.namespring.practice

import com.metacomputing.namespring.practice.lol.Champion
import com.metacomputing.namespring.practice.lol.Game
import com.metacomputing.namespring.practice.lol.Item
import com.metacomputing.namespring.practice.lol.Skill
import com.metacomputing.namespring.practice.lol.Team
import com.metacomputing.namespring.practice.vending.Product
import com.metacomputing.namespring.practice.vending.VendingMachine

// 1. 클래스를 정의해보자
// 2. 클래스의 상태와 행동을 정의해보자
// **메세징, 클래스간 상호작용, 관계를 정의**
// 3. 클래스간 메세징을 처리해보자.

// 피드백: 롤
// 1. 클래스의 정의는 좋았으나 클래스 간 "관계"에 대한 고민을 하는 연습이 필요
// 2. 아직까지 SRP가 온전하지 못했던 점 의식 연습 보완 필요 (Item -> Champion 내부 데이터를 변경하더라.)
// 3. 공통된 속성을 추출하고 추상화 및 다형성 구축 연습 습관화 필요: Champion은 뽑았는데, 제드, 애쉬 클래스는 없었음
//   - 공통된 속성과 값은 부모에서, 차별화 속성(스킬, 능력치값 등)은 자식에서

// 피드백: 자판기 구현: 실제 벤딩머신 시스템에 설치할 프로그램이라고 생각해서 짜자
// 1. 롤처럼 정의(책임, 상태, 행동, +@관계)를 명확히 연습하기
// 2. 중요점: 중복된 데이터 정의 절대 X
// 3. 시그너쳐(클래스간 명확한 관계정립) 연습하기
//

class ExampleMain {
    fun runLolApp() {
        val ashe = Champion("애쉬", 450, 100, 40).apply {
            // add(Skill("표창", 100, 20))
            add(Item("롱소드", 10))
        }

        val xed = Champion("제드", 450, 100, 40).apply {
            add(Skill("표창", 100, 20))
            add(Item("롱소드", 10))
        }

        val red = Team("레드", arrayListOf(ashe))

        val blue = Team("블루", arrayListOf(xed))

        val game = Game(red = red, blue = blue)
        game.start()
    }

    fun runVendingMachineApp() {
        val vm = VendingMachine(0)
        vm.start()
        vm.insertCoin(1000)
        //vm.selectProduct(vec2(1,3))
        vm.selectProduct(Product(1000, 1, "펩시제로", 123123))
        vm.returnCoin()
        vm.stop()
    }
}