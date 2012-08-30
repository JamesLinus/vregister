package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

case class Entry(var date:String, var content:String)

object Anounces extends MongoDocumentMeta[Anounces] {
  override def collectionName = "anounces"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Anounces(ObjectId.get, 0L, 0L, "", Nil)
}

case class Anounces(var _id: ObjectId, var classId:Long, var teacherId:Long,
							var teacherName:String,	var content:List[Entry]) 
							extends MongoDocument[Anounces] {
  def meta = Anounces
}