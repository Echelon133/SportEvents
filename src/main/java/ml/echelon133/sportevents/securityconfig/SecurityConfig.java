package ml.echelon133.sportevents.securityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private ClientRegistrationRepository clientRegistrationRepository;

    // Temporary solution
    @Value("${spring.admin.username}")
    private String adminUsername;

    @Autowired
    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(this.clientRegistrationRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/**")
                .authorizeRequests()
                    .antMatchers( "/oauth2/**").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/**").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login().userInfoEndpoint().userService(oAuth2UserService())
                .and()
                    .loginPage("/oauth2/authorization/github");
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        return (userRequest) -> {
            // Load our user info
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            // By default, every user logged in has only ROLE_USER authority
            Collection<? extends GrantedAuthority> initialAuthorities = oAuth2User.getAuthorities();
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(initialAuthorities);

            // Temporary solution - if the username read in from the info endpoint is the same as in our configuration
            // Give this user an extra authority - ROLE_ADMIN
            String username = oAuth2User.getAttributes().get("login").toString();
            if (username.equals(adminUsername)) {
                mappedAuthorities.add(() -> "ROLE_ADMIN");
            }

            // Reconstruct the user with all necessary data
            Map<String, Object> oAuth2UserAttributes = oAuth2User.getAttributes();
            String usernameAttributeName = userRequest
                                                .getClientRegistration()
                                                .getProviderDetails()
                                                .getUserInfoEndpoint()
                                                .getUserNameAttributeName();
            oAuth2User = new DefaultOAuth2User(mappedAuthorities, oAuth2UserAttributes, usernameAttributeName);
            return oAuth2User;
        };
    }
}