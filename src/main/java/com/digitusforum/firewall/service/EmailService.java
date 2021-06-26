package com.digitusforum.firewall.service;

import com.digitusforum.firewall.model.entity.EmailVerification;
import com.digitusforum.firewall.model.repository.EmailVerificationRepository;
import com.digitusforum.firewall.model.vo.EmailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vo.UserVO;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Properties;

//todo move this to the email city
public class EmailService {
    static final String FROM = "no-reply@digitusforum.com";
    static final String FROMNAME = "Digitus Forum";
    static final String SMTP_USERNAME = "AKIAWJKDMDKONDQGV45T";
    static final String SMTP_PASSWORD = "BJr4qqd7cOk7i82+d2Aw8lrjlIycGKWUcUzFCcxug/1z";
    static final String HOST = "email-smtp.us-east-1.amazonaws.com";
    static final int PORT = 587;
    GeneralService generalService = new GeneralService();
    //@Autowired
    //I18 i18 = new I18();

    Logger logger = LoggerFactory.getLogger(EmailService.class);

    public String sendEmailToCreateAccount(UserVO user,
                                           String l,
                                           EmailVerificationRepository emailVerificationRepository) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(user.getEmail());
        if (emailVerification != null) {
            if (generalService.pastOneMinute(emailVerification.getCreatedIn())) {
                emailVerificationRepository.delete(emailVerification);
            } else {
                return ""; //i18.get(l, "login.not_found.password");
            }
        }
        /*emailVerification = emailVerificationRepository.save(
                new EmailVerification(user,
                        emailVerificationRepository));*/
        EmailVO emailVO = new EmailVO();
        emailVO.setEmail(user.getEmail());
        emailVO.setReadableNumber(emailVerification.getReadableNumber());
        new Thread(() -> {
            Transport transport = null;
            try {
                Properties props = System.getProperties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.port", PORT);
                props.put("spring.mail.port", PORT);
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.auth", "true");
                Session session = Session.getDefaultInstance(props);
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("oi.eu.sou.pinguim@gmail.com", "Vendele"));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailVO.getEmail()));
                msg.setSubject("Criação de conta");
                StringWriter writer = new StringWriter();
                //IOUtils.copy(new FileInputStream(new File(EmailService.class.getClassLoader().getResource("orders.html").getFile())), writer);
                //String emailBody = writer.toString();
                String emailBody = "Digite este código de verificação para criar a sua conta no vendele " + emailVO.getReadableNumber();
                //emailBody = emailBody.replace("order number", emailVO.getOrder());
                msg.setContent(emailBody, "text/html");
                transport = session.getTransport();
                System.out.println("Sending email to create account in vendele...");
                transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
                transport.sendMessage(msg, msg.getAllRecipients());
                System.out.println("Email sent!");
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                System.out.println("The email was not sent.");
                System.out.println("Error message: " + ex.getMessage());
                //throw EmailException.SENDING_PROBLEM;
            } finally {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return "Código de verificação enviado para o seu email";
    }

    /*public void forgotPasswordEmail(EmailVO emailVO) throws MessagingException, IOException {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("spring.mail.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM, FROMNAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailVO.getEmail()));
        msg.setSubject("Iking password recovery");
        StringWriter writer = new StringWriter();
        //IOUtils.copy(new FileInputStream(new File(EmailService.class.getClassLoader().getResource("congratulation.html").getFile())), writer);
        //String emailBody = writer.toString();
        String emailBody = "http://iking.store.s3-website.us-east-2.amazonaws.com/recovery/" + emailVO.getReadableNumber();
        *//*emailBody = emailBody.replace("(nome do mano)", emailVO.getUser());
        emailBody = emailBody.replace("max@b1ts.com.br", emailVO.getLogin());
        emailBody = emailBody.replace("132mudar", emailVO.getPassword());*//*
        msg.setContent(emailBody, "text/html");
        Transport transport = session.getTransport();
        try {
            System.out.println("Sending email from forgotPasswordEmail...");
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            logger.error(ex.getMessage());
        } finally {
            transport.close();
        }
    }

    public void sendEmailToNewUser(EmailVO emailVO) throws MessagingException, IOException {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("spring.mail.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM, FROMNAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailVO.getEmail()));
        msg.setSubject("Welcome to iking");
        StringWriter writer = new StringWriter();
        //IOUtils.copy(new FileInputStream(new File(EmailService.class.getClassLoader().getResource("congratulation.html").getFile())), writer);
        //String emailBody = writer.toString();
        String emailBody = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html style=\"width:100%;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;\">\n" +
                " <head> \n" +
                "  <meta charset=\"UTF-8\"> \n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"> \n" +
                "  <meta name=\"x-apple-disable-message-reformatting\"> \n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\"> \n" +
                "  <title>Novo modelo de e-mail 2020-03-24</title> \n" +
                "  <!--[if (mso 16)]>\n" +
                "    <style type=\"text/css\">\n" +
                "    a {text-decoration: none;}\n" +
                "    </style>\n" +
                "    <![endif]--> \n" +
                "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> \n" +
                "  <!--[if !mso]><!-- --> \n" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Roboto:400,400i,700,700i\" rel=\"stylesheet\"> \n" +
                "  <!--<![endif]--> \n" +
                "  <style type=\"text/css\">\n" +
                "@media only screen and (max-width:600px) {p, ul li, ol li, a { font-size:16px!important; line-height:150%!important } h1 { font-size:30px!important; text-align:center; line-height:120%!important } h2 { font-size:26px!important; text-align:center; line-height:120%!important } h3 { font-size:20px!important; text-align:center; line-height:120%!important } h1 a { font-size:30px!important } h2 a { font-size:26px!important } h3 a { font-size:20px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } a.es-button { font-size:20px!important; display:inline-block!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:25%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } .es-desk-menu-hidden { display:table-cell!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }\n" +
                "#outlook a {\n" +
                "\tpadding:0;\n" +
                "}\n" +
                ".ExternalClass {\n" +
                "\twidth:100%;\n" +
                "}\n" +
                ".ExternalClass,\n" +
                ".ExternalClass p,\n" +
                ".ExternalClass span,\n" +
                ".ExternalClass font,\n" +
                ".ExternalClass td,\n" +
                ".ExternalClass div {\n" +
                "\tline-height:100%;\n" +
                "}\n" +
                ".es-button {\n" +
                "\tmso-style-priority:100!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "}\n" +
                "a[x-apple-data-detectors] {\n" +
                "\tcolor:inherit!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "\tfont-size:inherit!important;\n" +
                "\tfont-family:inherit!important;\n" +
                "\tfont-weight:inherit!important;\n" +
                "\tline-height:inherit!important;\n" +
                "}\n" +
                ".es-desk-hidden {\n" +
                "\tdisplay:none;\n" +
                "\tfloat:left;\n" +
                "\toverflow:hidden;\n" +
                "\twidth:0;\n" +
                "\tmax-height:0;\n" +
                "\tline-height:0;\n" +
                "\tmso-hide:all;\n" +
                "}\n" +
                "</style> \n" +
                " </head> \n" +
                " <body style=\"width:100%;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;\"> \n" +
                "  <div class=\"es-wrapper-color\" style=\"background-color:#FFFFFF;\"> \n" +
                "   <!--[if gte mso 9]>\n" +
                "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "\t\t\t\t<v:fill type=\"tile\" color=\"#ffffff\"></v:fill>\n" +
                "\t\t\t</v:background>\n" +
                "\t\t<![endif]--> \n" +
                "   <table class=\"es-wrapper\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"> \n" +
                "     <tr style=\"border-collapse:collapse;\"> \n" +
                "      <td valign=\"top\" style=\"padding:0;Margin:0;\"> \n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:10px;Margin:0;\"> \n" +
                "               <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"580\" align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px;\">\n" +
                "                        <img class=\"adapt-img\" src=\"https://fgtoih.stripocdn.email/content/guids/CABINET_140f474f27635998220054be895089c6/images/4791585104943175.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\" width=\"85\">\n" +
                "                      </td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "           <table class=\"es-header-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:20px;font-size:0px;\"><a href=\"https://viewstripo.email\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-size:14px;text-decoration:none;color:#F6A1B4;\"><img src=\"https://fgtoih.stripocdn.email/content/guids/4b2cad2d-be3e-4842-9abf-e37292cc51f2/images/301585088922234.jpg\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\" width=\"229\"></a></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;border-radius:3px;background-color:#FCFCFC;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#fcfcfc\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td class=\"es-m-txt-l\" align=\"left\" style=\"padding:0;Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;\"><h2 style=\"Margin:0;line-height:31px;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:26px;font-style:normal;font-weight:normal;color:#0E0E0E;\">You just started a new business. Congrats!</h2></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td bgcolor=\"#fcfcfc\" align=\"left\" style=\"padding:0;Margin:0;padding-top:10px;padding-left:20px;padding-right:20px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#333333;\">Welcome to iKing, (nome do mano)</p><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#333333;\"><br></p><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#333333;\">Starting a business is no easy task, bu there`s no better place to start than with us.<br>We`ll handle everything from marketing and payments to secure check out and shipping so you can focus on what you love.<br><br><strong>Here`s where you can find iKing&nbsp;online:</strong><br><br><a target=\"_blank\" href=\"https://www.iking.store\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-size:14px;text-decoration:none;color:#1A1FE9;\">https://www.iking.store </a><br><br><strong>And here`s where you can log in to iKing seller central</strong> (be sure to bookmark this page):<br><br><a target=\"_blank\" href=\"http://iking.store.s3-website.us-east-2.amazonaws.com/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-size:14px;text-decoration:none;color:#1A1FE9;\">http://iking.store.s3-website.us-east-2.amazonaws.com/ </a><br>\n" +
                "                        <h5>Login: max@b1ts.com.br</h5>  \n" +
                "                        <h5>Password: 132mudar</h5> \n" +
                "                        <br>What happens next?<br><br>Keep an eye on your e-mail, because we’ll be sending everything about your store.<br><br></p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#95D0EC;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#95d0ec\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"Margin:0;padding-bottom:5px;padding-top:20px;padding-left:20px;padding-right:20px;\"><h3 style=\"Margin:0;line-height:22px;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:18px;font-style:normal;font-weight:normal;color:#F9F0F0;\">Let's get social</h3></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td style=\"padding:0;Margin:0;background-color:#fff;\" bgcolor=\"#666666\" align=\"center\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;background-color:#95D0EC;border-radius:3px;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#95d0ec\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td bgcolor=\"#95d0ec\" align=\"center\" style=\"Margin:0;padding-top:5px;padding-bottom:5px;padding-left:20px;padding-right:20px;font-size:0;\"> \n" +
                "                       <table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td style=\"padding:0;Margin:0px;border-bottom:1px solid #95D0EC;background:none 0% 0% repeat scroll rgba(0, 0, 0, 0);height:1px;width:100%;margin:0px;\"></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"Margin:0;padding-top:5px;padding-left:20px;padding-right:20px;padding-bottom:25px;font-size:0;\"> \n" +
                "                       <table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px;\"><a target=\"_blank\" href=\"https://www.facebook.com/ikingstoreofficial/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-size:14px;text-decoration:none;color:#F6A1B4;\"><img title=\"Facebook\" src=\"https://fgtoih.stripocdn.email/content/assets/img/social-icons/logo-black/facebook-logo-black.png\" alt=\"Fb\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\"></a></td> \n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"><a target=\"_blank\" href=\"https://www.instagram.com/ikingstore.official/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-size:14px;text-decoration:none;color:#F6A1B4;\"><img title=\"Instagram\" src=\"https://fgtoih.stripocdn.email/content/assets/img/social-icons/logo-black/instagram-logo-black.png\" alt=\"Inst\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\"></a></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td style=\"padding:0;Margin:0;background-color:rgb(255, 255, 255);\" bgcolor=\"#fff\" align=\"center\"> \n" +
                "           <table class=\"es-footer-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#fff;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#fff\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"560\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td esdev-links-color=\"#999999\" align=\"center\" style=\"padding:0;Margin:0;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#FFFFFF;\"><br></p></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:5px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#FFFFFF;\"><br></p></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td esdev-links-color=\"#999999\" align=\"center\" style=\"padding:0;Margin:0;padding-bottom:5px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:21px;color:#FFFFFF;\"><br></p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table>\n" +
                "      </td> \n" +
                "     </tr> \n" +
                "   </table> \n" +
                "  </div>  \n" +
                " </body>\n" +
                "</html>";
        emailBody = emailBody.replace("(nome do mano)", emailVO.getUser());
        emailBody = emailBody.replace("max@b1ts.com.br", emailVO.getLogin());
        emailBody = emailBody.replace("132mudar", emailVO.getPassword());
        msg.setContent(emailBody, "text/html");
        Transport transport = session.getTransport();
        try {
            System.out.println("Sending email from new user...");
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        } finally {
            transport.close();
        }
    }

    public void sendEmailToDatena(EmailVO emailVO) throws MessagingException, IOException {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", PORT);
        props.put("spring.mail.port", PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM, FROMNAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailVO.getEmail()));
        msg.setSubject("Registered sale");
        StringWriter writer = new StringWriter();
        //IOUtils.copy(new FileInputStream(new File(EmailService.class.getClassLoader().getResource("orders.html").getFile())), writer);
        //String emailBody = writer.toString();
        String emailBody = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html style=\"width:100%;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;\">\n" +
                " <head> \n" +
                "  <meta charset=\"UTF-8\"> \n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"> \n" +
                "  <meta name=\"x-apple-disable-message-reformatting\"> \n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\"> \n" +
                "  <title>Novo e-mail</title> \n" +
                "  <!--[if (mso 16)]>\n" +
                "    <style type=\"text/css\">\n" +
                "    a {text-decoration: none;}\n" +
                "    </style>\n" +
                "    <![endif]--> \n" +
                "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> \n" +
                "  <!--[if !mso]><!-- --> \n" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Lato:400,400i,700,700i\" rel=\"stylesheet\"> \n" +
                "  <!--<![endif]--> \n" +
                "  <style type=\"text/css\">\n" +
                "@media only screen and (max-width:600px) {p, ul li, ol li, a { font-size:16px!important; line-height:150%!important } h1 { font-size:30px!important; text-align:center; line-height:120%!important } h2 { font-size:26px!important; text-align:center; line-height:120%!important } h3 { font-size:20px!important; text-align:center; line-height:120%!important } h1 a { font-size:30px!important } h2 a { font-size:26px!important } h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button { font-size:20px!important; display:block!important; border-width:15px 25px 15px 25px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } .es-desk-menu-hidden { display:table-cell!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }\n" +
                "#outlook a {\n" +
                "\tpadding:0;\n" +
                "}\n" +
                ".ExternalClass {\n" +
                "\twidth:100%;\n" +
                "}\n" +
                ".ExternalClass,\n" +
                ".ExternalClass p,\n" +
                ".ExternalClass span,\n" +
                ".ExternalClass font,\n" +
                ".ExternalClass td,\n" +
                ".ExternalClass div {\n" +
                "\tline-height:100%;\n" +
                "}\n" +
                ".es-button {\n" +
                "\tmso-style-priority:100!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "}\n" +
                "a[x-apple-data-detectors] {\n" +
                "\tcolor:inherit!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "\tfont-size:inherit!important;\n" +
                "\tfont-family:inherit!important;\n" +
                "\tfont-weight:inherit!important;\n" +
                "\tline-height:inherit!important;\n" +
                "}\n" +
                ".es-desk-hidden {\n" +
                "\tdisplay:none;\n" +
                "\tfloat:left;\n" +
                "\toverflow:hidden;\n" +
                "\twidth:0;\n" +
                "\tmax-height:0;\n" +
                "\tline-height:0;\n" +
                "\tmso-hide:all;\n" +
                "}\n" +
                "</style> \n" +
                " </head> \n" +
                " <body style=\"width:100%;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;\"> \n" +
                "  <div class=\"es-wrapper-color\" style=\"background-color:#F4F4F4;\"> \n" +
                "   <!--[if gte mso 9]>\n" +
                "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "\t\t\t\t<v:fill type=\"tile\" color=\"#f4f4f4\"></v:fill>\n" +
                "\t\t\t</v:background>\n" +
                "\t\t<![endif]--> \n" +
                "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;\"> \n" +
                "     <tr class=\"gmail-fix\" height=\"0\" style=\"border-collapse:collapse;\"> \n" +
                "      <td style=\"padding:0;Margin:0;\"> \n" +
                "       <table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"padding:0;Margin:0;line-height:1px;min-width:600px;\" height=\"0\"><img src=\"https://esputnik.com/repository/applications/images/blank.gif\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;max-height:0px;min-height:0px;min-width:600px;width:600px;\" alt width=\"600\" height=\"1\"></td> \n" +
                "         </tr> \n" +
                "       </table></td> \n" +
                "     </tr> \n" +
                "     <tr style=\"border-collapse:collapse;\"> \n" +
                "      <td valign=\"top\" style=\"padding:0;Margin:0;\"> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td style=\"padding:0;Margin:0;background-color:#0076FC;\" bgcolor=\"#0076fc\" align=\"center\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;background-color:#FFFFFF;border-radius:4px;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td bgcolor=\"#0076fc\" align=\"center\" style=\"Margin:0;padding-bottom:5px;padding-left:20px;padding-right:20px;padding-top:40px;font-size:0;\"> \n" +
                "                       <table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td style=\"padding:0;Margin:0px;border-bottom:1px solid #0076FC;background:none 0% 0% repeat scroll rgba(0, 0, 0, 0);height:1px;width:100%;margin:0px;\"></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;background-color:#FFFFFF;border-radius:4px;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td bgcolor=\"#ffffff\" align=\"center\" style=\"padding:0;Margin:0;padding-bottom:5px;padding-left:20px;padding-right:20px;font-size:0;\"> \n" +
                "                       <table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td style=\"padding:0;Margin:0px;border-bottom:1px solid #FFFFFF;background:rgba(0, 0, 0, 0) none repeat scroll 0% 0%;height:1px;width:100%;margin:0px;\"></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td style=\"padding:0;Margin:0;background-color:#0076FC;\" bgcolor=\"#0076fc\" align=\"center\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0;padding-top:20px;padding-left:30px;padding-right:30px;background-color:#FFFFFF;\"> \n" +
                "               <!--[if mso]><table width=\"540\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"260\" valign=\"top\"><![endif]--> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-left\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"260\" class=\"es-m-p20b\" align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px;position:relative;\"><img class=\"adapt-img\" src=\"https://fgycho.stripocdn.email/content/guids/CABINET_29a874ebedf8178c8670455a2c8bdd03/images/25311585328391445.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;width:185px;height:94px;\" width=\"185\"></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td><td width=\"20\"></td><td width=\"260\" valign=\"top\"><![endif]--> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"260\" align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"left\" class=\"es-m-txt-c\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0;padding-right:25px;padding-top:30px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:40px;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;line-height:60px;color:#333333;\">Salles Center</p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table> \n" +
                "               <!--[if mso]></td></tr></table><![endif]--></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td class=\"es-m-txt-l\" bgcolor=\"#ffffff\" align=\"center\" style=\"Margin:0;padding-bottom:15px;padding-top:20px;padding-left:30px;padding-right:30px;\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:20px;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;line-height:30px;color:#393737;\">Good news! One order has shipped and will arrive soon.</p></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;padding-left:30px;padding-right:30px;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"540\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" style=\"Margin:0;padding-left:10px;padding-right:10px;padding-top:25px;padding-bottom:40px;\"><span class=\"es-button-border\" style=\"border-style:solid;border-color:#0076FC;background:#0076FC;border-width:1px;display:inline-block;border-radius:2px;width:auto;\"><a href=\"http://iking.store.s3-website.us-east-2.amazonaws.com/\" class=\"es-button\" target=\"_blank\" style=\"mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;font-size:20px;color:#FFFFFF;border-style:solid;border-color:#0076FC;border-width:15px 45px;display:inline-block;background:#0076FC;border-radius:2px;font-weight:normal;font-style:normal;line-height:24px;width:auto;text-align:center;\">Backoffice</a></span></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;padding-left:30px;padding-right:30px;\"> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"540\" align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"center\" bgcolor=\"#f5f6f6\" style=\"padding:0;Margin:0;padding-top:10px;padding-right:10px;padding-bottom:30px;\"><br> \n" +
                "                       <table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:500px;\" class=\"cke_show_border\" role=\"presentation\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td style=\"padding:0;Margin:0;font-size:20px;\">Order Number</td> \n" +
                "                          <td style=\"padding:0;Margin:0;font-size:20px;text-align:right;\">order number</td> \n" +
                "                         </tr> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td style=\"padding:0;Margin:0;font-size:20px;\">Order Details</td> \n" +
                "                          <td style=\"padding:0;Margin:0;\"><br></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;padding-top:20px;padding-left:30px;padding-right:30px;\"> \n" +
                "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"540\" align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-left:20px;padding-right:20px;padding-bottom:40px;\"> \n" +
                "                       <table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:500px;\" class=\"cke_show_border\" role=\"presentation\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td style=\"padding:0;Margin:0;\"><strong>product quantity</strong> x <strong>product name</strong></td> \n" +
                "                          <td style=\"padding:0;Margin:0;\"><strong>product price</strong></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table> \n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;\"> \n" +
                "         <tr style=\"border-collapse:collapse;\"> \n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "           <table class=\"es-content-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;\"> \n" +
                "             <tr style=\"border-collapse:collapse;\"> \n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0;\"> \n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;border-radius:4px;background-color:#111111;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#111111\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td class=\"es-m-txt-l\" bgcolor=\"#0076fc\" align=\"left\" style=\"padding:0;Margin:0;padding-top:25px;padding-left:30px;padding-right:30px;\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#FFFFFF;text-align:center;\">event time</h2></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;border-radius:4px;background-color:#111111;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#111111\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td class=\"es-m-txt-l\" bgcolor=\"#0076fc\" align=\"left\" style=\"padding:0;Margin:0;padding-top:25px;padding-left:30px;padding-right:30px;\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#FFFFFF;text-align:center;\">iKing.store</h2> \n" +
                "                       <div style=\"text-align:center;\"> \n" +
                "                        <br> \n" +
                "                       </div></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "                 <tr style=\"border-collapse:collapse;\"> \n" +
                "                  <td width=\"600\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"> \n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;border-radius:4px;background-color:#111111;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#111111\" role=\"presentation\"> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td class=\"es-m-txt-c\" align=\"center\" style=\"padding:0;Margin:0;font-size:0px;background-color:#0076FC;\" bgcolor=\"#0076fc\"> \n" +
                "                       <table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;\"> \n" +
                "                         <tr style=\"border-collapse:collapse;\"> \n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px;\"><a target=\"_blank\" href=\"https://www.facebook.com/ikingstoreofficial/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;font-size:18px;text-decoration:underline;color:#EC6D64;\"><img title=\"Facebook\" src=\"https://fgycho.stripocdn.email/content/assets/img/social-icons/logo-white/facebook-logo-white.png\" alt=\"Fb\" width=\"32\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\"></a></td> \n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;\"><a target=\"_blank\" href=\"https://www.instagram.com/ikingstore.official/\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;font-size:18px;text-decoration:underline;color:#EC6D64;\"><img title=\"Instagram\" src=\"https://fgycho.stripocdn.email/content/assets/img/social-icons/logo-white/instagram-logo-white.png\" alt=\"Inst\" width=\"32\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic;\"></a></td> \n" +
                "                         </tr> \n" +
                "                       </table></td> \n" +
                "                     </tr> \n" +
                "                     <tr style=\"border-collapse:collapse;\"> \n" +
                "                      <td class=\"es-m-txt-l\" bgcolor=\"#0076fc\" align=\"left\" style=\"padding:0;Margin:0;padding-top:25px;padding-left:30px;padding-right:30px;\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:lato, 'helvetica neue', helvetica, arial, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#FFFFFF;text-align:center;\"><br></h2></td> \n" +
                "                     </tr> \n" +
                "                   </table></td> \n" +
                "                 </tr> \n" +
                "               </table></td> \n" +
                "             </tr> \n" +
                "           </table></td> \n" +
                "         </tr> \n" +
                "       </table></td> \n" +
                "     </tr> \n" +
                "   </table> \n" +
                "  </div>  \n" +
                " </body>\n" +
                "</html>";
        emailBody = emailBody.replace("order number", emailVO.getOrder());
        emailBody = emailBody.replace("product name", emailVO.getProduct());
        emailBody = emailBody.replace("product quantity", emailVO.getQuantity());
        emailBody = emailBody.replace("product price", "$ " + emailVO.getPrice());
        emailBody = emailBody.replace("event time", emailVO.getDate());
        msg.setContent(emailBody, "text/html");
        Transport transport = session.getTransport();
        try {
            System.out.println("Sending email to datena...");
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        } finally {
            transport.close();
        }
    }

    */
}
