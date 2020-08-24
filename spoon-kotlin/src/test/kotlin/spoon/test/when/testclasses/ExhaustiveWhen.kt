package spoon.test.`when`.testclasses

class ExhaustiveWhen {
    val branch = Branches.Branch1;
    fun m() {
        when(branch) {
            Branches.Branch1 -> {}
            Branches.Branch2 -> {}
        }
    }
}

enum class Branches {
    Branch1, Branch2;
}