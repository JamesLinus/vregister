package net.brosbit4u.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object ArticleContent extends MongoDocumentMeta[ArticleContent] {
  override def collectionName = "articlecontent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ArticleContent(ObjectId.get, "")
}

case class ArticleContent(var _id: ObjectId, var content:String) 
							extends MongoDocument[ArticleContent] {
  def meta = ArticleContent
}