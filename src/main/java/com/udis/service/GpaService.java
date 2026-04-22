package com.udis.service;

import com.udis.dao.GradeDao;
import com.udis.model.Grade;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GpaService {

    public static final Map<String, Double> LETTER_TO_POINTS;
    static {
        Map<String, Double> m = new LinkedHashMap<>();
        m.put("A+", 10.0);
        m.put("A",  10.0);
        m.put("A-",  9.0);
        m.put("B+",  8.5);
        m.put("B",   8.0);
        m.put("B-",  7.0);
        m.put("C+",  6.5);
        m.put("C",   6.0);
        m.put("D",   5.0);
        m.put("F",   0.0);
        LETTER_TO_POINTS = java.util.Collections.unmodifiableMap(m);
    }

    private final GradeDao gradeDao = new GradeDao();

    public double pointsFor(String letter) {
        Double p = LETTER_TO_POINTS.get(letter);
        if (p == null) throw new IllegalArgumentException("Unknown letter grade: " + letter);
        return p;
    }

    public double gpa(List<Grade> grades) {
        double num = 0, den = 0;
        for (Grade g : grades) {
            num += g.getGradePoints() * g.getCredits();
            den += g.getCredits();
        }
        return den == 0 ? 0 : num / den;
    }

    public double semesterGpa(String rollNo, int semester, int year) {
        return gpa(gradeDao.findBySemester(rollNo, semester, year));
    }

    public double cgpa(String rollNo) {
        return gpa(gradeDao.findByStudent(rollNo));
    }

    /** Semester key -> GPA. Key format: "Sem N / YYYY". Sorted by year then semester. */
    public Map<String, Double> semesterHistory(String rollNo) {
        List<Grade> all = gradeDao.findByStudent(rollNo);
        Map<String, List<Grade>> bySem = new TreeMap<>();
        for (Grade g : all) {
            String key = String.format("Sem %d / %d", g.getSemester(), g.getYear());
            bySem.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(g);
        }
        Map<String, Double> out = new LinkedHashMap<>();
        for (Map.Entry<String, List<Grade>> e : bySem.entrySet()) {
            out.put(e.getKey(), gpa(e.getValue()));
        }
        return out;
    }
}
