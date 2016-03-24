package ck.panda.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Files.getFileExtension;

@Service
public class FreemarkerTemplateService implements EmailTemplateService {

    @Autowired
    private Configuration freemarkerConfiguration;

    public String mergeTemplateIntoString(final String template,
                                   final Map<String, Object> model)
            throws IOException, TemplateException {
        checkArgument(!isNullOrEmpty(template.trim()), "The given template is null, empty or blank");
        checkArgument(Objects.equals(getFileExtension(template), "ftl"), "Expected a Freemarker template file");
        return FreeMarkerTemplateUtils.processTemplateIntoString(
                freemarkerConfiguration.getTemplate(template, Charset.forName("UTF-8").name()), model);
    }

}
