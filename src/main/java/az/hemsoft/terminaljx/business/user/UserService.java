package az.hemsoft.terminaljx.business.user;

public class UserService {
    public boolean signIn(String pin) {
        // Default pin for now
        return "1111".equals(pin);
    }

    public Object getCurrentUser() {
        return null;
    }
}
