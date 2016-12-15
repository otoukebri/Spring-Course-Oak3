package config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.view.InternalResourceViewResolver

/**
 * TODO-02: Add an InternalResourceViewResolver bean definition.
 *
 *
 * Set the prefix and suffix properties - the JSP views are all in
 * `src/main/webapp/WEB-INF/views`. Refer to the notes for help.
 */

@Configuration
@ComponentScan("accounts.web")
@EnableWebMvc
open class MvcConfig : WebMvcConfigurerAdapter() {

    /**
     * Map URL /resources/\* to serve static resources from classpath:/static/\*
     * This allows us to store and distribute css, images, etc. in JAR file.
     * This is the equivalent of <mvc:resources></mvc:resources>
     */

    @Bean
    open fun viewResolver() = InternalResourceViewResolver("/WEB-INF/views/", ".jsp")

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/")
    }

}
