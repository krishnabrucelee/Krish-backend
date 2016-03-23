package ck.panda.service;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * Defines a service for processing templates with a template engine
 */
public interface EmailTemplateService {

    /**
     * Call the template engine to process the given template with the given model object.
     *
     * @param template a template file to be processed
     * @param model    the model object to process the template
     * @return a processed templated (an HTML, or XML, or wathever the template engine can process)
     * @throws IOException thrown if the template file is not found or cannot be accessed
     * @throws TemplateException if the template cannot be processed with the given model object
     */
    String mergeTemplateIntoString(String template, Map<String, Object> model)
            throws IOException, TemplateException;


}
