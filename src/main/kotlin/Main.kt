import java.awt.Color

val utils = Utils()
val imageHelper = ImageHelper()

private const val INPUT_ERROR = "Can't read input file!"
private const val PASSWORD_ERROR = "Password can't be empty!"
private val END = utils.getBits("\u0000\u0000\u0003")

fun main() {
    var command = getCommand()

    while(command != "exit") {
        runCommand(command)
        command = getCommand()
    }

    println("Bye!")
}

private fun getCommand() = utils.getString("Task (hide, show, exit):")

private fun runCommand(command: String) {
    when (command) {
        "hide" -> hide()
        "show" -> show()
        else -> "Wrong task: $command"
    }.let { println(it) }
}

private fun hide(): String {
    val input = utils.getFile("Input")
    val output = utils.getFile("Output")
    val userMessage = utils.getString("Message to hide:").let { it.ifEmpty { null } } ?: return "Message can't be empty!"
    val password = utils.getPassword() ?: return PASSWORD_ERROR
    val image = imageHelper.getBufferedImage(input) ?: return INPUT_ERROR
    val enoughSpace = image.width * image.height >= userMessage.length * 8 + END.size
    val notLargeEnough = "The input image is not large enough to hold this message."
    val message = if (enoughSpace) "Message saved in ${output.name} image." else return notLargeEnough
    val bits = utils.transformBits(utils.getBits(userMessage), password) + END
    var index = 0

    start@ for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            if (index == bits.size) break@start
            val bit = bits[index++].toInt()
            val color = Color(image.getRGB(x, y)).let { Color(it.red, it.green, it.blue.and(254).or(bit)) }
            image.setRGB(x, y, color.rgb)
        }
    }

    return if (imageHelper.saveImage(image, output)) message else "Can't write to output file!"
}

private fun show(): String {
    val input = utils.getFile("Input")
    val password = utils.getPassword() ?: return PASSWORD_ERROR
    val image = imageHelper.getBufferedImage(input) ?: return INPUT_ERROR
    val message = mutableListOf<Byte>()

    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            message.add((Color(image.getRGB(x, y)).blue % 2).toByte())
            if (message.size >= END.size && message.size % 8 == 0 && message.takeLast(END.size) == END) {
                return "Message:\n" + utils.decodeMessage(message.dropLast(END.size), password)
            }
        }
    }

    return "No message was hidden!"
}