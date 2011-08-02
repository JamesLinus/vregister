/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing
import net.liftweb._
import mapper._

class TextInfo extends LongKeyedMapper[TextInfo] with IdPK  {
  def getSingleton = TextInfo

 object name extends MappedString(this,40)
 object description extends MappedPoliteString(this, 1024)
 object lang extends MappedLocale(this)
 object propertyStr extends MappedString(this,5)
 object idProperty extends MappedLong(this)


}

object TextInfo extends TextInfo with LongKeyedMetaMapper[TextInfo] {
  
}
