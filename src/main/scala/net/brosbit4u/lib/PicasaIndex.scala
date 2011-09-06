/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
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
  package lib {

    import net.brosbit4u.model.{ Gallery, ExtraData }
    //import java.io.File
    import java.net.URL
    import javax.mail._

    import com.google.gdata.client._
    import com.google.gdata.client.photos._
    import com.google.gdata.data._
    import com.google.gdata.data.media._
    import com.google.gdata.data.photos._

    import scala.collection.JavaConversions._

    class PicasaIndex {

      def make(): Boolean = {
        val email = ExtraData.getData("picasamail") + "@gmail.com"
        val password = ExtraData.getData("picasapass")
        if (email.split("@")(0).trim.length == 0 || password.trim == 0) return false

        val apiUrlStr = "https://picasaweb.google.com/data/feed/api/user/"

        val myService: PicasawebService = new PicasawebService("virtual-register")

        myService.setUserCredentials(email, password)
        val feedUrl: URL = new URL("https://picasaweb.google.com/data/feed/api/user/" + email + "?kind=album")

        val myUserFeed: UserFeed = myService.getFeed(feedUrl, classOf[UserFeed]);
        val albumEntries = asJavaList(myUserFeed.getAlbumEntries())
        var galList: List[Gallery] = Nil

        Gallery.findAll().foreach(g => g.delete_!)
        for (myAlbum <- albumEntries) {
          val g = Gallery.create
          g.title(myAlbum.getTitle().getPlainText())
          g.description(myAlbum.getDescription().getPlainText())

          //     println(myAlbum.getTitle().getPlainText()) //tytuł
          //     println(myAlbum.getDescription().getPlainText()) //opis?
          val feedUrl2: URL = new URL("https://picasaweb.google.com/data/feed/api/user/" + email + "/albumid/" + myAlbum.getId.toString.split("/").last)
          val feed: AlbumFeed = myService.getFeed(feedUrl2, classOf[AlbumFeed])
          var urls: List[(String, String)] = Nil
          var nrPhoto = 0
          for (photo <- feed.getPhotoEntries()) {
            nrPhoto += 1
            urls = (("http" + photo.getMediaThumbnails().get(1).getUrl().substring(5)) -> ("http" + photo.getMediaContents().get(0).getUrl().substring(5))) :: urls
            //       println(photo.getMediaThumbnails().get(1).getUrl())
            //       println(photo.getMediaThumbnails().get(2).getUrl())
            //       println(photo.getMediaContents().get(0).getUrl()) //full size
          }
          g.nrOfPhotos(nrPhoto.toString).thumb(urls.last._1).urls("[" + urls.map(x => "['" + x._1 + "'," + " '" + x._2 + "']").mkString(",") + "]").save
        }
        return true
      }

    }

  }
} //end packages
