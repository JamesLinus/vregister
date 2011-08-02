
package testing

import net.liftweb._
import mapper._

class IssueProperty extends LongKeyedMapper[IssueProperty] with IdPK {
  def getSingleton = IssueProperty
  val property = ""
  object name extends MappedString(this, 40)
  object icon extends MappedBinary(this)
  object lang {
    def getAll() = {
      TextInfo.findAll(By(TextInfo.propertyStr, getSingleton.property), By(TextInfo.idProperty, getSingleton.id))
    }
    def getLang(lang:String) = {
      TextInfo.findAll(By(TextInfo.propertyStr, getSingleton.property), By(TextInfo.idProperty, getSingleton.id), By(TextInfo.lang, lang))
    }
  }
}

object IssueProperty extends IssueProperty with LongKeyedMetaMapper[IssueProperty] {}
