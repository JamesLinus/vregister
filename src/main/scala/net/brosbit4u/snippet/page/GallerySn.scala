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
    //import _root_.net.liftweb.mapper.{Descending ,OrderBy,By}
    //import _root_.net.liftweb.http.{S}
    import Helpers._

    class GallerySn {
      val gal = Gallery.findAll()

      //dodaje listę galeriigalleryLe
      def drawSlider(node: NodeSeq): NodeSeq = {
        var nr = -1
        val n2 = <ul id={ "mycarousel" } class={ "jcarousel-skin-tango" }>
                   {
                     for (i <- gal) yield {
                       nr += 1
                       <li><img src={ i.thumb.is } width="120" height="120" alt={ i.title.is } onclick={ "draw_Gallery(" + nr.toString + ")" }/></li>
                     }
                   }
                 </ul>
        n2
      }

      //dodaje dwie zmienne
      def galleryList() = {
        var str = "var numGallery = " + gal.length.toString //ilość galerii
        str += "\n var gallerieArr =   ["
        for (g <- gal) {
          str += "[" + g.nrOfPhotos.is + "," + "'" + g.title.is + "'],"
        }
        str += "] \n var urlsArr =  ["
        for (g <- gal) {
          str += g.urls.is + ","
        }
        str = str.substring(0, str.length - 1)
        str += "]\n"
        "#data" #> <script>{ Text(str) }</script>
        //bind("g", n, "data" -> Text(str))
      }
    }

  }
} //end packages
