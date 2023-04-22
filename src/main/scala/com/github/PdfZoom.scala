package com.github

import com.lowagie.text.Document
import com.lowagie.text.pdf.{PdfArray, PdfCopy, PdfName, PdfReader, PdfRectangle, SimpleBookmark}
import scopt.OParser

import java.io.{File, FileInputStream, FileOutputStream}
import scala.util.Using

case class Args(
    input: Option[File] = None,
    output: Option[File] = None,
    skipPages: Set[Int] = Set.empty,
    left: Int = 0,
    bottom: Int = 0,
    right: Int = 0,
    top: Int = 0
)

object PdfZoom extends App {

  val builder = OParser.builder[Args]
  val parser = {
    import builder._
    OParser.sequence(
      programName("pdf-zoom"),
      head("pdf-zoom: zooms pages of the document"),
      opt[String]('s', "skip-pages").action { (s, a) =>
        val TwoNumbers = "^(\\d+)-(\\d+)$".r
        val OneNumber = "^(\\d+)$".r
        val builder = Set.newBuilder[Int]
        val parts = s.split(",")
        parts.foreach { part =>
          part.strip() match {
            case TwoNumbers(from, to) =>
              (from.toInt to to.toInt).foreach { i =>
                builder += i
              }

            case OneNumber(value) =>
              builder += value.toInt
          }
        }

        builder.result()
        a.copy(skipPages = builder.result())
      },
      opt[Int]('l', "left").action((i, a) => a.copy(left = i)),
      opt[Int]('b', "bottom").action((i, a) => a.copy(bottom = i)),
      opt[Int]('r', "right").action((i, a) => a.copy(right = i)),
      opt[Int]('t', "top").action((i, a) => a.copy(top = i)),
      arg[File]("input").action((f, a) => a.copy(input = Some(f))),
      arg[File]("output").action((f, a) => a.copy(output = Some(f)))
    )
  }

  OParser.parse(parser, args, Args()).foreach {
    case Args(Some(input), Some(output), skipPages, left, bottom, right, top) =>
      Using.Manager { use =>
        val in = use(new FileInputStream(input))
        val out = use(new FileOutputStream(output))
        val document = use(new Document)
        val reader = use(new PdfReader(in))
        reader.consolidateNamedDestinations()
        val writer = use(new PdfCopy(document, out))(_.close())
        document.open()
        (1 to reader.getNumberOfPages).foreach { pageNumber =>
          if (!skipPages.contains(pageNumber)) {
            val page = reader.getPageN(pageNumber)
            val mediaBox = page.get(PdfName.MEDIABOX).asInstanceOf[PdfArray]
            page.put(
              PdfName.MEDIABOX,
              new PdfRectangle(
                mediaBox.getAsNumber(0).floatValue() + left,
                mediaBox.getAsNumber(1).floatValue() + bottom,
                mediaBox.getAsNumber(2).floatValue() - right,
                mediaBox.getAsNumber(3).floatValue() - top
              )
            )
          }
          writer.addPage(writer.getImportedPage(reader, pageNumber))
        }
        writer.setOutlines(SimpleBookmark.getBookmarkList(reader))
      }

    case _ =>
      sys.error("Unexpected error while parsing arguments")
  }
}
