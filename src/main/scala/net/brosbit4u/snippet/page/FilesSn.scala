/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.brosbit4u {
  package snippet {

    import _root_.scala.xml.{ NodeSeq, Text }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import net.brosbit4u.model._
    import _root_.net.liftweb.http.{ S, SHtml, FileParamHolder, RequestVar }
    import _root_.net.liftweb.mapper.{ Ascending, OrderBy }
    import net.liftweb.http.js._
    import JsCmds._
    import JE._
    import Helpers._
    import java.awt.image.BufferedImage
    import java.awt.Image
    import javax.imageio.ImageIO
    import java.io.{ File, ByteArrayInputStream, FileOutputStream }
    /**
     * snipet ma dostarczać grafikę na stronę oraz obsługiwać zapis zdjęć i dostarczenie
     * ich do edytora - w przysłości obsługa plików statycznych
     */
    class FilesSn {

      object linkpath extends RequestVar[String]("")
      val pathRootImages = "/home/ms/imagesApp"
      val pathRootFiles = "/home/ms/filesApp"

      def addImg() = {
        var fileHold: Box[FileParamHolder] = Empty
        var mimeType = ""
        def isCorrect = fileHold match {
          case Full(FileParamHolder(_, mime, _, data)) if mime.startsWith("image/") => {
            mimeType = "." + mime.split("/")(1)
            S.notice(mime.toString) //tutaj do dać odczyt mime i rozszerzenie
            true
          }
          case Full(_) => {
            S.error("Nieprawidłowy format pliku!")
            false
          }
          case _ => {
            S.error("Brak pliku?")
            false
          }
        }

        def save() {

          if (isCorrect) {

            val dirRoot: File = new File(pathRootImages)
            val img = model.FileStore.create
            img.path("empty").save //potrzebuje id do wyliczenia hashu
            val subDirPath = ((img.id.is / 200) + 1).toString
            val imgDir = new File(dirRoot, subDirPath)
            //sprawdzam czy katalog do którego pasuje hash istnieje
            if (!imgDir.exists) {
              imgDir.mkdir
            }
            val link = imgDir.getAbsolutePath + "/" + img.id.toString + mimeType
            var imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(fileHold.get.file))
            //tutaj przeskalowanie
            var imBox: Box[BufferedImage] = Empty
            val w = imageBuf.getWidth
            val h = imageBuf.getHeight
            if (w > 500 || h > 500) {
              if (w > h) {
                val im: java.awt.Image = imageBuf.getScaledInstance(500, (h.toDouble * 500.0 / w.toDouble).toInt, Image.SCALE_SMOOTH)
                //val graf2D = imageBuf.createGraphics
                //graf2D.scale(1.0, 500.0/w.toDouble)
                //imageBuf.getGraphics.translate(500, (h.toDouble * 500.0/w.toDouble).toInt)
                imBox = Full(new BufferedImage(500, (h.toDouble * 500.0 / w.toDouble).toInt, BufferedImage.TYPE_INT_ARGB))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
                //imageBuf = im.asInstanceOf[BufferedImage]
              } else {
                val im: java.awt.Image = imageBuf.getScaledInstance((w.toDouble * 500.0 / h.toDouble).toInt, 500, Image.SCALE_SMOOTH)
                imBox = Full(new BufferedImage((w.toDouble * 500.0 / h.toDouble).toInt, 500, BufferedImage.TYPE_INT_ARGB))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
              }
            } else {
              imBox = Full(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB))
              imBox.get.getGraphics.drawImage(imageBuf, 0, 0, null)
            }
            //dodać do imbox obrazek gdy jest mały!
            //zapisx$1
            if (ImageIO.write(imBox.get, "png", new File(imgDir, img.id.toString + mimeType))) {
              img.path(link).save
              linkpath(serverPath + "/imgdata/" + img.id.toString + mimeType)
              //S.notice("Plik został zapisany")
            } else {
              img.delete_!
              S.notice("Zapis nieudany")
            }
          }
        }

        "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
          "#submit" #> SHtml.submit("Dodaj!", save) &
          "#linkimg" #> <span id="linking">{ linkpath.is }</span>

      }
      /* ładownie pliku na serwer -- dokończyć*/
      def uploadFile() = {
        var fileHold: Box[FileParamHolder] = Empty

        def save() {

          if (fileHold.isEmpty) {
            S.notice("Zapis nieudany")
          } else {
            val dirRoot: File = new File(pathRootFiles)
            val fileSt = model.FileStore.create
            fileSt.path("empty").save //potrzebuje id do wyliczenia hashu
            val subDirPath = ((fileSt.id.is / 200) + 1).toString
            val imgDir = new File(dirRoot, subDirPath)
            //sprawdzam czy katalog do którego pasuje hash istnieje
            if (!imgDir.exists) {
              imgDir.mkdir
            }
            S.notice(fileHold.get.fileName)
            val extension = fileHold.get.fileName.split('.').last //jakiś błąd
            val link = imgDir.getAbsolutePath + "/" + fileSt.id.toString + "." + extension
            val filename = fileSt.id.toString + "." + extension
            val fileOut = new FileOutputStream(new File(imgDir, filename))
            fileOut.write(fileHold.get.file)
            fileSt.path(link).save
            linkpath(serverPath + "/filedata/" + filename)
            //S.notice("Plik został zapisany")
          }

        }
        "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
          "#submit" #> SHtml.submit("Dodaj!", save) &
          "#linking" #> <span id="linking">{ linkpath.is }</span>
      }

      lazy val serverPath = {
        S.request match {
          case Full(r) => r.hostAndPath
          case _ => "add host name"
        }
      }

      //niezaimlpementowane - zrobić osobny moduł dla crona
      def cleanImages(node: NodeSeq): NodeSeq = {
        //przeszukiwanie wszystkich Post, Page i Comments i wyszukanie wszystkich 
        val n = <h1>Nieużywane obrazki  zostały usunięte</h1>
        n
      }

    }
  }
} //end packages
