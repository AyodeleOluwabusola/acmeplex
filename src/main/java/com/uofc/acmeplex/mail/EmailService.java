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
            mimeMessageHelper.setTo((mail.getRecipients() != null && mail.getRecipients().length > 0) ? mail.getRecipients() : new String[] {mail.getRecipient()});

            Map<String, Object> map = new HashMap<>();
            map.put("firstName", Optional.ofNullable(mail.getFirstName()).orElse(" "));
            map.put("message", mail.getMessageBody());
            map.put("action", Optional.ofNullable(mail.getAction()).orElse(".."));
            map.put("link", Optional.ofNullable(mail.getLinkUrl()).orElse("#"));
            map.put("amount", Optional.ofNullable(mail.getTotalAmount()).orElse(""));
            map.put("requestTime", Optional.ofNullable(mail.getRequestTime()).orElse(".."));
            map.put("details", getDetailDesign(mail.getDetails()));

            String content = getContentFromTemplate(map, mail.getMessageSubType());
            mimeMessageHelper.setText(content, true);

            mimeMessageHelper.addInline("fundgridImg", getFilePathResource(Constant.ACMEPLEX_ICON_PATH), IMAGE_FORMAT);

            if (StringUtils.isNotBlank(mail.getMessageSubType().getActionImgName())) {
                mimeMessageHelper.addInline("actionImg", getFilePathResource(mail.getMessageSubType().getActionImgName()), IMAGE_FORMAT);
            }

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            log.error("Error occurred while sending Email", e);
            return ResponseData.getInstance(ResponseCodeEnum.ERROR);
        }
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS);
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
