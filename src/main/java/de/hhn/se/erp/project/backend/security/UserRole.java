package de.hhn.se.erp.project.backend.security;

public enum UserRole {
	STUDENT, ADMIN, DOZENT, USER;

	public UserAuthority asAuthorityFor(final User user) {
		final UserAuthority authority = new UserAuthority();
		authority.setAuthority("ROLE_" + toString());
		authority.setUser(user);
		return authority;
	}

	public static UserRole valueOf(final UserAuthority authority) {
		switch (authority.getAuthority()) {
		case "ROLE_STUDENT":
			return STUDENT;
		case "ROLE_ADMIN":
			return ADMIN;
		case "ROLE_DOZENT":
			return DOZENT;
		case "ROLE_USER":
			return USER;
		}
		throw new IllegalArgumentException("No role defined for authority: "
				+ authority.getAuthority());
	}
}
