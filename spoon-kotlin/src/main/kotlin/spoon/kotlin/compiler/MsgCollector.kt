package spoon.kotlin.compiler

/**
 * Temporary simple printing/logging during early development. Will be removed later
 */
internal abstract class MsgCollector {
    val messages = ArrayList<Message>()

    open fun report(m : Message) {
        messages.add(m)
    }
}

enum class MessageType { COMMON, WARN }
data class Message(val msg : String, val type : MessageType)

internal class PrintingMsgCollector : MsgCollector() {
    override fun report(m : Message) {
        super.report(m)
        if(m.type == MessageType.WARN) {
            System.err.println(m.msg)
        }
        else {
            println(m.msg)
        }

    }
}

internal class SilentMsgCollector() : MsgCollector()
