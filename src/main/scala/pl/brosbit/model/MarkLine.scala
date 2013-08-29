package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

case class Mark(var time:Long, var teacher:String, var mark:String) {
      def mapString = Map[String, String](("time"-> time.toString),("teacher"->teacher),
		  			("mark"-> mark))
}


object MarkLine extends MongoDocumentMeta[MarkLine] {
  override def collectionName = "MarkLine"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = {
      var  array:Array[List[Mark]] = new Array(30)
      array = array.map(a => Nil )
      MarkLine(ObjectId.get, 0, 0L, 0L,  array, Nil, Nil)
  }
}

case class MarkLine(var _id: ObjectId, var sem:Int,  var subjectId: Long,  var pupilId:Long,
							var marks:Array[List[Mark]],  var propMark:List[Mark],  var semMark:List[Mark]) 
							extends MongoDocument[MarkLine] {
  def meta = MarkLine
}


