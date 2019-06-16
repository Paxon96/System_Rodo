package pl.p.lodz.system.rodo.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Mark")
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "MarkID")
    private int id;

    @Column(name = "Mark")
    private double mark;

    @Column(name = "EvalDate")
    private Timestamp evalDate;

    @Column(name = "Points")
    private double points;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "StudentID")
    private User user;
}
