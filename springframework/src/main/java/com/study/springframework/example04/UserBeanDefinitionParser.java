package com.study.springframework.example04;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 2:25 PM
 */
public class UserBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    private static final String USER_NAME = "userName";
    private static final String EMAIL = "email";
    private static final String ID = "id";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return User.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String username = element.getAttribute(USER_NAME);
        String email = element.getAttribute(EMAIL);
        String id = element.getAttribute(ID);

        if(StringUtils.isEmpty(id)){
            parserContext.getReaderContext().error("Id必须存在，不能为null或''",element);
        }
        builder.addPropertyValue("userId",id);

        if(StringUtils.hasText(username)){
            builder.addPropertyValue(USER_NAME,username);
        }

        if(StringUtils.hasText(email)){
            builder.addPropertyValue(EMAIL,email);
        }


    }
}
