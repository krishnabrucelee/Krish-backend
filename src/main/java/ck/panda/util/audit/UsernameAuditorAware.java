package ck.panda.util.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.util.TokenDetails;

/**
 * This component returns the user entity of the authenticated user.
 *
 */
public class UsernameAuditorAware implements AuditorAware<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsernameAuditorAware.class);

    /** Autowired user repository object. */
    @Autowired
    private UserRepository userRepository;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @Override
    public Long getCurrentAuditor() {
        LOGGER.debug("Getting the username of authenticated user.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            LOGGER.debug("Current user is anonymous. Returning null.");
            return null;
        }

        User user = null;
        try {
            if (!tokenDetails.getTokenDetails("domainname").equals("ROOT")) {
                user = userRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("id")));
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("NUMBER FORMAT EXCEPTION" + e.getMessage());
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }

        // Now returning the user id instead of user object.
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        } else {
            userId = 0L;
        }
        return userId;
    }
}
