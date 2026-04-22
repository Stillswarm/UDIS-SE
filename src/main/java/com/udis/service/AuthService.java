package com.udis.service;

import com.udis.dao.UserDao;
import com.udis.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

public class AuthService {

    public enum Module {
        STUDENT, COURSE, REGISTRATION, GRADE, INVENTORY, FINANCE, RESEARCH, QUERY, AUDIT
    }

    private static final Set<Module> SECRETARY_WRITE = Set.of(
            Module.STUDENT, Module.COURSE, Module.REGISTRATION, Module.GRADE,
            Module.INVENTORY, Module.FINANCE, Module.RESEARCH);

    private static User current;
    private final UserDao userDao = new UserDao();

    public User login(String username, String password) {
        UserDao.UserRecord rec = userDao.findByUsername(username);
        if (rec == null) return null;
        if (!BCrypt.checkpw(password, rec.passwordHash)) return null;
        current = rec.user;
        return rec.user;
    }

    public static User currentUser() { return current; }

    public static void logout() { current = null; }

    public static boolean canAccess(Module m) {
        if (current == null) return false;
        if (current.isAdmin()) return m == Module.AUDIT;
        if (m == Module.AUDIT) return false;
        return true;
    }

    public static boolean canWrite(Module m) {
        if (current == null) return false;
        if (current.isSecretary()) return SECRETARY_WRITE.contains(m);
        return false;
    }
}
