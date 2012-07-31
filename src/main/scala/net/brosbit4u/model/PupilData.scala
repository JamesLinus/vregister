/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package net.brosbit4u {
  package model {

    
import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

    import net.brosbit4u.lib.Formater
    
    object PupilData extends MongoDocumentMeta[PupilData] {
  override def collectionName = "pupildata"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new PupilData(ObjectId.get, 0L,"", "", 0L, 0L, "", 0)
}

case class PupilData(var _id: ObjectId, var user:Long,
			var birthDistrict:String, var address:String,
			var fatherId:Long, var matherId:Long, 
			var classInfo:String, var nr:Int) 
			extends MongoDocument[PupilData] {
  def meta = PupilData
}


  }
}
