package org.travlyn.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.travlyn.server.service.TravlynService;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.db.UserEntity;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String REGISTERED_USER_ROLE = "REGISTERED_USER";


    private static final List<Pair<String, String>> WHITE_LIST = Arrays.asList(
            new Pair<>("GET", "/"),
            new Pair<>("GET", "/user"),
            new Pair<>("PUT", "/user"),
            new Pair<>("GET", "/city/**"),
            new Pair<>("GET", "/trip/**"),
            new Pair<>("GET", "/stop/**"),
            new Pair<>("GET", "/swagger-ui/**"),
            new Pair<>("GET", "/swagger-ui*/**"),
            new Pair<>("GET", "/webjars/**"),
            new Pair<>("GET", "/swagger-resources/**"),
            new Pair<>("GET", "/csrf/**"),
            new Pair<>("GET", "/api-docs/**"));

    @Autowired
    private TravlynService travlynService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        final String header = request.getHeader(HEADER_STRING);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            String tokenString = header.substring(7);

            try {
                Optional<UserEntity> userOptional = travlynService.checkUsersToken(tokenString);
                if (userOptional.isPresent()) {
                    List<GrantedAuthority> authList = new ArrayList<>();
                    authList.add(new SimpleGrantedAuthority(REGISTERED_USER_ROLE));

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userOptional.get(), null, authList);
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.error("Failed to authenticate: Token is invalid.", e);
            }
        } else {
            logger.info("Failed to authenticate: Token is not present.");
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Failed to authenticate: Token is invalid.");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return WHITE_LIST.stream().anyMatch(path ->
                request.getMethod().equals(path.getKey())
                        && new AntPathMatcher().match(path.getValue(), request.getServletPath()));
    }
}
