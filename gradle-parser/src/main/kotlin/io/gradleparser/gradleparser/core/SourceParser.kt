package io.gradleparser.gradleparser.core

import org.apache.commons.lang.StringUtils
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs


class SourceParser internal constructor(data: String) : AutoCloseable {
    private val scanner: Scanner = Scanner(data)
    val nodes: MutableList<Node> = mutableListOf()

    companion object {
        val INDENTATION_PATTERN: Pattern = "    ".toPattern()
    }

    private fun countNumberOfIndentationSpaces(s: String): Int {
        val matcher: Matcher = INDENTATION_PATTERN.matcher(s)
        var count = 0
        while (matcher.find()) {
            count++
        }
        return count
    }

    private val line: String?
        get() {
            if (scanner.hasNextLine()) {
                val line: String = scanner.nextLine()
                return if (StringUtils.isEmpty(line.trim { it <= ' ' })) {
                    null
                } else line
            }
            return null
        }

    fun walkTree() {
        val root = Node("root", -1)
        val stack = Stack<Node>()

        var oldLineLevel = 0
        var currentLine = line
        while (currentLine != null) {
            val currentLineLevel = countNumberOfIndentationSpaces(currentLine)
            val spaceDiff = currentLineLevel - oldLineLevel
            val data = currentLine.substring(currentLine.indexOf("---") + 4).trim { it <= ' ' }

            parseCurrentLineData(root, stack, currentLineLevel, spaceDiff, data)

            oldLineLevel = currentLineLevel
            currentLine = line
        }
    }

    private fun parseCurrentLineData(root: Node, stack: Stack<Node>, currentLineLevel: Int, spaceDiff: Int, data: String) {
        if (spaceDiff >= 0) {
            when {
                currentLineLevel == 0 -> handleRootLevelNode(root, stack, currentLineLevel, data)
                spaceDiff == 0 -> handleSameLevelNode(stack, currentLineLevel, data)
                else -> handleNewNode(stack, currentLineLevel, data, stack.peek())
            }
        } else {
            removeNonParentNodesFromStack(stack, spaceDiff)
            when {
                !stack.empty() -> handleNewNode(stack, currentLineLevel, data, stack.peek())
                else -> handleNewNode(stack, currentLineLevel, data, root)
            }
        }
    }

    private fun removeNonParentNodesFromStack(stack: Stack<Node>, spaceDiff: Int) {
        // need to pop abs(spaceDiff) elements from the stack to get to the parent
        var numberOfPops = if (abs(spaceDiff) > 0) abs(spaceDiff) + 1 else abs(spaceDiff)
        while (numberOfPops > 0 && !stack.empty()) {
            stack.pop()
            numberOfPops--
        }
    }

    private fun handleNewNode(stack: Stack<Node>, currentLineLevel: Int, data: String, peek: Node) {
        val newNode = Node(data, currentLineLevel, peek)
        stack.push(newNode)
        nodes.add(newNode)
    }

    private fun handleSameLevelNode(stack: Stack<Node>, currentLineLevel: Int, data: String) {
        stack.pop()
        handleNewNode(stack, currentLineLevel, data, stack.peek())
    }

    private fun handleRootLevelNode(root: Node, stack: Stack<Node>, currentLineLevel: Int, data: String) {
        val newNode = Node(data, currentLineLevel, root)
        while (!stack.empty()) {
            stack.pop()
        }
        stack.push(newNode)
        nodes.add(newNode)
    }

    @Throws(Exception::class)
    override fun close() {
        scanner.close()
    }

}