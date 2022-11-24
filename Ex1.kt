import java.io.File
import java.nio.charset.Charset

sealed interface Element{
}

sealed interface TaggedElement : Element{
    // needs to be overridden by each inheriting class:
    val tag:String
    val openTag: String
        get() = "<$tag>"
    val closeTag: String
        get() = "</$tag>"
}
// for the all elements that a simple text as content:
sealed interface TextElement:Element{
    val text:String
}

// to have st like: <li>  Gomaa <\li> within: tag, text properties
sealed interface TaggedTextElement:TaggedElement, TextElement{
}

// base interface for the all tagged elements like <Div>, that may contain any number of further elements:
sealed interface ContainerElement:TaggedElement{
    val elements: List<Element>
}

// the class text:
data class Text(override val text: String) : TextElement{
}

data class Paragraph(override val text: String) :TaggedTextElement{
    override val tag = "p"
}

data class Heading(var level:Int = 1, override val text: String) :TaggedTextElement{
    init {
        if (level !in 1..6){
            println("Error, the level is invalid")
            level = 1
        }
    }
    override val tag = "h$level"
}

data class Div(override val elements: List<Element>) :ContainerElement{
    override val tag = "div"
    // a secondary constructor should be provided: (array to list)
    constructor(vararg parameter:Element) : this(elements = parameter.toList())
}

data class ListItem(override val elements: List<Element>) :ContainerElement{
    override val tag = "li"
    constructor(vararg parameter:Element): this(elements = parameter.toList())
}

data class HTMLList(val ordered:Boolean, override val elements: List<ListItem>) :ContainerElement{
    private var result:String = ""
    init {
        result = if(ordered) "ol"
        else "ul"
    }
    override val tag = result
    constructor(ordered2:Boolean, vararg parameter:ListItem): this(ordered = ordered2, elements = parameter.toList())
}

data class Page(val title:String, val elements: List<Element>){
    constructor(title_2: String, vararg parameter:Element): this (title = title_2, elements = parameter.toList())
}

object HTMLRnderer{
    // accept an element and return its String representation:
    private var level = 4

    private fun render(x:Element){
        val fileName = "EX1.txt"
        val fileObject = File(fileName)

        var spaces = ""
        val onespace = " "


        for (i in 1..level) spaces += onespace

        // the element could be a tagged element or a text one:
        when (x) {
            is Heading -> {
                fileObject.writeText("${spaces}${x.openTag}${x.text}${x.closeTag}")
            }
            is Paragraph -> {
                fileObject.writeText("${spaces}${x.openTag}${x.text}${x.closeTag}")
            }
            is Text -> {
                fileObject.writeText("${spaces}${x.text}")
            }
            is Div -> {
                fileObject.writeText("${spaces}${x.openTag}")
                level +=2
                for (k in x.elements){
                    render(k)
                }
                fileObject.writeText("${spaces}${x.closeTag}")
                level = 4
            }

            is HTMLList -> {
                fileObject.writeText("${spaces}${x.openTag}")
                level +=2
                var start = level
                for (k in x.elements){
                    level = start
                    render(k)
                }
                fileObject.writeText("${spaces}${x.closeTag}")
                level = 4
            }

            is ListItem -> {
                fileObject.writeText("${spaces}${x.openTag}")
                level += 2
                for (k in x.elements){
                    render(k)
                }
                fileObject.writeText("${spaces}${x.closeTag}")
                level = 4
            }

            else -> {
                fileObject.writeText("Not supported")
            }
        }

    }
    fun File.writeText(text: String, charset: Charset = Charsets.UTF_8): Unit{
        val content:String = ""
        println(content+text)
    }
    fun render(x:Page){
        val fileName = "EX1.txt"
        val fileObject = File(fileName)
        // for the title:
        fileObject.writeText("<html>")
        fileObject.writeText("  <head>")
        fileObject.writeText("    <title>${x.title}<\\title>")
        fileObject.writeText("  <\\head>")
        fileObject.writeText("  <body>")
        // here I will do the other calls: x.elements: list of elements:
        for (elem in x.elements){
            render(elem)
        }
        fileObject.writeText("  <\\body>")
        fileObject.writeText("<\\html>")
    }
}

// wraps the String to into the corresponding HTML element:
fun String.text():Text {return Text(this)}
fun String.p():Paragraph { return Paragraph(this) }
fun String.h1():Heading {return Heading(level = 1, text = this)}
fun String.h2():Heading {return Heading(level = 2, text = this)}
fun String.h3():Heading {return Heading(level = 3, text = this)}
fun String.h4():Heading {return Heading(level = 4, text = this)}
fun String.h5():Heading {return Heading(level = 5, text = this)}
fun String.h6():Heading {return Heading(level = 6, text = this)}



fun main(){
    val obj = Page( "My Page", "Welcome to the Kotlin course".h1(), Div( "Kotlin is".p(), HTMLList( true, ListItem( "General-purpose programming language".h3(), HTMLList( false, ListItem( "Backend, Mobile, Stand-Alone, Web, ...".text() ) ) ), ListItem( "Modern, multi-paradigm".h3(), HTMLList( false, ListItem( "Object-oriented, functional programming (functions as first-class citizens, …), etc.".text() ), ListItem( "Statically typed but automatically inferred types".text() ) ) ), ListItem( "Emphasis on conciseness / expressiveness / practicality".h3(), HTMLList( false, ListItem( "Goodbye Java boilerplate code (getter methods, setter methods, final, etc.)".text() ), ListItem( "Common tasks should be short and easy".text() ), ListItem( "Mistakes should be caught as early as possible".text() ), ListItem( "But no cryptic operators as in Scala".text() ) ) ), ListItem( "100% interoperable with Java".h3(), HTMLList( false, ListItem( "You have a Java project? Make it a Java/Kotlin project in minutes with 100% interop".text() ), ListItem( "Kotlin-to-Java as well as Java-to-Kotlin calls".text() ), ListItem( "For example, Kotlin reuses Java’s existing standard library (ArrayList, etc.) and extends it with extension functions (opposed to, e.g., Scala that uses its own list implementations)".text() ) ) ), ) ) )
    HTMLRnderer.render(obj)


}
