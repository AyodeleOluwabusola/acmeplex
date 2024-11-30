package com.uofc.acmeplex.mail;

import com.uofc.acmeplex.dto.request.mail.EmailMessage;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import com.uofc.acmeplex.util.Constant;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmailService  {

    @Value("${mail-sender}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;


    @Autowired
    @Qualifier("getFreeMarkerConfiguration")
    private Configuration fmConfiguration;

    @Value("${template.path}")
    private String templatePath;

    private static final String IMAGE_FORMAT = "image/png";

    public ResponseData sendSimpleMail(EmailMessage mail) {

        if (StringUtils.isBlank(mail.getMessageBody())) {
            log.debug("Message body is blank {}", mail);
            return ResponseData.getInstance(ResponseCodeEnum.NO_MESSAGE_PASSED);
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(sender);
            log.debug("EMAIL HERE: {}", mail.getRecipient());
            mimeMessageHelper.setTo(new String[] {mail.getRecipient()});

            Map<String, Object> map = new HashMap<>();
            map.put("title", mail.getMovie() != null? mail.getMovie().getMovieName() : " ");
            map.put("firstName", Optional.ofNullable(mail.getFirstName()).orElse(".."));
            map.put("message", mail.getMessageBody());
            map.put("theatre", Optional.ofNullable(mail.getTheatre()).orElse("Acmeplex"));
            map.put("genre", mail.getMovie() != null ? mail.getMovie().getMovieGenre() : " ");
            map.put("duration", mail.getMovie() != null ? mail.getMovie().getMovieDuration() : " ");
            map.put("seats", Optional.ofNullable(mail.getSeats()).orElse(".."));
            map.put("ticketCode", Optional.ofNullable(mail.getTicketCode()).orElse(".."));
            map.put("amount", Optional.ofNullable(mail.getTotalAmount()).orElse(".."));
            map.put("refundCode", Optional.ofNullable(mail.getRefundCode()).orElse(".."));
            map.put("showTime", mail.getShowTime() != null ? mail.getShowTime() : "..");
            map.put("supportEmail", "support@acmeplex.com");
            map.put("details", getDetailDesign(mail.getDetails()));

            if (mail.getMessageSubType() == MessageSubTypeEnum.PAYMENT_CONFIRMATION){
                getPaymentInfo(mail, map);
            }
            String content = getContentFromTemplate(map, mail.getMessageSubType());
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.addInline("acmeplexLogo", getFilePathResource(Constant.ACMEPLEX_ICON_PATH), IMAGE_FORMAT);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            log.error("Error occurred while sending Email", e);
            return ResponseData.getInstance(ResponseCodeEnum.ERROR);
        }
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS);
    }

    private void getPaymentInfo(EmailMessage mail, Map<String, Object> map) {
        map.put("paymentReference", mail.getPaymentReference());
        map.put("currentDate", LocalDateTime.now());
        map.put("amount", mail.getTotalAmount());
        map.put("cardType", mail.getCardType());
        map.put("email", mail.getEmail());
        map.put("cardHolderName", mail.getCardHolderName());
    }

    private String getDetailDesign(Map<String, String> details) {
        if (details == null || details.isEmpty()){
            return "";
        }
       return details.entrySet().stream().map(entry->
               String.format("<div class=\"item\"> " +
                "            <span class=\"itemTitle\">%s:</span> " +
                "            <span class=\"itemValue\">₦ %s</span> " +
                "        </div>", entry.getKey(), entry.getValue() )).collect(Collectors.joining(" "));

    }

    private InputStreamSource getFilePathResource(String path) {
        return new FileSystemResource(new File(templatePath + "/" + path));
    }

    public String getContentFromTemplate(Map<String, Object> model, MessageSubTypeEnum messageSubTypeEnum) throws IOException, TemplateException {
        StringBuilder content = new StringBuilder();

        Template template = fmConfiguration.getTemplate(messageSubTypeEnum.getTemplateName());
        content.append(FreeMarkerTemplateUtils.processTemplateIntoString(template, model));

        return content.toString();
    }
}
