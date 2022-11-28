import java.io.File
import kotlin.experimental.xor

class Utils {
    fun transformBits(message: List<Byte>, password: String) = getBits(password).let { keyBits ->
        message.mapIndexed {
                index, byte -> byte xor keyBits[index % keyBits.size]
        }
    }

    fun getBits(message: String) = message.map { it.code }.map {
        it.toString(2).padStart(8, '0').map { char ->
            char.digitToInt().toByte()
        }
    }.flatten()

    fun decodeMessage(message: List<Byte>, password: String) = transformBits(message, password).joinToString("").chunked(8).map {
        it.toInt(2).toChar()
    }.joinToString("")

    fun getPassword() = getString("Password:").let {
        it.ifEmpty {
            null
        }
    }

    fun getFile(type: String) = File(getString("$type image file:"))

    fun getString(message: String): String {
        println(message)

        return readln()
    }
}