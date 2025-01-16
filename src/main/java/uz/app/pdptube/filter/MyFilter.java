package uz.app.pdptube.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyFilter implements Filter {

    @Autowired
    @Lazy
    UserDetailsService userDetailsService;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println(request.getServletPath());
        String authorization = request.getHeader("Authorization");
        if (authorization == null){
            filterChain.doFilter(request, servletResponse);
            return;
        }
        if ( authorization.startsWith("Bearer ")) {
            authorization = authorization.substring(7);
            String email = jwtProvider.getEmailFromToken(authorization);
            setUserToContext(email);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    public void setUserToContext(String email) {
        User user = (User) userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
