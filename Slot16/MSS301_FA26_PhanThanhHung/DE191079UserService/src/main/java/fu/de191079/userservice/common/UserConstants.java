package fu.de191079.userservice.common;

public final class UserConstants {
    private UserConstants(){}
        // user status
        public static final String ACTIVE = "ACTIVE";
        public static final String SUSPENDED = "SUSPENDED";
        public static final String DEACTIVATED = "DEACTIVATED";

    // Regex for validation
    public static final String ROLE_REGEX = "USER|ADMIN|CREATOR";
    public static final String STATUS_REGEX = "ACTIVE|SUSPENDED|DEACTIVATED";

}
