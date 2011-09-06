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

    import java.util._
    import java.io._
    import javax.mail._
    import javax.mail.internet._

    class Emailer {

      /**
       * Send a single email.
       */
      def sendEmail(aToEmailAddr: String, aSubject: String, aBody: String) {
        val mailFrom = "zkpig26@zkpig.eu.org"
        val mailServer = "smtp.zkpig.eu.org" //smtp.gmail.com
        val mailPort = "587" // wp - 465

        val props = new Properties()
        props.put("mail.smtp.host", mailServer)
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.user", mailFrom)
        //props.put("mail.smtp.from", mailFrom)
        props.put("mail.smtp.port", mailPort)
        //     props.put("mail.smtp.starttls.enable", "true")
        //     props.put("mail.smtp.socketFactory.port", "465")
        //     props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        //     props.put("mail.smtp.socketFactory.fallback", "false")

        //props.put("mail.debug", "true"); //debuger!!!!!!

        val session: Session = Session.getInstance(props, new SMTPAuthenticator())
        val message: MimeMessage = new MimeMessage(session)
        try {
          //the "from" address may be set in code, or set in the
          //config file under "mail.from" ; here, the latter style is used
          message.setFrom(new InternetAddress(mailFrom))
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(aToEmailAddr))
          message.setSubject(aSubject)
          message.setText(aBody)
          Transport.send(message)
        } catch {
          case e => println("Cannot send email. " + e.toString)
        }
      }

    }

    class SMTPAuthenticator extends javax.mail.Authenticator {

      override def getPasswordAuthentication(): PasswordAuthentication = {
        val username: String = "zkpig26"
        val password: String = "2626262626"
        return new PasswordAuthentication(username, password);
      }
    }

  }
} //end packages

