package park.brothers.runwith_back.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig { //스웨거 config

    @Value("${SERVER_TYPE:LOCAL}")
    private String serverType;

    @Bean
    public OpenAPI openAPI() {
        //스웨거 내부에서 Bearer 토큰 인증을 사용하도록 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        //인증 도구를 사용할 수 있도록 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        String swaggerTitle = "RunWith API [" + serverType + "]";

        return new OpenAPI() //openapi 명세 설정
                .info(new Info().title(swaggerTitle)
                        .description("RunWith 프로젝트의" + serverType + "API 명세서 페이지입니다.")
                        .version("v1.0.0"))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}