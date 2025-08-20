package com.metacomputing.namespring.practice.vending.art

class VendingMachine(
    val cacheManager: CacheManager,
    val productManager: ProductManager
) {
    fun insert(coin: Int) {
        cacheManager.add(coin)
        display("입금됨: ${cacheManager.currentCache}")
    }

    fun order(row: Int, col: Int) {
        val currentCoin = cacheManager.currentCache
        val selectedProduct = productManager.products[row][col]
        if (currentCoin >= selectedProduct.price) {
            cacheManager.currentCache -= selectedProduct.price
            productManager.subtract(row, col, 1)
        }
        display("주문 완료! $selectedProduct")
    }

    fun display(str: String) {
        print(str)
    }

    fun returnChange() = cacheManager.returnChange()
}