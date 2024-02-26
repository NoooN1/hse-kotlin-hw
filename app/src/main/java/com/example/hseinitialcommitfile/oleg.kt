import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val expenseTracker = ExpenseTracker()

    loop@ while (true) {
        println("\nAvailable commands: \n1. Show Balance \n2. Add Expense \n3. Add Income \n4. Cancel Last Transaction \n5. Show History \n6. Add Category \n7. Show Balance By Category \n8. Exit")
        print("Enter command: ")

        when (readLine()) {
            "1" -> expenseTracker.showBalance()
            "2" -> expenseTracker.addTransaction("expense")
            "3" -> expenseTracker.addTransaction("income")
            "4" -> expenseTracker.cancelLastTransaction()
            "5" -> expenseTracker.showHistory()
            "6" -> expenseTracker.addCategory()
            "7" -> expenseTracker.showBalanceByCategory()
            "8" -> break@loop
            else -> println("Unsupported command.")
        }
    }
}

class ExpenseTracker {
    private var balance: Double = 0.0
    private val history = mutableListOf<Transaction>()
    private val categories = mutableSetOf("General")

    private fun getAmountAndCategory(): Pair<Double, String>? {
        print("Enter amount: ")
        val amount = readLine()?.toDoubleOrNull()
        if (amount == null) {
            println("Invalid amount.")
            return null
        }

        println("Available categories: ${categories.joinToString(", ")}")
        print("Enter category: ")
        val category = readLine() ?: "General"
        if (category !in categories) {
            println("Invalid category. Using 'General'.")
            return amount to "General"
        }

        return amount to category
    }

    fun addTransaction(type: String) {
        val (amount, category) = getAmountAndCategory() ?: return

        when (type) {
            "expense" -> {
                balance -= amount
                history.add(Transaction(amount, LocalDateTime.now(), type, category))
                println("Expense added.")
            }
            "income" -> {
                balance += amount
                history.add(Transaction(amount, LocalDateTime.now(), type, category))
                println("Income added.")
            }
        }
    }

    fun showBalance() {
        println("Current balance: $balance")
    }

    fun cancelLastTransaction() {
        if (history.isNotEmpty()) {
            val lastTransaction = history.removeAt(history.size - 1)
            if (lastTransaction.type == "expense") balance += lastTransaction.amount else balance -= lastTransaction.amount
            println("Last transaction cancelled.")
        } else {
            println("No transactions to cancel.")
        }
    }

    fun showHistory() {
        if (history.isEmpty()) {
            println("No transactions recorded.")
        } else {
            println("Transaction History:")
            history.forEach {
                println("${it.type.capitalize()}: ${it.amount} on ${it.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)} in category '${it.category}'")
            }
        }
    }

    fun addCategory() {
        print("Enter new category name: ")
        val newCategory = readLine() ?: return
        if (categories.add(newCategory)) {
            println("Category '$newCategory' added.")
        } else {
            println("Category already exists.")
        }
    }

    fun showBalanceByCategory() {
        if (categories.isEmpty()) {
            println("No categories defined.")
            return
        }
        categories.forEach { category ->
            val total = history.filter { it.category == category }.sumOf { if (it.type == "expense") -it.amount else it.amount }
            println("Total for '$category': $total")
        }
    }

    data class Transaction(val amount: Double, val dateTime: LocalDateTime, val type: String, val category: String)
}