package az.hemsoft.terminaljx.business.restaurant.api;

import az.hemsoft.terminaljx.business.restaurant.model.User;
import com.google.gson.Gson;

import java.net.http.HttpResponse;

/**
 * User API - istifadÃ‰â„¢ÃƒÂ§i ilÃ‰â„¢ baÃ„Å¸lÃ„Â± API ÃƒÂ§aÃ„Å¸Ã„Â±rÃ„Â±Ã…Å¸larÃ„Â±
 */
public class UserApi {
    private final ApiClient apiClient;
    private final Gson gson;

    public UserApi() {
        this.apiClient = ApiClient.getInstance();
        this.gson = new Gson();
    }

    /**
     * PIN ilÃ‰â„¢ giriÃ…Å¸
     * 
     * @param pin PIN kodu
     * @return TokenResponse obyekti vÃ‰â„¢ ya null
     */
    public az.hemsoft.terminaljx.business.restaurant.model.TokenResponse signInPos(String pin) {
        try {
            java.util.Map<String, String> headers = new java.util.HashMap<>();
            headers.put("posPassword", pin);
            // TODO: Config-dÃ‰â„¢n companyMail-i Ã‰â„¢ldÃ‰â„¢ et
            headers.put("companyMail", "test@hemsoft.az");

            // Body boÃ…Å¸ olmalÃ„Â±dÃ„Â±r
            String body = "{}";

            HttpResponse<String> response = apiClient.post("/v1/crud/user/signInPos", body, headers);

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), az.hemsoft.terminaljx.business.restaurant.model.TokenResponse.class);
            } else {
                System.err.println("GiriÃ…Å¸ xÃ‰â„¢tasÃ„Â±: " + response.statusCode() + ", body: " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.err.println("API ÃƒÂ§aÃ„Å¸Ã„Â±rÃ„Â±Ã…Å¸Ã„Â± xÃ‰â„¢tasÃ„Â±: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ã„Â°stifadÃ‰â„¢ÃƒÂ§i mÃ‰â„¢lumatlarÃ„Â±nÃ„Â± yÃƒÂ¼klÃ‰â„¢
     * 
     * @param userId Ã„Â°stifadÃ‰â„¢ÃƒÂ§i ID
     * @return User obyekti
     */
    public User getUser(int userId) {
        try {
            HttpResponse<String> response = apiClient.get("/user/" + userId);

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), User.class);
            }
            return null;
        } catch (Exception e) {
            System.err.println("API ÃƒÂ§aÃ„Å¸Ã„Â±rÃ„Â±Ã…Å¸Ã„Â± xÃ‰â„¢tasÃ„Â±: " + e.getMessage());
            return null;
        }
    }
}

