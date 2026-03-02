package service;
import dao.UserDAO;
import model.User;
import util.PasswordUtil;
import java.util.regex.Pattern;
public class UserService {
    private final UserDAO userDAO;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public UserService() {
        this.userDAO = new UserDAO();
    }
    public User registerUser(String username, String plainPassword, String email, String userType) {
        validateUsername(username);
        validatePassword(plainPassword);
        validateEmail(email);
        validateUserType(userType);
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User user = new User(username, hashedPassword, email, userType);
        boolean success = userDAO.create(user);
        if (!success) {
            throw new RuntimeException("Failed to create user");
        }
        return user;
    }
    public User authenticateUser(String username, String plainPassword) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }
    public User authenticateByEmail(String email, String plainPassword) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }
    public void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3-20 characters, letters and numbers only");
        }
    }
    public void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!PasswordUtil.isPasswordStrong(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
    public void validateUserType(String userType) {
        if (userType == null || userType.isEmpty()) {
            throw new IllegalArgumentException("User type cannot be empty");
        }
        if (!userType.equals("TEACHER") && !userType.equals("LEARNER")) {
            throw new IllegalArgumentException("User type must be TEACHER or LEARNER");
        }
    }
    public boolean isUsernameAvailable(String username) {
        return !userDAO.usernameExists(username);
    }
    public boolean isEmailAvailable(String email) {
        return !userDAO.emailExists(email);
    }
    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
}
