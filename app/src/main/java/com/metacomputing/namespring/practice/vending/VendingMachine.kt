package com.metacomputing.namespring.practice.vending

class VendingMachine(
    private var coin: Int,
    private var state: State = State.IDLE,
    private var products: ArrayList<Product> = ArrayList()
) {
    init {
        // 제품 등록했다 치자
        register(Product(100, 500, "펩시제로라임", 123123))
        register(Product(65100, 200, "꾀돌이", 123124))
        register(Product(10, 2000, "허쉬초콜렛", 123125))
    }

    fun register(product: Product) {

    }

    fun changeState(_state: State) {
        state = _state.also {
            it.display()
        }
    }

    fun selectProduct(product: Product): String {
        if (product.isAvail()) {
            products.first { p -> p.name == product.name }.apply {
                decrease()
            }
            coin -= product.price
            changeState(State.PROVIDE)
            return "주문완료되었습니다.(${product.name})"
        }
        return ""
    }

    fun insertCoin(amount: Int) {
        coin += amount
        changeState(State.SERVICE)
    }

    fun returnCoin() {
        changeState(State.IDLE)
    }

    fun start() {

    }

    fun stop() {

    }
}