package net.brosbit4u.model

import com.mongodb._
import net.liftweb.record._
import net.liftweb.record.field._
import net.liftweb.mongodb.record._
import org.bson.types.ObjectId
import net.liftweb.mongodb.record.field._

case class Mark(var time:Long, var teacher:String, var mark:String)

class MarkLine private() extends MongoRecord[MarkLine] with ObjectIdPk[MarkLine] {
   override def meta = MarkLine   
   
  val pupilId = new LongField[MarkLine](this)
  val pupilFullName = new StringField[MarkLine](this, 100)
  val sem = new StringField[MarkLine](this,1)
  val subjectId = new LongField[MarkLine](this)
  val marks = new MongoListField[MarkLine,List[Mark]](this)
}

object MarkLine extends MarkLine with MongoMetaRecord[MarkLine] {
  override def collectionName = "markline"

}