package com.student.cv.api.studentcvapi.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a keyword associated with a student.
 * Multiple keywords can belong to a single student.
 * Each combination of student_id and text is unique.
 */
@Entity
@Table(name ="keywords",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_student_keyword",
                        columnNames = {"student_id", "text"}
                )
        }
)
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String text;

    public Keyword(){

    }

    public Keyword(Student student, String text) {
        this.student = student;
        this.text = text;
    }

    public Keyword(Integer id, Student student, String text) {
        this.id = id;
        this.student = student;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword = (Keyword) o;
        return Objects.equals(id, keyword.id) && Objects.equals(text, keyword.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
