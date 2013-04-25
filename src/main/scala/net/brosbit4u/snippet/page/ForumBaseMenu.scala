package net.brosbit4u.snippet.page

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.brosbit4u.model._
import Helpers._

trait ForumBaseMenu {

     def classes() = {
       "span" #> ClassModel.findAll.map(classMod => {
      <span><a href={ "/forum?c=" + classMod.id.is.toString  + "&h=Klasa: " + classMod.classString}>
      { classMod.classString() }</a></span>
    })
  }
  
  def subjects() = {
      "span" #> SubjectName.findAll.filter(!_.scratched).map(subject => {
      <span><a href={ "/forum?s=" + subject.id.is.toString + "&h=Przedmiot:%" + subject.name.is.replace(' ', '%')}>
      { subject.name.is.replace(' ', '_') }</a></span>
    })
  }
  
  def tags() = {
      "span" #> <span></span>
  }
}