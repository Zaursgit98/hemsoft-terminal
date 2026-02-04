package az.hemsoft.terminaljx.business.restaurant.service;

import az.hemsoft.terminaljx.business.restaurant.api.UserApi;
import az.hemsoft.terminaljx.business.restaurant.model.User;

/**
 * User Service - istifadÃ‰â„¢ÃƒÂ§i ÃƒÂ¼zrÃ‰â„¢ business logic
 */
public class UserService {
    private final UserApi userApi;
    private User currentUser;

    public UserService() {
        this.userApi = new UserApi();
    }

    /**
     * PIN ilÃ‰â„¢ giriÃ…Å¸
     * 
     * @param pin PIN kodu
     * @return UÃ„Å¸urlu olub olmadÃ„Â±Ã„Å¸Ã„Â±
     */
    public boolean signIn(String pin) {
        az.hemsoft.terminaljx.business.restaurant.model.TokenResponse response = userApi.signInPos(pin);
        if (response != null && response.getUser() != null) {
            this.currentUser = response.getUser();
            // TODO: Token-i yadda saxla (tokenManager vÃ‰â„¢ ya singleton vasitÃ‰â„¢silÃ‰â„¢)
            System.out.println("GiriÃ…Å¸ uÃ„Å¸urlu: " + currentUser.getUsername());
            return true;
        }
        return false;
    }

    /**
     * Ãƒâ€¡Ã„Â±xÃ„Â±Ã…Å¸
     */
    public void signOut() {
        this.currentUser = null;
    }

    /**
     * Cari istifadÃ‰â„¢ÃƒÂ§i
     * 
     * @return User obyekti
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * GiriÃ…Å¸ edilibmi?
     * 
     * @return true Ã‰â„¢gÃ‰â„¢r giriÃ…Å¸ edilibsÃ‰â„¢
     */
    public boolean isSignedIn() {
        return currentUser != null;
    }
}

