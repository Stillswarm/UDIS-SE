package com.udis.service;

import com.udis.dao.CourseDao;
import com.udis.dao.RegistrationDao;
import com.udis.model.Course;
import com.udis.model.Registration;

public class RegistrationService {

    private final CourseDao courseDao = new CourseDao();
    private final RegistrationDao regDao = new RegistrationDao();

    public static class RegistrationException extends RuntimeException {
        public RegistrationException(String msg) { super(msg); }
    }

    public void register(String rollNo, String courseId, int semester, int year) {
        Course course = courseDao.findById(courseId);
        if (course == null) throw new RegistrationException("Course '" + courseId + "' does not exist.");

        if (regDao.alreadyRegistered(rollNo, courseId, semester, year)) {
            throw new RegistrationException("Already registered for " + courseId + " in Sem " + semester + "/" + year + ".");
        }

        String prereq = course.getPrerequisiteId();
        if (prereq != null && !prereq.isBlank() && !regDao.hasCompleted(rollNo, prereq)) {
            throw new RegistrationException(
                    "Cannot register for " + courseId + ": prerequisite '" + prereq + "' not cleared.");
        }

        Registration r = new Registration();
        r.setRollNo(rollNo);
        r.setCourseId(courseId);
        r.setSemester(semester);
        r.setYear(year);
        r.setStatus("REGISTERED");
        regDao.insert(r);
        AuditService.log("REGISTER", "registration:" + rollNo + "/" + courseId + "/" + semester + "/" + year);
    }
}
