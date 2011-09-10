/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.brosbit4u.model
import net.brosbit4u.model._
import net.liftweb.mapper._

class PupilChangeList extends LongKeyedMapper[PupilChangeList] with IdPK {
  def getSingleton = PupilChangeList
  
      object pupil extends MappedLongForeignKey(this, Pupil)
      object classIn extends MappedLongForeignKey(this, ClassModel)
      object birthDate extends MappedDate(this)
      object pesel extends MappedString(this, 11)
      object nr extends MappedInt(this)
      object father extends MappedLongForeignKey(this, User)
      object mather extends MappedLongForeignKey(this, User)
      object date extends MappedDate(this)
}
 object PupilChangeList extends PupilChangeList with LongKeyedMetaMapper[PupilChangeList] {
   
 }