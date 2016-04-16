package ck.panda.util;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.User;
import ck.panda.service.UserService;

@Service("LoginUserDetailsService")
public class LoginUserDetailsService implements UserDetailsService {

    /** User service reference. */
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
//        User user = userService.findByUserNameAndActive(username, true);
        User user = null;
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());
        UserDetails userDetails = (UserDetails) new org.springframework.security.core.userdetails.User(
                user.getUserName(), user.getPassword(), Arrays.asList(authority));
        return userDetails;
    }

}
